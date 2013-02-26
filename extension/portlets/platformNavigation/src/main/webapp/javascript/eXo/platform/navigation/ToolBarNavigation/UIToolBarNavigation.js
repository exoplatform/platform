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
        }
    };

    return {
        UIPortalNavigation:portalNavigation
    };
})($);
