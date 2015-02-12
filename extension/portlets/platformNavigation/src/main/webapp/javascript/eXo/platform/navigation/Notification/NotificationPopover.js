(function($, webNotif, cCometD){
  var NotificationPopover = {
      portletId : 'UINotificationPopoverToolbarPortlet',
      maxItem : 8,
      portlet : null,
      popupItem : null,
      popoverServeResourceLink : null,
      clusterUpdateCachedLink : null,
      markReadLink : '',
      removePopoverLink : '',
      resetNumberOnBadgeLink : '',
      showViewAll : '',
      isLive : false,
      liveTimeMenu : 30000, // milliseconds
      initCometd : function(eXoUser, eXoToken, contextName) {
        var me = NotificationPopover;
        if(!me.Cometd) me.Cometd = cCometD;
        var loc = window.location;
        me.Cometd.configure({
            url: loc.protocol + '//' + loc.hostname + (loc.port ? ':' + loc.port : '')  + '/' + contextName + '/cometd',
            'exoId': eXoUser, 'exoToken': eXoToken
        });
    
        if (me.currentUser !== eXoUser || me.currentUser === '') {
          me.currentUser = eXoUser;
          me.Cometd.subscribe('/eXo/Application/web/NotificationMessage', null, function(eventObj) {
            var message = JSON.parse(eventObj.data);
            webNotif.appendMessage(message);
          });
        }//end user
      },//end method
      init : function() {
        //
        var me = NotificationPopover;
        me.portlet = $('#' + me.portletId);
        me.markReadLink = me.portlet.find('#MarkRead').text();
        me.removePopoverLink = me.portlet.find('#RemovePopover').text();
        me.resetNumberOnBadgeLink = me.portlet.find('#ResetNumberOnBadge').text();
        me.clusterUpdateCachedLink = me.portlet.find('div#ClusterUpdateCachedLink:first').text();
        me.popoverServeResourceLink = me.portlet.find('div#PopoverServeResourceLink:first').text();
        me.uiDropdownWithIcon = me.portlet.find('div.uiDropdownWithIcon:first');
        me.viewAllBtn = me.uiDropdownWithIcon.find('li.actionLink');
        //
        me.viewAllBtn.hide();
        me.popupItem = me.portlet.find('ul.displayItems:first');
        me.popupItem.find('li').each(function(i) {
          me.applyAction($(this));
        });
        //
        me.maxItem = parseInt(me.portlet.find('#maxItemsInPopover').text());
        var badgeElm = me.portlet.find('span.badgeNotification:first');
        me.badgeElm = badgeElm;
        if (parseInt(badgeElm.text()) > 0) {
          badgeElm.show();
        }
        if(me.popupItem.find('li.unread').length > 0) {
          me.portlet.find('.actionMark:first').addClass('markAll');
        }
        // markAllRead
        me.portlet.find('.actionMark:first').find('a').click(function (evt) {
          evt.stopPropagation();
          webNotif.markAllRead();
        });
        //
        me.portlet.find('.dropdown-toggle:first').on('click', function() {
          var bagdeNumber = parseInt(me.badgeElm.text());
          //
          if (me.uiDropdownWithIcon.hasClass('open') == false && me.isLive == false) {
            // show/hide elements
            me.popupItem.html('');
            me.viewAllBtn.hide();
            me.portlet.find('.actionMark:first').removeClass('markAll');
            me.portlet.find('li.loadingIndicator:first').show();
            // ajax get data items
            me.isLive = true;
            webNotif.ajaxRequest(me.popoverServeResourceLink, me.renderMenu);
          }
          // call action clear badge
          if (bagdeNumber > 0) {
            me.badgeElm.text('0').hide();
            webNotif.ajaxRequest(me.resetNumberOnBadgeLink + 'reset');
          }
        });
        $(document).ready(function() {
          if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
            me.portlet.addClass('mobile-mode');
          }
        });
      },
      renderMenu : function(data) {
        var me = NotificationPopover;
        var notifications = data.notifications;
        me.portlet.find('li.loadingIndicator:first').hide();
        if (notifications && notifications.length > 0) {
          me.popupItem.append(notifications);
          me.viewAllBtn.show();
          me.popupItem.find('li').each(function(i) {
            me.applyAction($(this));
          });
        } else {
          me.portlet.find('.no-items:first').show();
        }
        // show/hide ViewAll page
        if (data.showViewAll == false) {
          me.portlet.find('.actionLink:first').hide();
        } else {
          me.portlet.find('.actionLink:first').show();
        }
        if(me.popupItem.find('li.unread').length > 0) {
          me.portlet.find('.actionMark:first').addClass('markAll');
        }
        //
        me.startMenuLifecycle();
      },
      startMenuLifecycle : function() {
        var me = NotificationPopover;
        if (me.T) {
          window.clearTimeout(me.T);
        }
        me.isLive = true;
        me.T = window.setTimeout(me.endMenuLifecycle, me.liveTimeMenu);
      },
      endMenuLifecycle : function() {
        NotificationPopover.isLive = false;
      },
      appendMessage : function(message) {
        var newItem = $($('<ul></ul>').html(message.body).html());
        var id = newItem.data('id');
        var moveTop = message.moveTop;
        var me = NotificationPopover;
        //
        var existItem = me.popupItem.find('li[data-id=' + id + ']');
        var isExisting = existItem.length > 0;
        if (isExisting) {
          //this process only mentions case like or comment, 
          //the content must be updated and NotificationID still kept
          if (moveTop) {
            existItem.remove();
          } else {
            existItem.hide();
            existItem.replaceWith(newItem);
            webNotif.showElm(me.applyAction(newItem));
          }
        } else if (me.popupItem.find('li').length === me.maxItem){
          me.popupItem.find('li:last').remove();
        } 
        if (moveTop) {
          me.popupItem.prepend(me.applyAction(newItem).hide());
          webNotif.showElm(newItem);
        }
        //
        me.badgeElm.text(message.numberOnBadge);
        if(parseInt(message.numberOnBadge) > 0) {
          me.badgeElm.show();
        } else {
          me.badgeElm.hide();
        }
        //
        me.portlet.find('.actionMark:first').addClass('markAll');
        me.portlet.find('.no-items:first').hide();
        me.portlet.find('.actionLink:first').show();
        //work-around in case of clustering
        webNotif.ajaxRequest(me.clusterUpdateCachedLink + '&notifId=' + id, function(data) {
      	  if(data && data.badge > 0) {
      	    me.badgeElm.text(data.badge).show();
      	  }
        });
      },
      applyAction : function(item) {
        item.find('.contentSmall:first').on('click', function(evt) {
          evt.stopPropagation();
          // mark read
          webNotif.markItemRead($(this).parents('li:first'));
          //
          webNotif.openURL($(this).data('link'));
        });
        //
        item.find('.remove-item').off('click')
            .on('click', function(evt) {
                evt.stopPropagation(); 
                //1.call ajax to remove this notification, and do something in commons side
                //2.remove this element on UI
                var elm = $(this).parents('li:first');
                NotificationPopover.removeItem(elm)
                webNotif.removeElm(elm);
             });
        item.find('.action-item').off('click')
            .on('click', function(evt) { evt.stopPropagation(); webNotif.doAction($(this)); });
        item.find('.cancel-item').off('click')
            .on('click', function(evt) { 
              evt.stopPropagation();
              var id = $(this).parents('li:first').data('id');
              webNotif.doCancelAction(id, $(this).data('rest')); 
            });
        //
        return item;
      },
      doAction : function(elm) {
          //1. call rest on social side: for example accept/refuse relationship
          //2. redirect to the uri, for example: view activity detail
          //var id = elm.parents('li:first').data('id');
        webNotif.ajaxRequest($(elm).data('rest')).openURL($(elm).data('link'));
      },
      doCancelAction : function(object) {
        var me = NotificationPopover;
        if (!object.data.showViewAll) {
          me.portlet.find('.actionLink:first').hide();
          me.showViewAll = "false";
        }
        me.removeItem(me.portlet.find('li[data-id=' + object.id + ']'));
      },
      markAllRead : function() {
        NotificationPopover.portlet.find('ul.displayItems:first').find('li.unread').removeClass('unread');
        NotificationPopover.badgeElm.text("0").hide();
        NotificationPopover.portlet.find('.actionMark:first').removeClass('markAll');
      },
      markItemRead : function(item) {
        var action = NotificationPopover.markReadLink + item.data('id');
        window.ajaxGet(action);
      },
      removeItem : function(item) {
        var me = NotificationPopover;
        var action = me.removePopoverLink + item.data('id');
        window.ajaxGet(action);
        //
        if(me.popupItem.find('li').length == 1) {
          webNotif.showElm(me.portlet.find('.no-items:first'));
          me.portlet.find('.actionMark:first').removeClass('markAll');
        } else if(me.popupItem.find('li.unread').length == 1 && item.hasClass('unread')) {
          me.portlet.find('.actionMark:first').removeClass('markAll');
        }
        webNotif.removeElm(item);
        return this;
      }
  };
  //
  webNotif.register(NotificationPopover);
  return NotificationPopover;
})(jQuery, webNotifications, cCometD);