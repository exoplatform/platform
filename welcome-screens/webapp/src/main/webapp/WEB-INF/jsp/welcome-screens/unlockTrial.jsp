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
		<link rel="stylesheet" type="text/css" href="/eXoSkin/skin/bootstrap/css/bootstrap.css"/>
		<link rel="stylesheet" type="text/css" href="/eXoSkin/skin/css/Core.css"/>
		<link rel="stylesheet" type="text/css" href="/eXoSkin/skin/css/platform/portlets/welcome-screens/unlockTrial.css"/>
		<script type="text/javascript" src="/platform-extension/javascript/jquery-1.7.1.js"></script>
		<script type="text/javascript" src="/eXoSkin/skin/bootstrap/js/bootstrap-tooltip.js"></script>
		<script type="text/javascript" src="/eXoSkin/skin/bootstrap/js/bootstrap-popover.js"></script>
		<script type='text/javascript'>
			function formValidation() {
				if(document.unlockForm.hashMD5.value!="")
					return true;
				else {
					document.getElementById("ERROR").style.display = "block";
					var elem = document.getElementById("KEYERROR");
					if (elem!=null) elem.style.display = "none";
					return false;
				}
			}
			function showPopover(element) {
				$(element).popover({template: '<div class="popover"><div class="arrow"></div><div class="inner"><h3 class="popover-title" style="display:none;"></h3><div class="popover-content"><p></p></div></div></div>'});
				$(element).popover('show');
			}
			function hidePopover(element) {
				$(element).popover('hide');
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
       <form action="<%=contextPath%>/trial" method="post" name="unlockForm" onsubmit="return formValidation();">
         <div class="product-label">
           <span>Product Code&#58;</span>
           <input type="text" class="Text" name="pc" value="<%=UnlockService.getProductCode() %>">
           <a data-toggle="popover" data-placement="top" data-content="This code identifies your eXo Platform instance. It is required to generate a unique unlock key." onmouseover="showPopover(this);" onmouseout="hidePopover(this);">
             <i class="uiIconQuestion uiIconLightGray"></i>
           </a>
         </div>
         <p>
           <strong>You must own a valid subscription in order to unlock this eXo Platform instance</strong>
         </p>
         <div class="steps clearfix">
           <div class="rightCol firstItem pull-right"><strong>Pickup your favorite plan and purchase a subscription</strong>
             <div class="center"><a target="_blank" class="btn btn-large btn-buy btn-primary" href="<%=UnlockService.getRegistrationFormUrl()%>?pc=<%=UnlockService.getProductCode()%>">Buy</a></div>
           </div>
           <div class="stepsNumber pull-left">1</div>
         </div>
         <div class="steps clearfix">
           <div class="rightCol pull-right">
             <strong>Enter the unlock key you received in the confirmation email</strong>
             <br /><br />
             <div id="ERROR" class="alert alert-error" style="display: none;"><i class="uiIconError"></i>Unlock key is mandatory.</div>
             <% if(request.getAttribute("errorMessage") != null && !request.getAttribute("errorMessage").toString().isEmpty()) {%>
		         <div id="KEYERROR" class="alert alert-error"><i class="uiIconError"></i><%=request.getAttribute("errorMessage").toString() %></div>
		       <% }%>
             <span class="unlock-label">Unlock Key&#58;</span>
             <input class="Text" type="text" name="hashMD5" id="hashMD5">
             <button class="btn btn-primary">Unlock</button>
           </div>
           <div class="stepsNumber pull-left">2</div>
         </div>
       </form>
			</div>

			<div class="bottom">
				Question about your eXo Platform evaluation?<br />
Contact us at <a href="mailto:info@exoplatform.com">info@exoplatform.com</a> or on our website <a href="http://www.exoplatform.com" target="_blank">www.exoplatform.com</a>
			</div>
		</div>
	</body>
</html>
