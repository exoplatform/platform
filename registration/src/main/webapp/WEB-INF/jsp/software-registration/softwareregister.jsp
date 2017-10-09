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
<%@ page import="org.exoplatform.platform.common.software.register.service.SoftwareRegistrationService" %>

<%@ page import="org.exoplatform.portal.resource.SkinService"%>
<%@ page import="org.exoplatform.container.PortalContainer"%>
<%@ page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.exoplatform.portal.config.UserPortalConfigService" %>
<%
  PortalContainer portalContainer = PortalContainer.getCurrentInstance(session.getServletContext());

  String contextPath = request.getContextPath();
  String lang = request.getLocale().getLanguage();
  response.setCharacterEncoding("UTF-8");
  response.setContentType("text/html; charset=UTF-8");
  SoftwareRegistrationService registrationService
          = portalContainer.getComponentInstanceOfType(SoftwareRegistrationService.class);
  boolean canSKip = registrationService.canShowSkipBtn();

  String registrationURL = (String)request.getAttribute("registrationURL");

  String notReachable = (String)session.getAttribute("notReachable");
   String errorCode = request.getParameter("error");

  UserPortalConfigService userPortalConfigService = portalContainer.getComponentInstanceOfType(UserPortalConfigService.class);
  SkinService skinService = portalContainer.getComponentInstanceOfType(SkinService.class);
  String skinName = userPortalConfigService.getDefaultPortalSkinName();
  String cssPath = skinService.getSkin("portal/SoftwareRegistration", skinName).getCSSPath();

  ResourceBundleService service = (ResourceBundleService) portalContainer.getComponentInstanceOfType(ResourceBundleService.class);
  ResourceBundle rb = service.getResourceBundle("locale.portal.webui", request.getLocale());

%>
<html>
<head>
  <title><%=rb.getString("SoftwareRegister.label.register")%></title>
  <!-- -->
  <meta content="text/html; charset=UTF-8" http-equiv="Content-Type">
  <meta content="width=device-width, initial-scale=1.0" name="viewport">
  <link href="<%=cssPath%>" rel="stylesheet" type="text/css"/>
  <script type="text/javascript" src="/eXoResources/javascript/jquery-3.2.1.js"></script>
  <script type="text/javascript" src="/registration/javascript/registration/software-registration.js"></script>
</head>
<body class="modal-open">
  <div class="UIPopupWindow uiPopup UIDragObject popupDarkStyle">
    <div class="popupHeader ClearFix">
        <span class="popupTitle center"><%=rb.getString("SoftwareRegister.label.register")%></span>
    </div>
    <div class="popupContent">
      <%@include file="PLFRegistrationIntro.jsp"%> 
      <% if(errorCode!=null){ %>
      <div class="alert alert-warning"><i class="uiIconWarning"></i><%=rb.getString("SoftwareRegister.label.warning").replaceAll("\\{1}", rb.getString("SoftwareRegister.label.warning_cancelled"))%> <a href="http://support.exoplatform.com"> <%=rb.getString("SoftwareRegister.label.support")%></a></div>
      <%}%>
      <%if("true".equals(notReachable)){%>
        <div class="alert alert-error"><i class="uiIconError"></i><%=rb.getString("SoftwareRegister.label.warning").replaceAll("\\{1}",rb.getString("SoftwareRegister.label.warning_not_complete"))%> <a href="http://support.exoplatform.com"> <%=rb.getString("SoftwareRegister.label.support")%></a></div>
      <% session.removeAttribute("notReachable"); }%>
      <div class="signin-regis-title" style="display:none;"><strong><%=rb.getString("SoftwareRegister.label.sign_in_regist")%></strong></div>
      <img src="/eXoSkin/skin/images/themes/default/platform/portlets/extensions/tribe1.png" class="img-responsive imgNoInternet" style="display: none;"/>
      <img src="/eXoSkin/skin/images/themes/default/platform/portlets/extensions/tribe2.png" class="img-responsive imgHasInternet" style="display: none;" />
      <div class="not-connected" style="display: none;">
        <div class="text-center"><strong><%=rb.getString("SoftwareRegister.label.about_that")%></strong></div>
        <div class="text-center"><%=rb.getString("SoftwareRegister.label.cannot_reach")%></div>
      </div>
      <div class="signin-title"><strong><%=rb.getString("SoftwareRegister.label.sign_in")%>:</strong></div>
      <div class="loading-text"></div>
      <div class="plf-registration">
        
        <form id="frmSoftwareRegistration" action="<%=contextPath+"/software-register-action"%>" method="post">
          
          <div class="uiAction" id="UIPortalLoginFormAction">
            <input class="btn" type="hidden" name="value" value="<%if("true".equals(notReachable)){%>notReachable<%}%>"/>
            <a class="registrationURL btn btn-primary" href="<%=registrationURL%>" style="display: none;" ><%=rb.getString("SoftwareRegister.label.register")%></a>
            <input class="btn btn-primary" type="button" name="btnContinue" value="<%=rb.getString("SoftwareRegister.label.continue")%>" disabled="disabled" />
            <input class="btn" type="button" name="btnSkip" value="<%=rb.getString("SoftwareRegister.label.skip")%>" <%if(!canSKip && !"true".equals(notReachable)){%>disabled="disabled"<%}%> />
          </div>
          
        </form>
      </div>

    </div>
  </div>
  
  
</body>
</html>
