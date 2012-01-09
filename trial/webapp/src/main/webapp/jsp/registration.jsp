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
	</head>
<body>
<%@include file="static/head.jsp" %>

<% if(outdated){%>
	<%@include file="static/trialExpired.jsp" %>
<%} else { %>
	<%@include file="static/welcome.jsp" %>
<%} %>

<% if(firstStart){%>
	  <%@include file="button/startEvaluationButton.jsp" %>
<%} else if(outdated) { %>
	  <%@include file="button/productKeyButton.jsp" %>
<%} %>

<% 
  if(!firstStart){
	if(outdated){%>
	  <%@include file="button/extendEvaluationButton.jsp" %>
<%  } else { %>
	  <%@include file="button/dismissButton.jsp" %>
<%
	}
  }
%>

<%@include file="button/buyNowButton.jsp" %>

<%@include file="static/footer.jsp" %>

<% 	if(!TrialService.isLoopfuseFormDisplayed()){ %>
	<iframe src="<%=TrialService.getPingBackUrl()%>" style="display:none;" id="pingBackUrlFrame" onload="setFormDisplayed()"></iframe>
	<iframe src="about:blank" style="display:none;" id="pingBackUrlActivation"></iframe>
	<script>
		function setFormDisplayed() {
			var pingBackUrlActivationElement = document.getElementById("pingBackUrlActivation");
			pingBackUrlActivationElement.src="/trial/PingBackServlet";
		}
	</script>
<% } %>
</body>
</html>