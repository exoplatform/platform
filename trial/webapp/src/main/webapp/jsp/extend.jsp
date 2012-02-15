<%@page import="org.exoplatform.trial.TrialService"%>
<%@page import="java.util.Calendar"%>
<%
  String rdate = TrialService.computeRemindDateFromTodayBase64();
  boolean outdated = TrialService.isOutdated();
  boolean firstStart = TrialService.isFirstStart();
%>
<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>Welcome to eXo Platform</title>
	<link rel="shortcut icon" type="image/x-icon"  href="/trial/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="/trial/skin/Stylesheet.css"/>
      <script type='text/javascript'>                 
        function submitValidationKey() {
          document.getElementById('submitActionButton').onclick='';
          document.unlockForm.submit();
        }
      </script>
</head>
<body>
<%@include file="static/head.jsp" %>
<h2 class="CenterTitle">Enter Your Platform 3.5 Evaluation Key</h2>
<div class="FormContainer">
  <form action="/trial/UnlockServlet" method="post" name="unlockForm">
    <table>
      <tr>
        <td>
          <label class="TextForm" id="hashMD5">Your Evaluation Key:</label>
        </td>
        <td>
          <input class="Text" type="text" name="hashMD5" id="hashMD5">
        </td>
        <td>
          <input type="submit" class="FormSubmit BlueFormRect" value="OK">
        </td>
      </tr>
<% if(request.getAttribute("errorMessage") != null && !request.getAttribute("errorMessage").toString().isEmpty()) {%>
      <tr>
        <td colspan="3" class="Red">
          <%=request.getAttribute("errorMessage").toString() %>
        </td>
      </tr>
<% }%>
    </table>
  </form>
</div>
<%@include file="static/footer.jsp" %>
</body>
</html>