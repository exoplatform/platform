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
  String portalName = contextPath.substring(1, contextPath.length()) ;
  String loginAction = contextPath  + "/j_security_check" ; 
    
  Cookie[] cookies = request.getCookies();
  Cookie cookie1 = null;
  Cookie cookie2 = null;
  if (cookies != null) {
    for (int i = 0; i< cookies.length; i++) {
      Cookie ele = cookies[i];
      if(ele.getName().equals("authentication.username")) {
        cookie1 = ele;
        userName = ele.getValue();
      }
      if(ele.getName().equals("authentication.password")) {
        cookie2 = ele;
        password = ele.getValue();
      }
    }
  }
 
  
  if(!(userName.equals("null") || password.equals("null"))) {
    response.sendRedirect(loginAction + "?j_username=" + userName + "&j_password=" + password ) ;
    return ;
  }

  boolean showForm = false ;  
  if (userName == null || userName.length() == 0  || userName.equals("null")) showForm = true ;
  if (password == null || password.length() == 0 || password.equals("null")) showForm  = true ;
  if(!showForm) {
    password = password + "@" + portalName ;
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
    <link rel='stylesheet' type='text/css' href='<%=contextPath%>/login/skin/Stylesheet.css' />
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
		              <label>User name:</label>
		              <input name="j_username" value="" />
			          </div>
		            <div class="FieldContainer">
		              <label>Password:</label>
		              <input type="password" name="j_password" value="" />
		            </div>
			            
		            <div class="TextHelp">Forgot Your User Name/Password?</div>
			          <div class="OverflowContainer">  
			            <input type="checkbox" />
			            <div class="ForgotMessage">Remember my login on this computer</div>
			          </div>  
			          <div class="LoginButton">
			            <div class="LoginButtonContainer">
			              <div class="Button">
			                <div class="ButtonLeft">
			                  <div class="ButtonRight">
			                    <div class="ButtonMiddle"><a href="javascript:login();">Login</a></div>
			                  </div>
			                </div>
			              </div>
			            </div>
			          </div>
			          <div style="clear: left"><span></span></div>
			          <script type='text/javascript'>
			            function set_Cookie( name, value, expires, secure ) {
										// set time, it's in milliseconds
										var today = new Date();
										today.setTime( today.getTime() );
										if ( expires )
										{
											expires = expires * 1000 * 60 * 60 * 24;
										}
										var expires_date = new Date( today.getTime() + (expires) );
									
										document.cookie = name + "=" +escape( value ) +
											( ( expires ) ? ";expires=" + expires_date.toGMTString() : "" ) + //expires.toGMTString()
											( ( secure ) ? ";secure" : "" );
								  }
			          
                  function login() {
                    document.loginForm.elements['j_password'].value =
                      document.loginForm.elements['j_password'].value + "@<%=portalName%>"  ;
                    set_Cookie("authentication.username", document.loginForm.elements['j_username'].value, 30, true);
                    set_Cookie("authentication.password", document.loginForm.elements['j_password'].value, 30, true);
                    document.loginForm.submit();
                   
                  }
                </script>
			        </form>
			        <%/*End form*/%>
		          <div class="MessageContainer">
		            <div class="SignupMessage">Not a member? <a href="#">Signup</a> for an account</div>
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
      <div class="CopyrightInfo">Copyright &copy; 2000-2006. Allrights Reserved eXo Platform SAS </div>
    </div>
  </body>
</html>
