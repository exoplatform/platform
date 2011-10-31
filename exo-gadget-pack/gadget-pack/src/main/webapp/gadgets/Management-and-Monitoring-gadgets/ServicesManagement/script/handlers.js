/**
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
ServicesManagement.prototype.registerHandler = function() {
	
	//======================Handler======================================//
	$("#servicesSelector").change(function () {
	  var serviceName = $(this).val();
	  serviceName = gadgets.util.unescapeString(!serviceName ? "" : serviceName);
	  var methodsURL = eXo.gadget.ServicesManagement.SERVICES_URL + "/" + encodeURIComponent(serviceName);
	  
	  var currView = gadgets.views.getCurrentView().getName();
	  if (currView == "home") {
	  	eXo.gadget.ServicesManagement.makeRequest(methodsURL, eXo.gadget.ServicesManagement.renderServiceDetailForHome);
	  } else {
	  	eXo.gadget.ServicesManagement.makeRequest(methodsURL, eXo.gadget.ServicesManagement.renderServiceDetailForCanvas);
	  }
	});

	$(".Tab").click(function () {
		var selectedTab = $(".TabSelected")[0];			
		if (this == selectedTab) {
			return;
		}

		$(selectedTab).removeClass("TabSelected");
		$(this).addClass("TabSelected");
		
		var selectedContent = eXo.gadget.ServicesManagement.getContentContainer(selectedTab);
		var content = eXo.gadget.ServicesManagement.getContentContainer(this);
		
		$(selectedContent).removeClass("ContentSelected");
		$(selectedContent).hide();
		eXo.gadget.ServicesManagement.fadeIn(content, function() {
			$(this).addClass("ContentSelected");
		});		
	});
	
	$(".DesIconHome").click(function () {
		eXo.gadget.ServicesManagement.fadeIn($(".DescriptionBox")[0], function() {
			var desBox = this; 
			window.setTimeout(function() {
				$(desBox).fadeOut(2000, function() {
					eXo.gadget.ServicesManagement.resetHeight();
				});
			}, 5000);
		});
	});
	
	$("#propertiesSelector").change(function () {
	  var propertyName = $(this).val();
	  propertyName = gadgets.util.unescapeString(!propertyName ? "" : propertyName);

	  var propertyData = $(this).data('properties');	
	  var property = null;
	  if (propertyData) {
		  for (var i = 0; i < propertyData.length; i++) {
		  	if (propertyData[i].name == propertyName) {
		  		property = propertyData[i];
		  	}
		  }
	  }

	  eXo.gadget.ServicesManagement.renderPropertyDetail(property);
	});
	
	$("#methodsSelector").change(function () {
      var methodName = $(this).val();
      methodName = gadgets.util.unescapeString(!methodName ? "" : methodName);

      var methodData = $(this).data('methods');
      var method = null;
      if (methodData) {
          for (var i = 0; i < methodData.length; i++) {
            if (methodData[i].name == methodName) {
                method = methodData[i];
            }
          }
      }

      eXo.gadget.ServicesManagement.renderMethodDetail(method);
    });
	
	$('.MethodActionButton').live('click', function(event) {
		event.preventDefault();
		var tr = this.parentNode.parentNode;		
		var methodName = gadgets.util.unescapeString($(".methodName", tr).text());
	  var reqMethod = gadgets.util.unescapeString($(".reqMethod", tr).text());
	  var serviceName = $("#servicesSelector").val();
	  serviceName = gadgets.util.unescapeString(!serviceName ? "" : serviceName);
	  var param = $("form", tr).serialize();
	  
		var execLink = eXo.gadget.ServicesManagement.SERVICES_URL + "/" + 
												encodeURIComponent(serviceName) + "/" + 
												encodeURIComponent(methodName);
		eXo.gadget.ServicesManagement.makeRequest(execLink, eXo.gadget.ServicesManagement.showMinimessage, param, "text", reqMethod);
	});
	
	$('.PropertyActionButton').live('click', function(event) {
      event.preventDefault();
      var tr = this.parentNode.parentNode;        
      var propName = gadgets.util.unescapeString($(".propName", tr).text());
      var reqMethod = "GET";
      var serviceName = $("#servicesSelector").val();
      serviceName = gadgets.util.unescapeString(!serviceName ? "" : serviceName);
      
      var execLink = eXo.gadget.ServicesManagement.SERVICES_URL + "/" + 
                                                encodeURIComponent(serviceName) + "/" + 
                                                encodeURIComponent(propName);
      eXo.gadget.ServicesManagement.makeRequest(execLink, eXo.gadget.ServicesManagement.showMinimessage, null, "text", reqMethod);
    });
};

/**
 * @param reqUrl - String
 * @param callback - Function
 * @param sendData - Data that will be send to server 
 * @param returnType - String html/xml/json/script
 * @param reqMethod - GET/POST/PUT...
 * @return XMLHttpRequest object
 */
ServicesManagement.prototype.makeRequest = function(reqUrl, callback, sendData, returnType, reqMethod) {	
	if (reqUrl == "") {
		return;
	}
	reqMethod = reqMethod ? reqMethod : "GET";
	returnType = returnType ? returnType : "json";
	
	return $.ajax({
					  url: reqUrl,
					  type: reqMethod,					  
					  success: callback,
					  contentType: "application/x-www-form-urlencoded",
					  error: function() {
						  var prefs = new _IG_Prefs();
						  alert(prefs.getMsg("badURL"));
					  },
					  data: sendData,
					  dataType: returnType,
					  beforeSend: function(xhr) {
					  	xhr.setRequestHeader("If-Modified-Since", "Thu, 1 Jan 1970 00:00:00 GMT");
					  } 
					});	
};
