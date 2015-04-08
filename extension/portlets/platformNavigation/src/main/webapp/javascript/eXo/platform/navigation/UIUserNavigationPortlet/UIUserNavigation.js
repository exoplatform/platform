(function($) {
  var UIUserNavigation = {
    MORE_LABEL : "",
    initNavigation : function(moreLabel) {
      UIUserNavigation.MORE_LABEL = moreLabel;
      
      //
      function autoMoveApps(){
        var ul = $('.userNavigation');
        
        var maxWith = ul.outerWidth();
        var liElements = ul.find('li.item');
        var w = 0, index = 0;
        for (var i = 0; i < liElements.length; ++i) {
          var wElm = liElements.eq(i).outerWidth();
          if((w + wElm) < maxWith) {
            w += wElm;
            index++;
          } else {
            break;
          }
        }
        
        UIUserNavigation.reInitNavigation(index);
      };
    
      function reset() {
        var ul = $('.userNavigation');
        var liElements = ul.find('li.item');
    
        var temp = $('<ul></ul>');
        temp.append(liElements);
        ul.empty().append(temp.find('li.item'));
      };
      
      $(document).ready(function(){
        var ul = $('.userNavigation');
        var liElements = ul.find('> li');
        liElements.addClass('item');
        autoMoveApps(); 
      });
  
      $(window).resize(function(){
        if (!$.browser.msie) {
          reset();
          autoMoveApps();
        }
      });
  
      $('.userNavigation').resize(function(){
        if ($.browser.msie) {
          reset();
          autoMoveApps();
        }
      });
    },
  	reInitNavigation : function(index) {
  	  //
      var tabContainer = $('ul.userNavigation');
      var tabs = tabContainer.find('li.item');
      
      var dropDownMenu = $('<ul/>', {
        'class' : 'dropdown-menu'
      });
  
      var dropDownToggle = $('<a/>', {
        'href' : '#',
        'class' : 'dropdown-toggle',
        'data-toggle' : 'dropdown'
      }).append($('<i/>', {
                            'class' : 'uiIconAppMoreButton'
                          }))
        .append($('<span/>', {
                               'text' : UIUserNavigation.MORE_LABEL
                             })
        );
  
      // clear
      tabContainer.empty();
  
      // rebuild
      $.each(tabs, function(idx, el) {
        if (idx < index - 1) {
          tabContainer.append(el);
        } else {
          dropDownMenu.append(el);
        }
      });
      
      if (dropDownMenu.children().length == 1) {
        var el = dropDownMenu.children(':first');
        dropDownMenu.remove();
        tabContainer.append(el);
      } else if (dropDownMenu.children().length > 1) {
  	    var dropDown = $('<li/>', {
  	      'class' : 'dropdown pull-right'
  	    }).append(dropDownToggle).append(dropDownMenu);
        
        tabContainer.append(dropDown);
      };
      
      // swap position if needed
      var swappedEl = $(dropDown).find('li.active');
      if ( swappedEl.length > 0 ) {
        var targetEl = $(dropDown).prevAll('li:first');
        var copy_to = $(swappedEl).clone(true);
        var copy_from = $(targetEl).clone(true);
        $(swappedEl).replaceWith(copy_from);
        $(targetEl).replaceWith(copy_to);
      }
      
      $(tabContainer).css({"visibility":"visible"});
  	}
  };
  
  return UIUserNavigation;
})(jq);
