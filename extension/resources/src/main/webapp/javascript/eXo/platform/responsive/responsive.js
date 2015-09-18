(function($) {
  // Check Size Window
  var windowsize = $(window).width();

  if(windowsize < 1025) {
    $('body').addClass('open-right-bar');  
  }


  var tabManagerApp = {
    container : $('#UIToolbarContainer'),
    init : function() {
      if ($('.OfficeRightTDContainer').length != 0) {    
        $('.OfficeMiddleTDContainer').append($('<a href="javascript:void(0)" class="visible-tablet toggle-right-bar"><i class="uiIconVerticalLines"></i></a>'));
        var _h = $(window).height(); 
        $('.toggle-right-bar').css('top',_h/2);  
        $('#OfficeRight').css('height',$('.RightBodyTDContainer ').height());     
      }
      this.toggleLeftBar();
      this.toggleRightBar();
      this.leftNavAccordion();
      this.displaySubMenu();
      // trigger back item when client lost focus on sub menu
      $('#UISetupPlatformToolBarPortlet').on('click', function(evt) {
        $('.back-item').trigger('click');
      });
      //
      $('#UIUserPlatformToolBarPortlet').on('click', function (evt) {
       
        $('.back-item').trigger('click');
      });
      tabManagerApp.showProfileMenu();
      //
      $('#ToolBarSearch > a').click(function(){
        tabManagerApp.searchOnTopNavivation();
      });
      // adapt dropdown menu with screen size if height too big
      $('.UIToolbarContainer .dropdown-toggle').on('click', function(event) {  
        tabManagerApp.setHeightMenu();
      });
      //
      this.setPositionRightButton();
    },
    setPositionRightButton : function() {
      var _w = $(window).width();     
      if ( _w  < 1025 && _w > 767) {         
        var _h = $(window).height();
        tabManagerApp.container.find('.toggle-right-bar:first').css('top', function() {
           return (_h - $(this).height()) / 2;
        });
      }
    },
    toggleLeftBar : function() {
       $('.toggle-left-bar').on('click', function() {
        if($('body').hasClass('open-left-bar')) {
          tabManagerApp.hideLeftPanel();  
        } else {
          tabManagerApp.showLeftPanel();  
        }
      });
    },
    toggleRightBar : function() {
      $('.toggle-right-bar').on('click', function() {
        if($('body').hasClass('hidden-right-bar')) {
          tabManagerApp.showRightPanel();
        } else {
          tabManagerApp.hideRightPanel();
        }
      });
    },
    showLeftPanel : function() {
      var leftNavi= $('.LeftNavigationTDContainer:first');
      $('body').addClass('open-left-bar');
      $('body').removeClass('hidden-left-bar');
      $('.mask-layer-right').remove();
      $('#RightBody').before('<div class="mask-layer-right"></div>');
      $('.RightBodyTDContainer:first').css('height', leftNavi.height());
      $('body,html').css('overflow-y',"hidden");
      $('.mask-layer-right').on('click',function(){
        tabManagerApp.hideLeftPanel();
      });
      leftNavi.addClass('expanded');      
    },
    hideLeftPanel : function() {
      var leftNavi= $('.LeftNavigationTDContainer:first');
      $('body').removeClass('open-left-bar');
      $('body').addClass('hidden-left-bar');
      $('body,html').css('overflow-y','');
      $('.mask-layer-right').remove();
      leftNavi.removeClass('expanded');
    },
    hideRightPanel: function() {
      var rightNavi= $('.OfficeRightTDContainer');
      $('body,html').css('overflow-y',"visible");
      $('body').removeClass('open-right-bar');
      $('body').addClass('hidden-right-bar');      
      rightNavi.addClass('expanded');      
    },
    showRightPanel : function() {
      var rightNavi= $('.OfficeRightTDContainer');
      $('body').removeClass('hidden-right-bar');  
      $('body').addClass('open-right-bar');  
      rightNavi.removeClass('expanded');    
    },
    displaySubMenu : function () {
      tabManagerApp.container.find('.dropdown-submenu > a').on('click', function(evt) {
        var dropdownSub = $(this);
        
        var dropdown = dropdownSub.parents('.dropdown-menu');
        
        var _w = $(window).width();     
        if ( _w < 1025 ) {
        evt.stopPropagation();
           evt.preventDefault();
           var backButton = $('<li class="back-item"><a href="javascript:void(0)"><i class="uiIconArrowLeft" style="margin-right: 2px;"></i>Back</a></li>');
           backButton.on('click', function(evt) {
              evt.stopPropagation();
              parent.find('.current')
                    .removeClass('current')
                    .find('ul.dropdown-menu:first')
                    .append(parent.find('.current-child')
                    .removeClass('current-child'));
              $(this).remove();
              parent.find('>li').show();
              dropdown.removeClass('parent-current');
           });
           //
           var parent = dropdownSub.parent().addClass('current').parent('ul');
           dropdown.addClass('parent-current');
           parent.find('>li').hide();
           parent.append(backButton);
          //
          var sub = dropdownSub.parent().find('.dropdown-menu:first > li').addClass('current-child');
          parent.append(sub);
        }
      });
    },
    leftNavAccordion : function() {
      var aTitle = $('#LeftNavigation .accordionBar').find('a');
      if ( windowsize < 1025 ) {
      
    if (windowsize <= 480 ) { //mobile 
      $('.title.accordionBar').first().addClass('active');      
      $('.accordionCont').first().addClass('active').show();     
    }else{
      $('.title.accordionBar').addClass('active');      
      $('.accordionCont').addClass('active').show();     
    }
        
    
      $('.title.accordionBar').prepend('<i class="uiIconArrowRight pull-right"></i>');  
      $('.uiSpaceNavigationPortlet .joinSpace').insertBefore($('.uiSpaceNavigationPortlet .spaceNavigation'));
        $('#LeftNavigation .accordionBar').click(function(e){
          if(windowsize>1024) return;
          var subContent = $(this).next();
          if ($(this).hasClass('active')) {
            $(this).removeClass('active');
            subContent.slideUp().removeClass('active');
          } else {
             if(windowsize<480) {
              $('#LeftNavigation .accordionBar').removeClass('active');
              $('.accordionCont').removeClass('active').slideUp();
            }
            $(this).addClass('active');
            subContent.slideDown().addClass('active');
          }
        });
    
        aTitle.data('link',  function() { return $(this).attr('href'); }).attr('href', 'javascript:void(0)');
      } else {
        aTitle.each(function(i) {
          if($(this).data('link')) {
            $(this).attr('href',  function() { return $(this).data('link'); });
          }
        });
      }
    },
    showProfileMenu : function() {
      var dropdow_menu = $('#UIUserPlatformToolBarPortlet .dropdown-menu'),
          avatar = $('#NavigationPortlet .uiUserToolBarPortlet .dropdown-toggle').clone(),
          help_button = $('#NavigationPortlet .uiHelpPLFToolbarPortlet .dropdown-toggle').clone().attr('class','help-link');

      if ( $('.action_top').length == 0 ) {
        // dropdow_menu.prepend(avatar);
        dropdow_menu.prepend($('<li class="clearfix avatar-help-action mobile-visible"></li><li class="divider top mobile-visible">&nbsp;</li>'));
        $('#NavigationPortlet .avatar-help-action').append($('<div class="help-link-box"></div>')).append(avatar).find('.help-link-box').append(help_button);
        if ($('#UISetupPlatformToolBarPortlet .uiIconPLF24x24Setup').length != 0) {
          dropdow_menu.prepend($('<li class="clearfix action_top mobile-visible"><span class="action-addon"> <span class="admin-setup"><i class="uiIconPLF24x24Setup"></i></span></span></li>'));
        }
        if (tabManagerApp.container.find('.uiNotifChatIcon').length != 0) {
          $('.action_top').prepend('<span class="action-addon"><span class="uiNotifChatIcon chat-button"><span id="chat-notification"></span></span></span>');
        }
         // show dropdown menu of administration menu
        $('.admin-setup').on('click', function(){
          $('.uiSetupPlatformToolBarPortlet .dropdown-toggle').trigger('click');
          return false;
        });
        // show dropdown menu of chat menu
        $('.chat-button').on('click',function(){
          $('.chatStatusPortlet  .dropdown-toggle').trigger('click');
          return false;
        });
      }
    },







    searchOnTopNavivation : function() {
     // var _w = $(window).width();
      var bar = $('.UIToolbarContainer .ToolbarContainer');    
      var toolBar = $("#ToolBarSearch");
      var bar_input = toolBar.find("input[type='text']");
      $('#RightBody').prepend('<div class="uiMasklayer"></div>');
      //
      if(windowsize < 1025 && toolBar.find('span.action_close').length == 0) {
        bar.addClass('active');
       // bar.parents('#UIToolbarContainer').addClass('active_search');
        toolBar.append('<span class="action_close"><i class="uiIconClose uiIconWhite"></i></span>');
      }
      
      bar_input.off('blur');
      if(windowsize < 1025) {
        bar_input.blur(function(){
          var bar_input = $(this);
          var T = setTimeout(function() {
            bar_input.hide().removeClass('loadding');
            bar.removeClass('active');
           // bar.parents('#UIToolbarContainer').removeClass('active_search');
            $('#ToolBarSearch .action_close').remove();
            $('#ToolBarSearch .uiQuickSearchResult').hide();
            if(windowsize < 768) {
              $('#RightBody > .uiMasklayer').remove();  
            }
            clearTimeout(T);
          }, 200);
        });
        //
        if(windowsize < 768) {
          bar_input.css('width', windowsize - 50);
          $('.uiQuickSearchResult').css('width', windowsize - 50);
          //
          $('.uiMasklayer,.action_close').click(function(){
            $("#ToolBarSearch input[type='text']").trigger('blur').removeClass('loadding');
          });
        }
      }
    },
    setHeightMenu : function(){
      var dropdow_toggle=$('.UIToolbarContainer .dropdown-toggle, .UIToolbarContainer .dropdown-menu');
      //var _w = $(window).width();
      if (windowsize < 768 ) {
        var dropdowWidth =  dropdow_toggle.next().height();
        
        var max_height= Math.max($(window).height(), dropdowWidth) - 70 ;

        dropdow_toggle.next().css({
           'max-height' : max_height
        }).addClass('overflow-y');
      }
    }
  };
  //OnLoad
  $(document).ready(function(event) {
    tabManagerApp.init();

     // add event touch on mobile

      $('body').on('swipe', function (event) {
          // if(event.direction === 'right') { // or right, down, left
          //   if($(this).hasClass('open-right-bar')) {
          //     tabManagerApp.hideRightPanel();
          //   } else if ($(this).hasClass('hidden-right-bar')) {
          //     tabManagerApp.showLeftPanel();
          //   }
          // }
         if(event.direction === 'left') { // or right, down, left
           // if($(this).hasClass('hidden-right-bar') && $(this).hasClass('hidden-left-bar')) {
           //   tabManagerApp.showRightPanel();
           // }

           if($(this).hasClass('open-left-bar') ) {
             tabManagerApp.hideLeftPanel();
           }
         }
      });

      //end event touch on mobile

  });
  //OnResize
  $(window).resize(function(event) {
    windowsize = $(window).width();
    
    setTimeout(function() {
      if($('#ToolBarSearch').find('.action_close').length > 0) {
        tabManagerApp.searchOnTopNavivation();
      }
      tabManagerApp.setHeightMenu();

    $('#OfficeRight').css('height',$('.RightBodyTDContainer ').height());
    }, 50);

  });

  function Responsive() {};

  Responsive.prototype.drawNavigation = function(){
    tabManagerApp.leftNavAccordion();
    $('#LeftNavigation .accordionBar').last().click();
  }
  eXo.ecm.Responsive = new Responsive();
  return {
    Responsive : eXo.ecm.Responsive
  };

})($);
