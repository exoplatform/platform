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
  <head><title>eXo Subscription Agreement</title>
    <title>Welcome to eXo Platform 3.5</title>
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
  <div class="MaskLayer"  style="position:absolute;width:100%;height:100%;background:#000;z-index:4;left:0;top:0;opacity:0.7;-ms-filter:'progid:DXImageTransform.Microsoft.Alpha(Opacity=70)'; filter: alpha(opacity=70); "></div><!--set again height use javasript (get height screen resolution)-->
    <div class="TermsConditions UIPopupWindow">
      <div class="OverflowContainer ">        
        <h5 class="PopupTitle">Terms and Conditions Agreement</h5>
      </div>
      <div class="UIWindowContent UITabPane">
        <div class="TabPaneContent">
          <div class="WorkingArea">
          
          <p class="c15 c17"><span class="c0 c4">eXo Platform SAS</span><span class="c0"><br />
          51 Federal Street, Suite 350, San Francisco, California 94105</span></p>

          <p class="c15 c17"><span class="c0 c4">Master Subscription Agreement</span></p>

          <p class="c9"><span class="c0">PLEASE READ THIS MASTER SUBSCRIPTION AGREEMENT BEFORE
          PURCHASING OR USING THE PRODUCTS OR SERVICES. BY USING OR PURCHASING THE PRODUCTS OR
          SERVICES, CUSTOMER SIGNIFIES ITS ASSENT TO THIS AGREEMENT. IF YOU ARE ACTING ON BEHALF
          OF AN ENTITY, THEN YOU REPRESENT THAT YOU HAVE THE AUTHORITY TO ENTER INTO THIS
          AGREEMENT ON BEHALF OF THAT ENTITY. IF CUSTOMER DOES NOT ACCEPT THE TERMS OF THIS
          AGREEMENT, THEN IT MUST NOT PURCHASE OR USE THE PRODUCTS OR SERVICES.</span></p>

          <p class="c9"><span class="c0"><br />
          This</span> <span class="c0 c4">Master Subscription Agreement</span> <span class=
          "c0">(the</span> <span class="c0 c4">&ldquo;Agreement&rdquo;</span><span class="c0">)
          is entered into by and between eXo&nbsp;Platform SAS, NA, with its principal place of
          business at 51 Federal Street, Suite 350, San Francisco, California 94105
          (</span><span class="c0 c4">&ldquo;eXo&rdquo;</span><span class="c0">) and the
          purchaser or user of eXo product and/or services that accepts the terms of this
          Agreement (</span><span class="c0 c4">&ldquo;Customer&rdquo;</span><span class="c0">).
          The effective date of this Agreement ('Effective Date') is the earlier of the date that
          Customer signs or accepts this Agreement by entering into an Order Form or the date
          that Customer uses eXo services.</span></p>

          <p class="c15"><span class="c0">Whereas eXo and Customer desire to establish certain
          terms and conditions under which Customer will, from time to time, license software and
          obtain services from eXo;</span></p>

          <p class="c15"><span class="c0">Now, therefore, for good and valuable consideration,
          the receipt and sufficiency of which is hereby acknowledged, Customer and eXo agree as
          follows:</span></p>

          <p class="c15"><span class="c0 c4">1. Definitions</span></p>

          <p class="c15"><span class="c0">Capitalized terms used in this Agreement are defined in
          this Section 1 or the Section in which they are first used:</span></p>

          <p class="c15"><span class="c0 c4">1.1 &ldquo;Additional
          Services&rdquo;</span><span class="c0">&nbsp;means the services provided by eXo to
          Customer outside of the Subscription and as further defined in Appendix
          2.</span><span class="c0 c4">&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">1.1 &ldquo;CORE Processor&rdquo;</span><span class=
          "c0">&nbsp;means the unit that reads and executes program
          instructions.</span><span class="c0 c4">&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">1.2 &ldquo;Documentation&rdquo;</span> <span class=
          "c0">means the standard end-user technical documentation and specifications that eXo
          supplies with the Software, as revised by eXo from time to time.&nbsp; Advertising and
          marketing materials are not Documentation.</span></p>

          <p class="c15"><span class="c0 c4">1.3 &ldquo;Error&rdquo;</span><span class=
          "c0">&nbsp;means a reproducible failure of the Software to perform in substantial
          conformity with its Documentation.</span></p>

          <p class="c15"><span class="c0 c4">1.4 &ldquo;Activation Key&rdquo;</span><span class=
          "c0">&nbsp;means a file evidencing a grant of one or more Licenses by eXo to Customer
          for the Term, as included in the Subscription purchased by Customer.</span></p>

          <p class="c15"><span class="c0 c4">1.5 &ldquo;License&rdquo;</span><span class=
          "c0">&nbsp;means a license granted, in accordance with a Subscription, by eXo to
          Customer to allow installation and use of the Software.</span></p>

          <p class="c15"><span class="c0 c4">1.6 &ldquo;Named Contact&rdquo;</span> <span class=
          "c0">means an employee of Customer who is proficient on the Software.</span></p>

          <p class="c15"><span class="c0 c4">1.7 &ldquo;Open Source Software&rdquo;</span>
          <span class="c0">means various open source software components licensed under the terms
          of applicable open source license agreements included in the materials relating to such
          software.&nbsp; Open Source Software is composed of individual software components,
          each of which has its own copyright and its own applicable license
          conditions.</span></p>

          <p class="c15"><span class="c0 c4">1.8 &ldquo;Order Form&rdquo;</span><span class=
          "c0">&nbsp;means an order form, whether in written or electronic form, defining the
          Subscription and/or Additional Services purchased by Customer.</span></p>

          <p class="c15"><span class="c0 c4">1.13 &ldquo;Release&rdquo;</span><span class=
          "c0">&nbsp;means a later version of the Software identified by a change in the first
          digit to the left of the decimal point ((X).xx); Version Release means a later version
          of the Software identified by a change in the first digit to the right of the decimal
          point ((x).Xx); and Maintenance Release means a later version of the Software
          identified by a change in the second digit to the right of the decimal point
          ((x).xX).</span></p>

          <p class="c15"><span class="c0 c4">1.9 &ldquo;Software&rdquo;</span><span class=
          "c0">&nbsp;means the software in object code format provided by eXo to Customer, as
          specified on the Order Form, licensed under this Agreement as of the Effective Date or
          a future date, including any Documentation incorporated therein, and Updates to such
          software&nbsp; that eXo may provide to Customer from time to time as part of
          Subscription Services.</span></p>

          <p class="c15"><span class="c0 c4">1.10 &ldquo;Subscription&rdquo;</span><span class=
          "c0">&nbsp;means the license for the Software and Subscription Services ordered and
          paid for by Customer and provided by eXo&nbsp; as specified on the Order
          Form.</span></p>

          <p class="c15"><span class="c0 c4">1.11 &ldquo;Subscription
          Services&rdquo;</span><span class="c0">&nbsp;means the subscription services provided
          by eXo to Customer as part of the Subscription and as further defined in Section 2.6,
          as further defined in Appendix 1 and as may be updated from time to time at</span>
          <span class="c0 c25"><a class="c8" target="_blank" href=
          "http://www.exoplatform.com/SLA">http://www.exoplatform.com/SLA</a></span><span class=
          "c0">&nbsp; and/or as may be specified on the applicable Order Form.</span></p>

          <p class="c15"><span class="c0 c4">1.12 &ldquo;Term&rdquo;</span><span class=
          "c0">&nbsp;means the period of time for the Subscription as specified in the Activation
          Key.</span></p>

          <p class="c15"><span class="c0 c4">1.13 &ldquo;Update&rdquo;</span><span class=
          "c0">&nbsp;means a Release, Version Release or Maintenance Release of the
          Software.</span></p>

          <p class="c15"><span class="c0 c4">2. General Terms</span></p>

          <p class="c15"><span class="c0 c4">2.1 Scope of Agreement.</span><span class=
          "c0">&nbsp; This Agreement governs all transactions between the parties with respect to
          the Software and Subscription Services provided hereunder.</span></p>

          <p class="c15"><span class="c0 c4">2.2 Orders.</span><span class="c0">&nbsp; Customer
          may from time to time place orders with eXo to purchase a Subscription at eXo&rsquo;s
          then-current prices.&nbsp; Customer may transmit such orders to eXo by telephone, mail,
          fax, email or other electronic channels.&nbsp; Customer, may, for its convenience,
          submit orders using its standard forms, but no terms, provisions or conditions of any
          Order Form, acknowledgement or other business form that Customer may use in connection
          with the acquisition or licensing of the Software will have any effect on the rights,
          duties or obligations of the parties under, or otherwise modify, this Agreement,
          regardless of any failure of eXo to object to such terms, provisions or
          conditions.&nbsp; Any such additional or conflicting terms and conditions on any
          Customer Order Form, acknowledgement or other business form are hereby rejected by
          eXo.</span></p>

          <p class="c15"><span class="c0 c4">2.3 Acceptance of Orders.</span><span class=
          "c0">&nbsp; eXo may accept orders in its sole discretion by sending to Customer an
          Order Form confirming the particulars of the order.&nbsp;&nbsp;</span> <span class=
          "c0 c4">&nbsp;&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">2.4 Delivery.</span><span class="c0">&nbsp; Delivery
          of the Software will be from the eXo Customer Portal web site.&nbsp; The Term is
          specified in the Activation Key, which will be separately delivered to the
          Customer.&nbsp; The Software will be deemed accepted by Customer upon delivery of the
          Activation Key.&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">2.5 Installation Services.</span><span class=
          "c0">&nbsp; Customer will be responsible for installing the Software on its computers
          as permitted under this Agreement.&nbsp; Additional Services (consulting, training and
          specific development), as set forth in Appendix 2, may be purchased on at
          time-and-materials basis at eXo&rsquo;s then-current rates as specified on the
          applicable Order Form.</span></p>

          <p class="c15"><span class="c0 c4">2.6 Subscription Services.</span><span class=
          "c0">&nbsp; During the time that Customer has paid the applicable annual Subscription
          fees, eXo will provide Customer Subscription Services for the Software under the terms
          outlined in Appendix 1 as may be updated from time to time at &nbsp;</span><span class=
          "c0 c25"><a class="c8" target="_blank" href=
          "http://www.exoplatform.com/SLA">http://www.exoplatform.com/SLA</a></span><span class=
          "c0">. Such Subscription Services are provided to Customer solely for Customer&rsquo;s
          internal use, and Customer may not use the Software or Subscription Services to supply
          any consulting, support or training services to any third party.</span></p>

          <p class="c15"><span class="c0 c4">2.7 Exclusions.</span><span class="c0">&nbsp; eXo
          will have no obligation to correct Errors caused by:&nbsp; (a) improper installation of
          the Software; (b) altered or modified Software, unless altered or modified by eXo; (c)
          use of the Software in a manner inconsistent with its Documentation or this Agreement;
          (d) any combination of the Software with hardware or software not specified in the
          Documentation; or (e) defects in the Software due to accident, hardware malfunction,
          abuse or improper use.</span></p>

          <p class="c15"><span class="c0 c4">2.8 Additional Services.</span><span class=
          "c0">&nbsp; Should Customer request that eXo provide Additional Services in connection
          with problems (a) caused by the factors listed in Section 2.7 or (b) that are otherwise
          beyond the scope of the Subscription Services or this Agreement, Customer will pay for
          such services eXo agrees to perform on a time-and-materials basis at eXo&rsquo;s
          then-current rates.</span></p>

          <p class="c15"><span class="c0 c4">2.9 Customer Obligations.</span><span class=
          "c0">&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">2.9.1</span> <span class="c0">As a condition to
          eXo&rsquo;s provision of the Subscription Services, Customer agrees to assure necessary
          competence for use of the Software through training as may be mutually agreed between
          the parties.</span></p>

          <p class="c15"><span class="c0 c4">2.9.2</span> <span class="c0">As a condition to
          eXo&rsquo;s provision of the Subscription Services, Customer must assist eXo in
          identifying and correcting any Errors, including executing reasonable diagnostic
          routines in accordance with any instructions provided by eXo. Customer agrees to
          provide eXo with such cooperation, materials, information, access and support which eXo
          deems to be reasonably required to allow eXo to successfully provide the Subscription
          Services, including, without limitation, as may be set forth in an applicable Order
          Form.&nbsp;&nbsp; Customer understands and agrees that eXo&rsquo;s obligations
          hereunder are expressly conditioned upon Customer providing such cooperation,
          materials, information, access and support.</span></p>

          <p class="c15"><span class="c0 c4">2.9.3</span> <span class="c0">Customer acknowledges
          that in order for eXo to provide the Subscription Services, Customer may be required to
          license and install certain third party software and provide certain third party
          hardware that are not provided or licensed by eXo (&ldquo;Third Party
          Products&rdquo;).&nbsp; eXo may provide Customer with links and instructions for
          obtaining Third Party Products, but it is Customer&rsquo;s responsibility to properly
          license and install any required Third Party Products from the relevant third party
          providers. eXo will have no liability with respect to any Third Party Products.&nbsp;
          In the event of a failure by Customer to timely provide Third Party Products as
          required, eXo may treat the applicable Order Form as having been cancelled by
          Customer.</span></p>

          <p class="c15"><span class="c0 c4">3. Licenses</span></p>

          <p class="c15"><span class="c0 c4">3.1 Grant.</span><span class="c0">&nbsp; For each
          Subscription that Customer purchases, eXo grants Customer a limited, non-exclusive,
          non-transferable, non-sublicensable (except as provided in Section 11.4) License under
          the Subscription for the Term to:</span></p>

          <p class="c15"><span class="c0 c4">a)</span><span class="c0">&nbsp;use, install and
          execute the Software licensed hereunder (in object code format) on any computers solely
          for Customer&rsquo;s own business purposes;&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">b)</span> <span class="c0">use,</span><span class=
          "c0 c4">&nbsp;</span><span class="c0">install the Software licensed hereunder (in
          object code format) in combination with the number of CPUs, as designated in the
          Activation Key, solely for Customer&rsquo;s own business purposes;</span></p>

          <p class="c15"><span class="c0">Each License is subject to the terms and conditions of
          this Agreement, including the restrictions set forth in this Section 3 and will be
          contingent upon Customer&rsquo;s timely payment of eXo's applicable Subscription fee
          (as specified on the Order Form) and issuance by eXo of the Activation Key.&nbsp; The
          License granted herein is solely to the entity specified as &ldquo;Customer&rdquo; and
          not, by implication or otherwise, to any parent, subsidiary or affiliate of such
          entity.</span></p>

          <p class="c15"><span class="c0 c4">3.2 Copies.</span><span class="c0">&nbsp; Customer
          may make up to two (2) copies of the Software licensed hereunder for archival, backup,
          installation or disaster recovery purposes only.&nbsp; Customer will include in any
          such copy all copyright, trademark, or other proprietary rights notices as included in
          or affixed to the original Software.</span></p>

          <p class="c15"><span class="c0 c4">3.3 Restrictions.</span><span class="c0">&nbsp;
          Customer shall not itself, or through any parent, subsidiary, affiliate, agent or other
          third party:</span></p>

          <p class="c15"><span class="c0">(a) decompile, disassemble, translate, reverse engineer
          or otherwise attempt to derive source code from the Software, in whole or in part, nor
          will Customer use any mechanical, electronic or other method to trace, decompile,
          disassemble, or identify the source code of the Software or encourage others to do so,
          except to the limited extent, if any, that applicable law permits such acts
          notwithstanding any contractual prohibitions, provided, however, before Customer
          exercises any rights that Customer believes to be entitled to based on mandatory law,
          Customer shall provide eXo with thirty (30) days prior written notice and provide all
          reasonably requested information to allow eXo to assess Customer&rsquo;s claim and, at
          eXo&rsquo;s sole discretion, to provide alternatives that reduce any adverse impact on
          eXo&rsquo;s intellectual property or other rights,<br />
          (b) allow access or permit use of the Software by any users other than Customer&rsquo;s
          employees, or authorized third-party contractors who are providing services to Customer
          and agree in writing to abide by the terms of this Agreement, provided further that
          Customer shall be liable for any failure by such employees and third-party contractors
          to comply with the terms of this Agreement,<br />
          (c) create, develop, license, install, use, or deploy any third party software or
          services to circumvent, enable, modify or provide access, permissions or rights which
          violate the technical restrictions of the Software, any additional licensing terms
          provided by eXo via product documentation, notification, and the terms of this
          Agreement,<br />
          (d) modify or create derivative works based upon the Software,<br />
          (e) use the Software in connection with any business operation for which Customer
          provides services to third parties, or<br />
          (f) disclose the results of any benchmark test of the Software to any third party
          without eXo&rsquo;s prior written approval, unless otherwise expressly permitted
          herein, provided, however, that the foregoing restriction shall apply to Customer only
          if Customer is a software or hardware vendor, or Customer is performing testing or
          benchmarking on the Software.</span></p>

          <p class="c15"><span class="c0 c4">3.4 Open Source Software.&nbsp;</span> <span class=
          "c0">The Open Source Software is licensed to Customer under the terms of the applicable
          open source license conditions and/or copyright notices that can be found in the
          open_source_licenses file, the documentation or other materials accompanying the
          Software.&nbsp; Copyrights to the Open Source Software are held by copyright holders
          indicated in the copyright notices in the corresponding source files or in the
          open_source_licenses file or other materials accompanying the Software.</span></p>

          <p class="c15"><span class="c0 c4">4. License Fees and Payment</span></p>

          <p class="c15"><span class="c0 c4">4.1 Subscription Fees.</span><span class="c0">&nbsp;
          Customer shall pay all fees for each Subscription as specified on the applicable Order
          Form. Customer may purchase additional Licenses via Subscription by placing any order
          in accordance with Section 2.2.&nbsp; Any added Licenses will be subject to the
          following:&nbsp; (i) added Licenses will be coterminous with the pre-existing Term
          (either the initial Term or the renewal Term); (ii) the Subscription fee for the added
          Licenses will be the then-current, generally applicable Subscription fee for such; and
          (iii) any Licenses added in the middle of a billing period will be prorated for that
          billing period.&nbsp; eXo reserves the right to modify its Subscription fees at any
          time, upon at least thirty (30) days prior notice to Customer, which notice may be
          provided by e-mail.</span></p>

          <p class="c15"><span class="c0 c4">4.2 Billing and Renewal.</span><span class=
          "c0">&nbsp; eXo charges and collects in advance for the Subscription.&nbsp; eXo will
          automatically renew and issue an invoice each billing period on the subsequent
          anniversary of the Subscription unless either party gives written notice of its intent
          not to renew at least thirty (30) days prior to the end of the current contract
          term.&nbsp; Upon any renewal, eXo&rsquo;s then-current terms and conditions for the
          Subscription Services and this Agreement will apply. The renewal charge will be equal
          to the then-current number of CPUs times eXo&rsquo;s then-current list price
          Subscription fee at the time of renewal.&nbsp; Fees for any other services will be
          charged on an as-quoted basis.&nbsp;&nbsp; All eXo supplied Software and Subscription
          Services will only be delivered to Customer electronically through the Internet. Unless
          otherwise specified on an Order Form, all invoices will be paid within thirty (30) days
          from the date of the invoice. Fees are non-refundable upon payment, unless otherwise
          set forth herein. Payments will be made without right of set-off or chargeback. All
          payments must be made in U.S. Dollars or Euros, as set forth in the applicable Order
          Form.&nbsp; Customer is responsible for all applicable Customer bank fees. &nbsp;Late
          payments will accrue interest at the rate of one and one half percent (1&frac12;%) per
          month, or, if lower, the maximum rate permitted under applicable law. If payment of any
          fee is overdue, eXo may also suspend provision of the Subscription Services until such
          delinquency is corrected</span></p>

          <p class="c15"><span class="c0 c4">4.3 Taxes.</span><span class="c0">&nbsp; The amounts
          payable to eXo under this Agreement do not include any taxes, levies, or similar
          governmental charges, however designated, including any related penalties and interest
          (</span><span class="c0 c4">&ldquo;Taxes&rdquo;</span><span class="c0">).&nbsp;
          Customer will pay (or reimburse eXo for the payment of) all Taxes except taxes on
          eXo&rsquo;s net income, unless Customer provides eXo a valid state sales/use/excise tax
          exemption certificate or Direct Pay Permit. If Customer is required to pay any
          withholding tax, charge or levy in respect of any payments due to eXo hereunder,
          Customer agrees to gross up payments actually made such that eXo shall receive sums due
          hereunder in full and free of any deduction for any such withholding tax, charge or
          levy.&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">4.4 Audit Rights.</span><span class="c0">&nbsp;
          Customer will maintain accurate records as to its use of the Software as authorized by
          this Agreement, for at least two (2) years from the last day on which Subscription
          Services expired for the applicable Software.&nbsp; eXo, or persons designated by eXo,
          will, at any time during the period when Customer is obliged to maintain such records,
          be entitled to audit such records and to ascertain completeness and accuracy, in order
          to verify that the Software are used by Customer in accordance with the terms of this
          Agreement and that Customer has paid the applicable license fees and Subscription
          Services fees for the Software, provided that: (a) eXo may conduct no more than one (1)
          audit in any twelve (12) month period; (b) any such audit shall be subject to a
          mutually agreed upon non-disclosure agreement negotiated in good faith and entered into
          by the parties (including any third party agent eXo may use in connection with such
          audit); (c) the audit will be conducted during normal business hours; and (d) eXo shall
          use commercially reasonable efforts to minimize the disruption of Customer&rsquo;s
          normal business activities in connection with any such audit.&nbsp; eXo, or persons
          designated by eXo, shall not have physical access to Customer&rsquo;s computing devices
          in connection with any such audit, without Customer&rsquo;s prior written
          consent.&nbsp; Customer shall promptly pay to eXo any underpayments revealed by any
          such audit.&nbsp; Any such audit will be performed at eXo&rsquo;s expense, provided,
          however, that Customer shall promptly reimburse eXo for the cost of such audit and any
          applicable fees if such audit reveals an underpayment by Customer of more than five
          percent (5%) of the license amounts payable by Customer to eXo for the period
          audited.</span></p>

          <p class="c15"><span class="c0 c4">5. Term and Termination</span></p>

          <p class="c15"><span class="c0 c4">5.1 Term.</span><span class="c0">&nbsp; Unless
          otherwise stated in the applicable Order Form, the Term of this Agreement will begin on
          the Effective Date and will continue until terminated as set forth in this Agreement,
          or until such time as either party shall notify the other of its intent to terminate
          the agreement by providing ninety (90) advanced written notice.</span></p>

          <p class="c15"><span class="c0 c4">5.2 Termination for Cause.</span><span class=
          "c0">&nbsp; Either party may terminate this Agreement for cause if the other party
          materially breaches, but only by giving the breaching party written notice of
          termination and specifying in such notice the alleged material breach.&nbsp; The
          breaching party will have a grace period of thirty (30) days after such notice is
          served to cure the breach described therein.&nbsp; If the breach is not cured within
          the foregoing time period, this Agreement will automatically terminate upon the
          conclusion of such period.&nbsp; Notwithstanding the foregoing, eXo, in its sole
          discretion, may terminate this Agreement if Customer violates its obligations under
          Sections 3 or 7.</span></p>

          <p class="c15"><span class="c0 c4">5.3 Effects of Termination.</span><span class=
          "c0">&nbsp; Upon termination of this Agreement for any reason:&nbsp; (a) any amounts
          owed to eXo under this Agreement before such termination will be immediately due and
          payable; (b) all License rights granted in this Agreement and any Order Form will
          immediately terminate; (c) Customer must promptly stop all use of the Software; (d)
          Customer must erase all copies of the Software from Customer&rsquo;s computers, and
          destroy all copies of the Software and Documentation on tangible media in
          Customer&rsquo;s possession or control or return such copies to eXo; (e) each party
          will return to the other party the Confidential Information of the other party that it
          obtained during the course of this Agreement; and (f) Customer must certify in writing
          to eXo that it has returned or destroyed such Software and Documentation.&nbsp;
          Sections 1, 4.4, 5.3, 6, 7, 8.3, 9 and 11 will survive expiration or termination of
          this Agreement for any reason.</span></p>

          <p class="c15"><span class="c0 c4">6. Proprietary Rights.</span></p>

          <p class="c15"><span class="c0 c4">6.1</span><span class="c0">&nbsp;As between the
          parties, Customer acknowledges and agrees the Software, including its sequence,
          structure, organization, and source code constitute certain valuable intellectual
          property rights including copyrights, trademarks, service marks, trade secrets,
          patents, patent applications, contractual rights of non-disclosure or any other
          intellectual property or proprietary right, however arising of eXo and its
          suppliers.&nbsp; The Software is licensed and not sold to Customer, and no title or
          ownership to the Software or the intellectual property rights embodied therein passes
          as a result of this Agreement or any act pursuant to this Agreement.&nbsp; The Software
          and Documentation are the exclusive property of eXo and its suppliers, and all rights,
          title and interest in and to such not expressly granted to Customer in this Agreement
          are reserved. &nbsp;eXo owns all copies of the Software, however made.&nbsp; Nothing in
          this Agreement will be deemed to grant, by implication, estoppel or otherwise, a
          license under any of eXo&rsquo;s existing or future patents (or the existing or future
          patents of its suppliers).</span></p>

          <p class="c15"><span class="c0 c4">6.2</span> <span class="c0">Customer acknowledges
          that in the course of performing any Subscription Services, eXo may create software or
          other works of authorship (collectively &ldquo;Work Product&rdquo;). Subject to
          Customer&rsquo;s rights in the Customer Confidential Information, eXo shall own all
          right title and interest in such Work Product, including all intellectual property
          rights therein and thereto.&nbsp; If any Work Product is delivered to Customer pursuant
          to or in connection with the performance of Subscription Services (a
          &ldquo;Deliverable&rdquo;), eXo hereby grants to Customer a license to such Deliverable
          under the same terms and conditions Customer&rsquo;s license to Software set forth in
          Section 3 above.</span></p>

          <p class="c15"><span class="c0 c4">6.3</span> <span class="c0">Customer is not
          obtaining any intellectual property right in or to any materials provided by eXo to
          Customer in connection with the provision to Customer of Subscription Services
          (&ldquo;Materials&rdquo;), other than the rights of use specifically granted in this
          Agreement.&nbsp; Customer will be entitled to keep and use all Materials provided by
          eXo to Customer, but without any other license to exercise any of the intellectual
          property rights therein, all of which are hereby strictly reserved to eXo. In
          particular and without limitation, Materials may not be copied electronically or
          otherwise whether or not for archival purposes, modified including translated,
          re-distributed, disclosed to third parties, lent, hired out, made available to the
          public, sold, offered for sale, shared, or transferred in any other way. All eXo
          trademarks, trade names, logos and notices present on the Materials will be preserved
          and not deliberately defaced, modified or obliterated except by normal wear and tear.
          Customer shall not use any eXo trademarks without eXo&rsquo;s express written
          authorization.</span></p>

          <p class="c15"><span class="c0 c4">7. Confidential Information.</span><span class=
          "c0">&nbsp; The term "Confidential Information" shall mean any information disclosed by
          either party (the "Discloser") to the other party (the "Recipient") in connection with
          this Agreement that is disclosed in writing, orally or by inspection and is identified
          as "Confidential" or "Proprietary", or which, under the circumstances surrounding
          disclosure ought to be treated as confidential by the Recipient.&nbsp;&nbsp;
          Notwithstanding the foregoing, the following is "Confidential Information" of
          eXo:&nbsp; Any information, in whatever form, disclosed by eXo that relates to the
          Software and that is not publicly known.&nbsp; The Recipient shall treat as
          confidential all Confidential Information received from the Discloser, shall not use
          such Confidential Information except as expressly permitted under this Agreement, and
          shall not disclose such Confidential Information to any third party without the
          Discloser's prior written consent; provided, however, the Recipient may disclose
          Confidential Information to its employees and contractors on a need-to-know-basis who
          have an agreement with the Recipient that would protect the Discloser to the same
          extent and which restricts disclosure of the Confidential Information in the same
          manner as this Agreement.&nbsp; The Recipient is liable for all acts and omissions of
          its employees and contractors that such act or omission would be a breach of this
          Agreement if it had been done by Recipient. The Recipient shall use the same measures
          to protect the Confidential Information that it takes with its own most confidential
          information, but in no event less than reasonable measures, to prevent the disclosure
          and unauthorized use of Confidential Information.&nbsp; Notwithstanding the above, the
          restrictions of this Section shall not apply to information that:&nbsp; (a) was
          independently developed by the Recipient without any use of the Confidential
          Information of the Discloser; (b) becomes known to the Recipient, without restriction,
          from a third party without breach of this Agreement and who had a right to disclose it;
          (c) was in the public domain at the time it was disclosed or becomes in the public
          domain through no act or omission of the Recipient; (d) was rightfully known to the
          Recipient, without restriction, at the time of disclosure; or (e) is disclosed pursuant
          to the order or requirement of a court, administrative agency, or other governmental
          body; provided, however, that the Recipient shall provide prompt notice thereof to the
          Discloser and shall use its reasonable best efforts to obtain a protective order or
          otherwise prevent public disclosure of such information. Recipient shall, at
          Discloser&rsquo;s request, return all originals, copies, reproductions and summaries of
          Confidential Information and all other tangible materials and devices provided to the
          Recipient as Confidential Information, or at Discloser's option, certify destruction of
          the same.</span></p>

          <p class="c15"><span class="c0 c4">8. Warranties</span></p>

          <p class="c15"><span class="c0 c4">8.1 Performance</span><span class="c0">.&nbsp; eXo
          warrants to Customer that, for a period of thirty (30) days from the Effective Date
          (&ldquo;</span><span class="c0 c4">Warranty Period</span><span class="c0">&rdquo;), the
          Software, when used as permitted under this Agreement and in accordance with its
          Documentation, will operate in substantial conformity with its Documentation.&nbsp;
          eXo&rsquo;s sole liability (and Customer&rsquo;s sole and exclusive remedy) for any
          breach of this warranty shall be, in eXo&rsquo;s sole discretion, to replace the
          non-conforming Software or use commercially reasonable efforts to correct the
          non-conformity; provided that eXo is notified in writing of such non-conformity within
          the Warranty Period.&nbsp; This warranty shall not apply if:&nbsp; (i) the Software is
          used outside the scope of this Agreement or used inconsistently with its Documentation;
          (ii) the Software is modified or altered in any way except by eXo; or (iii) damages are
          due to negligence or misuse or abuse of the Software.&nbsp; Any replacement or error
          correction will not extend the original Warranty Period.</span></p>

          <p class="c15"><span class="c0 c4">8.2 Subscription Services.&nbsp;</span> <span class=
          "c0">The Subscription Services shall be deemed to be accepted by Customer upon
          delivery. eXo warrants that the Subscription Services to be performed hereunder will be
          done in a workmanlike manner and shall conform to standards of the industry.
          eXo&rsquo;s sole liability (and Customer&rsquo;s sole and exclusive remedy) for any
          breach of this warranty shall be for eXo to re-perform the applicable Subscription
          Services; provided that eXo is notified in writing of such non-conformity within three
          (3) days following the performance of the relevant Subscription Services.</span></p>

          <p class="c15"><span class="c0 c4">8.3 Disclaimer.</span><span class="c0">&nbsp; THE
          SOFTWARE AND ANY SUBSCRIPTION SERVICES PROVIDED HEREUNDER ARE PROVIDED &ldquo;AS
          IS.&rdquo;&nbsp; EXCEPT FOR THE EXPRESS WARRANTIES PROVIDED IN SECTIONS 8.1 AND 8.2,
          EXO MAKES NO OTHER WARRANTIES WITH RESPECT TO THE SOFTWARE, SUBSCRIPTION SERVICES OR
          ANY OTHER MATERIAL, INFORMATION OR SERVICES PROVIDED HEREUNDER.&nbsp; EXO HEREBY
          DISCLAIMS ALL OTHER WARRANTIES, WHETHER EXPRESS, IMPLIED OR STATUTORY, INCLUDING THE
          IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, ACCURACY,
          RESULT, EFFORT, TITLE AND NON-INFRINGEMENT.&nbsp; EXO DOES NOT WARRANT THAT ANY
          SOFTWARE OR ANY SUBSCRIPTION SERVICES WILL BE PROVIDED ERROR FREE, WILL OPERATE WITHOUT
          INTERRUPTION OR WILL FULFILL ANY OF CUSTOMER&rsquo;S PARTICULAR PURPOSES OR
          NEEDS.&nbsp; CUSTOMER ACKNOWLEDGES THAT IT HAS RELIED ON NO WARRANTIES OTHER THAN THE
          EXPRESS WARRANTIES SET FORTH IN SECTIONS 8.1 AND 8.2 AND THAT NO WARRANTIES ARE MADE BY
          ANY OF EXO&rsquo;S SUPPLIERS OR DISTRIBUTORS.&nbsp; CUSTOMER ACKNOWLEDGES AND AGREES
          THAT THE PRICES OFFERED UNDER THIS AGREEMENT REFLECT THESE NEGOTIATED WARRANTY
          PROVISIONS.&nbsp; TO THE EXTENT THAT EXO CANNOT DISCLAIM ANY SUCH WARRANTY AS A MATTER
          OF APPLICABLE LAW, THE SCOPE AND DURATION OF SUCH WARRANTY WILL BE THE MINIMUM
          PERMITTED UNDER SUCH LAW.&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">9. Limitation of Liability.&nbsp;</span>
          <span class="c0">Neither party will be liable to any other party for any indirect,
          incidental, special, consequential, punitive or exemplary damages arising out of or
          related to this Agreement under any legal theory, including but not limited to (I) lost
          profits, lost data or business interruption, even if such party has been advised of,
          knows of, or should know of the possibility of such damages, AND (II) ANY CLAIM
          ATTRIBUTABLE TO ERRORS, OMISSIONS OR OTHER INACCURACIES IN OR DESTRUCTIVE PROPERTIES OF
          THE SOFTWARE OR ANY SUBSCRIPTION SERVICES. &nbsp;REGARDLESS OF THE CAUSE OF ACTION,
          WHETHER IN CONTRACT, TORT OR OTHERWISE. NEITHER PARTY&rsquo;S TOTAL CUMULATIVE
          LIABILITY FOR ACTUAL DAMAGES ARISING OUT OF OR RELATED TO THIS AGREEMENT WILL EXCEED
          THE TOTAL AMOUNT OF SUBSCRIPTION FEES THAT CUSTOMER HAS PAID FOR THE SOFTWARE OR
          SUBSCRIPTION SERVICES GIVING RISE TO SUCH LIABILITY.&nbsp; NEITHER PARTY SHALL BRING
          ANY CLAIM BASED ON THE SOFTWARE NOR THE SUBSCRIPTION SERVICES PROVIDED HEREUNDER MORE
          THAN EIGHTEEN (18) MONTHS AFTER THE CAUSE OF ACTION ACCRUES. NOTWITHSTANDING ANYTHING
          TO THE CONTRARY HEREIN, THE LIMITATIONS OF THIS SECTION 9 WILL NOT APPLY TO OR
          OTHERWISE LIMIT EITHER PARTY&rsquo;S BREACH OF ITS OBLIGATIONS OF NONDISCLOSURE UNDER
          SECTION 7 OR CUSTOMER&rsquo;S BREACH OF THE LICENSE RESTRICTIONS IN SECTION 3.&nbsp;
          THE PARTIES ACKNOWLEDGE THAT THIS SECTION 9 REFLECTS THE ALLOCATION OF RISK BETWEEN THE
          PARTIES AND THAT NEITHER PARTY WOULD ENTER INTO THIS AGREEMENT WITHOUT THESE
          LIMITATIONS ON ITS LIABILITY.&nbsp; THIS LIMITATION OF LIABILITY WILL APPLY
          NOTWITHSTANDING THE FAILURE OF ESSENTIAL PURPOSE OF ANY LIMITED REMEDY SET FORTH
          HEREIN.</span></p>

          <p class="c15"><span class="c0 c4">10. Indemnification</span></p>

          <p class="c15"><span class="c0 c4">10.1 eXo&rsquo;s Obligation</span><span class=
          "c0">.&nbsp; Subject to the remainder of Section 10, eXo will defend Customer against
          any third party claim that the Software licensed hereunder infringes any U.S. patents
          or U.S. copyrights registered or issued as of the Effective Date (&ldquo;Infringement
          Claim&rdquo;) and indemnify Customer from the resulting costs and damages awarded
          against Customer to the third party making such Infringement Claim, by a court of
          competent jurisdiction or agreed to in settlement ; provided that Customer&nbsp; (i)
          notifies eXo promptly in writing of such Infringement Claim, (ii) grants eXo sole
          control over the defense and settlement thereof, and (iii) reasonably cooperates in
          response to a eXo request for assistance.&nbsp; eXo will have the exclusive right to
          defend any such Infringement Claim and make settlements thereof at its own discretion,
          and Customer may not settle or compromise such Infringement Claim, except with prior
          written consent of eXo.&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">10.2 Cure</span><span class="c0">.&nbsp; Should any
          Software become, or in eXo&rsquo;s opinion be likely to become, the subject of such an
          Infringement Claim, eXo shall, at its option and expense, (a) procure for Customer the
          right to make continued use thereof, (b) replace or modify such so that it becomes
          non-infringing, or (c) request return of the Software and, upon receipt thereof, the
          corresponding licenses are terminated and eXo shall refund the price paid by Customer,
          less straight-line depreciation based on a three (3) year useful life.</span></p>

          <p class="c15"><span class="c0 c4">10.3 Exclusions</span><span class="c0">.&nbsp; eXo
          shall have no liability if the alleged infringement is based on (1) combination with
          non-eXo products, (2) use for a purpose or in a manner for which the Software were not
          designed, (3) use of any older version of the Software when use of a newer eXo revision
          would have avoided the infringement, (4) any modification not made by anyone other than
          eXo, (5) any modifications made by eXo pursuant to Customer&rsquo;s specific
          instructions, , or (6) any intellectual property right owned or licensed by Customer,
          excluding the Software.</span></p>

          <p class="c15"><span class="c0 c4">10.4 Limitation.&nbsp; THIS SECTION STATES
          CUSTOMER&rsquo;S SOLE AND EXCLUSIVE REMEDY AND EXO&rsquo;S ENTIRE LIABILITY FOR
          INFRINGEMENT CLAIMS.</span></p>

          <p class="c15"><span class="c0 c4">11. General</span></p>

          <p class="c15"><span class="c0 c4">11.1 Non-Solicitation.</span> <span class=
          "c0">Customer may not hire, or directly or indirectly solicit or employ, any employee
          or contractor of eXo who is or was involved in the development, use or provision of
          Subscription Services to Customer, without the prior written consent of eXo, for a
          period of: (i) two (2) years after the termination of this Agreement, or (ii) during
          the time the employee is employed by eXo and for a period of one (1) year thereafter,
          whichever is later.</span></p>

          <p class="c15"><span class="c0 c4">11.2 &nbsp;Notices.</span><span class="c0">&nbsp;
          All notices under this Agreement must be delivered in writing in person, by courier, or
          by certified or registered mail (postage prepaid and return receipt requested) to the
          other party at the address set forth in the applicable Order Form and will be effective
          upon receipt or three (3) business days after being deposited in the mail as required
          above, whichever occurs sooner.&nbsp; Either party may change its address by giving
          written notice of the new address to the other party.</span></p>

          <p class="c15"><span class="c0 c4">11.3 Relationship of the Parties.</span><span class=
          "c0">&nbsp; The parties hereto are independent contractors.&nbsp; Nothing in this
          Agreement shall be deemed to create an agency, employment, partnership, fiduciary or
          joint venture relationship between the parties.&nbsp; Neither party has the power or
          authority as agent, employee or in any other capacity to represent, act for, bind or
          otherwise create or assume any obligation on behalf of the other party for any purpose
          whatsoever.&nbsp; There are no third party beneficiaries to this
          Agreement.<br /></span></p>

          <p class="c15"><span class="c0 c4">11.4 Compliance with Export Control
          Laws.</span><span class="c0">&nbsp; Customer acknowledges and agrees that it will
          comply with all applicable export and import control laws and regulations of the United
          States and the foreign jurisdiction in which the Software is used and, in particular,
          Customer will not export or re-export Software without all required United States and
          foreign government licenses.&nbsp; Customer will defend, indemnify, and hold harmless
          eXo from any breach of the foregoing.</span></p>

          <p class="c15"><span class="c0 c4">11.5 Assignments.</span><span class="c0">&nbsp; In
          the event of a transfer of all or substantially all of Customer&rsquo;s business
          assets, whether by merger, sale of assets, sale of stock or otherwise, Customer may
          assign or transfer, by operation of law or otherwise, any of its rights or delegate any
          of its duties under this Agreement (including its licenses with respect to the
          Software) to any third party upon prior written notice to eXo, provided the assignee,
          transferee, or surviving entity is not a competitor of eXo, in which case Customer must
          obtain eXo&rsquo;s prior written approval.&nbsp; Any other attempted assignment or
          transfer by Customer in violation of the foregoing will be void. Subject to the
          foregoing, this Agreement will be binding upon and will inure to the benefit of the
          parties and their respective successors and assigns.&nbsp;&nbsp;</span></p>

          <p class="c15"><span class="c0 c4">11.6 U.S. Government End Users.</span><span class=
          "c0">&nbsp; The Software and any other software covered under this Agreement are
          "commercial items" as that term is defined at 48 C.F.R. 2.101, consisting of
          "commercial computer software" and "commercial computer software documentation" as such
          terms are used in 48 C.F.R. 12.212.&nbsp; Consistent with 48 C.F.R. 12.212 and 48
          C.F.R. 227.7202-1 through 227.7202-4, all U.S. Government end users acquire the
          Software and any other software and documentation covered under this Agreement with
          only those rights set forth therein.</span></p>

          <p class="c15"><span class="c0 c4">11.7 Governing Law and Venue.</span><span class=
          "c0">&nbsp; This Agreement will be governed by the laws of the State of California in
          the United States of America, as such laws apply to contracts between California
          residents entered into and performed entirely within California, without regard to
          California&rsquo;s conflict of law principles.&nbsp; The United Nations Convention on
          Contracts for the International Sale of Goods and the Uniform Computer Information
          Transactions Act (UCITA) do not apply to this Agreement.&nbsp; Any action or proceeding
          arising from or relating to this Agreement must be brought exclusively in a federal
          court in the Northern District of California or in state court in San Francisco County,
          California, and each party irrevocably consents to such personal jurisdiction and
          waives all objections thereto.&nbsp; The parties hereto have expressly agreed that this
          Agreement will be written and construed in the English language.</span></p>

          <p class="c15"><span class="c0 c4">11.8 Marketing Activities.</span><span class=
          "c0">&nbsp; Customer agrees that eXo may from time to time identify Customer (with its
          name, logo and/or trademark) as a eXo customer in or on its Web site, sales and
          marketing materials or press releases, subject to Customer&rsquo;s trademark and logo
          usage guidelines provided by Customer.</span></p>

          <p class="c15"><span class="c0 c4">11.9 Remedies.</span><span class="c0">&nbsp; Except
          as specifically provided otherwise in this Agreement, the parties&rsquo; rights and
          remedies under this Agreement are cumulative.&nbsp; Customer acknowledges that the
          Software contains valuable trade secrets and proprietary information of eXo and that
          any actual or threatened disclosure or misapplication of such Software or Confidential
          Information will constitute immediate and irreparable harm to eXo for which monetary
          damages would be an inadequate remedy and for which eXo will be entitled to seek
          injunctive relief.&nbsp; If any legal action is brought to enforce this Agreement, the
          prevailing party will be entitled to receive its attorneys&rsquo; fees, court costs,
          and other collection expenses, in addition to any other relief it may
          receive.</span></p>

          <p class="c15"><span class="c0 c4">11.10 Waivers.</span><span class="c0">&nbsp; All
          waivers must be in writing.&nbsp; Any waiver or failure to enforce any provision of
          this Agreement on one occasion will not be deemed a waiver of any other provision or of
          such provision on any other occasion.</span></p>

          <p class="c15"><span class="c0 c4">11.11 Severability.</span><span class="c0">&nbsp; If
          any provision of this Agreement is adjudicated to be unenforceable, such provision will
          be changed and interpreted to accomplish the objectives of such provision to the
          greatest extent possible under applicable law and the remaining provisions will
          continue in full force and effect.&nbsp; Without limiting the generality of the
          foregoing, Customer agrees that Section 9 will remain in effect notwithstanding the
          unenforceability of any provision in Section 8.3.</span></p>

          <p class="c15"><span class="c0 c4">11.12 Force Majeure.</span><span class="c0">&nbsp;
          Except for Customer's obligations to pay eXo hereunder, neither party shall be liable
          to the other party for any failure or delay in performance caused by reasons beyond its
          reasonable control to the extent the occurrence is caused by fires, floods, epidemics,
          famines, earthquakes, hurricanes and other natural disasters or acts of God; regulation
          or acts of any civilian or military authority or act of any self-regulatory authority;
          wars, terrorism, riots, civil unrest, sabotage, theft or other criminal acts of third
          parties.</span></p>

          <p class="c15"><span class="c0 c4">11.13 Entire Agreement.</span><span class=
          "c0">&nbsp; This Agreement (including each Order Form, and attachment thereto)
          constitutes the entire agreement between the parties regarding the subject hereof and
          supersedes all prior or contemporaneous agreements, understandings and communications,
          whether written or oral.&nbsp; This Agreement may be amended only by a written document
          signed by both parties.&nbsp; The terms of this Agreement will control over any
          conflicting provisions in an Order Form or any standard terms and conditions set forth
          on either party&rsquo;s form documents, including any purchase order or click-through
          agreement contained on a Web site and any conflicting terms in any
          &ldquo;click-to-accept&rdquo; end user license agreement that may be embedded within
          the Software, except for terms regarding Open Source Software which are incorporated
          herein by reference under Section 3.4 (&ldquo;Open Source Software&rdquo;).</span></p>

          <p class="c23 c6"></p>

          <p class="c7"><span class="c0 c4">Appendix 1: Subscription Services</span></p>

          <p class="c9 c6 c14"></p>

          <p class="c1"><span class="c0 c4">1. &nbsp;eXo Subscription Services</span></p>

          <p class="c1"><span class="c0">The Subscription Services are intended only for use by
          Customer (including through its contractors and agents) and for the benefit of the
          Customer and only for the Installed Systems (as defined below) for which Customer has
          purchased a Subscription. &nbsp;Any unauthorized use of the Subscription Services will
          be deemed to be a material breach of this Agreement. Each Installed Systems running eXo
          Software will require an active Subscription.</span></p>

          <p class="c1"><span class="c0">During the time that Customer has paid the applicable
          annual Subscription fees, Customer will receive access to (a) the applicable eXo
          Software via the eXo portal, (b) the applicable Software Updates, when and if
          available, via the eXo portal, and (c) the applicable level of Subscription Services
          described in Section 2 herein. &nbsp;eXo only provides production Subscription Services
          for generally available Software. &nbsp;</span></p>

          <p class="c1"><span class="c0">1.1 Installed Systems</span></p>

          <p class="c1"><span class="c0">For purposes of the Subscription Services described in
          this Appendix, the term &ldquo;Installed System&rdquo; means a group of CORE Processors
          (e.g., up to 64 or up to 256) for which Customer is receiving Subscription
          Services.</span></p>

          <p class="c1"><span class="c0">1.2 Subscription Services Start Date</span></p>

          <p class="c1"><span class="c0">Unless otherwise agreed in an Order Form, the
          Subscription Services will begin on the date Customer purchases the Subscription as set
          forth in the applicable Order Form.</span></p>

          <p class="c1"><span class="c0">1.3 Development Purposes</span></p>

          <p class="c1"><span class="c0">&ldquo;Development&rdquo; means using the Software for
          the specific purpose of developing, prototyping and demonstrating software or hardware
          that runs with or on the Software.</span></p>

          <p class="c1"><span class="c0">1.4 Production Purposes</span></p>

          <p class="c1"><span class="c0">&ldquo;Production&rdquo; means using the Software in a
          production, pre production and integration environment, generally using live data
          and/or applications for a purpose other than development and/or prototyping software or
          hardware.</span></p>

          <p class="c1"><span class="c0 c4">2. Subscription Services details</span></p>

          <table cellpadding="0" cellspacing="0" class="c26">
            <tbody>
              <tr class="c16">
                <td class="c18">
                  <p class="c7"><span class="c0 c4">&nbsp;Developer Subscription</span></p>

                  <p class="c7"><span class="c0 c4">Standard</span></p>

                  <p class="c7 c6"></p>
                </td>

                <td class="c34">
                  <p class="c9 c6"></p>

                  <ol class="c19" start="1">
                    <li class="c9 c11"><span class="c0">Access to entire eXo Software portfolio
                    for up to five (5) developers</span></li>

                    <li class="c9 c11"><span class="c0">20 support cases included</span></li>

                    <li class="c9 c11"><span class="c0">Access to developer Documentation and
                    Knowledge Base</span></li>

                    <li class="c9 c11"><span class="c0">Maintenance benefits (As defined in
                    section 5)</span></li>

                    <li class="c9 c11"><span class="c0">Support:</span></li>
                  </ol>

                  <ol class="c33" start="1">
                    <li class="c9 c24"><span class="c0">Availability: 8h-18h GMT, from Monday to
                    Friday, excluding eXo holidays (&ldquo;Business Day&rdquo;)</span></li>

                    <li class="c9 c24"><span class="c0">Customer Named Contacts: &nbsp;Two
                    (2)</span></li>

                    <li class="c9 c24"><span class="c0">Target response time: Two (2) Business
                    Days</span></li>

                    <li class="c9 c24"><span class="c0">Limited to Development purposes and for 1
                    project only</span></li>
                  </ol>

                  <p class="c9 c6"></p>

                  <p class="c9 c6"></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c18">
                  <p class="c7"><span class="c0 c4">Developer Subscription</span></p>

                  <p class="c7"><span class="c0 c4">Advanced</span></p>

                  <p class="c7 c6"></p>
                </td>

                <td class="c34">
                  <p class="c9 c6"></p>

                  <ol class="c19" start="6">
                    <li class="c9 c11"><span class="c0">Access to entire eXo Software portfolio
                    for up to ten (10) developers</span></li>

                    <li class="c9 c11"><span class="c0">50 support cases included</span></li>

                    <li class="c9 c11"><span class="c0">Access to developer Documentation and
                    Knowledge Base</span></li>

                    <li class="c9 c11"><span class="c0">Maintenance benefits (As defined in
                    section 5)</span></li>

                    <li class="c9 c11"><span class="c0">Designated contact within eXo support
                    team</span></li>

                    <li class="c9 c11"><span class="c0">Support :</span></li>
                  </ol>

                  <ol class="c33" start="1">
                    <li class="c9 c24"><span class="c0">Availability: eXo Business Days,
                    excluding eXo holidays</span></li>

                    <li class="c9 c24"><span class="c0">Customer Named Contacts: &nbsp;Four
                    (4)</span></li>

                    <li class="c9 c24"><span class="c0">Target response time: One (1) Business
                    Day</span></li>

                    <li class="c9 c24"><span class="c0">Limited to Development purposes and for 1
                    project only</span></li>
                  </ol>
                </td>
              </tr>
            </tbody>
          </table>

          <p class="c1 c6"></p>

          <table cellpadding="0" cellspacing="0" class="c26">
            <tbody>
              <tr class="c16">
                <td class="c38">
                  <p class="c7"><span class="c0 c4">Subscription</span></p>

                  <p class="c7"><span class="c0 c4">Standard</span></p>

                  <p class="c7 c6"></p>

                  <p class="c7 c6"></p>

                  <p class="c7"><span class="c0 c4">Available for&nbsp;:</span></p>

                  <p class="c7"><span class="c0 c4">eXo Platform Professional</span></p>

                  <p class="c7"><span class="c0 c4">eXo Platform Express</span></p>

                  <p class="c7"><span class="c0 c4">eXo Platform for SMB</span></p>

                  <p class="c7"><span class="c0 c4">eXo Platform for Jboss<br /></span></p>
                </td>

                <td class="c32">
                  <p class="c9 c6"></p>

                  <ol class="c19" start="1">
                    <li class="c9 c11"><span class="c0">Access to certified Production-ready
                    Software</span></li>

                    <li class="c9 c11"><span class="c0">Multi-year support and Update
                    policies</span></li>

                    <li class="c9 c11"><span class="c0">Access to user, IT operation
                    Documentation</span></li>

                    <li class="c9 c11"><span class="c0">Certified Updates, patches and bug fixes
                    through Maintenance benefits program &nbsp; (As defined in section
                    5)</span></li>
                  </ol>

                  <ol class="c19" start="12">
                    <li class="c9 c11"><span class="c0">eXo Business Day Production Support
                    &nbsp;(As defined in section 3)</span></li>

                    <li class="c9 c11"><span class="c0">Unit: 8, 16 or 64 Core Processor
                    band</span></li>
                  </ol>

                  <p class="c9 c6 c35"></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c38">
                  <p class="c7"><span class="c0 c4">Subscription</span></p>

                  <p class="c7"><span class="c0 c4">Premium</span></p>

                  <p class="c7 c6"></p>

                  <p class="c7 c6"></p>

                  <p class="c7"><span class="c0 c4">Available for&nbsp;:</span></p>

                  <p class="c7"><span class="c0 c4">eXo Platform Professional</span></p>

                  <p class="c7"><span class="c0 c4">eXo Platform Express</span></p>

                  <p class="c7"><span class="c0 c4">eXo Platform for SMB</span></p>

                  <p class="c7"><span class="c0 c4">eXo Platform for Jboss</span></p>
                </td>

                <td class="c32">
                  <p class="c9 c6"></p>

                  <ol class="c19" start="5">
                    <li class="c9 c11"><span class="c0">Access to certified Production-ready
                    Software</span></li>

                    <li class="c9 c11"><span class="c0">Multi-year support and Update
                    policies</span></li>

                    <li class="c9 c11"><span class="c0">Access to user, &nbsp;IT operation
                    Documentation</span></li>

                    <li class="c9 c11"><span class="c0">Certified Updates, patches and bug fixes
                    through Maintenance benefits program &nbsp;(As defined in section
                    5)</span></li>
                  </ol>

                  <ol class="c19" start="14">
                    <li class="c9 c11"><span class="c0">24/7 Production Support &nbsp;(As defined
                    in section 3)</span></li>

                    <li class="c9 c11"><span class="c0">Unit: 8, 16 or 64 Core Processor
                    band</span></li>
                  </ol>

                  <p class="c9 c6 c35"></p>
                </td>
              </tr>
            </tbody>
          </table>

          <p class="c1 c6"></p>

          <table cellpadding="0" cellspacing="0" class="c26">
            <tbody>
              <tr class="c16">
                <td class="c37">
                  <p class="c7"><span class="c0 c4">Technical Account Manager</span></p>

                  <p class="c7 c6"></p>
                </td>

                <td class="c5">
                  <p class="c6 c9"></p>

                  <ol class="c19" start="16">
                    <li class="c9 c11"><span class="c0">Designated technical account
                    leader</span></li>

                    <li class="c9 c11"><span class="c0">Availability: eXo Business Days,
                    excluding eXo holidays</span></li>

                    <li class="c9 c11"><span class="c0">Target response time: Four (4)
                    hours</span></li>

                    <li class="c9 c11"><span class="c0">On-Site Coverage: &nbsp;Two (2) on-site
                    technical reviews per year by eXo</span></li>

                    <li class="c9 c11"><span class="c0">Customer &nbsp;Named Contacts : Two
                    (2)</span></li>

                    <li class="c9 c11"><span class="c0">Scope of Coverage: &nbsp;</span></li>
                  </ol>

                  <ol class="c33" start="1">
                    <li class="c9 c24"><span class="c0">Centralized management of Subscription
                    Services by a team of skilled eXo support engineers familiar with
                    Customer&rsquo;s technical environment.</span></li>

                    <li class="c9 c24"><span class="c0">Early identification of issues related to
                    the deployment of eXo Software (beta testing, bug/feature
                    escalation/resolution).</span></li>

                    <li class="c9 c24"><span class="c0">Access to current information relating to
                    eXo&rsquo;s technology and development plans.</span></li>

                    <li class="c9 c24"><span class="c0">eXo liaison for Customer into eXo&rsquo;s
                    product engineering team</span></li>
                  </ol>

                  <ol class="c19" start="22">
                    <li class="c9 c11"><span class="c0">Requires an active Subscription.
                    &nbsp;(e.g. eXo Platform &nbsp;Production Subscription Premium)</span></li>
                  </ol>

                  <p class="c9 c6 c20"></p>
                </td>
              </tr>
            </tbody>
          </table>

          <p class="c1 c6"></p>

          <p class="c1"><span class="c0 c4">3. Support</span></p>

          <p class="c12"><span class="c0">3.1 Technical Support Procedures</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">Level One, Two and Three Subscription services will be
          provided in the English language only. eXo will respond according to the support
          collaboration targets and guidelines as defined here after. eXo will specify initial
          technical escalation contacts, which may be updated from time to time , which may be in
          the form of electronic mail.</span></p>

          <ol class="c19" start="1">
            <li class="c12 c11"><span class="c0 c4">Level One Support</span><span class=
            "c0">&nbsp;means the first point of Customer contact, confirms post warranty or
            service contract, and basic troubleshooting, and provides solution or dispatch on
            most Errors.</span></li>

            <li class="c12 c11"><span class="c0 c4">Level Two Support</span><span class=
            "c0">&nbsp;means the escalation point for Level One Support. Level Two Support
            provides support for issues requiring more than thirty (30) minutes to resolve,
            in-depth research and troubleshooting. All Errors with known solutions are Level One
            and Two Support issues.</span></li>

            <li class="c12 c11"><span class="c0 c4">Level Three Support</span><span class=
            "c0">&nbsp;means a category of Errors reported for the Software which, after initial
            analysis is determined most likely to be the result of a design defect with the
            Software or the result of a complex interaction that requires a bug fix as in the eXo
            Software maintenance program.</span></li>
          </ol>

          <p class="c12 c6 c20"></p>

          <p class="c12"><span class="c0">3.2 Support Incident Response by Severity</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">Incident severity levels (defined below) are utilized
          in establishing the Error impact to the Customer upon Error receipt and will be used to
          set expectations between Customer and eXo. Severities are established by eXo in
          accordance with the Severity Level definitions below during escalation and are subject
          to change during the life of each specific incident.</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">3.3 Technical Support Engagement</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">To help ensure a smooth transition during technical
          collaboration or escalation, it is essential that all parties remain engaged until the
          next level is fully engaged, including access to all relevant contact information and
          technical activity to date.</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">3.4 Solution Delivery</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">Unless otherwise set forth in an applicable Order Form,
          (1) eXo will be the primary source for communication with Customer' and (2) Updates,
          when and if available, will be delivered to Customer via eXo Network. Customer will
          provide eXo with two (2) Named Contacts for escalation of Customer issues related to
          the eXo Software. Customer will provide to eXo data, anecdotes, and other information
          reasonably necessary to enable eXo to evaluate the level of customer service being
          provided to 'Customer'.</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">3.5 Support Scope of Coverage</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">Production Support consists of assistance for
          installation, usage, configuration and diagnosis on the applicable Software. Support
          does not include assistance with code development, system and/or network design,
          architectural design, upgrade or for third party software made available with eXo
          Software. eXo does not provide maintenance and/or support for Software that has been
          modified or that is running on hardware that is not supported.</span></p>

          <p class="c12 c6"></p>

          <p class="c9"><span class="c0">Development Support consists of assistance for
          installation, usage, configuration, code development guidelines, and diagnosis on the
          applicable Software. Requests for architecture, design, development, prototyping,
          deployments and upgrades &nbsp;are not included within the scope of Development
          Support, but rather are available on a consulting basis under the terms of a separate
          agreement</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0 c4">4. Support Guidelines</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">eXo will use commercially reasonable efforts to provide
          support in accordance with the guidelines set forth in Table below. eXo's Technical
          Support standard business hours ("Standard Business Hours") are 8h-18h GMT, from Monday
          to Friday, excluding eXo holidays.</span></p>

          <p class="c6 c12"></p>

          <p class="c12"><span class="c0 c4">Table: Support Guidelines</span></p>

          <table cellpadding="0" cellspacing="0" class="c26">
            <tbody>
              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Production Support</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0 c4">Standard</span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0 c4">Premium</span></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Hours of Coverage</span><span class=
                  "c0">&nbsp;</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0">Standard Business Hours</span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0">Standard Business Hours &amp; 24x7</span></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Support Channel</span><span class=
                  "c0">&nbsp;</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0">Web</span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0">Web and Phone</span></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Number of Cases</span><span class=
                  "c0">&nbsp;</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0">Unlimited</span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0">Unlimited</span></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Number of Named Contacts</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0">3<br /></span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0">5<br /></span></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Software Maintenance</span><span class=
                  "c0">&nbsp;</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0">via eXo management portal</span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0">via eXo management portal</span></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9 c6"></p>

                  <p class="c9"><span class="c0 c4">Target Response Times and
                  Guidelines:</span><span class="c0">&nbsp;</span></p>
                </td>

                <td class="c2">
                  <p class="c9"><span class="c0">&nbsp;</span></p>
                </td>

                <td class="c13">
                  <p class="c9 c6"></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Severity 1 (Blocker):</span><span class=
                  "c0">&nbsp;An Error which severely impacts Customer&rsquo;s production
                  environment (such as loss of production data) or in which Customer&rsquo;s
                  production systems are not functioning. The situation halts Customer&rsquo;s
                  business operations, and no procedural work around exists.</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0">4 Business Hours</span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0">1 hour on a 24x7 basis</span></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Severity 2 (Major):</span><span class=
                  "c0">&nbsp;An Error where Customer&rsquo;s system is functioning but in a
                  severely reduced capacity. The situation is causing a high impact to portions
                  of Customer&rsquo;s business operations, and no procedural work around
                  exists.</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0">1 Business Day</span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0">4 Business Hours</span></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Severity 3 (Minor):</span><span class=
                  "c0">&nbsp;An Error which involves partial, non-critical functionality loss of
                  a production or development system. There is a medium-to-low impact on
                  Customer&rsquo;s business, but Customer&rsquo;s business continues to function,
                  including by using a procedural work around.</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0">2 Business Days</span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0">2 Business Day</span></p>
                </td>
              </tr>

              <tr class="c16">
                <td class="c10">
                  <p class="c9"><span class="c0 c4">Severity 4 (None/Info):</span><span class=
                  "c0">&nbsp;A general usage question, reporting of a documentation error or
                  recommendation for a future product enhancement or modification. There is
                  low-to-no impact on Customer&rsquo;s business or the performance or
                  functionality of Customer&rsquo;s system.</span></p>
                </td>

                <td class="c2">
                  <p class="c7"><span class="c0">3 Business Days</span></p>
                </td>

                <td class="c13">
                  <p class="c7"><span class="c0">3 Business Days</span></p>
                </td>
              </tr>
            </tbody>
          </table>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">4.1 Support Processes</span></p>

          <p class="c12"><span class="c0">Both Customer and eXo will document and maintain
          support contact detail, Error reporting and status procedures, management escalation
          contacts, Error resolution process flows and service level expectations.</span></p>

          <p class="c12"><span class="c0">Support Processes are ruled by the eXo Subscription
          Services description made available at
          http://www.exoplatform.com/support-operations</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0 c4">5. Maintenance Benefits</span></p>

          <p class="c12 c6"></p>

          <p class="c12"><span class="c0">During the time that Customer has paid the applicable
          annual Subscription fees, eXo shall provide to Customer copyrighted patches and Updates
          for the installed Software (including any related Documentation) which are commercially
          released. Maintenance benefits are ruled by the eXo maintenance program visible at
          http://www.exoplatform.com/maintenance-program. eXo will provide Updates for a prior
          Release for at least one (1) year after a new Release is made available, except when
          specified as "early release" after which eXo may in its sole discretion discontinue
          Subscription Services for that prior Version.</span></p>

          <p class="c1 c6"></p>

          <p class="c23 c6"></p>

          <p class="c6 c23"></p>

          <p class="c1 c6"></p>
            
          </div>
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
