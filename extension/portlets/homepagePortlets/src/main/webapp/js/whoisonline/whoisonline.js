(function($) {

    if($('#onlineList li').length == 0) {
        $("#OnlinePortlet").hide();
    } else {
        $("#OnlinePortlet").show();
    }

    var showTooltip = function() {
        $('#onlineList li').each(function() {
            var activity = $('#onlineList input[name=activity]').val();
            var connect = $('#onlineList input[name=connect]').val();
            var messageLabel = $('#onlineList input[name=messageLabel]').val();

            var userId = $(this).find("input[name=userId]").val();
            var userAvatar = $(this).find("input[name=userAvatar]").val();
            var profileURL = $(this).find("input[name=profileURL]").val();
            var fullName = $(this).find("input[name=fullName]").val();
            var userPosition = $(this).find("input[name=userPosition]").val();
            var userIdentity = $(this).find("input[name=userIdentity]").val();
            var userRelationId = $(this).find("input[name=userRelationId]").val();
            $("#"+userId).tipTip({ content: "<div id='tipName' class='clearfix'><a target='_parent' class='pull-left avatarXSmall'><img src='"+userAvatar+"' alt='image' /></a><div class='detail'><div class='name'><a href='"+profileURL+"'>"+fullName+"</a></div><div class='displayName'>"+userPosition+"</div></div></div>"+activity+connect,defaultPosition: "left", keepAlive: true,maxWidth: "240px"});

            $("#" + userId +"connect a.connect").live("click", function(){
                $.getJSON("/rest/homepage/intranet/people/contacts/connect/" + userIdentity, null);
                $("#" + userId + "connect").fadeOut(500, function () {
                    $(this).html("<div id='connectMessge'>" + messageLabel + "</div>");
                    $(this).fadeIn(500, function() {});
                });

                setTimeout(refresh, 500);
            });

            $("#" + userId + "accept a.connect").live("click", function(){
                $.getJSON("/rest/homepage/intranet/people/contacts/confirm/" + userRelationId, null);
                $("#" + userId + "accept").hide();
                setTimeout(refresh, 500);
            });
        });
    };

    var refresh = function() {
        $("#onlineList").each(function() {
            $(this).jzLoad("WhoIsOnLineController.users()", showTooltip);
        });
    };
    // Wait 1/2 second (not realistic of course)
    // And we should use setInterval with 60 seconds
    setTimeout(refresh, 500);
    setInterval(refresh,60000);
})($);
