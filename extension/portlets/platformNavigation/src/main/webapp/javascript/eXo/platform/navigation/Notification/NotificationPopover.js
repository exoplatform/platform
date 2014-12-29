(function(Cometd, $){
  var NotificationPopover = {
      popupId : 'NotificationPopup',
      maxItem : 8,
      nbUnreadNotif : 0,
      nbDisplay : 0,
      portlet : null,
      popupItem : null,
      markReadLink : '',
      removeLink : '',
      deleteLink : '',
      initCometd : function(eXoUser, eXoToken, contextName) {
        if (String(eXoToken) != '') {
          if (Cometd.isConnected() === false) {
            if (NotificationPopover.currentUser !== eXoUser || NotificationPopover.currentUser === '') {
              NotificationPopover.currentUser = eXoUser;
              document.cookie = 'forumCurrentUserId=' + escape(eXoUser) + ';path=/portal';
              Cometd._connecting = false;
              Cometd.currentTransport = null;
              Cometd.clientId = null;
            }
            Cometd.url = '/' + contextName + '/cometd';
            Cometd.exoId = eXoUser;
            Cometd.exoToken = eXoToken;
            Cometd.addOnConnectionReadyCallback(NotificationPopover.subcribeSendNotification);
            Cometd.init(Cometd.url);
          } else {
            NotificationPopover.subcribeSendNotification();
          }
        }
      },
      init : function() {
        //
        NotificationPopover.portlet = $('#' + NotificationPopover.popupId).parents('.uiNotificationPopoverToolbarPortlet:first');
        NotificationPopover.markReadLink = NotificationPopover.portlet.find('#MarkRead').text();
        NotificationPopover.removeLink = NotificationPopover.portlet.find('#Remove').text();
        NotificationPopover.deleteLink = NotificationPopover.portlet.find('#Delete').text();
        //
        NotificationPopover.popupItem = NotificationPopover.portlet.find('ul.displayItems:first');
        NotificationPopover.popupItem.find('li').each(function(i) {
          NotificationPopover.applyAction($(this));
        });
        //
        NotificationPopover.maxItem = NotificationPopover.portlet.data('max-item') || 8;
        NotificationPopover.nbUnreadNotif = NotificationPopover.popupItem.find('li.unread').length;
        NotificationPopover.nbDisplay = NotificationPopover.nbUnreadNotif;
        if(NotificationPopover.nbDisplay > 0) {
          var number = (NotificationPopover.nbDisplay <= NotificationPopover.maxItem) ? NotificationPopover.nbDisplay : (NotificationPopover.maxItem + "+");
          NotificationPopover.portlet.find('span.badgeDefault:first').text(number).show();
          NotificationPopover.portlet.find('.actionMark:first').show();
        }
        // markAllRead
        NotificationPopover.portlet.find('.actionMark:first').find('a').click(function (evt) {
          evt.stopPropagation();
          NotificationPopover.markAllRead();
        });
        //
        NotificationPopover.portlet.find('.dropdown-toggle:first').on('click', function() { 
          NotificationPopover.nbDisplay = 0;
          NotificationPopover.portlet.find('span.badgeDefault:first').text('0').hide();
        });
      },
      subcribeSendNotification : function() {
        Cometd.subscribe('/eXo/Application/web/NotificationMessage', function(eventObj) {
          var obj = JSON.parse(eventObj.data);
          NotificationPopover.appendMessage(obj.body);
        });
      },
      appendMessage : function(message) {
        var newItem = NotificationPopover.applyAction($($('<ul></ul>').html(message).html()));
        var id = newItem.data('id');
        //
        var target = $('<ul></ul>').append(NotificationPopover.popupItem.find('li'));
        target.find('li[data-id=' + id + ']').remove();
        target.find('li').each(function(i){
          if((i + 1) < NotificationPopover.maxItem) {
            NotificationPopover.popupItem.append($(this));
          }
        });
        target.remove();
        //
        NotificationPopover.popupItem.prepend(newItem.hide());
        NotificationPopover.showElm(newItem)
        //
        NotificationPopover.nbUnreadNotif = parseInt(NotificationPopover.nbUnreadNotif) + 1;
        var badge = NotificationPopover.portlet.find('span.badgeDefault:first');
        if(parseInt(NotificationPopover.nbDisplay) >= NotificationPopover.maxItem) {
          NotificationPopover.nbDisplay += "+";
        } else {
          NotificationPopover.nbDisplay = parseInt(NotificationPopover.nbDisplay) + 1;
        }
        badge.text(NotificationPopover.nbDisplay).show();
        //
        NotificationPopover.portlet.find('.actionMark:first').show();
        NotificationPopover.portlet.find('.no-items:first').hide();
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
          NotificationPopover.removeElm(elm.parents('li:first'));
        });
        //
        item.find('.delete-item').off('click').on('click', function(evt){
          evt.stopPropagation();
          //
          var elm = $(this);
          NotificationPopover.deleteItem(elm.parents('li:first'));
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
          NotificationPopover.removeElm(elm.parents('li:first'));
        });
        //
        return item;
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
      downBadge : function() {
        //
        var badge = NotificationPopover.portlet.find('span.badgeDefault:first');
        NotificationPopover.nbUnreadNotif = parseInt(NotificationPopover.nbUnreadNotif) - 1;
        NotificationPopover.nbDisplay = parseInt(NotificationPopover.nbDisplay) - 1;
        if(NotificationPopover.nbUnreadNotif < 1) {
          NotificationPopover.markAllRead();
        } else if (NotificationPopover.nbDisplay >= 1) {
          badge.text(NotificationPopover.nbDisplay);
        }
      },
      markAllRead : function() {
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
        if(item.hasClass('unread')) {
          NotificationPopover.downBadge();
        }
        //
        if(NotificationPopover.popupItem.find('li').length == 1) {
          NotificationPopover.showElm(NotificationPopover.portlet.find('.no-items:first'));
        }
      },
      deleteItem : function(item) {
        var action = NotificationPopover.deleteLink + item.data('id');
        window.ajaxGet(action);
        //
        if(item.hasClass('unread')) {
          NotificationPopover.downBadge();
        }
        //
        if(NotificationPopover.popupItem.find('li').length == 1) {
          NotificationPopover.showElm(NotificationPopover.portlet.find('.no-items:first'));
        }
      }
  };
  
  NotificationPopover.init();
  return NotificationPopover;
})(cometd, jQuery);