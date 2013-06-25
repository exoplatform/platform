<%@ page import="org.exoplatform.platform.welcomescreens.service.UnlockService" %>
<%
    int rday = UnlockService.getNbDaysBeforeExpiration();
    boolean outdated = UnlockService.isOutdated();
    String css="backNotOutdated";
    String label1="You have";
    String label2="days left in your evaluation";
    String productCode= UnlockService.getProductCode();
    if (outdated)  {
        css="backOutdated";
        label1= "Your evaluation has expired"  ;
        label2= "days ago";
        rday = UnlockService.getNbDaysAfterExpiration();
    }
    String contextPath = request.getContextPath() ;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>Welcome to eXo Platform</title>
		<link rel="shortcut icon" type="image/x-icon"  href="<%=contextPath%>/favicon.ico" />
		<link rel="stylesheet" type="text/css" href="<%=contextPath%>/css/Stylesheet.css"/>
		<script type='text/javascript'>
			function formValidation() {
				if(document.unlockForm.hashMD5.value!="")
					return true;
				else {
					ERROR.innerHTML="unlock key is mandatory.";
					var elem = document.getElementById("KEYERROR");
					if (elem!=null) elem.style.display = "none";
					return false;
				}
			}
		</script>
	</head>
	<body>
		<div class="backLight"></div>
		<div class="uiWelcomeBox">
			<div class="header">
				<div class="logo">Unlock Evaluation</div>
			</div>
			<div class="content">
				<div class="<%=css%>">
					<strong> <%=label1%>  <%=rday%> <%=label2%></strong>
				</div>
				<div style="text-align: center;"><div id="ERROR" class="uiIconError"> </div></div>
                    <% if(request.getAttribute("errorMessage") != null && !request.getAttribute("errorMessage").toString().isEmpty()) {%>
                            <div style="text-align: center;"><div id="KEYERROR" class="uiIconError"><%=request.getAttribute("errorMessage").toString() %></div></div>
                    <% }%>
				<p><strong>You must own a valid subscription in order to unlock this eXo Platform instance</strong></p>
				<div class="steps clearfix">
					<div class="rightCol firstItem pull-right"><strong>Pickup your favorite <a class="" href="<%=UnlockService.getSubscriptionUrl()%>" target="_blank">subscription plan</a> and buy it</strong></div>
					<div class="stepsNumber pull-left">1</div>
				</div>
                <form action="<%=contextPath%>/trial" method="post" name="unlockForm" onsubmit="return formValidation();">
				<div class="steps clearfix">
					<div class="rightCol pull-right">
						<strong>Grab your product code and request an unlock key</strong>
						<div>
							<span>Product Code</span>
                            <input type="text" class="Text" name="pc" value="<%=UnlockService.getProductCode() %>">
                            <!-- Please IT MUST BE A LINK not a button !!!!! it need only to be resized And it would re take its layout Please!!!-->
                            <a class="btn" target="_blank" href="<%=UnlockService.getRegistrationFormUrl()%>?pc=<%=UnlockService.getProductCode()%>">Request a Key</a>
						</div>
					</div>
					<div class="stepsNumber pull-left">2</div>
				</div>
				<div class="steps clearfix">
					<div class="rightCol pull-right">
						<strong>Enter your unlock key below to unlock the product</strong>
						
						<div>
							<span>Unlock Key</span>
                            <input class="Text" type="text" name="hashMD5" id="hashMD5">
							<button class="btn">Unlock</button>
						</div>
                        
					</div>
					<div class="stepsNumber pull-left">3</div>
				</div>
                </form>
			</div>

			<div class="bottom">
				Question about your eXo Platform evaluation?<br />
Contact us at <a href="mailto:info@exoplatform.com">info@exoplatform.com</a> or our website <a href="http://www.exoplatform.com" target="_blank">www.exoplatform.com</a>
			</div>
		</div>
	</body>
</html>