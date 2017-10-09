<%@ page import="org.exoplatform.platform.common.software.register.UnlockService" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.exoplatform.container.PortalContainer"%>
<%@ page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%
    int rday = UnlockService.getNbDaysBeforeExpiration();
    boolean outdated = UnlockService.isOutdated();
    String css="backNotOutdated";
      
   PortalContainer portalContainer = PortalContainer.getCurrentInstance(session.getServletContext());
  ResourceBundleService service = (ResourceBundleService) portalContainer.getComponentInstanceOfType(ResourceBundleService.class);
  ResourceBundle rb = service.getResourceBundle("locale.portal.webui", request.getLocale());
   
   
    String label1 = rb.getString("UnlockTrial.label.day_left");
    String productCode= UnlockService.getProductCode();
    if (outdated)  {
        css="backOutdated";
        label1= rb.getString("UnlockTrial.label.expired");
        rday = UnlockService.getNbDaysAfterExpiration();
    }
    String contextPath = request.getContextPath() ;
    
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title><%=rb.getString("UnlockTrial.label.welcome")%></title>
		<link rel="shortcut icon" type="image/x-icon"  href="<%=contextPath%>/favicon.ico" />	
		<link rel="stylesheet" type="text/css" href="/eXoSkin/skin/bootstrap/css/bootstrap.css"/>
		<link rel="stylesheet" type="text/css" href="/eXoSkin/skin/css/Core.css"/>
		<link rel="stylesheet" type="text/css" href="/eXoSkin/skin/css/platform/portlets/welcome-screens/unlockTrial.css"/>
		<script type="text/javascript" src="/eXoResources/javascript/jquery-3.2.1.js"></script>
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
				<div class="logo"><%=rb.getString("UnlockTrial.label.unlock_evaluation")%></div>
			</div>
			<div class="content">
				<div class="<%=css%>">
					<strong> <%=label1.replaceAll("\\{1}", rday + "")%></strong>
				</div>
       <form action="<%=contextPath%>/trial" method="post" name="unlockForm" onsubmit="return formValidation();">
         <div class="product-label">
           <span>Product Code&#58;</span>
           <input type="text" class="Text" name="pc" value="<%=UnlockService.getProductCode() %>">
           <a data-toggle="popover" data-placement="top" data-content="<%=rb.getString("UnlockTrial.label.identifies")%>." onmouseover="showPopover(this);" onmouseout="hidePopover(this);">
             <i class="uiIconQuestion uiIconLightGray"></i>
           </a>
         </div>
         <p>
           <strong><%=rb.getString("UnlockTrial.label.you_must")%></strong>
         </p>
         <div class="steps clearfix">
         	<div class="stepsNumber pull-left">1</div>
           <div class="rightCol firstItem"><strong><%=rb.getString("UnlockTrial.label.pickup")%></strong>
            <div class="center"><a target="_blank" class="btn btn-large btn-buy btn-primary" href="<%=UnlockService.getRegistrationFormUrl()%>?pc=<%=UnlockService.getProductCode()%>"><%=rb.getString("UnlockTrial.label.buy")%></a></div>
           </div>
           
         </div>
         <div class="steps clearfix">
         	<div class="stepsNumber pull-left">2</div>
           <div class="rightCol">
             <strong><%=rb.getString("UnlockTrial.label.unlock_key")%></strong>
             <br />
             <div id="ERROR" class="alert alert-error" style="display: none;"><i class="uiIconError"></i><%=rb.getString("UnlockTrial.label.mandatory")%></div>
             <% if(request.getAttribute("errorMessage") != null && !request.getAttribute("errorMessage").toString().isEmpty()) {%>
		         <div id="KEYERROR" class="alert alert-error"><i class="uiIconError"></i><%=request.getAttribute("errorMessage").toString() %></div>
		       <% }%>
		     <button class="btn btn-primary btn-unlock"><%=rb.getString("UnlockTrial.label.unlock")%></button>
             <span class="unlock-label"><%=rb.getString("UnlockTrial.label.unlock_key")%>&#58;</span>
             <div class="form-input"><input class="Text" type="text" name="hashMD5" id="hashMD5"></div>
             
           </div>
           
         </div>
       </form>
			</div>

			<div class="bottom">
				<%=rb.getString("UnlockTrial.label.question_about")%><br />
<%=rb.getString("UnlockTrial.label.contact_us").replaceAll("\\{1}","<a href=\"mailto:info@exoplatform.com\">info@exoplatform.com</a>")%> <a href="http://www.exoplatform.com" target="_blank">www.exoplatform.com</a>
			</div>
		</div>
	</body>
</html>
