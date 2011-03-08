<%@page import="org.apache.commons.codec.binary.Base64"%>
<%@page import="org.exoplatform.trial.Utils"%>
<%@page import="java.util.Calendar"%>
<%
  String rdate = Utils.computeRemindDateFromTodayBase64();
  boolean outdated = Utils.outdated;
%>
<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>Welcome to eXo Platform</title>
		<link rel="shortcut icon" type="image/x-icon"  href="/trial/favicon.ico" />
		<link rel="stylesheet" type="text/css" href="skin/Stylesheet.css"/>
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
					<% if(outdated){%>
							eXo Platform 3 Trial Period Expired
					<%} else { %>
							eXo Platform 3 Evaluation Subscription
					<%} %>
						</span>
						<p>eXo Platform is more than an enterprise portal. As the user experience platform for Java, you can build rich applications with social, collaboration, knowledge and content management features.<br><br>

				Thank you for downloading eXo Platform 3. Your 30-day evaluation subscription will allow you to explore and being developing with eXo.</p>
						<div class="ActionForm ClearFix">
							<div class="OrangeButton">
								<div class="OrangeButtonL">
									<div class="OrangeButtonR">
										<div class="OrangeButtonM">
											<a href="<%=Utils.registrationFormUrl%>" target="_blank">Buy Now</a>
										</div>
									</div>
								</div>
							</div>
							<div class="Lable">Upgrade to a development or production subscription.</div>
						</div>
						<div class="ActionForm ClearFix">
					<% if(outdated){%>
							<div class="GreyButton">
								<div class="GreyButtonL">
									<div class="GreyButtonR">
										<div class="GreyButtonM">
											<a class="GreyText" href="#">Trial Period Expired</a>
					<%} else { %>
							<div class="BlueButton">
								<div class="BlueButtonL">
									<div class="BlueButtonR">
										<div class="BlueButtonM">
											<a href="/trial/UnlockServlet?rdate=<%= rdate %>"><%=Utils.delayPeriod %>-Day Trial</a>
					<%} %>
										</div>
									</div>
								</div>
							</div>
					<% if(outdated){%>
							<div class="Lable GreyText">Your <%=Utils.delayPeriod %>-day evaluation subscription has now ended. To continue using eXo Platform 3, you must purchase a subscription license.</div>
					<%} else { %>
							<div class="Lable">Continue to the eXo Platform 3 welcome page to begin your <%=Utils.delayPeriod %>-Day Trial. Evaluation subscriptions include 3 development support tickets.</div>
					<%} %>
						</div>
						<div class="ActionForm ClearFix">
							<div class="BlueButton">
								<div class="BlueButtonL">
									<div class="BlueButtonR">
										<div class="BlueButtonM">
											<a href="/trial/validation/unlock.jsp">Enter License Key</a>
										</div>
									</div>
								</div>
							</div>
							<div class="Lable">If you have already purchased a subscription, enter the license key provided by your eXo account manager.</div>
						</div>
						<p>Questions about your eXo Platform evaluation? <br> Contact us at  <a href="#"class="OrangeText"> info@exoplatform.com </a> or on our website <a href="#"class="OrangeText"> www.exoplatform.com</a></p>
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
</body>
</html>