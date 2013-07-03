(function($) {

	var localizeStatus = $("div#status"); 
	var labelEnable = $("label#label-enable",localizeStatus).attr("value");
	var labelDisable = $("label#label-disable",localizeStatus).attr("value");
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
					if (data.status == "enable")
						action.attr("value",labelDisable);
					else action.attr("value",labelEnable);
				}
			}).fail(function(jqXHR, textStatus) {
				alert( "Request failed: " + textStatus + ". "+jqXHR);
			});
		}
		
		function saveSenderInfo(name,email) {
			$("div#senderInfoMsgOK").hide();
			$("div#senderInfoMsgKO").hide();
			$("#notificationAdmin").jzAjax({				
				url : "NotificationsAdministration.setSender()",
				data : {
					"name" : name,
					"email" : email
				},
				success : function(response) {
					if (response.status == "OK"){
						$("div#senderInfoMsgOK").show();
					}											
				}
			}).fail(function(jqXHR, textStatus) {
				$("div#senderInfoMsgKO").show();
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
