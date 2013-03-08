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
    String contextPath = request.getContextPath() ;
    String lang = request.getLocale().getLanguage();
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=UTF-8");
%>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <link href="<%=request.getContextPath()%>/css/welcome-screens/jquery.qtip.min.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/css/welcome-screens/Style.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="<%=contextPath%>/javascript/welcome-screens/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=contextPath%>/javascript/welcome-screens/jquery.qtip.min.js"></script>
    <script type="text/javascript" src="<%=contextPath%>/javascript/welcome-screens/jquery.scrollTo.js"></script>
    <script type="text/javascript" src="<%=contextPath%>/javascript/welcome-screens/welcomescreens.js"></script>
    <style type="text/css">
        body {
            background: url("/platform-extension/jsp/welcome-screens/images/login_texture.jpg") repeat;
            font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
            font-size: 13px;
            color: #333;
            overflow: auto;
        }

        @font-face {
            font-family: lucida;
            src: url("/platform-extension/jsp/welcome-screens/font/L_0.eot")
        }

        @font-face {
            font-family: lucida;
            src: url("/platform-extension/jsp/welcome-screens/font/L_0.TTF")
        }

        h5 {
            font-size: 14px;
            font-weight: bold;
            margin: 0;
            padding: 20px 0 0;
        }

        .backLight {
            border-radius: 80px 80px 80px 80px;
            box-shadow: 0 0 200px white;
            height: 280px;
            margin: 215px auto 0;
            width: 280px;
        }

        .uiWelcomeBox {
            background-color:  #fff;
            border-radius: 4px 4px 4px 4px;
            box-shadow: 0 1px 3px #3d3d3d;
            margin: -400px auto 0;
            width: 512px;
        }

        .header {
            background: url("/platform-extension/jsp/welcome-screens/images/headerBG.png") repeat-x scroll 0 0 transparent;
            border: 1px solid #393939;
            border-radius: 4px 4px 0 0;
            width: 511px;
            line-height: 44px;
            text-align: center;
            font-size: 18px;
            font-weight: bold;
            color: #c1c1c1;
        }

        .content {
            background-color: #FFFFFF;
            margin: 0 auto;
            padding: 10px 30px;
            position: static;
            width: auto;
        }

        input {
            -moz-box-sizing: border-box;
            border: 1px solid #C7C7C7;
            box-shadow: 0 2px 1px rgba(0, 0, 0, 0.075) inset;
            color: #333333;
            font-size: 13px;
            height: 28px;
            padding: 3px 6px;
            display: inline-block;
            margin-bottom: 0;
            vertical-align: middle;
            background-color: #FFFFFF;
            transition: border 0.2s linear 0s, box-shadow 0.2s linear 0s;
            border-radius: 4px 4px 4px 4px;
            line-height: 20px;
        }

        .bottom {
            background: url("/platform-extension/jsp/welcome-screens/images/bottomBG.png") no-repeat center bottom;
            text-align: center;
            padding: 25px 0;
        }

        button {
            background: url("/platform-extension/jsp/welcome-screens/images/loginAction.png") repeat-x;
            border: 1px solid #224886;
            border-radius: 4px;
            text-align: center;
            color: white;
            font-weight: bold;
            font-size: 16px;
            padding: 0 20px;
            height: 43px;
            cursor: pointer;
        }

        .form-horizontal .control-group:before, .form-horizontal .control-group:after {
            content: "";
            display: table;
            line-height: 0;
        }

        .form-horizontal .control-group {
            margin-bottom: 12px;
        }

        .form-horizontal .control-label {
            float: left;
            padding-top: 5px;
            text-align: right;
            width: 75px;
        }

        .form-horizontal .controls {
            margin-left: 85px;
        }

        .form-horizontal .controls input:first-child {
            margin-right: 10px;
        }

        .inputFieldMini {
            width: 128px;
            -webkit-width: 131px;
        }

        .inputFieldMedium {
            width: 162px;
        }

        .inputFieldLarge {
            width: 334px;
        }

        .confirmLabel {
            padding: 0 10px 0 5px;
        }

        .desc {
            font-size: 12px;
            color: #999;
            margin: 10px 0 15px;
        }

        .screenShot {
            padding: 20px 0 30px;
            text-align: center;
        }

        .screenShot a {
            background-color: #fff;
            background-repeat: repeat-x;
            border: 1px solid #f6f6f6;
            border-radius: 4px 4px 4px 4px;
            box-shadow: 0 2px 4px rgba(13, 13, 13, 0.27);
            height: 174px;
            padding: 7px;
            width: 406px;
            display: inline-block;
        }
        .screenShot a img {
            border: 1px solid #dfdfdf;
            border-radius: 7px;
        }
    </style>
<head>
<div class="backLight"></div>
<div class="uiWelcomeBox" id="AccountSetup1"  >
    <div class="header">Account Setup</div>
    <div class="content form-horizontal" id="AccountSetup">
        <h5>Create your account</h5>
        <p class="desc">This will be your primary user account</p>
        <form name="tcForm" action="<%= contextPath + "/accountSetup"%>" method="post">
            <div class="control-group" id ="usernameId">
                <label class="control-label">Username:</label>
                <div class="controls"><input type="text" name="username" id="userNameAccount" placeholder="User name" class="inputFieldLarge"/></div>
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
            <p class="desc">Login as root user with the following password for super user access</p>
            <div class="control-group" id="adminUsernameId">
                <label class="control-label">Username:</label>
                <div class="controls"><input type="text" name="adminFirstName" id="adminFirstName" placeholder="root" readonly="readonly" class="inputFieldLarge" /></div>
            </div>
            <div class="control-group" id="adminPasswordId">
                <label class="control-label">Password:</label>
                <div class="controls">
                    <input type="password" name="adminPassword" id="adminPassword" class="inputFieldMini" /><span class="confirmLabel">Confirm:</span><input type="password" name="confirmAdminPassword" id="confirmAdminPassword" class="inputFieldMini" />
                </div>
            </div>
    </div>
    <!-- Please do not make it Button it may cause blocker problem -->
    <div class="bottom"><a class="btn btn-primary" id="continueButton" onclick="WelcomeScreens.exit();">Submit</a></div>
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
            <div class="screenShot"><a href="javascript:void(0);"><img src="/platform-extension/jsp/welcome-screens/images/greetingSS.png" alt="" width="404" height="172"/></a></div>
        </div>
        <div class="bottom"><button class="btn btn-primary">Start</button></div>
    </div>
</div>
</form>
</html>