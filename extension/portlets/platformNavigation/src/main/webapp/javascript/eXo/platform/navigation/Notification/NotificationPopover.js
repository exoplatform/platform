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
            //logLevel: 'debug'
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
        me.markReadLink = me.portlet.find('#MarkRead').data('link');
        me.removePopoverLink = me.portlet.find('#RemovePopover').data('link');
        me.resetNumberOnBadgeLink = me.portlet.find('#ResetNumberOnBadge').data('link');
        me.clusterUpdateCachedLink = me.portlet.find('div#ClusterUpdateCachedLink:first').data('link');
        me.popoverServeResourceLink = me.portlet.find('div#PopoverServeResourceLink:first').data('link');
        me.markAllReadLink = me.portlet.find('div#MarkAllAsReadLink:first').data('link');
        me.uiDropdownWithIcon = me.portlet.find('div.uiDropdownWithIcon:first');
        me.viewAllBtn = me.uiDropdownWithIcon.find('li.actionLink');
        //
        me.popupItem = me.portlet.find('ul.displayItems:first');
        me.popupItem.find('li').each(function(i) {
          me.applyAction($(this));
        });
        //
        me.maxItem = parseInt(me.portlet.find('#maxItemsInPopover').text());
        var badgeElm = me.portlet.find('span.badgeNotification:first');
        me.badgeElm = badgeElm;
        if (parseInt(badgeElm.text()) > 0) {
          eXo.core.Browser.setTitlePrefix('(' + badgeElm.text() + ') ');
          badgeElm.show();
        }
        if(me.popupItem.find('li.unread').length > 0) {
          me.portlet.find('.actionMark:first').addClass('enabled');
        }
        // markAllRead
        me.portlet.find("#markAllReadLink").click(function(evt) {
          evt.stopPropagation();
          webNotif.ajaxReq(me.markAllReadLink, function() {
            webNotif.markAllRead();
          });
        });
        //
        me.portlet.find('.dropdown-toggle:first').off('click').on('click', function() {
          var badgeNumber = parseInt(me.badgeElm.text());
          //
          if (me.uiDropdownWithIcon.hasClass('open') == false && me.isLive == false) {
            // show/hide elements
            me.popupItem.html('');
            me.viewAllBtn.hide();
            me.portlet.find('.actionMark:first').removeClass('enabled');
            me.portlet.find('li.loadingIndicator:first').show();
            // ajax get data items
            me.isLive = true;
            webNotif.ajaxReq(me.popoverServeResourceLink, me.renderMenu);
          }
          // call action clear badge
          if (badgeNumber > 0) {
            me.badgeElm.text('0').hide();
            webNotif.ajaxReq(me.resetNumberOnBadgeLink + 'reset');
            eXo.core.Browser.setTitlePrefix('');
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
          var $notifications = $(notifications);
          me.inlineImageLabel = data.inlineImageLabel;
          $notifications.find(".contentSmall .content img").replaceWith("<i>[" + data.inlineImageLabel + "]</i>");
          notifications = $('<div>').append($notifications.clone()).html();
          me.popupItem.append(notifications);
          me.viewAllBtn.show();
          me.popupItem.find('li').each(function(i) {
            me.applyAction($(this));
          });

          // Long message of text will be truncated by CSS
          $.each(me.portlet.find('.displayItems').find('.content'), function(i, item) {
            if ($(item).html().trim().length > 120 && $(item).children(".confirm").length == 0) {
              $(item).css('height','40px');
            }
          });
        } else {
          me.portlet.find('.no-items:first').show();
          me.portlet.find('.actionMark:first').hide();
        }
        // show/hide ViewAll page
        if (data.showViewAll == false) {
          me.portlet.find('.actionLink:first').hide();
        } else {
          me.portlet.find('.actionLink:first').show();
        }
        if(me.popupItem.find('li.unread').length > 0) {
          me.portlet.find('.actionMark:first').addClass('enabled');
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
        var $message = $(message.body);
        $message.find(".contentSmall .content img").replaceWith("<i>[" + NotificationPopover.inlineImageLabel + "]</i>");
        message.body = $('<div>').append($message.clone()).html();

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
          eXo.core.Browser.setTitlePrefix('(' + message.numberOnBadge + ') ');
          me.badgeElm.show();
        } else {
          me.badgeElm.hide();
        }
        //
        me.portlet.find('.actionMark:first').show();
        me.portlet.find('.actionMark:first').addClass('enabled');
        me.portlet.find('.no-items:first').hide();
        me.portlet.find('.actionLink:first').show();
        //work-around in case of clustering
        webNotif.ajaxReq(me.clusterUpdateCachedLink + '&notifId=' + id, function(data) {
          if(data && data.badge > 0) {
            me.badgeElm.text(data.badge).show();
          }
        });
      },
      applyAction : function(item) {
        var me = NotificationPopover;
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
              webNotif.doCancelAction(id, me.appendCSRFToken($(this).data('rest')));
            });
        //
        return item;
      },
      doAction : function(elm) {
        var me = NotificationPopover;
          //1. call rest on social side: for example accept/refuse relationship
          //2. redirect to the uri, for example: view activity detail
          //var id = elm.parents('li:first').data('id');
          //3. trigger a custom event to update other applications concerned by this change
        webNotif.ajaxReq(me.appendCSRFToken($(elm).data('rest')),
            function() {
              $(document).trigger("exo-invitation-updated");
            }
        ).openURL($(elm).data('link'));
      },
      doCancelAction : function(object) {
        var me = NotificationPopover;
        if (!object.data.showViewAll) {
          me.portlet.find('.actionLink:first').hide();
          me.showViewAll = "false";
        }
        me.removeItem(me.portlet.find('li[data-id=' + object.id + ']'));
        $(document).trigger("exo-invitation-updated");
      },
      appendCSRFToken : function(url) {
        url.indexOf('?') >= 0 ? url += '&' : url += '?';
        url += 'portal:csrf=' + eXo.env.portal.csrfToken;
        return url;
      },
      markAllRead : function() {
        NotificationPopover.portlet.find('ul.displayItems:first').find('li.unread').removeClass('unread');
        NotificationPopover.badgeElm.text("0").hide();
        NotificationPopover.portlet.find('.actionMark:first').removeClass('enabled');
      },
      markItemRead : function(item) {
        var action = NotificationPopover.markReadLink + item.data('id');
        window.ajaxGet(action);
      },
      removeItem : function(item) {
        var me = NotificationPopover;
        if(item.length) {
          var action = me.removePopoverLink + item.data('id');
          window.ajaxGet(action);
        }
        //
        if(me.popupItem.find('li').length == 1) {
          webNotif.showElm(me.portlet.find('.no-items:first'));
          me.portlet.find('.actionMark:first').removeClass('enabled');
          me.portlet.find('.actionMark:first').hide();
        } else if(me.popupItem.find('li.unread').length == 1 && item.hasClass('unread')) {
          me.portlet.find('.actionMark:first').removeClass('enabled');
        }
        if(item.length) {
          webNotif.removeElm(item);
        }
        return this;
      }

  };
  //
  NotificationPopover.init();
  webNotif.register(NotificationPopover);

  return NotificationPopover;
})(jQuery, webNotifications, cCometD);
