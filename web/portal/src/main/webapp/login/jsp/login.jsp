<%@ page import="java.net.URLEncoder"%>
<%@ page import="javax.servlet.http.Cookie"%>
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
  if (userName == null || userName.length() == 0) showForm = true ;
  if (password == null || password.length() == 0) showForm  = true ;
  if(!showForm) {
    password = URLEncoder.encode(password) ;
    response.sendRedirect(loginAction + "?j_username=" + userName + "&j_password=" + password ) ;
    return ;
  }
  
%>
<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Login</title>
    <link rel='stylesheet' type='text/css' href='<%=contextPath%>/login/skin/Stylesheet.css'/>
    <script type="text/javascript" src="/eXoResources/javascript/eXo.js"></script>
    <script type="text/javascript" src="/eXoResources/javascript/eXo/portal/UIPortalControl.js"></script>
  </head>
  <body style="text-align: center">
    <div class="UILoginPortlet">
      <div class="LoginHeader"><span></span></div>
      <div class="LoginContent">
        <div class="LeftLoginContent">
          <div class="RightLoginContent">
            <div class="CenterLoginContent">
              <%/*Begin form*/%>
              <form name="loginForm" action="<%=loginAction%>">        
	              <div class="FieldContainer">
		              <label>User name:</label><input name="j_username" value=""/>
			          </div>
		            <div class="FieldContainer" id="UIPortalLoginFormControl">
		              <label>Password:</label><input type="password" name="j_password" value=""/>
		            </div>
			            
		            <a class="TextHelp" href="#">Forgot Your User Name/Password?</a>
			          <div class="OverflowContainer">  
			            <input type="checkbox"/>
			            <div class="ForgotMessage">Remember my login on this computer</div>
			          </div>  
			          <div class="LoginButton">
			            <div class="LoginButtonContainer">
			              <div class="Button">
			                <div class="LeftButton">
			                  <div class="RightButton">
			                    <div class="MiddleButton"><a href="javascript:login();" id="UIPortalLoginFormAction">Login</a></div>
			                  </div>
			                </div>
			              </div>
			            </div>
			          </div>
			          <div style="clear: left"><span></span></div>
			          <script type='text/javascript'>			            
                  function login() {
                    document.loginForm.submit();                   
                  }
                </script>
			        </form>
			        <%/*End form*/%>
		          <div class="MessageContainer">
		            <div class="SignupMessage">Not a member? 
		              <a href="/portal/public/site/?portal:componentId=UIPortal&portal:action=LoadPage&pageId=portal::site::register">
		                Signup
		              </a> 
		              for an account
	              </div>
		          </div>			         
            </div>
          </div>
        </div>
      </div>
      <%/*Begin LoginFooter*/%>
      <div class="LoginFooter">
        <div class="LoginLeftFooter">
          <div class="LoginRightFooter">
            <div class="LoginRepeatFooter"><span></span></div>
          </div>
        </div>
      </div>
      <%/*End LoginFooter*/%>
      <div class="CopyrightInfo">Copyright &copy 2000-2007. All rights Reserved, eXo Platform SAS.</div>
    </div>
    <script type='text/javascript'>
      eXo.portal.UIPortalControl.onKeyPress();
    </script>
  </body>
</html>
