<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
	<title>Welcome to eXo Platform</title>
	<link rel="shortcut icon" type="image/x-icon"  href="/trial/favicon.ico" />
		<link rel="stylesheet" type="text/css" href="skin/Stylesheet.css"/>
		<script type='text/javascript'>			            
		  function submitValidationKey() {
			document.getElementById('submitActionButton').onclick='';
			document.unlockForm.submit();
		  }
		</script>
	</head>
<body>
<form action="/trial/UnlockServlet" method="post" name="unlockForm">
			
<div class="UIExoSubscriptionUser">

	<div class="UILicenseKeyForm">
		<div class="LicenseKeyForm">
			<span class="Title OrangeText">
				Enter Your eXo Platform 3 License Key
			</span>
			<p>Thank you for your subscription purchase. Please enter your code bellow:</p>
				<div class="LicenseForm ClearFix">
					<div class="Lable">Your license key</div>
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
					<p>Questions about your subscription? Contact the eXo team at <a href="#"class="OrangeText"> info@exoplatform.com </a> or on our website at <a href="#"class="OrangeText">www.exoplatform.com</a></p>
		</div>
		</div>
	</div>
	
			</form>
</body>
</html>