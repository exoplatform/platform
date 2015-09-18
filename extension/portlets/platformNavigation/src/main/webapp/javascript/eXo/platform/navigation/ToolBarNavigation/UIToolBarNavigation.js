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

            if(!!parent.find("#QuickAddEventContainer").length){
             uiCreateList.addClass('openingAddEvent');
            }

            if(!!parent.find("#UICreatePoll").length){
             uiCreateList.addClass('openingAddPoll');
            }

            if(!!parent.find("#UICreateTopic").length){
             uiCreateList.addClass('openingCreatePoll');
            }

            if(!!parent.find("#UploadFileSelectorPopUpWindow").length){
             uiCreateList.addClass('openingUploadFile');
            }
            
            if(!!parent.find("#UICreateForm").length){
             uiCreateList.addClass('openingWiki');
            }
        }
    };

    return {
        UIPortalNavigation:portalNavigation
    };
})($);
