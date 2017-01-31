(function($) {
    var glables = null;
    if($('#onlineList li').length == 0) {
        $("#OnlinePortlet").hide();
    } else {
        $("#OnlinePortlet").show();
    }

    var showTooltip = function(users) {

        if(users.length > 0) {
            var txt = '';
            for (var i = 0; i < users.length; i++) {
                var user = users[i];
                txt += '<li id="' + user.id + '">' +
                    '<a class="avatarXSmall" href="' + user.profileUrl + '"><img src="' + user.avatar + '" alt="image" /></a>' +
                    '</li>';
            }
            $('#onlineList').html(txt);

            $("#OnlinePortlet").show();
        } else {
            $("#OnlinePortlet").hide();
        }

        // User Profile Popup initialize
        var portal = eXo.social.portal;
        var restUrl = '//' + window.location.host + portal.context + '/' + portal.rest + '/social/people' + '/getPeopleInfo/{0}.json';



        var userLinks = $('#onlineList').find('a:[href*="/profile/"]');
        $.each(userLinks, function (idx, el) {
            var userUrl = $(el).attr('href');
            var userId = userUrl.substring(userUrl.lastIndexOf('/') + 1);

            $(el).userPopup({
                restURL: restUrl,
                labels: glables,
                content: false,
                defaultPosition: "left",
                keepAlive: true,
                maxWidth: "240px"
            });
        });
    };

    var refresh = function() {

        $.getJSON('/rest/state/onlinefriends', function (users) {
            showTooltip(users);
        });

    };
    // Wait 1/2 second (not realistic of course)
    // And we should use setInterval with 60 seconds
    setTimeout(refresh, 500);
    setInterval(refresh,60000);
    return {
        ajaxWhoIsOnLine: function(labels) {
            var labels = labels;

            glables = labels;
            $.getJSON('/rest/state/onlinefriends', function (users) {
                showTooltip(users);
            });

        }
    };
})($);
