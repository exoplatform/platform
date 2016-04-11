(function ($) {
    var portalNavigation = {

        cancelNextClick:function (ComponentId, baseId, message, link) {
            var component = $("#" + ComponentId);
            var parent = $("#" + baseId);
            var $span = parent.find('span');

            var portalNav = portalNavigation;

            component.hide();

            var html = message;
            if (link) {
                html = '<a href="'+link+'">' + message + '</a>';
            }

            $span.html(html).css('display', 'inline');
            setTimeout(function() {
                $span.fadeOut(1000, function() {
                    component.parent().removeClass('open');
                    component.removeAttr("style");
                });
            }, 5000);
        },
        ClickActionButton:function ( baseId) {

            var parent = $("#" + baseId);

            parent.toggleClass('open');
			$('#UICreateList').find('.btn').next().click(function(){
			  $('#UICreateList').css('opacity','0').removeClass('create-form-dropdown').delay(200).css('opacity','1');
			//create new wiki page on top navigation
		  });     
        }
    };

    return {
        UIPortalNavigation:portalNavigation
    };
})($);
