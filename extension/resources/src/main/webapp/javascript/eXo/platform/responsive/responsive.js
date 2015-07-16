(function($) {
	// add toggle right button
	$(".OfficeMiddleTDContainer").append("<a href='javascript:void(0)' class='visible-tablet  toggle-right-bar'><i class='uiIconArrowLeft'></i><i class='uiIconArrowRight' ></i></a>");
	
	var taApp = {}; 

	// Toggle left bar
	$('.toggle-left-bar').on('click',function(){
		
		if($('body').hasClass('open-left-bar')) {
			taApp.hideLeftPanel();	
		}
		else {
			taApp.showLeftPanel();	
		}
	})


	// Toggle right  bar
	$('.toggle-right-bar').on('click',function(){
		if($('body').hasClass('hidden-right-bar')) {
			taApp.hideRightPanel();		
		}
		else {
			taApp.showRightPanel();
		}
	})



	// show left navigation	
  	taApp.showLeftPanel = function() {
    	var leftNavi= $('.LeftNavigationTDContainer');
    	$('body').addClass('open-left-bar');    
    	$('.mask-layer-right').remove();
    	$('#RightBody').before('<div class="mask-layer-right"></div>');
    	$('.RightBodyTDContainer').css('height',$('.LeftNavigationTDContainer ').height());
    	$('body,html').css('overflow-y',"hidden");
    	$('.mask-layer-right').on('click',function(){
			taApp.hideLeftPanel();	
		})

    	$(leftNavi).addClass('expanded');			
    };

    // hidden left navigation
 	taApp.hideLeftPanel = function() {
    	var leftNavi= $('.LeftNavigationTDContainer');
    	$('body').removeClass('open-left-bar');	
    	$('.mask-layer-right').remove();
    	$(leftNavi).removeClass('expanded');		
    };
    // Show right  navigation	
    taApp.showRightPanel = function() {
    	var RightNavi= $('.OfficeRightTDContainer');
    	$('body,html').css('overflow-y',"visible");
    	$('body').addClass('hidden-right-bar');    	
    	$(RightNavi).addClass('expanded');			
    };
    // hidden Right navigation
 	taApp.hideRightPanel = function() {
    	var RightNavi= $('.OfficeRightTDContainer');
    	$('body').removeClass('hidden-right-bar');	
    	$(RightNavi).removeClass('expanded');		
    };


    // display sub menu on mobile

     $('.dropdown-submenu > a').on('click', function(evt) {     	  
		 var _w = Math.max($(window).width());
		 
		 if ( _w < 1025 ) {
		 	 evt.stopPropagation();
     	     evt.preventDefault();
		 	var parent = $(this).parent().addClass('current').parent('ul');
		 	parent.find('>li').hide();	
		 	parent.append($('<li class="back-item"><a href="javascript:void(0)" ><i class="uiIconArrowLeft" style=" margin-right: 2px;"></i>Back</a></li>')
		 		.on('click', function(evt) {
		 		  	evt.stopPropagation();
			 		parent.find('.current').removeClass('current').find('ul.dropdown-menu:first')
			 		.append(parent.find('.current-child').removeClass('current-child'));
			 		$(this).remove();
			 		parent.find('>li').show();
			}));
		 	var sub = $(this).parent().find('.dropdown-menu:first > li').addClass('current-child');
		 	parent.append(sub);
		 }
	});


	// function accordion for left navigation
    
 taApp.left_nav_accordion=function(){
		var companyNav = $('.uiCompanyNavigationPortlet .title.accordionBar').addClass('active');	
		$('.title.accordionBar').prepend('<i class="uiIconArrowRight pull-right"></i>');	
		$('.uiCompanyNavigationPortlet .accordionCont').addClass('active').show();
		$('#LeftNavigation .accordionBar').click(function(){	

			var subContent = $(this).next();
			if (subContent.is(':visible')) {
				return false;
			}
			else {
				 $('.accordionBar').removeClass('active');
			 	$(this).addClass('active')
				$('.accordionCont').removeClass('active').hide();
				$(this).next().addClass('active').slideDown();
			}
			return false;
		});
	};
	// call accordion function
	taApp.left_nav_accordion();


	// function showProfileMenu
		
	taApp.showProfileMenu = function(){
		var _w = Math.max($(window).width());
		var dropdow_menu = $('#UIUserPlatformToolBarPortlet .dropdown-menu');
		var avatar = $('.uiUserToolBarPortlet  .dropdown-toggle').clone();
		var help_button = $('.uiHelpPLFToolbarPortlet   .dropdown-toggle').clone().attr('class','help-link');
		
		 if ( _w < 481 &&  $('.action_top').length == 0 ) {
		 	dropdow_menu.prepend(avatar);
		 	dropdow_menu.prepend('<li class="divider top mobile-visible">&nbsp;</li>');
		 	dropdow_menu.prepend('<li class="clearfix avatar-help-action mobile-visible"></li> ');		 	
		 	$('.avatar-help-action').append('<div class="help-link-box"></div>');
		 	$('.help-link-box').append(help_button);
		 	$('.avatar-help-action').append(avatar);
		 	dropdow_menu.prepend('<li class="clearfix action_top mobile-visible"><span class="action-addon"> <span class="admin-setup"><i class="uiIconPLF24x24Setup"></i></span></span></li>');
		 	if ($('.uiNotifChatIcon').length != 0) {
		 		$('.action_top').prepend('<span class="action-addon"><span class="uiNotifChatIcon chat-button"><span id="chat-notification"></span></span></span>');
		 	}
		 	
			 // show dropdown menu of administration menu
			 $('.admin-setup').on('click',function(){
			 	$('.uiSetupPlatformToolBarPortlet .dropdown-toggle').click();
			 	return false;
			 });
			  // show dropdown menu of chat menu
			  $('.chat-button').on('click',function(){
			 	$('.chatStatusPortlet  .dropdown-toggle').click();
			 	return false;
			 });
		}
		
	};

	$('#UIUserPlatformToolBarPortlet').on('click',  function(event) {
		event.preventDefault();
		taApp.showProfileMenu();
	});
	
	// Search action on top navigation
	taApp.search_on_top_nav = function(){
		var _w = $(window).width();
		if (_w < 481) {
			$("#ToolBarSearch").append("<span><i class='uiIconClose uiIconWhite'></i></span>");
			$('.UIToolbarContainer .ToolbarContainer').addClass('active');

			$("#ToolBarSearch input[name='adminkeyword']").focusout(function(){
				$('.UIToolbarContainer .ToolbarContainer').removeClass('active');
				$('#ToolBarSearch .uiIconClose').remove();
			});
		}
	};
	$('#ToolBarSearch > a').click(function(){
			taApp.search_on_top_nav();
	 });	
		taApp.setheight = function($height1,$height2){
			if($height1 > $height2 ) {
				return $height1;
			}
			else{
				return $height2;
			}
		}
/*	$(window).resize(function(event) {
		taApp.showProfileMenu();
	});*/

	// adapt dropdown menu with screen size if height too big
	$('.UIToolbarContainer .dropdown-toggle').on('click',  function(event) {	
		
		var _w = $(window).width();
		 if ( _w < 481 ) {		
			var _dropdow =  $(this).next().height(); 	
			var _h = $(window).height();	
		 	var max_height= taApp.setheight(_h,_dropdow) - 70 ;

		 	$(this).next().css({
			   'max-height' : max_height
			}).addClass('overflow-y');
		 }

		
		
	});

	taApp.setHeightMenu = function(){
		var dropdow_toggle=$('.UIToolbarContainer .dropdown-toggle');
		 $(window).resize(function(event) {
		 	var _w = $(window).width();
			 	if ( _w < 481 ) {		
				var _dropdow =  dropdow_toggle.next().height(); 	
				var _h = $(window).height();	
			 	var max_height= taApp.setheight(_h,_dropdow) - 70 ;

			 	dropdow_toggle.next().css({
				   'max-height' : max_height
				}).addClass('overflow-y');
			 }
		 });
	}
	taApp.setHeightMenu();
})($);

