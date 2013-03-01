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
<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>   
    <link rel="shortcut icon" type="image/x-icon"  href="<%=contextPath%>/favicon.ico" />
    <style>
					
			body {
				background: url("/platform-extension/login/jsp/images/login_texture.jpg") repeat;
				font-family: lucida, arial, tahoma, verdana
			}
			@font-face {
			 font-family: lucida;
			 src: url("/platform-extension/login/jsp/images/L_0.eot") 
			}
			
			@font-face {
			 font-family: lucida;
			 src: url("/platform-extension/login/jsp/images/L_0.TTF") 
			}

			.uiLogin {
				border-radius: 4px 4px 4px 4px;
				box-shadow: 0 1px 3px #545555;
				color: #333333;
				font-size: 12px;
				height: 332px;
				margin: 110px auto 7px;
				padding: 0 40px;
				position: relative;
				width: 312px;
				background: #fff url("/platform-extension/login/jsp/images/loginBG.png") no-repeat bottom;
			}
			  
			.uiLogin .loginHeader {
				background: url("/platform-extension/login/jsp/images/LoginHeader.png") repeat-x scroll 0 0 transparent;
				border: 1px solid #393939;
				border-radius: 0 0 4px 4px;
				margin-top: -8px;
				padding: 8px 12px;
				position: absolute;
				width: 286px;
				box-shadow: 1px 1px 1px #b6b6b6;
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
				font-size: 17px;
				font-weight: bold;
				line-height: 41px;
				padding-left: 52px;
			}
			
			.uiLogin .loginContent{
			  padding-top: 45px;
			}

			.uiLogin .loginContent .centerLoginContent {
			  height: 157px;
			}
			
			.uiLogin .loginContent .username{
				background: url("/platform-extension/login/jsp/images/username.png") no-repeat left;
			}
			
			.uiLogin .loginContent .password{
				background: url("/platform-extension/login/jsp/images/password.png") no-repeat left;
			}

			.uiLogin .loginContent input {
			  width: 264px ;  
			  height: 36px;
			  border: solid 1px #cbcbcb ;
			  background: white;
				margin: 0 0 14px;
				color: #333333;
				box-shadow: 1px 2px 1px rgba(0, 0, 0, 0.075) inset;
				border-radius: 4px;
				padding-left: 50px;
				color: #999;
				font-size: 18px;
			}
			
			.uiLogin .loginContent .loginButton {
			  padding: 52px 0 0 0;
			}
			
			.uiLogin .loginContent .loginButton .button{
			  background: url("/platform-extension/login/jsp/images/loginAction.png") repeat-x;
			  border: 1px solid #224886;
			  border-radius: 4px;
			  text-align: center;
			  color: white;
			  font-weight: bold;
			  font-size: 16px;
			  width: 313px;
			  height: 43px;
			  cursor: pointer;
			}
			
			.uiLogin .spaceRole{
				clear: both;
				padding: 12px 0;
			}
			
			.uiLogin .iPhoneCheckContainer, .iPhoneCheckContainer label {
				-moz-user-select: none;
			}
			.uiLogin .iPhoneCheckContainer {
				cursor: pointer;
				height: 18px;
				overflow: hidden;
				position: relative;
				float: left;
			}
			.uiLogin .iPhoneCheckContainer input {
				left: 30px;
				opacity: 0;
				position: absolute;
				top: 5px;
			}
			.uiLogin label.iPhoneCheckLabelOff {
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
			}
			.uiLogin label.iPhoneCheckLabelOff span {
				padding-right: 10px;
			}
			
			.uiLogin label.iPhoneCheckLabelOn {
				background-color: #476CA7;
				background-image: linear-gradient(to bottom, #4B72B4, #426393);
				background-repeat: repeat-x;
				border: 1px inset #224886;
				border-bottom-left-radius: 9px;
				border-top-left-radius: 9px;
				box-shadow: 0 3px 5px #213B68 inset;
				color: white;
				left: 0;
				text-shadow: 0 0 2px rgba(0, 0, 0, 0.6);
				text-align: center;
			}
			.uiLogin .iPhoneCheckContainer, .iPhoneCheckContainer label {
				-moz-user-select: none;
			}
			.uiLogin .iPhoneCheckContainer label {
				cursor: pointer;
				display: block;
				font-family: "Helvetica Neue",Arial,Helvetica,sans-serif;
				font-size: 11px;
				font-weight: bold;
				height: 15px;
				line-height: 16px;
				overflow: hidden;
				position: absolute;
				top: 0;
				white-space: nowrap;
				width: auto;
			}
			.uiLogin label.iPhoneCheckLabelOn span {
				padding-left: 4px;
			}
			.uiLogin .iPhoneCheckHandle {
				background-color: #F9F9F9;
				background-image: linear-gradient(to bottom, #FFFFFF, #F1F1F1);
				background-repeat: repeat-x;
				border-radius: 50% 50% 50% 50%;
				box-shadow: -1px 0 2px rgba(0, 0, 0, 0.6);
				cursor: pointer;
				display: block;
				height: 18px;
				left: 0;
				position: absolute;
				top: 0;
				width: 18px;
			}
			
			.uiLogin .iPhoneCheckHandle {
				cursor: pointer;
			}
			
			.uiLogin .rememberTxt{
				float: left;
				font-size: 13px;
				padding-left: 12px;
				color: #707070;
			}

		</style>

    <script type="text/javascript">
        var require = {
            "shim":{
                "SHARED/iphonePlugin":{"deps":["SHARED/jquery"]}
            },
            "paths":{
                "SHARED/jquery":"/portal/scripts/3.5.2.Final/SHARED/jquery-min",
                "SHARED/iphonePlugin":"/portal/scripts/3.5.2.Final/SHARED/iphonePlugin-min"
            }
        };
    </script>
    <script type="text/javascript" src="/portal/scripts/3.5.2.Final/SHARED/bootstrap-min.js"></script>

  </head>
  <body>
    <div class="uiLogin">
      <div class="loginHeader introBox">
		<div class="userLoginIcon">Connect to your account</div>
	  </div>
      <div class="loginContent">
				<div style="line-height: 12px; padding: 6px 3px 0 0; height: 27px; font-size: 11px;">
					<%/*Begin form*/%>
          <%
                if(error) {
          %>
          <font color="red"><%=res.getString("UILoginForm.label.SigninFail")%></font><%}%>
				</div>
        <div class="centerLoginContent">
          <form name="loginForm" action="<%= contextPath + "/login"%>" method="post" style="margin: 0px;">
                <% if (uri != null) { 
                        uri = EntityEncoder.FULL.encode(uri);
                %>
          		<input type="hidden" name="initialURI" value="<%=uri%>"/>
                <% } %>	
					
				<input class="username" id="username" name="username" type="text" value="Username"/>
				<input class="password" id="UIPortalLoginFormControl" onkeypress="//eXo.portal.UIPortalControl.onEnterPress(event);"  type="password" id="password" name="password" value="......"/>
                <div class="spaceRole">
					<input type="checkbox" class="yesno" checked="checked" style="visibility: hidden;" id="rememberme" name="rememberme" value="true"/>
					<label for="rememberme"><%=res.getString("UILoginForm.label.RememberOnComputer")%></label>
				</div>
                <script type="text/javascript">
                    require(["SHARED/jquery", "SHARED/iphonePlugin"], function (gj) {
                        gj("div.spaceRole").children('input:checkbox').each(function () {
                            gj(this).iphoneStyle({
                                checkedLabel:'YES',
                                uncheckedLabel:'NO'});
                        });
                    });
                </script>

				<div id="UIPortalLoginFormAction" class="loginButton" onclick="login();">
					<button class="button" href="#"><%=res.getString("UILoginForm.label.Signin")%></button>
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
    <div style="font-size: 12px; color: #c8c8c8; text-align: center">Copyright &copy; 2010 eXo Platform SAS, all rights reserved.</div>
  </body>
</html>
