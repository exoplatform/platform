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
<%@ page import="org.exoplatform.services.organization.impl.UserImpl" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="org.exoplatform.portal.resource.SkinConfig" %>
<%@ page import="java.util.Collection" %>
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

    User user = (User)request.getAttribute("portalUser");
    if (user == null) {
        user = new UserImpl();
    }

    List<String> errors = (List<String>)request.getAttribute("register_errors");
    Set<String> errorFields = (Set<String>)request.getAttribute("register_error_fields");
    if (errors == null) {
        errors = new ArrayList<String>();
    }
    if (errorFields == null) {
        errorFields = new HashSet<String>();
    }
    String errorClass = "error";

    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Oauth register</title>
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
        <div class="uiFormWithTitle uiBox uiOauthRegister">
            <h5 class="title"><%=res.getString("UIRegisterForm.title")%></h5>
            <div class="uiContentBox">
                <% if (errors.size() > 0) { %>
                <div class="error">
                    <ul>
                        <% for (String error : errors) { %>
                        <li><%=error%></li>
                        <%}%>
                    </ul>
                </div>
                <%}%>
                <form name="registerForm" action="<%= contextPath + "/login"%>" method="post" style="margin: 0px;">
                    <div class="content">
                        <div class="form-horizontal">
                            <div class="control-group">
                                <label class="control-label"><%=res.getString("UIRegisterForm.label.username")%></label>
                                <div class="controls">
                                    <input class="username <%if(errorFields.contains("username")) out.print(errorClass);%>" name="username" type="text" value="<%=(user.getUserName() == null ? "" : user.getUserName())%>" />
                                </div>
                            </div>

                            <div class="control-group">
                                <label class="control-label"><%=res.getString("UIRegisterForm.label.password")%></label>
                                <div class="controls">
                                    <input class="password <%if(errorFields.contains("password")) out.print(errorClass);%>" name="password" type="password" />
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label"><%=res.getString("UIRegisterForm.label.confirmPassword")%></label>
                                <div class="controls">
                                    <input class="password <%if(errorFields.contains("password2")) out.print(errorClass);%>" name="password2" type="password" />
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label"><%=res.getString("UIRegisterForm.label.firstName")%></label>
                                <div class="controls">
                                    <input type="text" class="<%if(errorFields.contains("firstName")) out.print(errorClass);%>" name="firstName" value="<%=(user.getFirstName() == null ? "" : user.getFirstName())%>"/>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label"><%=res.getString("UIRegisterForm.label.lastName")%></label>
                                <div class="controls">
                                    <input type="text" class="<%if(errorFields.contains("lastName")) out.print(errorClass);%>" name="lastName" value="<%=(user.getLastName() == null ? "" : user.getLastName())%>"/>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label"><%=res.getString("UIRegisterForm.label.displayName")%></label>
                                <div class="controls">
                                    <input type="text" class="<%if(errorFields.contains("displayName")) out.print(errorClass);%>" name="displayName" value="<%=(user.getDisplayName() == null ? "" : user.getDisplayName())%>"/>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label"><%=res.getString("UIRegisterForm.label.emailAddress")%></label>
                                <div class="controls">
                                    <input type="email" class="<%if(errorFields.contains("email")) out.print(errorClass);%>" name="email" value="<%=(user.getEmail() == null ? "" : user.getEmail())%>" />
                                </div>
                            </div>

                            <input type="hidden" name="login_controller" value="submit_register"/>
                        </div>
                    </div>
                    <div id="UIPortalLoginFormAction" class="uiAction">
                        <button type="submit" class="btn btn-primary"><%=res.getString("UIRegisterForm.action.SubscribeOAuth")%></button>
                        <button type="reset" class="btn ActionButton LightBlueStyle"><%=res.getString("UIRegisterForm.action.Reset")%></button>
                        <a class="btn ActionButton LightBlueStyle" href="<%= contextPath + "/login?login_controller=oauth_cancel"%>"><%=res.getString("UIRegisterForm.action.Cancel")%></a>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
