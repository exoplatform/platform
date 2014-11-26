(function($){
  var NotificationPopover = {
      popupId : 'NotificationPopup',
      maxItem : 13,
      portlet : null,
      popupItem : null,
      markReadLink : '',
      removeLink : '',
      init : function() {
        //
        var socketUrl = 'ws://' + location.hostname + ':8181/channels/notification-web/' + window.eXo.env.portal.userName;
        var socket = new WebSocket(socketUrl);
        socket.onmessage = function(evt) {
          var obj = JSON.parse(evt.data);
          NotificationPopover.appendMessage(obj.message);
        }
        socket.onopen = function(evt) {
          if (socket.readyState == WebSocket.OPEN) {
            socket.send('{"action": "subscribe", "identifier" : "notification-web"}');
          } else {
            window.console.log("The socket is not open.");
          }
        }
        socket.onclose = function(evt) {
          window.console.log("Web Socket closed.");
        }
        //
        NotificationPopover.portlet = $('#' + NotificationPopover.popupId).parents('.uiNotificationPopoverToolbarPortlet:first');
        NotificationPopover.markReadLink = NotificationPopover.portlet.find('#MarkRead').text();
        NotificationPopover.removeLink = NotificationPopover.portlet.find('#Remove').text();
        //
        NotificationPopover.popupItem = NotificationPopover.portlet.find('ul.displayItems:first');
        NotificationPopover.popupItem.find('li').each(function(i) {
          NotificationPopover.applyAction($(this));
        });
        //
        var current = NotificationPopover.popupItem.find('li').length;
        if(current > 0) {
          NotificationPopover.portlet.find('span.badgeDefault:first').text(current).show();
        }
        // markAllRead
        NotificationPopover.portlet.find('.actionMark:first').find('a').click(NotificationPopover.markAllRead);
        //
        NotificationPopover.portlet.find('.dropdown-toggle:first').on('click', function() { console.log('Show menu')});
      },
      appendMessage : function(message) {
        var newItem = NotificationPopover.applyAction($($('<ul></ul>').html(message).html()));
        
        //
        var target = $('<ul></ul>').append(NotificationPopover.popupItem.find('li'));
        //
        NotificationPopover.popupItem.append(newItem);
        //
        target.find('li').each(function(i){
          if((i + 1) < NotificationPopover.maxItem) {
            NotificationPopover.popupItem.append($(this));
          }
        });
        target.remove();
        //
        var badge = NotificationPopover.portlet.find('span.badgeDefault:first');
        var current = parseInt(badge.text());
        if(current > NotificationPopover.maxItem) {
          badge.text(NotificationPopover.maxItem + "+").show();
        } else {
          badge.text((current + 1) + "").show();
        }
        //
        NotificationPopover.portlet.find('.actionMark:first').show();
      },
      applyAction : function(item) {
        item.find('.contentSmall:first').on('click', function(evt) {
          evt.stopPropagation();
          // mark read
          NotificationPopover.markItemRead($(this).parents('li:first'));
          //
          var link = $(this).data('link');
          if(link  && link.length > 0) {
            window.location.href = link;
          }
        }).find('a').click(function(evt) {
          evt.stopPropagation();
          var href = $(this).attr('href');
          if(href && href.indexOf('javascript') !== 0) {
            window.location.href= href;
          }
        });
        //
        item.find('.remove-item').off('click').on('click', function(evt){
          evt.stopPropagation();
          //
          var elm = $(this);
          NotificationPopover.removeItem(elm.parents('li:first'));
          //
          var rest = elm.data('rest');
          if(rest  && rest.length > 0) {
            $.ajax(rest);
          }
          //
          var link = elm.data('link');
          if(link  && link.length > 0) {
            window.location.href = link;
          }
          //
          elm.parents('li:first').remove();
          
        });
        //
        return item;
      },
      downBadge : function() {
        //
        var badge = NotificationPopover.portlet.find('span.badgeDefault:first');
        var current = parseInt(badge.text().trim());
        if(current <= 1) {
          NotificationPopover.markAllRead();
        } else {
          badge.text((current - 1) + "");
        }
      },
      markAllRead : function(evt) {
        NotificationPopover.portlet.find('ul.displayItems:first').find('li.unread').removeClass('unread');
        NotificationPopover.portlet.find('span.badgeDefault:first').text('0').hide();
        NotificationPopover.portlet.find('.actionMark:first').hide();
      },
      markItemRead : function(item) {
        var action = NotificationPopover.markReadLink + item.data('id');
        window.ajaxGet(action);
        //
        NotificationPopover.downBadge();
      },
      removeItem : function(item) {
        var action = NotificationPopover.removeLink + item.data('id');
        window.ajaxGet(action);
        //
        NotificationPopover.downBadge();
      }
  };
  
  NotificationPopover.init();
  return NotificationPopover;
})(jQuery);