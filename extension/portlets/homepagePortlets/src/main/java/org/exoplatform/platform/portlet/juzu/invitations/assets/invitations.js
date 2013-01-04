function initIncoming() {
    var visibility;
    var link;
    $.getJSON("/rest/homepage/intranet/invitations/allInvitations", function(items){

        if (items.length > 0){
            $("#InvitationsPortlet").show();
        }

        $.each(items, function(i, item){
            link = "";
            if(item.invitationType == "people"){

                if (i < 4)
                    link += "<li id='"+item.relationId+"'>";
                else
                    link += "<li style='display:none;' id='"+item.relationId+"'>";
                var peopleAvatar;
                if (item.avatar == undefined)
                    peopleAvatar = "/social-resources/skin/ShareImages/Avatar.gif";
                else
                    peopleAvatar = item.avatar;
                link += "<div class='peopleInvitePicture' ><img src='"+peopleAvatar+"'></div>";
                link += "<div class='peopleInviteInfo'>";

                link += "<div class='peopleInviteName'><div class='name'>"+item.senderName+"</div><div class='peopleInviteAction' style='visibility:hidden;'><a class='connect' href='#' onclick='return false'>Accept</a> <a class='deny' href='#' onclick='return false'><img src='/homepage-portlets/style/images/deny.png'></a></div></div>";

                if (item.position != undefined)
                    link += "<div class='peopleInvitePosition'>"+item.position+"</div>";
                link += "</div></li>";

                $("#requests").append(link);

                $("#"+item.relationId).mouseover(function() { $("#"+item.relationId+" .name").addClass("actionInviteAppears");$("#"+item.relationId+" .peopleInviteAction").css('visibility','visible'); });

                $("#"+item.relationId).mouseout(function() { $("#"+item.relationId+" .name").removeClass("actionInviteAppears"); $("#"+item.relationId+" .peopleInviteAction").css('visibility','hidden'); });

                $("#"+item.relationId+" a.connect").live("click", function(){
                    $.getJSON("/rest/homepage/intranet/people/contacts/confirm/"+item.relationId, null);

                    if($("#requests").children().length == 1) {
                        $("#InvitationsPortlet").fadeOut(500, function () {
                            $("#"+item.relationId).remove();
                            $("#InvitationsPortlet").hide();
                        });
                    }
                    else {
                        $("#"+item.relationId).fadeOut(500, function () {
                            $("#"+item.relationId).remove();
                            var count = parseInt($("#inviteCounter").html());
                            $("#inviteCounter").html(count-1);
                            $('#InvitationsPortlet li:hidden:first').fadeIn(500, function() {});
                        });
                    }
                });

                $("#"+item.relationId+" a.deny").live("click", function(){
                    $.getJSON("/rest/homepage/intranet/people/contacts/deny/"+item.relationId, null);

                    if($("#requests").children().length == 1) {
                        $("#InvitationsPortlet").fadeOut(500, function () {
                            $("#"+item.relationId).remove();
                            $("#InvitationsPortlet").hide();
                        });
                    }
                    else {
                        $("#"+item.relationId).fadeOut(500, function () {
                            $("#"+item.relationId).remove();
                            var count = parseInt($("#inviteCounter").html());
                            $("#inviteCounter").html(count-1);
                            $('#InvitationsPortlet li:hidden:first').fadeIn(500, function() {});

                        });
                    }
                });

            }
            else{
                if (i < 4)
                    link += "<li id='"+item.spaceId+"'>";
                else
                    link += "<li style='display:none;' id='"+item.spaceId+"'>";
                var spaceAvatar;
                if (item.avatar == undefined)
                    spaceAvatar =  "/social-resources/skin/ShareImages/SpaceImages/SpaceLogoDefault_61x61.gif";
                else
                    spaceAvatar = item.avatar;
                link += "<div class='spaceInvitePicture' ><img src='"+spaceAvatar+"'></div>";
                link += "<div class='spaceInviteInfo'>";
                link += "<div class='spaceInviteName'>"+item.displayName+"</div>";
                if(item.registration == "open")
                    visibility = "public";
                else
                    visibility = "private";
                link += "<div class='spaceproperties'><div class='spacevisibility'>"+visibility+" space - "+item.number+" Members </div><div class='spaceInviteAction' style='visibility:hidden;' ><a class='connect' href='#' onclick='return false'>Accept</a>  <a class='deny' href='#' onclick='return false'><img src='/homepage-portlets/style/images/deny.png'></a></div> </div>"
                link += "</div></li>";

                $("#requests").append(link);
                $("#"+item.spaceId).mouseover(function() {  $("#"+item.spaceId+" .spacevisibility").addClass("actionSpaceAppears"); $("#"+item.spaceId+" .spaceInviteAction").css('visibility','visible'); });
                $("#"+item.spaceId).mouseout(function() { $("#"+item.spaceId+" .spacevisibility").removeClass("actionSpaceAppears"); $("#"+item.spaceId+" .spaceInviteAction").css('visibility','hidden'); });

                $("#"+item.spaceId+" a.connect").live("click", function(){
                    $.getJSON("/rest/homepage/intranet/spaces/accept/"+item.spaceId, null);

                    if($("#requests").children().length == 1) {
                        $("#InvitationsPortlet").fadeOut(500, function () {
                            $("#"+item.spaceId).remove();
                            $("#InvitationsPortlet").hide();

                        });
                    }
                    else {
                        $("#"+item.spaceId).fadeOut(500, function () {
                            $("#"+item.spaceId).remove();
                            var count = parseInt($("#inviteCounter").html());
                            $("#inviteCounter").html(count-1);
                            $('#InvitationsPortlet li:hidden:first').fadeIn(500, function() {});
                        });
                    }
                });

                $("#"+item.spaceId+" a.deny").live("click", function(){
                    $.getJSON("/rest/homepage/intranet/spaces/deny/"+item.spaceId, null);

                    if($("#requests").children().length == 1) {
                        $("#InvitationsPortlet").fadeOut(500, function () {
                            $("#"+item.spaceId).remove();
                            $("#InvitationsPortlet").hide();
                        });
                    }
                    else {
                        $("#"+item.spaceId).fadeOut(500, function () {
                            $("#"+item.spaceId).remove();
                            var count = parseInt($("#inviteCounter").html());
                            $("#inviteCounter").html(count-1);
                            $('#InvitationsPortlet li:hidden:first').fadeIn(500, function() {});
                        });
                    }
                });


            }
            $("#inviteCounter").html(i+1);
        });
    });
}

$(document).ready(function() {
    initIncoming();
});