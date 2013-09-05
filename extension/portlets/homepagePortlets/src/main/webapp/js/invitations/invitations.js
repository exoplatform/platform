(function ($) {
    var visibility;
    var link;
    var acceptlabel;
    var memberslabel;
    var spacelabel;
    var publiclabel;
    var privatelabel;

    $(".invitlabel").each(function() {
        acceptlabel = $(this).data("acceptlabel");
        memberslabel = $(this).data("memberslabel");
        spacelabel = $(this).data("spacelabel");
        publiclabel = $(this).data("publiclabel");
        privatelabel = $(this).data("privatelabel");

    });

    return {
        initInvitations: function() {
            $.getJSON("/rest/homepage/intranet/invitations/allInvitations", function(items){

                if (items.length > 0){
                    $("#InvitationsPortlet").show();
                }

                var reversedItems = items.slice().reverse();
                $.each(reversedItems, function(i, item){
                    link = "";
                    if(item.invitationType == "people"){

                        if (i < 4)
                            link += "<li class='clearfix' id='"+item.relationId+"'>";
                        else
                            link += "<li class='clearfix' style='display:none;' id='"+item.relationId+"'>";
                        var peopleAvatar;
                        if (item.senderAvatarUrl == undefined)
                            peopleAvatar = "/social-resources/skin/images/ShareImages/UserAvtDefault.png";
                        else
                            peopleAvatar = item.senderAvatarUrl;
                        link += "<div class='peopleInvitePicture pull-left avatarXSmall'><a href='"+item.profile_url+"'><img src='"+peopleAvatar+"'></a></div>";
                        link += "<div class='peopleInviteInfo'>";

                        link += "<div class='peopleInviteName'><div class='name'><a href='"+item.profile_url+"'>"+item.senderName+"</a></div><div class='peopleInviteAction' style='visibility:hidden;'><a class='connect btn-primary btn btn-mini' href='#' onclick='return false'>"+acceptlabel+"</a> <a class='deny' href='#' onclick='return false'><i class='uiIconClose'></i></a></div></div>";

                        if (item.senderPosition != undefined)
                            link += "<div class='peopleInvitePosition'>"+item.senderPosition+"</div>";
                        link += "</div></li>";

                        $("#requests").append(link);

                        $("#"+item.relationId).mouseover(function() { $("#"+item.relationId+" .peopleInvitePosition").addClass("actionInviteAppears");$("#"+item.relationId+" .peopleInviteAction").css('visibility','visible'); });

                        $("#"+item.relationId).mouseout(function() { $("#"+item.relationId+" .peopleInvitePosition").removeClass("actionInviteAppears"); $("#"+item.relationId+" .peopleInviteAction").css('visibility','hidden'); });

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
                            link += "<li class='clearfix' id='"+item.spaceId+"'>";
                        else
                            link += "<li class='clearfix' style='display:none;' id='"+item.spaceId+"'>";
                        var spaceAvatar;
                        if (item.spaceAvatarUrl == undefined)
                            spaceAvatar =  "/social-resources/skin/images/ShareImages/UserAvtDefault.png";
                        else
                            spaceAvatar = item.spaceAvatarUrl;
                        link += "<div class='spaceInvitePicture pull-left avatarXSmall'><img src='"+spaceAvatar+"'></div>";
                        link += "<div class='spaceInviteInfo'>";
                        link += "<div class='spaceInviteName'>"+item.spaceDisplayName+"</div>";
                        if(item.spaceRegistration == "open")
                            visibility = publiclabel;
                        else
                            visibility = privatelabel;
                        if (spacelabel == "Space")
                            link += "<div class='spaceproperties'><div class='spacevisibility'><i class='uiIconSocGroup uiIconSocLightGray'></i> "+visibility+" "+spacelabel+" - " +item.membersNumber+" "+memberslabel+"</div><div class='spaceInviteAction' style='visibility:hidden;' ><a class='connect  btn-primary btn btn-mini' href='#' onclick='return false'>"+acceptlabel+"</a>  <a class='deny' href='#' onclick='return false'><i class='uiIconClose'></i></a></div> </div>"
                        else
                            link += "<div class='spaceproperties'><div class='spacevisibility'><i class='uiIconSocGroup uiIconSocLightGray'></i> "+spacelabel+" "+visibility+" - " +item.membersNumber+" "+memberslabel+"</div><div class='spaceInviteAction' style='visibility:hidden;' ><a class='connect  btn-primary btn btn-mini' href='#' onclick='return false'>"+acceptlabel+"</a>  <a class='deny' href='#' onclick='return false'><i class='uiIconClose'></i></a></div> </div>"
                        link += "</div></li>";

                        $("#requests").append(link);
                        $("#"+item.spaceId).mouseover(function() {
                            var $item = $(this);
                            $item.find(".spacevisibility").addClass("actionSpaceAppears");
                            $item.find(".name").addClass("actionInviteAppears");
                            $item.find(".spaceInviteAction").css('visibility','visible');
                        });
                        $("#"+item.spaceId).mouseout(function() {
                            var $item = $(this);
                            $item.find(".spacevisibility").removeClass("actionSpaceAppears");
                            $item.find(".name").removeClass("actionInviteAppears");
                            $item.find(".spaceInviteAction").css('visibility','hidden');
                        });

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
    };

})($);
