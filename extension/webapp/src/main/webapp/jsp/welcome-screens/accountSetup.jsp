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
<link href="<%=request.getContextPath()%>/css/welcome-screens/jquery.qtip.min.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/welcome-screens/Style.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="<%=contextPath%>/javascript/welcome-screens/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<%=contextPath%>/javascript/welcome-screens/jquery.qtip.min.js"></script>
<script type="text/javascript" src="<%=contextPath%>/javascript/welcome-screens/jquery.scrollTo.js"></script>
<script type="text/javascript" src="<%=contextPath%>/javascript/welcome-screens/welcomescreens.js"></script>

<div class="GetStartedPage">
    <div id="wrapper">
        <div id="mask">
            <div class="BottomBox ClearFix">
                <form name="tcForm" action="<%= contextPath + "/accountSetup"%>" method="post">
            </div>
            <div class="item" id="AccountSetup">
                <div class="UIFormBox StartedStep content" name="" >
                    <h3>Create Your account <span><a id="infoTipUserId" href="#"><img class="infoTip" src="<%=contextPath%>/css/welcome-screens/background/infoIcon.png" alt="info" title="This will be your primary user account"></a></span></h3>
                    <table class="BorderDot">
                        <tbody>
                            <tr id ="usernameId">
                                <td class="FieldLabel UserInput">Username</td>
                                <td class="FieldComment FieldMini" colspan='3'>
                                    <input type="text" name="username" id="userNameAccount" placeholder="User name">
                                </td>
                            </tr>
                            <tr id="fullnameId">
                                <td class="FieldLabel UserInput">Full name</td>
                                <td class="FieldComment FieldMini">
                                    <input type="text" name="firstNameAccount" id="firstNameAccount" placeholder="First name">
                                </td>
                                <td class="FieldComment FieldMini" colspan='3'>
                                    <input type="text" name="lastNameAccount" id="lastNameAccount" placeholder="Last name">
                                </td>
                            </tr>
                            <tr id="emailId">
                                <td class="FieldLabel UserInput">Email</td>
                                <td class="FieldComment FieldMini" colspan='3'>
                                    <input type="text" name="emailAccount" id="emailAccount" >
                                </td>
                             </tr>
                             <tr id="passwordId">
                                <td class="FieldLabel UserInput">Password</td>
                                <td class="FieldComment FieldMini">
                                    <input type="password" name="password" id="userPasswordAccount"/>
                                </td>
                                <td class="FieldLabel UserInput">Confirm</td>
                                <td class="FieldComment FieldMini">
                                    <input type="password" name="confirmUserPasswordAccount" id="confirmUserPasswordAccount"/>
                                </td>
                             </tr>
                        </tbody>
                    </table>
                    <h3>Admin password <span><a id="infoTipAdminId" href="#"><img class="infoTip" src="<%=contextPath%>/css/welcome-screens/background/infoIcon.png" alt="info" title="Login as <b>root</b> user with the following password for super user access"></a></span></h3>
                    <table class="BorderDot" cols="4">
                        <tbody>
                            <tr id="adminUsernameId">
                                <td class="FieldLabel UserInput">Username</td>
                                <td class="FieldComment FieldMini" colspan='2'>
                                    <input type="text" name="adminFirstName" id="adminFirstName" placeholder="root"  readonly="readonly"/>
                                </td>
                            </tr>
                            <tr id="adminPasswordId">
                                <td class="FieldLabel UserInput">Password</td>
                                <td class="FieldComment FieldMini" colspan='2'>
                                    <input type="password" name="adminPassword" id="adminPassword"/>
                                </td>
                                <td class="FieldLabel UserInput">Confirm</td>
                                <td class="FieldComment FieldMini" colspan='2'>
                                    <input type="password" name="confirmAdminPassword" id="confirmAdminPassword"/>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <a class="SubmitButton" id="continueButton"  href="#"  onclick="WelcomeScreens.exit();">Submit</a>

                </div>
        </div>
        <div class="item" id="Greetings">
            <div class="UIFormBox StartedStep content" name="">
                <div style="text-align: center; background-color: #808080; height: 40px;width: 676px;font-size: 2em " >
                  <b>  Greetings!    </b>
                </div>
                <div style="text-align: center;padding: 10px">
                    <b>You are almost done,</b> Add your colleagues to your new social intranet and start collaborating together
                </div>
                    <div style="padding: 10px;">
                        <center>
                            <img src="<%=contextPath%>/css/welcome-screens/background/greetings.png"/>
                        </center>
                    </div>
                    <div style="background-color: #98BDCD; height: 80px;">
                        <button  class="submitbutton" > Start </button>
                    </div>
            </div>
        </form>
        </div>
    </div>
</div>
</div>
