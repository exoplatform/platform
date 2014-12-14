(function($) {
  var localizeStatus = $("div#labelBundle");
  var NotificationAdmin = {
      status : {
        OK : 'OK',
        NOK : 'NOK'
      },
      label : {
          Information : $("span#Information", localizeStatus).text(),
          Error : $("span#Error", localizeStatus).text(),
          OK : $("span#labelOK", localizeStatus).text()
      },
      msg : {
        OK : $("span#msgSaveOK", localizeStatus).text(),
        NOK: $("span#msgSaveKO", localizeStatus).text()
      },
      init : function() {
        $('input.providerAction').iphoneStyle({ 
        	checkedLabel:'YES', 
        	uncheckedLabel:'NO',
        	onChange : function() {
        		var input = $(this.elem);
        		NotificationAdmin.switchStatus(input.attr('name'), input.hasClass("disable"));
        	}
        });
        //
        $("#btSetSender").click(function() {
          NotificationAdmin.saveSenderInfo($("input#senderName").val(), $("input#senderEmail").val());
        });
      },
      
      switchStatus : function(pluginId, isEnable) {
        //
        $("#notificationAdmin").jzAjax({
          url : "NotificationsAdministration.saveActivePlugin()",
          data : {
            "pluginId" : pluginId,
            "enable" : isEnable
          },
          success : function(data) {
            var clazz = (data.isEnable === true) ? 'enable' : 'disable';
            var plugin = $("tr#" + data.pluginId);
            plugin.attr("class", clazz);
            var action = $('input[name=' + data.pluginId + ']')
            action.attr('class', 'providerAction yesno ' + clazz);
          }
        }).fail(function(jqXHR, textStatus) {
          alert("Request failed: " + textStatus + ". " + jqXHR);
        });
      },
      
      saveSenderInfo : function(name, email) {
        $("#notificationAdmin").jzAjax({
          url : "NotificationsAdministration.saveSender()",
          data : {
            "name" : name,
            "email" : email
          },
          success : function(res) {
            if (res.status === "OK") {
              var msgOk = NotificationAdmin.msg.OK;
              msgOk = msgOk.replace('{0}', res.name).replace('{1}', res.email);
              NotificationAdmin.showMessage(msgOk, NotificationAdmin.status.OK);
            } else {
              NotificationAdmin.showMessage(NotificationAdmin.msg.NOK, NotificationAdmin.status.NOK);
            }
          }
        }).fail(function(jqXHR, textStatus) {
          NotificationAdmin.showMessage(NotificationAdmin.msg.NOK, NotificationAdmin.status.NOK);
        });
      },
      
      showMessage : function(message, type) {
        var msgContainer = $('div#confirmMessage');
        if(type === NotificationAdmin.status.OK) {
          msgContainer.attr('class', 'alert alert-success')
          .find('i:first').attr('class', 'uiIconSuccess');
        } else {
          msgContainer.attr('class', 'alert alert-error')
          .find('i:first').attr('class', 'uiIconError');
        }
        msgContainer.hide().stop().find('span.message').text(message);
        msgContainer.show('fast').delay(4500).hide('slow');
      }
      
  };

  NotificationAdmin.init();
  return NotificationAdmin;

})(gj);
