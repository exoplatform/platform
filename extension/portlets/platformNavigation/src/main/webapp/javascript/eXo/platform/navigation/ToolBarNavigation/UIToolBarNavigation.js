(function ($) {
    var portalNavigation = {

        cancelNextClick:function (ComponentId, baseId, message) {
            var component = $("#" + ComponentId);
            var parent = $("#" + baseId);

            var portalNav = portalNavigation;

            component.hide();
            parent.find("span").html(message).css('display', 'inline').fadeOut(2000, function () {
                component.removeAttr("style");

            });
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
