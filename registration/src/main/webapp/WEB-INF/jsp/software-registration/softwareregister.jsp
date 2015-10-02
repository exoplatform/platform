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
<%
  String contextPath = request.getContextPath();
  String lang = request.getLocale().getLanguage();
  response.setCharacterEncoding("UTF-8");
  response.setContentType("text/html; charset=UTF-8");
  boolean isRegisted = UnlockService.isRegisted();
  boolean canSKip = UnlockService.canSkipRegister();

  String registrationURL = request.getServletContext().getAttribute("registrationURL").toString();
%>
<html>
<head>
  <title>Register your Software</title>
  <script type="text/javascript" src="/platform-extension/javascript/jquery-1.7.1.js"></script>
  <script type="text/javascript" src="/registrationPLF/javascript/registration/software-registration.js"></script>

</head>
<body>
  <div class="loading">Loading...</div>
  <div class="plf-registration" >
    <a href="<%=registrationURL%>">Register your software</a>
    <form id="frmSoftwareRegistration" action="<%=contextPath+"/software-register-action"%>" method="post">
      <input type="hidden" name="value" />
      <input type="button" name="btnContinue" value="Continue" <%if(!isRegisted){%>disabled<%}%> />
      <input type="button" name="btnSkip" value="Skip" <%if(!canSKip){%>disabled<%}%> />
    </form>
  </div>
</body>
</html>
