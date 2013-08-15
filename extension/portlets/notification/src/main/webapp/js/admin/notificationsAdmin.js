(function(sUtils, $) {
  var localizeStatus = $("div#labelBundle");
  var NotificationAdmin = {
      label : {
          Enable : $("span#labelEnable", localizeStatus).text(),
          Disable : $("span#labelDisable", localizeStatus).text(),
          Information : $("span#Information", localizeStatus).text(),
          OK : $("span#labelOK", localizeStatus).text(),
          Close : $("span#labelClose", localizeStatus).text()
      },
      msg : {
        OK : $("span#msgSaveOK", localizeStatus).text(),
        NOK: $("span#msgSaveKO", localizeStatus).text()
      },
      init : function() {
        var buttons = $("input.providerAction");
        buttons.on('click', function(e) {
          NotificationAdmin.switchStatus($(this).attr('name'), $(this).hasClass("disable"));
        });
        //
        buttons.each(function(index) {
          $(this).attr('value', ($(this).hasClass('enable')) ? NotificationAdmin.label.Disable : NotificationAdmin.label.Enable);
        });
        //
        $("#btSetSender").click(function() {
          NotificationAdmin.saveSenderInfo($("input#senderName").val(), $("input#senderEmail").val());
        });

      },
      
      switchStatus : function(pluginId, isEnable) {
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
            action.attr('class', 'providerAction ' + clazz);
            action.val((data.isEnable === true) ? NotificationAdmin.label.Disable : NotificationAdmin.label.Enable);
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
            if (res.status == "OK") {
              var msgOk = NotificationAdmin.msg.OK;
              msgOk = msgOk.replace('{0}', res.name).replace('{1}', res.email).replace('<', '&lt;');
              sUtils.PopupConfirmation.confirm('notificationAdmin', {}, NotificationAdmin.label.Information, msgOk, NotificationAdmin.label.Close);
            }
          }
        }).fail(function(jqXHR, textStatus) {
          var msgKO = NotificationAdmin.msg.NOK;
          sUtils.PopupConfirmation.confirm('notificationAdmin', {}, NotificationAdmin.label.Information, msgKO, NotificationAdmin.label.Close);
        });
      }
  };

  NotificationAdmin.init();
  return NotificationAdmin;

})(socialUtil, gj);
