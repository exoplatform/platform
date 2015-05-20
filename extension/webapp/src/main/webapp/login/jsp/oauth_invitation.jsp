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

<%@ page import="org.exoplatform.container.PortalContainer"%>
<%@ page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@ page import="org.exoplatform.portal.resource.SkinService"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="org.exoplatform.services.organization.User"%>
<%@ page import="java.util.Collection" %>
<%@ page import="org.exoplatform.portal.resource.SkinConfig" %>
<%@ page import="org.exoplatform.portal.resource.SkinVisitor" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.exoplatform.portal.resource.SkinKey" %>
<%@ page language="java" %>
<%
    PortalContainer portalContainer = PortalContainer.getCurrentInstance(session.getServletContext());
    ResourceBundleService service = portalContainer.getComponentInstanceOfType(ResourceBundleService.class);
    ResourceBundle res = service.getResourceBundle(service.getSharedResourceBundleNames(), request.getLocale()) ;
    String contextPath = portalContainer.getPortalContext().getContextPath();

    SkinService skinService = PortalContainer.getCurrentInstance(session.getServletContext())
            .getComponentInstanceOfType(SkinService.class);

    Collection<SkinConfig> skins = skinService.getPortalSkins("Default");
    String loginCssPath = skinService.getSkin("portal/login", "Default").getCSSPath();

    User detectedUser = (User)request.getAttribute("detectedUser");

    String error = (String)request.getAttribute("invitationConfirmError");

    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Oauth invitation</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link rel="shortcut icon" type="image/x-icon"  href="<%=contextPath%>/favicon.ico" />
        <% for (SkinConfig skin : skins) {
            if ("CoreSkin".equals(skin.getModule()) || "CoreSkin1".equals(skin.getModule())) {%>
                <link href="<%=skin.getCSSPath()%>" rel="stylesheet" type="text/css" test="<%=skin.getModule()%>"/>
            <%}%>
        <%}%>
        <link href="<%=loginCssPath%>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="/platform-extension/javascript/jquery-1.7.1.js"></script>
        <script type="text/javascript" src="/platform-extension/javascript/switch-button.js"></script>
    </head>
    <body>
        <div class="uiFormWithTitle uiBox uiOauthInvitation">
            <h5 class="title"><%= res.getString("UIOAuthInvitationForm.title") %></h5>
            <div class="uiContentBox">
                <form name="registerForm" action="<%= contextPath + "/login"%>" method="post" style="margin: 0px;">
                    <div class="content">
                        <p><%=res.getString("UIOAuthInvitationForm.message.detectedUser")%><br/><strong><%=detectedUser.getUserName()%>/<%= detectedUser.getEmail() %></strong></p>
                        <p><%=res.getString("UIOAuthInvitationForm.message.inviteMessage")%></p>
                        <% if (error != null) { %>
                        <p class="error"><%=error%></p>
                        <%}%>
                        <p class="clearfix">
                            <label><%=res.getString("UIOAuthInvitationForm.label.password")%></label>
                            <span class="pull-right">
                                <input class="password <%=(error != null ? "error" : "")%>" type="password" name="password" placeholder="<%=res.getString("portal.login.Password")%>" onblur="this.placeholder = '<%=res.getString("portal.login.Password")%>'" onfocus="this.placeholder = ''"/>
                            </span>
                            <input type="hidden" name="login_controller" value="confirm_account"/>
                        </p>
                    </div>
                    <div class="uiAction uiActionBorder">
                        <button type="submit" class="btn btn-primary"><%=res.getString("portal.login.Confirm")%></button>
                        <a class="btn ActionButton LightBlueStyle" href="<%= contextPath + "/login?login_controller=register"%>"><%=res.getString("portal.login.CreateNewAccount")%></a>
                        <a class="btn ActionButton LightBlueStyle" href="<%= contextPath + "/login?login_controller=oauth_cancel"%>"><%=res.getString("portal.login.cancel")%></a>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
