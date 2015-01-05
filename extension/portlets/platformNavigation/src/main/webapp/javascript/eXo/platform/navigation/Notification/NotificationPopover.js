(function($){
  var NotificationPopover = {
      popupId : 'NotificationPopup',
      maxItem : 8,
      nbDisplay : 0,
      portlet : null,
      popupItem : null,
      markReadLink : '',
      removeLink : '',
      deleteLink : '',
      initCometd : function(eXoUser, eXoToken, contextName) {
      if(!NotificationPopover.Cometd) NotificationPopover.Cometd = $.cometd;
        var eXoProps = {'exoId': eXoUser, 'exoToken': eXoToken};
        var loc = window.location;
        NotificationPopover.Cometd.configure({
            url: loc.protocol + '//' + loc.hostname + (loc.port ? ':' + loc.port : '')  + '/' + contextName + '/cometd'
        });
    
        if (NotificationPopover.currentUser !== eXoUser || NotificationPopover.currentUser === '') {
          NotificationPopover.currentUser = eXoUser;
          if (NotificationPopover.Cometd.isDisconnected()) {
            NotificationPopover.Cometd.handshake(eXoProps, function(reply) {
              if (reply.successful) {
                NotificationPopover.Cometd.subscribe('/eXo/Application/web/NotificationMessage', null, function(eventObj) {
                  var message = JSON.parse(eventObj.data);
                  NotificationPopover.appendMessage(message);
                }, eXoProps);//end function
              }//end if successful
            });//end handshake
          }//end disconnected
        }//end user
      },//end method
      init : function() {
        //
        NotificationPopover.portlet = $('#' + NotificationPopover.popupId).parents('.uiNotificationPopoverToolbarPortlet:first');
        NotificationPopover.markReadLink = NotificationPopover.portlet.find('#MarkRead').text();
        NotificationPopover.removeLink = NotificationPopover.portlet.find('#Remove').text();
        NotificationPopover.deleteLink = NotificationPopover.portlet.find('#Delete').text();
        NotificationPopover.clearBadgeLink = NotificationPopover.portlet.find('#ClearBadge').text();
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
            NotificationPopover.ajaxRequest(NotificationPopover.clearBadgeLink + 'clear');
          }
          NotificationPopover.nbDisplay = 0;
        });
      },
      appendMessage : function(message) {
        var newItem = NotificationPopover.applyAction($($('<ul></ul>').html(message.body).html()));
        var id = newItem.data('id');
        //
        var target = $('<ul></ul>').append(NotificationPopover.popupItem.find('li'));
        var updateItem = target.find('li[data-id=' + id + ']');
        var isUpdate = (updateItem.length > 0);
        if (isUpdate) {
          updateItem.remove();
        }
        target.find('li').each(function(i){
          if((i + 1) < NotificationPopover.maxItem) {
            NotificationPopover.popupItem.append($(this));
          }
        });
        target.remove();
        //
        NotificationPopover.popupItem.prepend(newItem.hide());
        NotificationPopover.showElm(newItem);
        //
        var badgeElm = NotificationPopover.badgeElm;
        if (isUpdate) {
          NotificationPopover.nbDisplay = (message.badgeNumber == 0) ? 1 : message.badgeNumber;
        } else {
          NotificationPopover.nbDisplay = parseInt(NotificationPopover.nbDisplay) + 1;
        }
        if (NotificationPopover.nbDisplay > NotificationPopover.maxItem) {
          badgeElm.text(NotificationPopover.maxItem + "+");
        } else {
          badgeElm.text(NotificationPopover.nbDisplay);
        }
        badgeElm.show();
        //
        NotificationPopover.portlet.find('.actionMark:first').show();
        NotificationPopover.portlet.find('.no-items:first').hide();
      },
      openURL : function (url) {
        if(url && url.length > 0) {
          NotificationPopover.T = setTimeout(function() {
            clearTimeout(NotificationPopover.T);
            window.open(url, "_self");
          }, 500);
        }
      },
      ajaxRequest : function (url, callBack) {
        if(url && url.length > 0) {
          $.ajax(url).done(function(data) {
            if(callBack && typeof callBack === 'function') {
              callBack(data);
            }
          });
        }
      },
      applyAction : function(item) {
        item.find('.contentSmall:first').on('click', function(evt) {
          evt.stopPropagation();
          // mark read
          NotificationPopover.markItemRead($(this).parents('li:first'));
          //
          NotificationPopover.openURL($(this).data('link'));
        }).find('a').click(function(evt) {
          evt.stopPropagation();
          var href = $(this).attr('href');
          if(href && href.indexOf('javascript') !== 0) {
            NotificationPopover.openURL(href);
          }
        });
        //
        item.find('.remove-item').off('click')
            .on('click', NotificationPopover.removeAction);
        item.find('.delete-item').off('click')
            .on('click', NotificationPopover.removeAction);
        //
        return item;
      },
      removeAction : function(evt) {
        evt.stopPropagation();
        //
        var elm = $(this);
        var link = (elm.hasClass('delete-item')) ? NotificationPopover.deleteLink : NotificationPopover.removeLink;
        NotificationPopover.removeItem(elm.parents('li:first'), link);
        //
        NotificationPopover.ajaxRequest(elm.data('rest'));
        //
        NotificationPopover.removeElm(elm.parents('li:first'));
        //
        NotificationPopover.openURL(elm.data('link'));
      },
      removeElm : function(elm) {
        elm.css('overflow', 'hidden').animate({
          height : '0px'
        }, 300, function() {
          $(this).remove();
        });
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
        NotificationPopover.portlet.find('span.badgeDefault:first').text('0').hide();
        NotificationPopover.portlet.find('.actionMark:first').hide();
      },
      markItemRead : function(item) {
        var action = NotificationPopover.markReadLink + item.data('id');
        window.ajaxGet(action);
      },
      removeItem : function(item, link) {
        var action = link + item.data('id');
        window.ajaxGet(action);
        //
        if(NotificationPopover.popupItem.find('li').length == 1) {
          NotificationPopover.showElm(NotificationPopover.portlet.find('.no-items:first'));
        }
      }
  };
  
  NotificationPopover.init();
  return NotificationPopover;
})(jQuery);