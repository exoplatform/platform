<%--

    Copyright (C) 2009 eXo Platform SAS.
    
    This is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation; either version 2.1 of
    the License, or (at your option) any later version.
    
    This software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public
    License along with this software; if not, write to the Free
    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
    02110-1301 USA, or see the FSF site: http://www.fsf.org.

--%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="javax.servlet.http.Cookie"%>
<%@ page import="org.exoplatform.container.PortalContainer"%>
<%@ page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="org.exoplatform.web.login.InitiateLoginServlet"%>
<%@ page import="org.gatein.common.text.EntityEncoder"%>
<%@ page language="java" %>
<%
  String contextPath = request.getContextPath() ;
  String lang = request.getLocale().getLanguage();

  ResourceBundleService service = (ResourceBundleService) PortalContainer.getCurrentInstance(session.getServletContext()).getComponentInstanceOfType(ResourceBundleService.class);
  ResourceBundle res = service.getResourceBundle(service.getSharedResourceBundleNames(), request.getLocale()) ;

  String uri = (String)request.getAttribute("org.gatein.portal.login.initial_uri");

  response.setContentType("text/html; charset=UTF-8");
  response.setCharacterEncoding("UTF-8"); 
%>
<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=lang%>" lang="<%=lang%>">
  <head>
    <!--test-->
    <title>eXo Subscription Agreement</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="shortcut icon" type="image/x-icon"  href="/portal/favicon.ico" />
    
<style type="text/css">  
html {
  margin: 0px; padding: 0px;
}
*+html {
  overflow-x: hidden;
}

p,h1,h2,h3,h4,h5,h6 {
  margin: 10px 0;
}

img {
  border: none; vertical-align: middle;
}

a {
  text-decoration: none;
  color: black;
}

div, table, th, tr, td, img, form {
    font-family: Verdana,Arial,sans-serif;
  font-size:12px;
}
h5 {
    font-size: 12px;
}

p, h1, h2, h3, h4, h5, h6 {
    margin: 10px 0;
}
/*###############################################################################################*/

.ClearFix:after {
  content: ".";
  display: block;
  height: 0;
  clear: both;
  visibility: hidden;
}

*+html .ClearFix {
  display: inline-block;
  zoom: 1;
  display: block; 
}


.FL {
  float: left; /* orientation=rt */
}

.FR {
  float: right; 
}

.UIPopupWindow {
    border: 1px solid #CACACA;
    border-radius: 5px 5px 5px 5px;
    box-shadow: 0px 0px 10px #AFAFAF;
    position: absolute;
    display: block;
    left: 349px;
    top: 39px;
    width: 650px;
    z-index: 4;
}
.UIPopupWindow .PopupTitle {
    color: #515151;
    cursor: move;
    display: block;
    font-size: 12px;
    font-weight: bold;
    line-height: 30px;
    margin: 0 25px 0 9px;
    vertical-align: middle;
}
.UIPopupWindow .CloseButton {
    background: url("background/CloseIcon.png") no-repeat scroll right top;
    cursor: pointer;
    float: right;
    height: 18px;
    margin: 5px 5px 0 0;
    width: 18px;
}
.UIPopupWindow > .OverflowContainer {
    background: url("background/PortalComposer.png") repeat-x scroll left top transparent;
    border-radius: 3px 3px 0 0;
    height: 30px;
}

.UIPopupWindow .UIWindowContent {
    background: none repeat scroll 0 0 #EBEBEB;
    border-radius: 0 0 5px 5px;
    padding: 5px;
}

.UITabPane .TabPaneContent {
    padding: 8px;
}
.UIAction {
    padding: 8px 0;
    text-align: center;
}
.UIAction a {
    display: inline-block;
}
.UIAction .ActionButton {
    cursor: pointer;
    display: inline-block;
    margin: 0 3px;
}


.UIAction .LightBlueStyle {
    background: url("/eXoResources/skin/DefaultSkin/webui/component/UIBarDecorator/UIAction/background/LightBlueStyle.gif") repeat-x scroll center center transparent;
    border: 1px solid #ACACAC;
    border-radius: 10px 10px 10px 10px;
    color: #464646;
    font-size: 12px;
    line-height: 22px;
    padding: 0 14px;
    text-align: center;
    white-space: nowrap;
}



  ol {
    margin:0;padding:0
  }
  p {
    margin:0
  }
  .c5{vertical-align:middle;width:302.7pt;border-style:solid;border-color:#000000;border-width:1pt;padding:0.8pt 0.8pt 0.8pt 0.8pt}
  .c2{vertical-align:middle;width:67.1pt;border-style:solid;border-color:#000000;border-width:1pt;padding:0.8pt 0.8pt 0.8pt 0.8pt}
  .c38{vertical-align:middle;width:175.4pt;border-style:solid;border-color:#000000;border-width:1pt;padding:0.8pt 0.8pt 0.8pt 0.8pt}
  .c34{vertical-align:middle;width:302pt;border-style:solid;border-color:#000000;border-width:1pt;padding:0.8pt 0.8pt 0.8pt 0.8pt}
  .c37{vertical-align:middle;width:171pt;border-style:solid;border-color:#000000;border-width:1pt;padding:0.8pt 0.8pt 0.8pt 0.8pt}
  .c18{vertical-align:middle;width:171.8pt;border-style:solid;border-color:#000000;border-width:1pt;padding:0.8pt 0.8pt 0.8pt 0.8pt}
  .c10{vertical-align:middle;width:333pt;border-style:solid;border-color:#000000;border-width:1pt;padding:0.8pt 0.8pt 0.8pt 0.8pt}
  .c32{vertical-align:middle;width:298.4pt;border-style:solid;border-color:#000000;border-width:1pt;padding:0.8pt 0.8pt 0.8pt 0.8pt}
  .c13{vertical-align:middle;width:73.4pt;border-style:solid;border-color:#000000;border-width:1pt;padding:0.8pt 0.8pt 0.8pt 0.8pt}
  .c15{line-height:1.2;padding-top:5pt;direction:ltr;padding-bottom:5pt}
  .c7{line-height:1.2;text-align:center;direction:ltr;padding-bottom:0pt}
  .c1{line-height:1.2;text-align:justify;direction:ltr;padding-bottom:8pt}
  .c12{line-height:1.2;padding-top:0.1pt;direction:ltr;padding-bottom:0.1pt}
  .c33{list-style-type:circle;margin:0;padding:0}
  .c28{list-style-type:lower-latin;margin:0;padding:0}
  .c21{list-style-type:decimal;margin:0;padding:0}
  .c9{line-height:1.2;direction:ltr;padding-bottom:0pt}
  .c19{list-style-type:disc;margin:0;padding:0}
  .c30{max-width:468pt;background-color:#ffffff;padding:72pt 72pt 72pt 72pt}
  .c0{font-size:12px;font-family:Verdana,Arial,sans-serif}
  .c11{padding-left:0pt;margin-left:36pt}
  .c25{color:#0000ff;text-decoration:underline}
  .c22{padding-left:0pt;margin-left:54pt}
  .c24{padding-left:0pt;margin-left:72pt}
  .c8{color:inherit;text-decoration:inherit}
  .c4{font-weight:bold}
  .c23{direction:ltr}
  .c3{text-indent:36pt}
  .c27{margin-left:18pt}
  .c29{padding-left:0pt}
  .c6{height:11pt}
  .c14{text-align:justify}
  .c20{margin-left:36pt}
  .c35{margin-left:72pt}
  .c31{text-indent:-36pt}
  .c16{height:0pt}
  .c36{margin-left:54pt}
  .c26{border-collapse:collapse}
  .c17{text-align:center}
  .title{padding-top:24pt;line-height:1.2;text-align:left;color:#000000;font-size:36pt;font-family:Calibri;font-weight:bold;padding-bottom:6pt}
  .subtitle{padding-top:18pt;line-height:1.2;text-align:left;color:#666666;font-style:italic;font-size:24pt;font-family:Georgia;padding-bottom:4pt}
  body{color:#000000;font-size:11pt;font-family:Calibri}
  h1{padding-top:12pt;line-height:1.2;text-align:left;color:#000000;font-size:16pt;font-family:Arial;font-weight:bold;padding-bottom:3pt}
  h2{padding-top:12pt;line-height:1.2;text-align:left;color:#000000;font-style:italic;font-size:14pt;font-family:Arial;font-weight:bold;padding-bottom:3pt}
  h3{padding-top:12pt;line-height:1.2;text-align:left;color:#000000;font-size:13pt;font-family:Arial;font-weight:bold;padding-bottom:3pt}
  h4{padding-top:12pt;line-height:1.2;text-align:left;color:#000000;font-size:14pt;font-family:Calibri;font-weight:bold;padding-bottom:3pt}
  h6{padding-top:12pt;line-height:1.2;text-align:left;color:#000000;font-size:11pt;font-family:Calibri;font-weight:bold;padding-bottom:3pt}




/**********************************************************************
copy style from here (dont use css line up , they exiting on product)
**********************************************************************/

.TermsConditions {
    width: 650px;
  background:#ebebeb;
  box-shadow: 0px 0px 2px #888;
  line-height:20px;
  padding-bottom:5px;
  left: 50%;
    top: 50%;
  visibility: visible;
    z-index: 4;
  margin:-273px 0 0 -325px;
}

.TermsConditions .UITabPane .TabPaneContent  {
  background:#fff;
  padding: 8px 8px 0px;
  border-radius:3px;
}

.TermsConditions .WorkingArea {
  padding:0 10px;
  overflow:auto;
  height:440px;
}
.TermsConditions  .BottomBox  {
  border-top: 1px solid #ededed;
  margin:10px 0 0 0;
}
.TermsConditions  input[type='checkbox']{
  vertical-align: -1px;
}
.TermsConditions  .inactive{
  color: #BBB;
  cursor:default;
}
.TermsConditions  .active{
  color: #464646;
  cursor:pointer;
}
</style>
</head>

<body>
  <!--begin popup terms conditions-->
  <div class="MaskLayer"  style="position:absolute;width:100%;height:100%;background:#000;z-index:4;left:0;top:0;opacity:0.7;-ms-filter:'progid:DXImageTransform.Microsoft.Alpha(Opacity=70)'; filter: alpha(opacity=70); ">
  </div><!--set again height use javasript (get height screen resolution)-->
    <div class="TermsConditions UIPopupWindow">
      <div class="OverflowContainer ">        
        <h5 class="PopupTitle">Terms and Conditions Agreement</h5>
      </div>
      <div class="UIWindowContent UITabPane">
        <div class="TabPaneContent">
          
          <div>...</div>
          
          <div class="BottomBox ClearFix">
            <form name="tcForm" action="<%= contextPath + "/terms-and-conditions-action"%>" method="post" style="margin: 0px;">
            
            <% if (uri != null) { %>
            <input type="hidden" name="tacURI" value="<%=uri%>" />
            <% } %>
            
            <div class="UIAction FR">
              <a class="ActionButton LightBlueStyle inactive" id="continueButton"  href="javascript:void(0)"  onclick="validate();">Continue</a>
            </div>
            <div class="UIAction FL">
              <input type="checkbox" id="agreement" name="checktc" value="false" onclick="toggleState();" />
              <label for="agreement">I agree with this terms and conditions agreement.</label>
            </div>
            <script type='text/javascript'>                 
              function validate() {
                var eltAgreement = document.getElementById("agreement");
                if(eltAgreement.checked == true) {
                  document.tcForm.submit();
                }
              }
              function toggleState() {
                var eltAgreement = document.getElementById("agreement");
                
                if(eltAgreement.checked == false) {
                  // Uncheck
                  eltAgreement.value = false;
                  setInactive();
                }
                else {
                  // Check
                  eltAgreement.value = true;
                  setActive();
                }
              }
              function setInactive() {
                  var elt = document.getElementById("continueButton");
                  var classValue = elt.className;
                  var newClassValue = classValue.replace("active", "inactive");
                  elt.className = newClassValue;
                }
                function setActive() {
                  var elt = document.getElementById("continueButton");
                  var classValue = elt.className;
                  var newClassValue = classValue.replace("inactive", "active");
                  elt.className = newClassValue;
                }
            </script>
            </form>
          </div>
        </div>
      </div>    
    </div>  
    <!--end popup terms conditions-->
    <!--end html from here  -->
  
  </body>
</html>
