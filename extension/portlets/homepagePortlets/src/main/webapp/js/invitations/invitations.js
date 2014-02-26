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
                            link += "<li class='clearfix peopleList' id='"+item.relationId+"'>";
                        else
                            link += "<li class='clearfix peopleList' style='display:none;' id='"+item.relationId+"'>";
                        var peopleAvatar;
                        if (item.senderAvatarUrl == undefined)
                            peopleAvatar = "/eXoSkin/skin/less/social/skin/images/ShareImages/UserAvtDefault.png";
                        else
                            peopleAvatar = item.senderAvatarUrl;
                        link += "<div class='peopleInvitePicture pull-left avatarXSmall'><a href='"+item.profile_url+"'><img src='"+peopleAvatar+"'></a></div>";
                        link += "<div class='peopleInviteInfo'>";

                        link += "<div class='peopleInviteName'><div class='name'><a href='"+item.profile_url+"'>"+item.senderName+"</a></div>";
						link += "<div class='inviteAction'><div class='peopleInviteAction'><a class='connect btn-primary btn btn-mini' href='#' onclick='return false'>"+acceptlabel+"</a> <a class='deny' href='#' onclick='return false'><i class='uiIconClose'></i></a></div>";
						if (item.senderPosition != undefined)
                            link += "<div class='peopleInvitePosition'>"+item.senderPosition+"</div>";
						link += "</div></div>";
                        
                        link += "</div></li>";

                        $("#requests").append(link);

                        $("#"+item.relationId).mouseover(function() { $("#"+item.relationId+" .peopleInvitePosition").addClass("actionInviteAppears");$("#"+item.relationId+" .peopleInviteAction").addClass('active'); });

                        $("#"+item.relationId).mouseout(function() { $("#"+item.relationId+" .peopleInvitePosition").removeClass("actionInviteAppears"); $("#"+item.relationId+" .peopleInviteAction").removeClass('active'); });

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
                            link += "<li class='clearfix spaceList' id='"+item.spaceId+"'>";
                        else
                            link += "<li class='clearfix spaceList' style='display:none;' id='"+item.spaceId+"'>";
                        var spaceAvatar;
                        if (item.spaceAvatarUrl == undefined)
                            spaceAvatar =  "/eXoSkin/skin/less/social/skin/images/ShareImages/UserAvtDefault.png";
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
                            link += "<div class='spaceproperties'><div class='spaceInviteAction'  ><a class='accept  btn-primary btn btn-mini' href='#' onclick='return false'>"+acceptlabel+"</a>  <a class='deny' href='#' onclick='return false'><i class='uiIconClose'></i></a></div><div class='spacevisibility'><i class='uiIconSocGroup uiIconSocLightGray'></i>&nbsp"+visibility+" "+spacelabel+" - " +item.membersNumber+" "+memberslabel+"</div></div>"
                        else
                            link += "<div class='spaceproperties'><div class='spaceInviteAction'  ><a class='accept  btn-primary btn btn-mini' href='#' onclick='return false'>"+acceptlabel+"</a>  <a class='deny' href='#' onclick='return false'><i class='uiIconClose'></i></a></div><div class='spacevisibility'><i class='uiIconSocGroup uiIconSocLightGray'></i>&nbsp"+spacelabel+" "+visibility+" - " +item.membersNumber+" "+memberslabel+"</div></div>"
                        link += "</div></li>";

                        $("#requests").append(link);
                        $("#"+item.spaceId).mouseover(function() {
                            var $item = $(this);
                            $item.find(".spacevisibility").addClass("actionSpaceAppears");
                            $item.find(".name").addClass("actionInviteAppears");
                            $item.find(".spaceInviteAction").addClass('active');
                        });
                        $("#"+item.spaceId).mouseout(function() {
                            var $item = $(this);
                            $item.find(".spacevisibility").removeClass("actionSpaceAppears");
                            $item.find(".name").removeClass("actionInviteAppears");
                            $item.find(".spaceInviteAction").removeClass('active');
                        });

                        $("#"+item.spaceId+" a.accept").live("click", function(){
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
