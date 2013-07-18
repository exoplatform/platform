<%@ page import="org.exoplatform.platform.common.account.setup.web.PingBackServlet" %>
<%
    /**
     * Copyright ( C ) 2012 eXo Platform SAS.
     *
     * This is free software; you can redistribute it and/or modify it
     * under the terms of the GNU Lesser General Public License as
     * published by the Free Software Foundation; either version 2.1 of
     * the License, or (at your option) any later version.
     *
     * This software is distributed in the hope that it will be useful,
     * but WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
     * Lesser General Public License for more details.
     *
     * You should have received a copy of the GNU Lesser General Public
     * License along with this software; if not, write to the Free
     * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
     * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
     */
%>
<%@ page language="java" %>
<%
    String usernameRegExp = System.getProperty("gatein.validators.username.regexp");
    if(usernameRegExp==null) usernameRegExp="";
    String formatMsg = System.getProperty("gatein.validators.username.format.message");
    String usernameMinLength = System.getProperty("gatein.validators.username.length.min");
    String usernameMaxLength = System.getProperty("gatein.validators.username.length.max");
    int max=0;
    int min=0;
    if(usernameMaxLength!=null) {
        max = Integer.parseInt(usernameMaxLength);
    }
    if(usernameMinLength!=null) {
        min = Integer.parseInt(usernameMinLength);
    }
    String contextPath = request.getContextPath() ;
    String lang = request.getLocale().getLanguage();
    response.setCharacterEncoding("iso-8859-1");
    response.setContentType("text/html; charset=iso-8859-1");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="<%=lang%>">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <link href="/platform-extension/css/welcome-screens/jquery.qtip.min.css" rel="stylesheet" type="text/css" />
    <link href="/platform-extension/css/welcome-screens/Style.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="/platform-extension/javascript/jquery-1.7.1.js"></script>
    <script type="text/javascript" src="/platform-extension/javascript/ie-placeholder.js"></script>
    <script type="text/javascript" src="/platform-extension/javascript/welcome-screens/welcomescreens.js"></script>

<head>
<div class="backLight"></div>
<div class="uiWelcomeBox" id="AccountSetup1"  >
    <div class="header">Account Setup</div>
    <div class="content form-horizontal" id="AccountSetup">
        <h5>Create your account</h5>
        <p class="desc">This will be your primary user account</p>
        <form name="tcForm" action="<%= contextPath + "/accountSetupAction"%>" method="post">
            <div class="control-group" id ="usernameId">
                <label class="control-label">Username:</label>
                <div class="controls">
                    <input type="text" name="username" id="userNameAccount" placeholder="User name" class="inputFieldLarge"/>
                    <input type="hidden" id="usernameRegExpid" value="<%=usernameRegExp%>"/>
                    <input type="hidden" id="formatMsgid" value="<%=formatMsg%>"/>
                    <input type="hidden" id="usernameMaxLengthid" value="<%=max%>"/>
                    <input type="hidden" id="usernameMinLengthid" value="<%=min%>"/>
                </div>
            </div>
            <div class="control-group" id="fullnameId">
                <label class="control-label">Full name:</label>
                <div class="controls"><input type="text" name="firstNameAccount" id="firstNameAccount" placeholder="First name" class="inputFieldMedium"/><input type="text" name="lastNameAccount" id="lastNameAccount" placeholder="Last name" class="inputFieldMedium" /></div>
            </div>
            <div class="control-group" id="emailId">
                <label class="control-label">Email:</label>
                <div class="controls"><input type="text" name="emailAccount" id="emailAccount" class="inputFieldLarge" /></div>
            </div>
            <div class="control-group" id="passwordId">
                <label class="control-label">Password:</label>
                <div class="controls"><input type="password" name="password" id="userPasswordAccount" class="inputFieldMini" /><span class="confirmLabel">Confirm:</span><input type="password" name="confirmUserPasswordAccount" id="confirmUserPasswordAccount" class="inputFieldMini" />
                </div>
            </div>

            <h5>Admin Password</h5>
            <p class="desc">Login as root user with the following password for super administrator access</p>
            <div class="control-group" id="adminUsernameId">
                <label class="control-label">Username:</label>
                <div class="controls"><input type="text" name="adminFirstName" id="adminFirstName" placeholder="root" readonly="readonly" class="inputFieldLarge disable" /></div>
            </div>
            <div class="control-group" id="adminPasswordId">
                <label class="control-label">Password:</label>
                <div class="controls">
                    <input type="password" name="adminPassword" id="adminPassword" class="inputFieldMini" /><span class="confirmLabel">Confirm:</span><input type="password" name="confirmAdminPassword" id="confirmAdminPassword" class="inputFieldMini" />
                </div>
            </div>
    </div>
    <!-- Please do not make it Button it may cause blocker problem -->
    <div class="bottom"><button type="button" class="btn btn-primary" id="continueButton" onclick="WelcomeScreens.exit();return false;">Submit</button></div>
</div>
</div>
<div>
    <!--	<div class="backLight"></div>    -->
    <div class="uiWelcomeBox" id="Greetings" style="display: none">
        <div class="header">Greetings !</div>
        <div class="content form-horizontal" id="AccountSetup">
            <p>
                <strong>You are almost done</strong>, add your colleagues to your new social
                intranet and start collaborating together.
            </p>
            <div class="screenShot"><a href="javascript:void(0);"><img src="/platform-extension/css/welcome-screens/images/greetingSS.png" alt="" width="404" height="172"/></a></div>
        </div>
        <div class="bottom"><button type="button class="btn btn-primary" >Start</button></div>
    </div>
</div>
</form>
<% 	if(!PingBackServlet.isLandingPageDisplayed()){ %>
<iframe src="<%=PingBackServlet.getPingBackUrl()%>" style="display:none;" id="pingBackUrlFrame" onload="setFormDisplayed()"></iframe>
<iframe src="about:blank" style="display:none;" id="pingBackUrlActivation"></iframe>
<script>
    function setFormDisplayed() {
        var pingBackUrlActivationElement = document.getElementById("pingBackUrlActivation");
        pingBackUrlActivationElement.src="/platform-extension/PingBackServlet";
    }
</script>
<% }
%>
</html>