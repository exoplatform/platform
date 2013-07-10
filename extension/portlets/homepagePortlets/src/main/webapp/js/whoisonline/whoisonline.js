(function($) {
	
	var glables = null;

    return {
        ajaxWhoIsOnLine: function(labels) {
            //
            var labels = labels;
            
            glables = labels;
            
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
    
    var refresh = function() {
    	//
    	if (glables != null) {
    		// User Profile Popup initialize
    		var portal = eXo.social.portal;
    		var restUrl = 'http://' + window.location.host + portal.context + '/' + portal.rest + '/social/people' + '/getPeopleInfo/{0}.json';

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
    	} 
    }
    
    // Wait 1/2 second (not realistic of course)
    // And we should use setInterval with 60 seconds
    setTimeout(refresh, 500);
    setInterval(refresh,60000);
    
})($);
