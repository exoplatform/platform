(function ($, base) {
  var UILeftNavigation = {
    init: function () {
      var $leftNavi = $('#LeftNavigation');
      this.resize();
      if ($(window).width()  > 1024) {
        $leftNavi.perfectScrollbar({'suppressScrollX': true});
      }
      base.Browser.addOnResizeCallback('UILeftNavigation', function() {
        var $leftNavi = $('#LeftNavigation');
        if ($(window).width()  > 1024) {
          $leftNavi.css('position', 'fixed').perfectScrollbar({'suppressScrollX': true});
        } else {
          $leftNavi.css('position', 'static').removeClass('ps').perfectScrollbar('destroy');
        }
      });
    },
    resize: function () {
      var $leftNavi = $('#LeftNavigation').parent();
      var myHeight = $(window).height();
      $leftNavi.css('height', myHeight + 'px');
    }
  };
  return UILeftNavigation;
})($, base);
