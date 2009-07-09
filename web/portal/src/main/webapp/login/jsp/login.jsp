<%@ page import="java.net.URLEncoder"%>
<%@ page import="javax.servlet.http.Cookie"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<%@ page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="org.exoplatform.web.login.InitiateLoginServlet"%>
<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%
  String contextPath = request.getContextPath() ;

  String username = (String)request.getParameter("j_username");
  if(username == null) username = "";
 	String password = (String)request.getParameter("j_password");
 	if(password == null) password = "";

  ResourceBundleService service = (ResourceBundleService)ExoContainerContext.getContainerByName("portal")
  														.getComponentInstanceOfType(ResourceBundleService.class);
  ResourceBundle res = service.getResourceBundle(service.getSharedResourceBundleNames(), request.getLocale()) ;
  
  Cookie cookie = new Cookie(InitiateLoginServlet.COOKIE_NAME, "");
	cookie.setPath(request.getContextPath());
	cookie.setMaxAge(0);
	response.addCookie(cookie);
%>
<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Login</title>
    <link rel="shortcut icon" type="image/x-icon"  href="/portal/favicon.ico" />
    <link rel='stylesheet' type='text/css' href='<%=contextPath%>/login/skin/Stylesheet.css'/>
    <script type="text/javascript" src="/eXoResources/javascript/eXo.js"></script>
    <script type="text/javascript" src="/eXoResources/javascript/eXo/portal/UIPortalControl.js"></script>
  </head>
  <body style="text-align: center; background: #f5f5f5; font-family: arial, tahoma, verdana">
    <div class="UILogin">
      <div class="LoginHeader">Sign In</div>
      <div class="LoginContent">
        <div class="WelcomeText">Welcome to eXo Portal</div>
        <div class="CenterLoginContent">
          <%/*Begin form*/%>
          <%
            if(username.length() > 0 || password.length() > 0) {
          %>
            <font color="red">Sign in failed. Wrong username or password.</font><%}%>
          <form name="loginForm" action="<%= contextPath + "/login"%>" method="post" style="margin: 0px;">    
          		<input type="hidden" name="uri" value="<%=session.getAttribute("initialURI") %>"/>   
          		<table> 
	              <tr class="FieldContainer">
		              <td class="FieldLabel"><%=res.getString("UILoginForm.label.UserName")%></td>
		              <td><input class="UserName" name="username" value="<%=username%>"/></td>
			          </tr>
		            <tr class="FieldContainer" id="UIPortalLoginFormControl" onkeypress="eXo.portal.UIPortalControl.onEnterPress(event);">
		              <td class="FieldLabel"><%=res.getString("UILoginForm.label.password")%></td>
		              <td><input class="Password" type="password" name="password" value=""/></td>
		            </tr>
		            <tr class="FieldContainer">
		              <td class="FieldLabel"><input type="checkbox" name="rememberme" value="true"/></td>
		              <td><%=res.getString("UILoginForm.label.RememberOnComputer")%></td>
		            </tr>
		          </table>
		          <div id="UIPortalLoginFormAction" class="LoginButton" onclick="login();">
		            <div class="LoginButtonContainer">
		              <div class="Button">
		                <div class="LeftButton">
		                  <div class="RightButton">
		                    <div class="MiddleButton">
		                    	<a href="#"><%=res.getString("UILoginForm.label.Signin")%></a>
		                    </div>
		                  </div>
		                </div>
		              </div>
		            </div>
		          </div>
		          <div class="ClearLeft"><span></span></div>
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
    <span style="margin: 10px 0px 0px 5px; font-size: 11px; color: #6f6f6f; text-align: center">Copyright &copy 2000-2009. All rights Reserved, eXo Platform SAS.</span>
  </body>
</html>