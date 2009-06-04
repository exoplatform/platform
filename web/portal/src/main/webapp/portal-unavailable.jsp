<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
  <head>
	<meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
	<title>Portal Warning </title>
  </head>
  <style>
	.UIPortalWarning {
	  width: 600px;
	  margin: 200px auto auto auto;
	  font-family: tahoma;
	  font-size: 12px;
	}

	.UIPortalWarning .LeftTopWarning {
	  background: url('/eXoResources/skin/sharedImages/LeftTopWarning7x58.gif') no-repeat left center;	
	  height: 58px;
	  padding-left: 7px;
	}

	.UIPortalWarning .RightTopWarning {
	  background: url('/eXoResources/skin/sharedImages/RightTopWarning7x58.gif') no-repeat right center;	
	  height: 58px;
	  padding-right: 7px;
	}

	.UIPortalWarning .CenterTopWaring {
	  background: url('/eXoResources/skin/sharedImages/CenterTopWarning1x58.gif') repeat-x center;	
	  height: 58px;
	  line-height: 58px;
	}

	.UIPortalWarning .CenterTopWaring  .TitleWaring {
	  background: url('/eXoResources/skin/sharedImages/WarningIcon.gif') no-repeat left center;	
	  padding-left: 50px;
	  font-size: 18px;
	  font-weight: bold;
	  line-height: 58px;
	  text-align: left;
	  margin-left: 20px;
	}

	.UIPortalWarning .LeftMiddleWarning {
	  background: url('/eXoResources/skin/sharedImages/LeftMiddleWarning7x1.gif') repeat-y left;	
	  padding-left: 7px; 
	}

	.UIPortalWarning .RightMiddleWarning {
	  background: url('/eXoResources/skin/sharedImages/RightMiddleWarning7x1.gif') repeat-y right;	
	  text-align: left;
	  padding: 30px 30px 50px 30px;
	}

	.UIPortalWarning .RightMiddleWarning .Icon {
	  background: url('/eXoResources/skin/sharedImages/BlueNextArrow.gif') no-repeat left 5px;	
	  padding: 6px 0px 6px 20px;
	  text-align: left;
	}

	.UIPortalWarning .LeftBottomWarning {
	  background: url('/eXoResources/skin/sharedImages/LeftBottomWarning7x7.gif') no-repeat left center;	
	  height: 7px;
	  padding-left: 7px;
	}

	.UIPortalWarning .RightBottomWarning {
	  background: url('/eXoResources/skin/sharedImages/RightBottomWarning7x7.gif') no-repeat right center;	
	  height: 7px;
	  padding-right: 7px;
	}

	.UIPortalWarning .CenterBottomWaring {
	  background: url('/eXoResources/skin/sharedImages/CenterBottomWarning1x7.gif') repeat-x center;	
	  height: 7px;
	}

  </style>
  <body style="text-align: center;">
	<div class="UIPortalWarning">

	  <div class="LeftTopWarning">
		<div class="RightTopWarning">
		  <div class="CenterTopWaring">
			<div class="TitleWaring">Can not connect to this portal</div>
		  </div>
		</div>
	  </div>
	  <div class="LeftMiddleWarning">
		<div class="RightMiddleWarning">
		  <div class="WarningContent">
			<div class="Icon">Try private mode by URL format: <strong>http://$hostname$/portal/private/$portalname$/</strong></div>
			<div class="Icon">Try another Portal or user URL "<strong>http://$hostname$/portal</strong> for default portal.</div>
		  </div>
		</div>
	  </div>
	  <div class="LeftBottomWarning">
		<div class="RightBottomWarning">
		  <div class="CenterBottomWaring"><span></span></div>
		</div>
	  </div>

	</div>
  </body>
</html>
  