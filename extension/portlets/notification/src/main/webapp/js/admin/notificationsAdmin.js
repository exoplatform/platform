(function($) {

	var localizeStatus = $("div#labelBundle"); 
	var labelEnable = $("samp#labelEnable",localizeStatus).html();
	var labelDisable = $("samp#labelDisable",localizeStatus).html();
	var senderInfoMsg = $("div#senderInfoMsg");
	
		function switchStatus(providerId,isEnable) {
			$("#notificationAdmin").jzAjax({				
				url : "NotificationsAdministration.setProvider()",
				data : {
					"providerId" : providerId,
					"enable" : isEnable
				},
				success : function(data) {
					provider = $("tr#"+data.provider);
					provider.attr("class",data.status);
					action = $("input.providerAction",provider)
					action.attr("class","providerAction "+data.status);
					if (data.isEnable == "true")
						action.attr("value",labelDisable); //label with value of action
					else action.attr("value",labelEnable);
				}
			}).fail(function(jqXHR, textStatus) {
				alert( "Request failed: " + textStatus + ". "+jqXHR);
			});
		}
		
		function saveSenderInfo(name,email) {
			senderInfoMsg.hide();
			$("#notificationAdmin").jzAjax({				
				url : "NotificationsAdministration.setSender()",
				data : {
					"name" : name,
					"email" : email
				},
				success : function(res) {
					if (res.status == "OK"){
						var msgOK = $("samp#msgSaveOK",localizeStatus).html();
						senderInfoMsg.html(msgOK+" \""+res.name+" ["+res.email+"] \"");
						senderInfoMsg.show();
					}											
				}
			}).fail(function(jqXHR, textStatus) {
				var msgKO = $("samp#msgSaveKO",localizeStatus).html();
				senderInfoMsg.html(msgKO+textStatus);
				msgKO.show();
			});
		}		
			
		$("#btSetSender").click(function(){
			saveSenderInfo($("input#senderName").attr("value"),$("input#senderEmail").attr("value"));
		});
		
		$("input.providerAction").each(function( index ) {
			if ($(this).hasClass('enable')) $(this).attr('value',labelDisable);
			else  if ($(this).hasClass('disable')) $(this).attr('value',labelEnable);
						
			$(this).click(function(){
				switchStatus($(this).attr("name"),$(this).attr("value")==labelEnable);				
			});
		});
				
})($);
