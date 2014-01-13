<%--

    Copyright (C) 2009 eXo Platform SAS.
    
    This is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation; either version 2.1 of
    the License, or (at your option) any later version.
    
    This software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public
    License along with this software; if not, write to the Free
    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
    02110-1301 USA, or see the FSF site: http://www.fsf.org.

--%>

<%@ page import="java.net.URLEncoder"%>
<%@ page import="javax.servlet.http.Cookie"%>
<%@ page import="org.exoplatform.container.PortalContainer"%>
<%@ page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="org.gatein.common.text.EntityEncoder"%>
<%@ page language="java" %>
<%
  String contextPath = request.getContextPath() ;

  String username = request.getParameter("username");
  if(username == null) {
      username = "";
  } else {
      EntityEncoder encoder = EntityEncoder.FULL;
      username = encoder.encode(username);
  }

  ResourceBundleService service = (ResourceBundleService) PortalContainer.getCurrentInstance(session.getServletContext())
  														.getComponentInstanceOfType(ResourceBundleService.class);
  ResourceBundle res = service.getResourceBundle(service.getSharedResourceBundleNames(), request.getLocale()) ;
  
  Cookie cookie = new Cookie(org.exoplatform.web.login.LoginServlet.COOKIE_NAME, "");
	cookie.setPath(request.getContextPath());
	cookie.setMaxAge(0);
	response.addCookie(cookie);

  //
  String uri = (String)request.getAttribute("org.gatein.portal.login.initial_uri");
  boolean error = request.getAttribute("org.gatein.portal.login.error") != null;

  response.setCharacterEncoding("UTF-8"); 
  response.setContentType("text/html; charset=UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>   
    <link rel="shortcut icon" type="image/x-icon"  href="<%=contextPath%>/favicon.ico" />
    <style>
					
			body {
				background: url("/platform-extension/login/jsp/images/login_texture.jpg") repeat;
				font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
			}
			
			.loginBGLight{
				wbackground: none repeat scroll 0 0 white;
				border-radius: 80px 80px 80px 80px;
				box-shadow: 0 0 550px white;
				left: 50%;
		    margin: -100px;
		    position: fixed;
		    top: 50%;
		    width: 200px;
		    height: 200px;
			}

			.uiLogin {
				
				color: #333333;
				display: block;
				font-size: 12px;
				left: 50%;
		    margin: -180px -195px 0;
		    position: fixed;
		    top: 50%;
		    width: 392px;
			}
			 
			.uiLogin .loginContainer {  
				border-radius: 4px;
				background: #fff url("/platform-extension/login/jsp/images/loginBG.png") no-repeat bottom;
				padding: 0 40px;
			}	
				
			.uiLogin .loginHeader {
				background: url("/platform-extension/login/jsp/images/LoginHeader.png") repeat-x scroll 0 0 transparent;
				border: 1px solid #555555;
				border-radius: 0 0 4px 4px;
				margin-top: -8px;
				padding: 8px 12px;
				position: absolute;
				width: 286px;
				box-shadow: 1px 2px 3px -1px rgba(0, 0, 0, 0.43);
			}
			
			.uiLogin .introBox:after {
				border-bottom: 8px solid #2a323e;
				border-right: 7px solid transparent;
				right: -8px;
			}
			.uiLogin .introBox:before, .uiLogin .introBox:after {
				border-top: medium none;
				content: "";
				display: inline-block;
				height: 0;
				position: absolute;
				top: -1px;
				vertical-align: top;
				width: 0;
			}
			
			.uiLogin .introBox:before {
				border-bottom: 8px solid #2a323e;
				border-left: 7px solid transparent;
				left: -8px;
			}
			
			.uiLogin .introBox:before, .uiLogin .introBox:after {
				border-top: medium none;
				content: "";
				display: inline-block;
				height: 0;
				position: absolute;
				top: -1px;
				vertical-align: top;
				width: 0;
			}
			
			.uiLogin .loginHeader .userLoginIcon{
				background: url("/platform-extension/login/jsp/images/UserLoginIcon.png") no-repeat scroll 0 0 transparent;
				color: #CCCCCC;
				font-size: 20px;
				font-weight: 500;
				line-height: 41px;
				padding-left: 52px;
				width: 236px;
			}
			
			.uiLogin .loginContent{
			  padding-top: 45px;
			}
			
			.uiLogin .loginContent .username{
                margin-top: 15px;
				background: url("/platform-extension/login/jsp/images/username.png") no-repeat left top;
			}
			
			.uiLogin .loginContent .password{
				background: url("/platform-extension/login/jsp/images/password.png") no-repeat left top;
			}

			.uiLogin .loginContent input {
			  width: 260px ;  
			  height: 36px;
			  border: solid 1px #cbcbcb ;
			  background: white;
				margin: 0 0 14px;
				color: #333333;
				box-shadow: 1px 2px 1px rgba(0, 0, 0, 0.075) inset;
				border-radius: 4px;
				padding-left: 50px;
				font-size: 18px;
				outline: none;
			}
			.uiLogin .loginContent input::-webkit-input-placeholder {
			   color: #999999;
			}
			
			.uiLogin .loginContent input:-moz-placeholder { 
			  color: #999999;
			}
			
			.uiLogin .loginContent input::-moz-placeholder { 
			   color: #999999;
			}
			
			.uiLogin .loginContent input:-ms-input-placeholder {  
			   color: #999999; 
			}
			.uiLogin .loginContent input:focus{
				color: #333;
				border-color: #94b0df;
				box-shadow: 0px 0px 5px rgba(19, 75, 159, 0.45) ;
				background-position: 0 -41px;
			}
			
			.uiLogin .loginContent .loginButton {
			  padding: 44px 0 30px;
			}
			
			.uiLogin .loginContent .loginButton .button{
				border: 1px solid #224886;
				border-radius: 4px;
				text-align: center;
				color: white;
				font-size: 16px;
				width: 313px;
				height: 43px;
				cursor: pointer;
				 text-shadow: 0 -2px 0px rgba(23, 33, 37, 0.25);
				background-color: #567ab6;
				background-image: -moz-linear-gradient(top, #638acd, #426393);
				background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#638acd), to(#426393));
				background-image: -webkit-linear-gradient(top, #638acd, #426393);
				background-image: -o-linear-gradient(top, #638acd, #426393);
				background-image: linear-gradient(to bottom, #638acd, #426393);
				background-repeat: repeat-x;
				filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff638acd', endColorstr='#ff426393', GradientType=0);
				border-color: #426393 #426393 #2a3f5e;
				border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
				*background-color: #426393;
				/* Darken IE7 buttons by default so they stand out more given they won't have borders */

				filter: progid:DXImageTransform.Microsoft.gradient(enabled = false);
				border-color: #224886;
				font-family: "Helvetica Neue Bold", Helvetica, Lucida, Arial, sans-serif;
				font-weight: bold;
				-webkit-box-shadow: inset 0 1px 0 rgba(255,255,255,.2), 0 1px 2px rgba(0,0,0,.05);
				-moz-box-shadow: inset 0 1px 0 rgba(255,255,255,.2), 0 1px 2px rgba(0,0,0,.05);
				box-shadow: inset 0 1px 0 rgba(255,255,255,.2), 0 1px 2px rgba(0,0,0,.05);
				padding: 0;
				outline: none;
			}
			.uiLogin .loginContent .loginButton .button:hover {
				 
				  background-color: #476ba7;
				  background-image: -moz-linear-gradient(top, #4b71b3, #426394);
				  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#4b71b3), to(#426394));
				  background-image: -webkit-linear-gradient(top, #4b71b3, #426394);
				  background-image: -o-linear-gradient(top, #4b71b3, #426394);
				  background-image: linear-gradient(to bottom, #4b71b3, #426394);
				  background-repeat: repeat-x;
				  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff4b71b3', endColorstr='#ff426394', GradientType=0);
				  border-color: #426394 #426394 #2a405f;
				  border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
				  *background-color: #426394;
				  /* Darken IE7 buttons by default so they stand out more given they won't have borders */

				  filter: progid:DXImageTransform.Microsoft.gradient(enabled = false);
				}
				.uiLogin .loginContent .loginButton .button:active{
					background-color: #426393; 
					*background-color: #3a5781;
					outline: none;
				}
				.uiLogin .loginContent .loginButton .button:focus {
					outline: none;
				}
			.uiLogin .spaceRole{
				clear: both;
				padding: 2px 0 11px;
			}
			
			.uiLogin .iPhoneCheckDisabled {
			   filter: progid:DXImageTransform.Microsoft.Alpha(Opacity = 50);
			   opacity: 0.5;
			}
			
			.uiLogin .iPhoneCheckContainer, .iPhoneCheckContainer label {
				-moz-user-select: none;
			}
			.uiLogin .iPhoneCheckContainer {
				-webkit-transform: translate3d(0, 0, 0);
			   position: relative;
			   height: 21px;
			   cursor: pointer;
			   overflow: hidden;
			   padding-top: 1px;
			   float: left;
			   margin-right: 15px;
			}
			.uiLogin .iPhoneCheckContainer input {
				position: absolute;
				top: 1px;
				left: 30px;
				filter: progid:DXImageTransform.Microsoft.Alpha(Opacity = 0);
				opacity: 0;
			}
			.uiLogin .iPhoneCheckContainer label.iPhoneCheckLabelOff {
				background-color: rgba(0, 0, 0, 0.035);
				background-image: linear-gradient(to bottom, transparent, rgba(0, 0, 0, 0.09));
				background-repeat: repeat-x;
				border-bottom-right-radius: 9px;
				border-top-right-radius: 9px;
				box-shadow: 0 1px 1px rgba(0, 0, 0, 0.43) inset;
				color: #8B8B8B;
				right: 0;
				text-align: right;
				text-shadow: 1px 0 0 #FFFFFF;
				height: 17px;
			}
			.uiLogin label.iPhoneCheckLabelOff span {
				line-height: 18px;
				padding-right: 17px;
			}
			
			.uiLogin .iPhoneCheckContainer label.iPhoneCheckLabelOn {
				background-color: #476CA7;
				background-image: linear-gradient(to bottom, #4B72B4, #426393);
				background-repeat: repeat-x;
				border: 1px solid #224886;
				border-bottom-left-radius: 9px;
				border-top-left-radius: 9px;
				box-shadow: 0 3px 5px #213B68 inset;
				color: white;
				left: 1px;	
				min-width: 9px;
				top: 1px;
				text-shadow: 0 0 2px rgba(0, 0, 0, 0.6);
				text-align: center;
			}
			.uiLogin .iPhoneCheckContainer, .iPhoneCheckContainer label {
				user-select: none;
			   -moz-user-select: none;
			   -khtml-user-select: none;
			}
			.uiLogin .iPhoneCheckContainer label {
				white-space: nowrap;
				font-size: 11px;
				line-height: 16px;
				font-weight: bold;
				font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
				cursor: pointer;
				display: block;
				height: 16px;
				position: absolute;
				width: auto;
				top: 1;
				overflow: hidden;
			}
			.uiLogin label.iPhoneCheckLabelOn span {
				line-height: 18px;
				padding: 0 8px 0 4px;
			}
			.uiLogin .iPhoneCheckHandle {
				background-color: #F9F9F9;
				background-image: linear-gradient(to bottom, #FFFFFF, #F1F1F1);
				background-repeat: repeat-x;
				border-radius: 50% 50% 50% 50%;
				box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.8);
				cursor: pointer;
				display: block;
				height: 18px;
				left: 1;
				position: absolute;
				top: 1px;
				width: 18px;
			}
			
			.uiLogin .rememberTxt{
				font-size: 13px;
				color: #999999;
				font-weight: bold;
			}

			.uiLogin .signinFail{
		    font-size: 13px;
		    line-height: 18px;
		    margin: 12px 0 0 1px;
		    font-weight: bold;
				width: 310px;
      }
			
			.uiLogin .uiIconError{
                background: url("/eXoSkin/skin/images/Icons/uiIconColor.png") no-repeat left top;
				padding-left: 20px;
			}

            #platformInfoDiv {
                font-size: 12px;
                text-align:center;
                color: #FFFFFF;
                opacity: 0.6;
                margin-top: 15px;
            }
			.titleLogin {
				line-height: 12px; 
				padding: 6px 3px 0 0;
				min-height: 31px;
				font-size: 11px;
			}

		</style>

    <script type="text/javascript" src="/platform-extension/javascript/jquery-1.7.1.js"></script>
    <script type="text/javascript" src="/platform-extension/javascript/iphone-style-checkboxes.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            var startlabelfooter = jQuery("#platformInfoDiv").data("labelfooter");
            var htmlContent = startlabelfooter +" eXo Platform ";
            var divContent = jQuery("#platformInfoDiv");
            var requestJsonPlatformInfo = jQuery.ajax({ type: "GET", url: "/portal/rest/platform/info", async: false, dataType: 'json' });
            if(requestJsonPlatformInfo.readyState == 4 && requestJsonPlatformInfo.status == 200){
                //readyState 4: request finished and response is ready
                //status 200: "OK"
                var myresponseText = requestJsonPlatformInfo.responseText;
                var jsonPlatformInfo = jQuery.parseJSON(myresponseText);
                htmlContent += "v"
                htmlContent += jsonPlatformInfo.platformVersion;
                htmlContent += " - build "
                htmlContent += jsonPlatformInfo.platformBuildNumber;
            }else{
                htmlContent += "4.0"
            }
            divContent.text(htmlContent);
        });
    </script>
  </head>
  <body>
  	<div class="loginBGLight"><span></span></div>
    <div class="uiLogin">
    	<div class="loginContainer">
	      <div class="loginHeader introBox">
					<div class="userLoginIcon"><%=res.getString("portal.login.Connectlabel")%></div>
				</div>
	      <div class="loginContent">
				<div class="titleLogin">
					<%/*Begin form*/%>
          <%
                if(error) {
          %>
          <div class="signinFail"><i class="uiIconError"></i><%=res.getString("portal.login.SigninFail")%></div><%}%>
				</div>
        <div class="centerLoginContent">
          <form name="loginForm" action="<%= contextPath + "/login"%>" method="post" style="margin: 0px;">
                <% if (uri != null) { 
                        uri = EntityEncoder.FULL.encode(uri);
                %>
          		<input type="hidden" name="initialURI" value="<%=uri%>"/>
                <% } %>	
					
				<input class="username" tabindex="1" id="username" name="username" type="text" placeholder="<%=res.getString("portal.login.Username")%>" onblur="this.placeholder = '<%=res.getString("portal.login.Username.blur")%>'" onfocus="this.placeholder = ''"/>
				<input class="password" tabindex="2" id="UIPortalLoginFormControl" type="password" id="password" name="password" placeholder="<%=res.getString("portal.login.Password")%>" onblur="this.placeholder = '<%=res.getString("portal.login.Password")%>'" onfocus="this.placeholder = ''"/>
                <div class="spaceRole">
					<input type="checkbox" tabindex="3" class="yesno" checked="checked" style="visibility: hidden;" id="rememberme" name="rememberme" value="true" data-yes="<%=res.getString("portal.login.Yes")%>" data-no="<%=res.getString("portal.login.No")%>"/>
					<label class="rememberTxt" for="rememberme"><%=res.getString("portal.login.RememberOnComputer")%></label>
				</div>
                <script type="text/javascript">
                    $("div.spaceRole").click(function()
                    {
                        var input = $(this).find("#rememberme");
                        var remembermeOpt = input.attr("value") == "true" ? "false" : "true";
                        input.attr("value", remembermeOpt);
                    });
                    var yeslabel;
                    var nolabel;
                    $("div.spaceRole").children('input:checkbox').each(function () {
                        yeslabel = $(this).data("yes");
                        nolabel = $(this).data("no");
                        $(this).iphoneStyle({
                                checkedLabel:yeslabel,
                                uncheckedLabel:nolabel});

                        $(this).change(function()
                        {
                            $(this).closest("div.spaceRole").trigger("click");
                        });
                    });
                </script>

				<div id="UIPortalLoginFormAction" class="loginButton">
					<button class="button" tabindex="4"  onclick="login();"><%=res.getString("portal.login.Signin")%></button>
				</div>

                <script type='text/javascript'>


					function login() {
						document.loginForm.submit();                   
					}
				</script>
				</form>
				<%/*End form*/%>
        </div>
      </div>
    	</div>
    	<div id="platformInfoDiv" data-labelfooter="<%=res.getString("portal.login.Footer")%>" ></div>
    </div>
    
  </body>
</html>
