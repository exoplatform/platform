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
        OK : $("span#labelOK", localizeStatus).text(),
        email: $("span#labelEmailNotifications", localizeStatus).text(),
        intranet: $("span#labelIntranetNotifications", localizeStatus).text()
      },
      msg : {
        OK : $("span#msgSaveOK", localizeStatus).text(),
        NOK: $("span#msgSaveKO", localizeStatus).text()
      },
      init : function() {
    	$("a.edit-setting").click(function() {
    	  $(this).parents('.plugin-container:first').removeClass('view').addClass('edit');
    	});
    	$(".save-setting").click(function() {
          var thizSave = $(this);
          var saveBlock = thizSave.parents("div.plugin-container:first");
          var inputs=[];
          saveBlock.find('.edit-mode:first').find("input").each(function(i) {
        	  inputs.push(($(this).data('channel') + "=" + $(this).is(":checked")));
          });
          //
          $("#notificationAdmin").jzAjax({
            url : "NotificationsAdministration.saveActivePlugin()",
            data : {
            	"pluginId" : thizSave.data('plugin'),
            	"inputs" : inputs.join('&')
            },
            success : function(data) {
              if(data.status == 'ok') {
                if(data.result) {
                  var t = 0;
                  $.each(data.result, function(i) {
                    var elm = saveBlock.find('.' + i + ':first').removeClass('hide');
                    if(data.result[i] == 'false') {
                      elm.addClass('hide');
                      ++t;
                    }
                  });
                  saveBlock.find('.view-mode').find('> span').removeClass('hide');
                  if(t !== saveBlock.find('.view-mode').find('> div').length) {
                    saveBlock.find('.view-mode').find('> span').addClass('hide');
                  }
                  
                }
                saveBlock.removeClass('edit').addClass('view');
              }
            }
          }).fail(function(jqXHR, textStatus) {
            alert("Request failed: " + textStatus + ". " + jqXHR);
          });
    	});
    	//
        $("#btSetSender").click(function() {
          NotificationAdmin.saveSenderInfo($("input#senderName").val(), $("input#senderEmail").val());
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
