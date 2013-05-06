(function($) {
    var UILeftNavigation = {
        resize: function() {
            var myHeight = $(document).height()-42;
            var popUpHeight=    $('.LeftNavigationTDContainer');
            $('.LeftNavigationTDContainer').css('height',myHeight+'px');
        }
    };
    return UILeftNavigation;
})($);