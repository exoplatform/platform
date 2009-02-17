<%@ page import="java.net.URLEncoder"%>
<%@ page import="javax.servlet.http.Cookie"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<%@ page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page language="java" %>
<%@ page contentType="text/html" %>
<%
  String userName = (String) session.getAttribute("authentication.username") ;
  request.getSession().removeAttribute("authentication.username");
  String password = (String) session.getAttribute("authentication.password") ;
  request.getSession().removeAttribute("authentication.password");
  String contextPath  =  request.getContextPath() ;
  String loginAction = contextPath  + "/j_security_check" ; 
  Cookie[] cookies = request.getCookies();
  if (cookies != null && (userName == null || userName.length() == 0)) {
    for (int i = 0; i< cookies.length; i++) {
      if("authentication.username".equals(cookies[i].getName())) {
        userName = cookies[i].getValue();
      } else if("authentication.password".equals(cookies[i].getName())) {
        password = cookies[i].getValue();
      }
    }
  }
  boolean showForm = false ;
  if (userName == null || userName.length() == 0) {
    userName = request.getParameter("j_username");
    if(userName == null) userName = "";
    showForm = true ;
  }  
  if (password == null || password.length() == 0) {
  	password = request.getParameter("j_password");
    if(password == null) password = "";
    showForm = true ;  	
  }
  if(!showForm) {
    password = URLEncoder.encode(password) ;
    response.sendRedirect(loginAction + "?j_username=" + userName + "&j_password=" + password ) ;
    return ;
  }
  ResourceBundleService service = (ResourceBundleService)ExoContainerContext.getContainerByName("portal")
  														.getComponentInstanceOfType(ResourceBundleService.class);
  ResourceBundle res = service.getResourceBundle(service.getSharedResourceBundleNames(), request.getLocale()) ;
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
      <div class="LoginHeader">Sign in</div>
      <div class="LoginContent">
        <div class="WelcomeText">Welcome to eXo Portal</div>
        <div class="CenterLoginContent">
          <%/*Begin form*/%>
          <%
            if(userName.length() > 0 || password.length() > 0) {
          %>
            <font color="red">Sign in failed. Wrong username or password.</font><%}%>
          <form name="loginForm" action="<%=loginAction%>" method="post" style="margin: 0px;">        
              <div class="FieldContainer">
	              <label><%=res.getString("UILoginForm.label.UserName")%></label><input class="UserName" name="j_username" value="<%=userName%>"/>
		          </div>
	            <div class="FieldContainer" id="UIPortalLoginFormControl" onkeypress="eXo.portal.UIPortalControl.onEnterPress(event);">
	              <label><%=res.getString("UILoginForm.label.password")%></label><input class="Password" type="password" name="j_password" value=""/>
	            </div>
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
