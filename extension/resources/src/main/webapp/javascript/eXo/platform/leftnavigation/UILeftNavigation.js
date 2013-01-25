(function($) {
    var UILeftNavigation = {
        resize: function() {
            var myHeight = $(document).height()-42;
            $('.LeftNavigationTDContainer').attr('height',myHeight+'px');
        }
    };
    return UILeftNavigation;
})($);