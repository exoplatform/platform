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
<center><h2>Enter Your eXo Platform 3 License Key</h2></center>
<form action="/trial/UnlockServlet" method="post" name="unlockForm">
<center>
  <div class="KeyContentForm">
    <% if(request.getAttribute("errorMessage") != null && !request.getAttribute("errorMessage").toString().isEmpty()) {%>
      <div class="Label"><%=request.getAttribute("errorMessage").toString() %></div>
      <br/>
    <% }%>
	<div class="Label">Your license key: </div>
	<div class="InputForm"><input type="text" id="hashMD5" name="hashMD5"/></div>
	<div class="BlueButton">
		<div class="BlueButtonL">
			<div class="BlueButtonR">
				<div class="BlueButtonM">
					<a href="#" onclick="submitValidationKey()" id="submitActionButton"> Ok </a>
				</div>
			</div>
		</div>
	</div>
  </div>
</center>
</form>

<%@include file="static/footer.jsp" %>

</body>
</html>