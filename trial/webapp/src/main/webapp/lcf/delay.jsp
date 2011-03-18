<%@page import="org.exoplatform.trial.Utils"%>
<%@page import="org.exoplatform.trial.TrialFilter"%>
<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>Welcome to eXo Platform</title>
		<meta http-equiv="refresh" content="3;url=<%= TrialFilter.calledUrl %>">
		<link rel="shortcut icon" type="image/x-icon"  href="/trial/favicon.ico" />
		<link rel="stylesheet" type="text/css" href="skin/Stylesheet.css"/>
		<script src="/eXoResources/javascript/eXo.js"></script>
		<script src="/eXoResources/javascript/eXo/core/Util.js"></script>
	</head>
	<body>
		<div class="UIExoSubscriptionUser">
			<div class="UIDecorationSubscriptionForm">
				<div class="DecorationTL">
					<div class="DecorationTR">
						<div class="DecorationTM"><span></span></div>
					</div>
				</div>
				<div class="DecorationML">
					<div class="DecorationMR">
						<div class="DecorationMC">
							<div class="UISubscriptionForm">
								<span class="Title OrangeText">
									eXo Platform 3 Trial
								</span>
								<span class="largeText">
								<p>
									<b><%= Utils.daysBeforeExpire %></b> days before trial expiration.
									<br/>
									<i>Redirecting</i>&nbsp;<b>...</b>
								</p>
								</span>
								<% Utils.daysBeforeExpire = 0;%>
							</div>
						</div>
					</div>
				</div>
				<div class="DecorationBL">
					<div class="DecorationBR">
						<div class="DecorationBM"><span></span></div>
					</div>
				</div>
			</div>
		</div>
	<% 	if(Utils.loopfuseFormDisplayed){ %>
		<iframe src="<%=Utils.pingBackUrl%>" style="display:none;" id="pingBackUrlFrame" onload="setFormDisplayed()"></iframe>
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