(function($) {

    return {
        ajaxWhoIsOnLine: function(labels) {
            //
            var labels = labels;
            
            
            // User Profile Popup initialize
              var portal = eXo.social.portal;
              var restUrl = 'http://' + window.location.host + portal.context + '/' + portal.rest + '/social/people' + '/getPeopleInfo/{0}.json';
            
            var userLinks = $('#onlineList').find('a:[href*="/profile/"]');
            $.each(userLinks, function (idx, el) {
                var userUrl = $(el).attr('href');
                var userId = userUrl.substring(userUrl.lastIndexOf('/') + 1);
                
                $(el).userPopup({
                  restURL: restUrl,
                  labels: labels,
                  content: false,
                  defaultPosition: "left",
                  keepAlive: true,
                  maxWidth: "240px"
                });
            });

        }

    };
})($);
