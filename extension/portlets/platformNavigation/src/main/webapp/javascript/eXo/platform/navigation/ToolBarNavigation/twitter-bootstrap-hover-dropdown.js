(function($, window, undefined) {
  $.fn.dropdownHover = function() {
    $(this).hover( function() {
      if ( !$(this).parent().hasClass("open")) {
        $(this).click();
      }}, function (){}
    );
  }

  $(document).ready(function() {
    // apply dropdownHover to all elements with the data-hover="dropdown" attribute
    $('[data-hover="dropdown"]').dropdownHover();
  });
  $(document).ajaxComplete(function() {
    // apply dropdownHover to all elements with the data-hover="dropdown" attribute
    $('[data-hover="dropdown"]').dropdownHover();
  });
})(jQuery, this);
