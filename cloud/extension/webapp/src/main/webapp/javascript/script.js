
	                 function Tenants() { }
	               
	        	 var prefixUrl = location.protocol + '//' + location.hostname;
			 if (location.port) {
			    prefixUrl += ':' + location.port;
			  }
	                  var user;
                          var auth = null;
				 
	
	                   /** Getting list chain */
			  Tenants.prototype.init = function() {
	                  accessUrl = prefixUrl + '/cloud-admin/rest/cloud-admin';
	                  accessSecureUrl = prefixUrl + '/cloud-admin/rest/private/cloud-admin';
	                  tenantServicePath = accessUrl + "/public-tenant-service/";
	                  infoServicePath =accessSecureUrl + "/info-service/";
	                  refreshInterval = 10000;
                          is_chrome  = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
	                  }
	
	
    	                Tenants.prototype.getList = function() {
	                    var editedDocList = document.getElementById("list");
	                    if (editedDocList.hasChildNodes())
			    {
			    while ( editedDocList.childNodes.length >= 1 )
	        	       {
		                 editedDocList.removeChild( editedDocList.firstChild );       
			       } 
			    }

	                    _gel('tenantsForm').style.display = "none";
	                    _gel('idForm').style.display = "none";
	                    _gel('messageString').style.display = "none";
	
                            if (auth == null) {
	                    _gel('credentialForm').style.display = "";
                             (is_chrome) ? gadgets.window.adjustHeight(500) :gadgets.window.adjustHeight();
                           } else {
                              tenants.getStatus();
                            }
	                  }  
		 	
		
                         Tenants.prototype.getStatus = function() {
                            var authString = "";
                            if (auth == null) {
                            authString = encode64(_gel("t_cred_name").value + ":" + _gel("t_cred_pass").value);
                            auth = authString;
                             } else {
                             authString = auth;
                           }
                             var params = {};
                             params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.SIGNED;
                             params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.GET;
                             params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
                             params[gadgets.io.RequestParameters.HEADERS] = {"Authorization" : "Basic "  + authString}

                             var url = infoServicePath + "tenant-list-all";
                            gadgets.io.makeRequest(url, tenants.displayList, params);
                         }
                  
                       
                         /* Showing list */
                         Tenants.prototype.displayList = function(resp) {
                          if (resp.errors!="" ) {
                                 _gel('messageString').style.display = "";
                                 _gel("messageString").innerHTML="Wrong cloud-admin credentials! Consult with the administrator.";
                                 auth = null;
                                } else {
                             document.getElementById('credentialForm').style.display = "none";
                             _gel('messageString').style.display = "none";
   			     var data = gadgets.json.parse(resp.text);

                             for (var i = 0; i < data.length; i++) {
                                var editedDocList = document.getElementById("list");
				var doc = data[i];
				var AppClass = document.createElement('div');

				var showHTML = "<b>Name:</b> ";
                                showHTML += doc["tenantName"];
                                showHTML += "<br/>";
                                showHTML += "<b>Id :</b> ";
                                showHTML += doc["uuid"];
                                showHTML += "<br/>";
                                showHTML += "<b>State:</b> ";
                                if (doc["state"]=="ONLINE")
 				     showHTML += "<img src=\"/cloud-extension/gadgets/skin/green.png\" title=\"" + doc["state"]+ "\" />";
                                else if (doc["state"]=="UNKNOWN" || doc["state"]=="SUSPENDED")
                                    showHTML += "<img src=\"/cloud-extension/gadgets/skin/grey.png\" title=\"" + doc["state"]+ "\" />";
                                else if (doc["state"]=="EMAIL_VALIDATE_FAIL" || doc["state"]=="CREATION_FAIL")
                                    showHTML += "<img src=\"/cloud-extension/gadgets/skin/red.png\" title=\"" + doc["state"]+ "\" />";
                                 else
                                    showHTML += "<img src=\"/cloud-extension/gadgets/skin/yellow.png\" title=\"" + doc["state"]+ "\" />";

                                showHTML += "<br>";

                                AppClass.innerHTML = showHTML;
				AppClass.className = 'ItemIcon';
                                editedDocList.style.display = "";
				editedDocList.appendChild(AppClass);
                               }
				(is_chrome) ? gadgets.window.adjustHeight(500) :gadgets.window.adjustHeight();
                           } 
			}
                         

                         /* Showing form */
                         Tenants.prototype.displayForm = function() {
                           _gel("messageString").innerHTML="";
                           _gel('messageString').style.display = "";
                           _gel('tenantsForm').style.display = "";
                           _gel('idForm').style.display = "none";
                           _gel("list").style.display = "none";
                           _gel('credentialForm').style.display = "none";
                           (is_chrome) ? gadgets.window.adjustHeight(500) :gadgets.window.adjustHeight();
			}

                         /* Showing form */
                         Tenants.prototype.displayIdForm = function() {
                           _gel("messageString").innerHTML="";
                           _gel('messageString').style.display = "";
                           _gel('tenantsForm').style.display = "none";
                           _gel('idForm').style.display = "";
                           _gel("list").style.display = "none";
                          _gel('credentialForm').style.display = "none";
                          
                           (is_chrome) ? gadgets.window.adjustHeight(350) :gadgets.window.adjustHeight();
			}

                         /* Sending request */
                         Tenants.prototype.doCreationRequest = function() {

                                 var url = tenantServicePath + "create-with-confirm/" + _gel("t_name").value+ "/"+ _gel("t_email").value+"/" ;
                                 var params = {};
                                 params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
			         params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
                                 _gel("t_submit").value="Wait..";
                                 _gel("t_submit").disabled=true;
                                 gadgets.io.makeRequest(url, tenants.handleCreationResponse, params);
                                
                       	}

                         /* Sending request */
                         Tenants.prototype.doConfirmationRequest = function() {
 
                                 var url = tenantServicePath + "create-confirmed/?id=" + _gel("t_id").value;
                              
                                 var params = {};
                                 params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.POST;
			         params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
                                 _gel("t_submitId").value="Wait..";
                                 _gel("t_submitId").disabled=true;
                                 gadgets.io.makeRequest(url, tenants.handleConfirmationResponse, params);
                                
                       	}

                       Tenants.prototype.handleCreationResponse = function(resp) {

                           if (resp.errors=="") {
                            _gel("messageString").innerHTML="<div class=\"Ok\">Tenant creation request sent successfully! Check your email for instructions.</div>";
                           } else {
                              _gel("messageString").innerHTML=resp.text;
                           }
                           _gel("t_submit").disabled=false;
                           _gel("t_submit").value="Submit";
                           _gel("t_name").value="";
                           _gel("t_email").value="";
                           (is_chrome) ? gadgets.window.adjustHeight(500) :gadgets.window.adjustHeight();
			}

                      Tenants.prototype.handleConfirmationResponse = function(resp) {

                           if (resp.errors=="") {
                            _gel("messageString").innerHTML="<div class=\"Ok\">Tenant creation request sent successfully! You will receive email when done.</div>";
                           } else {
                             _gel("messageString").innerHTML=resp.text;
                           }
                           
                           _gel("t_submitId").disabled=false;
                           _gel("t_submitId").value="Submit";
                          _gel("t_id").value="";
      
                          (is_chrome) ? gadgets.window.adjustHeight(350) :gadgets.window.adjustHeight();
			}

                        var tenants = new Tenants();

                        //TODO: Remove when OAuth could be done;
			 var keyStr = "ABCDEFGHIJKLMNOP" +
			                "QRSTUVWXYZabcdef" +
			                "ghijklmnopqrstuv" +
			                "wxyz0123456789+/" +
			                "=";
			  
			   function encode64(input) {
			      var output = "";
			      var chr1, chr2, chr3 = "";
			      var enc1, enc2, enc3, enc4 = "";
			      var i = 0;
			  
			      do {
			         chr1 = input.charCodeAt(i++);
			         chr2 = input.charCodeAt(i++);
			         chr3 = input.charCodeAt(i++);
			  
			         enc1 = chr1 >> 2;
			         enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			         enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			         enc4 = chr3 & 63;
			  
			         if (isNaN(chr2)) {
			            enc3 = enc4 = 64;
			         } else if (isNaN(chr3)) {
			            enc4 = 64;
			         }
			  
			         output = output +
			            keyStr.charAt(enc1) +
			            keyStr.charAt(enc2) +
			            keyStr.charAt(enc3) +
			            keyStr.charAt(enc4);
			         chr1 = chr2 = chr3 = "";
			         enc1 = enc2 = enc3 = enc4 = "";
			      } while (i < input.length);

			      return output;
			   }


                         function detectUser() {
                            var viewer = null;
                            var context = null;

                            var req = opensocial.newDataRequest();
		            var opts = {};
				  
			    opts[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [
				      opensocial.Person.Field.PROFILE_URL,
				      "portalName",
				      "restContext",
				      "host"];
				
			    req.add(req.newFetchPersonRequest("VIEWER", opts), 'viewer');
			    req.send(onLoad);
				
		       function onLoad(data) {
			 if (!data.hadError()) {
			   viewer = data.get('viewer').getData();
			   var profile_url =  viewer.getField(opensocial.Person.Field.PROFILE_URL);
			   var userId = profile_url.substr(profile_url.lastIndexOf('/') + 1);
                           }
                      }
                 }
