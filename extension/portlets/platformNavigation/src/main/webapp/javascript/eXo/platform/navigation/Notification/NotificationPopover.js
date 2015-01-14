(function($, cCometD){
  var NotificationPopover = {
      popupId : 'NotificationPopup',
      maxItem : 8,
      nbDisplay : 0,
      portlet : null,
      popupItem : null,
      markReadLink : '',
      removePopoverLink : '',
      takeEventLink : '',
      resetNumberOnBadgeLink : '',
      initCometd : function(eXoUser, eXoToken, contextName) {
      if(!NotificationPopover.Cometd) NotificationPopover.Cometd = cCometD;
        var loc = window.location;
        NotificationPopover.Cometd.configure({
            url: loc.protocol + '//' + loc.hostname + (loc.port ? ':' + loc.port : '')  + '/' + contextName + '/cometd',
            'exoId': eXoUser, 'exoToken': eXoToken
        });
    
        if (NotificationPopover.currentUser !== eXoUser || NotificationPopover.currentUser === '') {
          NotificationPopover.currentUser = eXoUser;
          NotificationPopover.Cometd.subscribe('/eXo/Application/web/NotificationMessage', null, function(eventObj) {
            var message = JSON.parse(eventObj.data);
            NotificationPopover.appendMessage(message);
          });
        }//end user
      },//end method
      init : function() {
        //
        NotificationPopover.portlet = $('#' + NotificationPopover.popupId).parents('.uiNotificationPopoverToolbarPortlet:first');
        NotificationPopover.markReadLink = NotificationPopover.portlet.find('#MarkRead').text();
        NotificationPopover.removePopoverLink = NotificationPopover.portlet.find('#RemovePopover').text();
        NotificationPopover.takeEventLink = NotificationPopover.portlet.find('#TakeEvent').text();
        NotificationPopover.resetNumberOnBadgeLink = NotificationPopover.portlet.find('#ResetNumberOnBadge').text();
        //
        NotificationPopover.popupItem = NotificationPopover.portlet.find('ul.displayItems:first');
        NotificationPopover.popupItem.find('li').each(function(i) {
          NotificationPopover.applyAction($(this));
        });
        //
        NotificationPopover.maxItem = parseInt(NotificationPopover.portlet.find('#maxItemsInPopover').text());
        var badgeElm = NotificationPopover.portlet.find('span.badgeNotification:first');
        NotificationPopover.badgeElm = badgeElm;
        NotificationPopover.nbDisplay = parseInt(badgeElm.text());
        if (NotificationPopover.nbDisplay > 0) {
          if (NotificationPopover.nbDisplay > NotificationPopover.maxItem) {
            badgeElm.text(NotificationPopover.maxItem + "+");
          }
          badgeElm.show();
        }
        if(NotificationPopover.popupItem.find('li.unread').length > 0) {
          NotificationPopover.portlet.find('.actionMark:first').show();
        }
        // show/hide ViewAll page
        if(NotificationPopover.popupItem.find('li').length == 0) {
          NotificationPopover.portlet.find('.actionLink:first').hide();
        }
        
        // markAllRead
        NotificationPopover.portlet.find('.actionMark:first').find('a').click(function (evt) {
          evt.stopPropagation();
          NotificationPopover.markAllRead();
        });
        //
        NotificationPopover.portlet.find('.dropdown-toggle:first').on('click', function() { 
          NotificationPopover.badgeElm.text('0').hide();
          // call action clear badge
          if (NotificationPopover.nbDisplay > 0) {
            NotificationPopover.ajaxRequest(NotificationPopover.resetNumberOnBadgeLink + 'reset');
          }
          NotificationPopover.nbDisplay = 0;
        });
      },
      appendMessage : function(message) {
        var newItem = $($('<ul></ul>').html(message.body).html());
        var id = newItem.data('id');
        var moveTop = message.moveTop;
        //
        var existItem = NotificationPopover.popupItem.find('li[data-id=' + id + ']');
        var isExisting = existItem.length > 0;
        if (isExisting) {
          //this process only mentions case like or comment, 
          //the content must be updated and NotificationID still kept
          if (moveTop) {
            existItem.remove();
          } else {
            existItem.hide();
            existItem.replaceWith(newItem);
            NotificationPopover.showElm(NotificationPopover.applyAction(newItem));
          }
          
        } else if (NotificationPopover.popupItem.find('li').length === NotificationPopover.maxItem){
          NotificationPopover.popupItem.find('li:last').remove();
        } 
        
        if (moveTop) {
          NotificationPopover.popupItem.prepend(NotificationPopover.applyAction(newItem).hide());
          NotificationPopover.showElm(newItem);
          //
          var badgeElm = NotificationPopover.badgeElm;
          NotificationPopover.nbDisplay = parseInt(NotificationPopover.nbDisplay) + 1;
          if (NotificationPopover.nbDisplay > NotificationPopover.maxItem) {
            badgeElm.text(NotificationPopover.maxItem + "+");
          } else {
            badgeElm.text(NotificationPopover.nbDisplay);
          }
          badgeElm.show();
        }
        //
        NotificationPopover.portlet.find('.actionMark:first').show();
        NotificationPopover.portlet.find('.no-items:first').hide();
        NotificationPopover.portlet.find('.actionLink:first').show();
      },
      openURL : function (url) {
        if(url && url.length > 0) {
          NotificationPopover.T = setTimeout(function() {
            clearTimeout(NotificationPopover.T);
            window.open(url, "_self");
          }, 500);
        }
        return this;
      },
      ajaxRequest : function (url, callBack) {
        if(url && url.length > 0) {
          $.ajax(url).done(function(data) {
            if(callBack && typeof callBack === 'function') {
              callBack(data);
            }
          });
        }
        return this;
      },
      applyAction : function(item) {
        item.find('.contentSmall:first').on('click', function(evt) {
          evt.stopPropagation();
          // mark read
          NotificationPopover.markItemRead($(this).parents('li:first'));
          //
          NotificationPopover.openURL($(this).data('link'));
        });
        //
        item.find('.remove-item').off('click')
            .on('click', function(evt) {
                evt.stopPropagation(); 
                //1.call ajax to remove this notification, and do something in commons side
                //2.remove this element on UI
                NotificationPopover.removeItem($(this).parents('li:first'))
                                   .removeElm($(this).parents('li:first'));

             });
        item.find('.action-item').off('click')
            .on('click', function(evt) { evt.stopPropagation(); NotificationPopover.doAction($(this)); });
        item.find('.cancel-item').off('click')
            .on('click', function(evt) { evt.stopPropagation(); NotificationPopover.doCancelAction($(this)); });
        //
        return item;
      },
      doAction : function(elm) {
          //1. call rest on social side: for example accept/refuse relationship
          //2. do action to update the message and send back
          //3. redirect to the uri, for example: view activity detail
          //var id = elm.parents('li:first').data('id');
          NotificationPopover.ajaxRequest(elm.data('rest'))
                             .openURL(elm.data('link'));
      },
      doCancelAction : function(elm) {
        var id = elm.parents('li:first').data('id');
        NotificationPopover.ajaxRequest(elm.data('rest') + "/" + id)
                             .removeElm(elm.parents('li:first'));
      },
      removeElm : function(elm) {
        elm.css('overflow', 'hidden').animate({
          height : '0px'
        }, 300, function() {
          $(this).remove();
        });
        return this;
      },
      showElm : function(elm) {
        elm.css({'visibility':'hidden', 'overflow':'hidden'}).show();
        var h = elm.height();
        elm.css({'height' : '0px', 'visibility':'visible'}).animate({ 'height' : h + 'px' }, 300, function() {
          $(this).css({'height':'', 'overflow':'hidden'});
        });
        return elm;
      },
      markAllRead : function() {
        NotificationPopover.portlet.find('ul.displayItems:first').find('li.unread').removeClass('unread');
        NotificationPopover.badgeElm.text("0").hide();
        NotificationPopover.portlet.find('.actionMark:first').hide();
      },
      markItemRead : function(item) {
        var action = NotificationPopover.markReadLink + item.data('id');
        window.ajaxGet(action);
      },
      removeItem : function(item) {
        var action = NotificationPopover.removePopoverLink + item.data('id');
        window.ajaxGet(action);
        //
        if(NotificationPopover.popupItem.find('li').length == 1) {
          NotificationPopover.showElm(NotificationPopover.portlet.find('.no-items:first'));
        }
        return this;
      }
  };
  
  NotificationPopover.init();
  return NotificationPopover;
})(jQuery, cCometD);