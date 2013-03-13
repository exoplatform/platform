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

%>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>Welcome to eXo Platform</title>
		<link rel="shortcut icon" type="image/x-icon"  href="/welcome-screens/favicon.ico" />
		<link rel="stylesheet" type="text/css" href="/welcome-screens/css/Stylesheet.css"/>
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
		<style type="text/css">
			body {
				 background: url("/platform-extension/jsp/welcome-screens/images/login_texture.jpg") repeat;
				 font-family: HelveticaNeue, Arial, sans-serif;
				 font-size: 13px;
				 color: #333;
				 overflow: auto;
			}

			h5 {
				font-size: 14px;
				font-weight: bold;
				margin: 0;
				padding: 20px 0 0;
			}
			
			a {
				color: #2f5e92;
			}
			
			.clearfix {
			  *zoom: 1;
			}
			.clearfix:before,
			.clearfix:after {
			    display: table;
			    content: "";
			    line-height: 0;
			}
			  
			.clearfix:after {
			    clear: both;
			}
			
			.pull-left {
				float: left;
			}
			
			.pull-right {
				float: right;
			}

			.backLight {
				border-radius: 80px 80px 80px 80px;
				box-shadow: 0 0 200px white;
				height: 280px;
				margin: 215px auto 0;
				width: 280px;
			}

			.uiWelcomeBox {
				background-color:  #fff;
				border-radius: 10px;
				box-shadow: 0 1px 3px #3d3d3d;
				margin: -400px auto 0;
				width: 512px;
			}

			.uiWelcomeBox .header {
				background: url("/platform-extension/jsp/welcome-screens/images/headerBG.png") repeat-x scroll 0 0 transparent;
				border: 1px solid #393939;
				border-radius: 4px 4px 0 0;
				width: 511px;
				line-height: 44px;
				text-align: center;
				font-size: 18px;
				font-weight: bold;
				color: #c1c1c1;
			}
			
			.uiWelcomeBox .logo {
				background: url("/platform-extension/jsp/welcome-screens/images/Logo.png") no-repeat scroll 18px center;
				text-align: center;
			}
			
			.uiWelcomeBox .content {
				background-color: #FFFFFF;
			    margin: 0 auto;
			    padding: 10px 23px;
			    position: static;
			    width: auto;
			}
			
			.uiWelcomeBox .content p {
				text-align: center;
				margin-bottom: 30px;
			}

			.uiWelcomeBox input {
				border: 1px solid #cdcdcd;
				border-radius: 4px 4px 4px 4px;
				box-shadow: 0 1px 1px rgba(0,0,0,0.1) inset;
				color: #999;
				height: 28px;
				margin: 0 5px 0 0;
				padding: 0 6px;
				width: 170px;
				font-size: 13px;
			}
			
			.uiWelcomeBox input.disable {
				background-color: #e6e6e6;
			}
			
			.uiWelcomeBox .bottom {
				background: url("/platform-extension/jsp/welcome-screens/images/bottomBG.png") no-repeat center bottom;
				text-align: center;
				padding: 30px 0;
			}
			
			.btn {
			  color: #333333;
			  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.75);
			  background-color: #f5f5f5;
			  width: 115px;
			  vertical-align: top;
			  font-weight: bold;
			  background-image: -moz-linear-gradient(top, #ffffff, #e6e6e6);
			  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#ffffff), to(#e6e6e6));
			  background-image: -webkit-linear-gradient(top, #ffffff, #e6e6e6);
			  background-image: -o-linear-gradient(top, #ffffff, #e6e6e6);
			  background-image: linear-gradient(to bottom, #ffffff, #e6e6e6);
			  background-repeat: repeat-x;
			  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffffff', endColorstr='#ffe6e6e6', GradientType=0);
			  border-color: #e6e6e6 #e6e6e6 #bfbfbf;
			  border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
			  *background-color: #e6e6e6;
			  /* Darken IE7 buttons by default so they stand out more given they won't have borders */
			
			  filter: progid:DXImageTransform.Microsoft.gradient(enabled = false);
			  border: 1px solid #bbbbbb;
			  line-height: 25px;
			  -webkit-border-radius: 4px;
			  -moz-border-radius: 4px;
			  border-radius: 4px;
			  *margin-left: .3em;
			  -webkit-box-shadow: inset 0 1px 0 rgba(255,255,255,.2), 0 1px 2px rgba(0,0,0,.05);
			  -moz-box-shadow: inset 0 1px 0 rgba(255,255,255,.2), 0 1px 2px rgba(0,0,0,.05);
			  box-shadow: inset 0 1px 0 rgba(255,255,255,.2), 0 1px 2px rgba(0,0,0,.05);
			}
			.btn:hover,
			.btn:active,
			.btn.active {
			  color: #333333;
			  background-color: #e6e6e6;
			  *background-color: #d9d9d9;
			}
			.btn:active,
			.btn.active {
			  background-color: #cccccc;
			}
			
			.btn:hover {
			  background-color: #efefef;
			  background-image: -moz-linear-gradient(top, #f1f1f1, #ebebeb);
			  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#f1f1f1), to(#ebebeb));
			  background-image: -webkit-linear-gradient(top, #f1f1f1, #ebebeb);
			  background-image: -o-linear-gradient(top, #f1f1f1, #ebebeb);
			  background-image: linear-gradient(to bottom, #f1f1f1, #ebebeb);
			  background-repeat: repeat-x;
			  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fff1f1f1', endColorstr='#ffebebeb', GradientType=0);
			}
			.btn.active,
			.btn:active {
			  background-color: #e6e6e6;
			  background-color: #d9d9d9;
			  background-image: none;
			  outline: 0;
			  -webkit-box-shadow: inset 0 2px 4px rgba(0,0,0,.15), 0 1px 2px rgba(0,0,0,.05);
			  -moz-box-shadow: inset 0 2px 4px rgba(0,0,0,.15), 0 1px 2px rgba(0,0,0,.05);
			  box-shadow: inset 0 2px 4px rgba(0,0,0,.15), 0 1px 2px rgba(0,0,0,.05);
			}
			
			.btn {
			  border-color: #c7c7c7;
			}
			.btn-primary {
			  color: #ffffff;
			  text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);
			  background-color: #5179bd;
			  background-image: -moz-linear-gradient(top, #6289cb, #3862a9);
			  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#6289cb), to(#3862a9));
			  background-image: -webkit-linear-gradient(top, #6289cb, #3862a9);
			  background-image: -o-linear-gradient(top, #6289cb, #3862a9);
			  background-image: linear-gradient(to bottom, #6289cb, #3862a9);
			  background-repeat: repeat-x;
			  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff6289cb', endColorstr='#ff3862a9', GradientType=0);
			  border-color: #3862a9 #3862a9 #25406f;
			  border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
			  *background-color: #3862a9;
			  /* Darken IE7 buttons by default so they stand out more given they won't have borders */
			
			  filter: progid:DXImageTransform.Microsoft.gradient(enabled = false);
			  border-color: #224886;
			}
			.btn-primary:hover,
			.btn-primary:active{
			  color: #ffffff;
			  background-color: #3862a9;
			  *background-color: #325795;
			}
			.btn-primary:active,
			.btn-primary.active {
			  background-color: #2b4c82;
			}
			.btn-primary:hover {
			  background-color: #4a70b1;
			  background-image: -moz-linear-gradient(top, #4b71b3, #496fae);
			  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#4b71b3), to(#496fae));
			  background-image: -webkit-linear-gradient(top, #4b71b3, #496fae);
			  background-image: -o-linear-gradient(top, #4b71b3, #496fae);
			  background-image: linear-gradient(to bottom, #4b71b3, #496fae);
			  background-repeat: repeat-x;
			  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff4b71b3', endColorstr='#ff496fae', GradientType=0);
			}
			
			.uiWelcomeBox .backOutdated {
				background: #e97f7f;
				color: #fff;
				border-radius: 4px;
				text-align: center;
				padding: 8px;
				margin: 10px 0 15px;
			}
			
			.uiWelcomeBox .backNotOutdated {
				background: #b5b5b5;
				color: #fff;
				border-radius: 4px;
				text-align: center;
				padding: 8px;
				margin: 10px 0 15px;
			}
			
			.uiWelcomeBox .rightCol {
				width: 420px;
			}
			
			.uiWelcomeBox .steps {
				margin: 0 0 35px;
			}
			
			.uiWelcomeBox .firstItem {
				line-height: 35px;
			}
			
			.uiWelcomeBox .rightCol.pull-right > div {
			    margin-top: 5px;
			}
			
			.uiWelcomeBox .rightCol.pull-right > div span {
				width: 85px;
				display: inline-block;
			}
	
			.uiWelcomeBox .stepsNumber {
				background: #B5B5B5;
			    border-radius: 19px 19px 19px 19px;
			    color: #FFFFFF;
			    display: block;
			    font-size: 24px;
			    font-weight: bold;
			    height: 38px;
			    line-height: 40px;
			    text-align: center;
			    width: 38px;
				float: left;
			}
		</style>
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
				<p><strong>You must own a valid subscription in order to unlock this eXo Platform instance</strong></p>
				<div class="steps clearfix">
					<div class="rightCol firstItem pull-right"><strong>Pickup your favorite <a class="" href="<%=UnlockService.getSubscriptionUrl()%>" target="_blank">subscription plan</a> and buy it</strong></div>
					<div class="stepsNumber pull-left">1</div>
				</div>
                <form action="/welcome-screens/trial" method="post" name="unlockForm" onsubmit="return formValidation();">
				<div class="steps clearfix">
					<div class="rightCol pull-right">
						<strong>Grab your product code and request an unlock key</strong>
						<div>
							<span>Product Code</span>
                            <input type="text" class="disable"  name="pc" value="<%=UnlockService.getProductCode() %>">
                            <!-- Please IT MUST BE A LINK not a button !!!!! it need only to be resized And it would re take its layout Please!!!-->
                            <a target="_blank" href="<%=UnlockService.getRegistrationFormUrl()%>?pc=<%=UnlockService.getProductCode()%>">Request a Key</a>
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
							<button class="btn btn-primary">Unlock</button>
						</div>
                        <span  id="ERROR" style="color: red"> </span>
                        <% if(request.getAttribute("errorMessage") != null && !request.getAttribute("errorMessage").toString().isEmpty()) {%>
                                <span id="KEYERROR" style="display: block"><%=request.getAttribute("errorMessage").toString() %> </span>
                        <% }%>
					</div>
					<div class="stepsNumber pull-left">3</div>
				</div>
                </form>
			</div>

			<div class="bottom">
				Question about your eXo Platorm evaluation?<br />
Contact us at <a href="mailto:info@exoplatform.com">info@exoplatform.com</a> or our website <a href="www.exoplatform.com">www.exoplatform.com</a>
			</div>
		</div>
	</body>
</html>