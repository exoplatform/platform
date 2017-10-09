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
<%@ page import="org.exoplatform.portal.resource.SkinService"%>
<%@ page import="org.exoplatform.container.PortalContainer"%>
<%@ page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@ page import="org.exoplatform.portal.config.UserPortalConfigService" %>
<%@ page import="java.util.ResourceBundle" %>
<%
  String contextPath = request.getContextPath();
  String status = request.getServletContext().getAttribute("status").toString();
  String lang = request.getLocale().getLanguage();
  response.setCharacterEncoding("UTF-8");
  response.setContentType("text/html; charset=UTF-8");

  PortalContainer portalContainer = PortalContainer.getCurrentInstance(session.getServletContext());

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
  <meta content="text/html; charset=UTF-8" http-equiv="Content-Type">
  <meta content="width=device-width, initial-scale=1.0" name="viewport">
  <link href="<%=cssPath%>" rel="stylesheet" type="text/css"/>
  <script type="text/javascript" src="/eXoResources/javascript/jquery-3.2.1.js"></script>
  <script type="text/javascript" src="/registration/javascript/registration/software-registration.js"></script>

</head>
<body  class="modal-open">
  <div class="UIPopupWindow uiPopup UIDragObject popupDarkStyle">
    <div class="popupHeader ClearFix">
        <span class="popupTitle center"><%=rb.getString("SoftwareRegister.label.register")%></span>
    </div> 
    <div class="popupContent">
      <%@include file="PLFRegistrationIntro.jsp"%>

      <div class="alert alert-success"><i class="uiIconSuccess"></i><strong><%=rb.getString("SoftwareRegisterSuccess.label.thanks")%></strong> <%=rb.getString("SoftwareRegisterSuccess.label.successfully_registered")%></div>
      <img src="/eXoSkin/skin/images/themes/default/platform/portlets/extensions/tribe3.png" class="img-responsive"/>
      <div class="uiAction">
        <form id="frmSoftwareRegistration" action="<%=contextPath+"/software-register-action"%>" method="post">
          <input class="btn btn-primary" type="button" name="btnContinue" value="<%=rb.getString("SoftwareRegister.label.continue")%>"/>
        </form>
      </div>
    </div>
  </div>
</body>
</html>
