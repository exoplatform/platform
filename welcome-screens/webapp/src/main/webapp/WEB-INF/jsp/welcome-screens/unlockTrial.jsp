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
					ERROR.innerHTML="Unlock key is mondatory";
					var elem = document.getElementById("KEYERROR");
					if (elem!=null) elem.style.display = "none";
					return false;
				}
			}
		</script>
		<style type="text/css">
			body {
				 background: url("/platform-extension/jsp/welcome-screens/images/login_texture.jpg") repeat;
				 font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
				 font-size: 13px;
				 color: #333;
				 overflow: auto;
			}

			@font-face {
				font-family: lucida;
				src: url("/platform-extension/jsp/welcome-screens/font/L_0.eot") 
			}
						
			@font-face {
				font-family: lucida;
				src: url("/platform-extension/jsp/welcome-screens/font/L_0.TTF") 
			}

			h5 {
				font-size: 14px;
				font-weight: bold;
				margin: 0;
				padding: 20px 0 0;
			}
			
			.clearfix {
			  *zoom: 1;
			}
			.clearfix:before,
			.clearfix:after {
			    display: table;
			    content: "";
			    // Fixes Opera/contenteditable bug:
			    // http://nicolasgallagher.com/micro-clearfix-hack/#comment-36952
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
				border-radius: 4px 4px 4px 4px;
				box-shadow: 0 1px 3px #3d3d3d;
				margin: -400px auto 0;
				width: 512px;
			}

			.header {
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
			
			.logo {
				background: url("/platform-extension/jsp/welcome-screens/images/Logo.png") no-repeat scroll 18px center;
				text-align: center;
			}
			
			.content {
				background-color: #FFFFFF;
			    margin: 0 auto;
			    padding: 10px 20px;
			    position: static;
			    width: auto;
			}
			
			.content p {
				text-align: center;
				margin-bottom: 30px;
			}

			input {
				-moz-box-sizing: border-box;
				border: 1px solid #C7C7C7;
				box-shadow: 0 2px 1px rgba(0, 0, 0, 0.075) inset;
				color: #333333;
				font-size: 13px;
				height: 28px;
				padding: 3px 6px;
				 display: inline-block;
				margin-bottom: 0;
				vertical-align: middle;
				background-color: #FFFFFF;
				transition: border 0.2s linear 0s, box-shadow 0.2s linear 0s;
				border-radius: 4px 4px 4px 4px;
				line-height: 20px;
			}
			
			input.disable {
				background-color: #e6e6e6;
			}
			.bottom {
				background: url("/platform-extension/jsp/welcome-screens/images/bottomBG.png") no-repeat center bottom;
				text-align: center;
				padding: 25px 0;
			}

			button {
				background-color: #567AB6;
			    background-image: linear-gradient(to bottom, #638ACD, #426393);
			    background-repeat: repeat-x;
			    border-color: #224886;
			    color: #FFFFFF;
			    font-family: "Helvetica Neue Bold",Helvetica,Lucida,Arial,sans-serif;
			    font-weight: bold;
			    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);
			    border-radius: 4px 4px 4px 4px;
    			box-shadow: 0 1px 0 rgba(255, 255, 255, 0.2) inset, 0 1px 2px rgba(0, 0, 0, 0.05);
			}
			
			.notification {
				background: #e97f7f;
				color: #fff;
				border-radius: 4px;
				text-align: center;
				padding: 10px;
				margin: 10px 0 20px;
			}
			
			.rightCol {
				width: 420px;
			}
			.steps {
				margin: 0 0 40px;
			}
			
			.stepsNumber {
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
				<div class="notification">
					<strong>Your evalution has expried 3 days ago</strong>
				</div>
				<p><strong>You must own a valid subscription in order to unlock this eXo Platform</strong></p>
				<div class="steps clearfix">
					<div class="rightCol pull-right"><strong>Pickup your favorite subcription plan and buy it</strong></div>
					<div class="stepsNumber pull-left">1</div>
				</div>
				<div class="steps clearfix">
					<div class="rightCol pull-right">
						<strong>Grab your product code and request an unlock key</strong>
						<div class="clearfix">
							<button class="btn btn-primary pull-right">Request Key</button>
							<div class="pull-left">
								<span>Product Code</span>
								<input class="disable" type="text" />
							</div>
						</div>
					</div>
					<div class="stepsNumber pull-left">2</div>
					
				</div>
				
				<div class="steps clearfix">
					<div class="rightCol pull-right">
						<strong>Enter the unlock key below to unlock the product</strong>
						<div class="clearfix">
							<button class="btn btn-primary pull-right">Unlock</button>
							<div class="pull-left">
								<span>Unlock Key</span>
								<input type="text" />
							</div>
						</div>
					</div>
					<div class="stepsNumber pull-left">3</div>
					
				</div>
							
			</div>	
			<div class="bottom">
				Question about your eXo Platorm evaluation?<br />
Contact us at <a href="">info@exoplatform.com</a> or our website <a href="">www.exoplatform.com</a>
			</div>
		</div>
	</body>
</html>