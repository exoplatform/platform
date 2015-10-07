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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.exoplatform.platform.common.software.register.UnlockService" %>
<%@ page import="org.exoplatform.platform.common.account.setup.web.PingBackServlet" %>


<%@ page import="org.exoplatform.portal.resource.SkinService"%>
<%@ page import="org.exoplatform.container.PortalContainer"%>
<%
  String contextPath = request.getContextPath();
  String lang = request.getLocale().getLanguage();
  response.setCharacterEncoding("UTF-8");
  response.setContentType("text/html; charset=UTF-8");
  boolean isRegisted = UnlockService.isRegisted();
  boolean canSKip = UnlockService.canSkipRegister();

  String registrationURL = request.getServletContext().getAttribute("registrationURL").toString();
   String errorCode = request.getParameter("error");
  //
  SkinService skinService = (SkinService) PortalContainer.getCurrentInstance(session.getServletContext()).getComponentInstanceOfType(SkinService.class);
  String cssPath = skinService.getSkin("portal/SoftwareRegistration", "Default").getCSSPath();
%>
<html>
<head>
  <title>Register your Software</title>
  <!-- -->
  <link href="<%=cssPath%>" rel="stylesheet" type="text/css"/>
  <script type="text/javascript" src="/platform-extension/javascript/jquery-1.7.1.js"></script>
  <script type="text/javascript" src="/registrationPLF/javascript/registration/software-registration.js"></script>
</head>
<body> 

  <div class="UIPopupWindow uiPopup UIDragObject popupDarkStyle">
    <div class="popupHeader ClearFix">
        <span class="popupTitle center">Register your Software</span>
    </div>
    <div class="popupContent">
      <%@include file="PLFRegistrationIntro.jsp"%> 
      <% if(errorCode!=null){ %>
      <div class="alert alert-warning"><i class="uiIconWarning"></i>The registration process has been cancelled.   Please try again or contact the <a href="http://support.exoplatform.com"> support.</a></div>
      <%}%>
      <div class="signin-regis-title" ><strong>Sign in and register your installation on the Tribe</strong></div>
      <img src="/eXoSkin/skin/images/themes/default/platform/portlets/extensions/tribe1.png" class="img-responsive imgNoInternet"/>
      <img src="/eXoSkin/skin/images/themes/default/platform/portlets/extensions/tribe2.png" class="img-responsive imgHasInternet"/>
      <div class="not-connected" >
        <div class="text-center"><strong>Well, about that...</strong></div>
        <div class="text-center">It seems we cannot reach the eXo Tribe at the moment, You can skip this step and register your software at the next start</div>
      </div>
      <div class="signin-title"><strong>Sign in to the eXo Tribe:</strong></div>
      <div class="loading-text"></div>
      <div class="plf-registration" >
        
        <form id="frmSoftwareRegistration" action="<%=contextPath+"/software-register-action"%>" method="post">
          
          <div class="uiAction" id="UIPortalLoginFormAction">
            <input class="btn" type="hidden" name="value" value="<%=session.getAttribute("notReacheble")%>"/>
            <a class="registrationURL btn btn-primary" href="<%=registrationURL%>">Register your software</a>
            <input class="btn btn-primary" type="button" name="btnContinue" value="Continue"/>
            <input class="btn" type="button" name="btnSkip" value="Skip" <%if(!canSKip){%>disabled="disabled<%}%> />
          </div>
          
        </form>
      </div>

    </div>
  </div>
  
  
</body>
</html>
