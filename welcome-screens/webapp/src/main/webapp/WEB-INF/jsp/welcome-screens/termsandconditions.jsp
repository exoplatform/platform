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
<%@ page import="java.net.URLEncoder" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="org.exoplatform.container.PortalContainer" %>
<%@ page import="org.exoplatform.services.resources.ResourceBundleService" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.gatein.common.text.EntityEncoder" %>
<%@ page language="java" %>
<%
    String contextPath = request.getContextPath();
    String lang = request.getLocale().getLanguage();
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=lang%>" lang="<%=lang%>">
	<head>
		<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
		<title>eXo Subscription Agreement</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

		<link href="<%=request.getContextPath()%>/skin/css/termsandconditions.css" rel="stylesheet" type="text/css"/>
	</head>
	<body>
		<div class="backLight"></div>
		<div class="uiWelcomeBox" id="AccountSetup">
			<div class="header">Terms and Conditions Agreement</div>
			<div class="content" id="AccountSetup">

				<p class="c15 c17"><span class="c0 c4">eXo Platform SAS</span><span class="c0"><br/>
						  stock company with a capital of 184,588 euros, registered with the Vannes Commerce Registry under the number B 450 511 506, 
						  with its registered seat at Parc d’Innovation de Bretagne Sud, 56038 Vannes - France</span></p>

				<p class="c15 c17"><span class="c0 c4">Master Subscription Agreement</span></p>

				<p class="c9"><span class="c0">PLEASE READ THIS MASTER SUBSCRIPTION AGREEMENT BEFORE
						  PURCHASING OR USING THE PRODUCTS OR SERVICES. BY USING OR PURCHASING THE PRODUCTS OR
						  SERVICES, CUSTOMER SIGNIFIES ITS ASSENT TO THIS AGREEMENT. IF YOU ARE ACTING ON BEHALF
						  OF AN ENTITY, YOU REPRESENT THAT YOU HAVE THE AUTHORITY TO ENTER INTO THIS
						  AGREEMENT ON BEHALF OF THAT ENTITY. IF CUSTOMER DOES NOT ACCEPT THE TERMS OF THIS
						  AGREEMENT, THEN IT MUST NOT PURCHASE NOR USE THE PRODUCTS OR SERVICES.</span></p>

				<p class="c9"><span class="c0"><br/>
						  This</span> <span class="c0 c4">Master Subscription Agreement</span> <span class="c0">(the</span> <span
						class="c0 c4">&ldquo;Agreement&rdquo;</span><span class="c0">)
						  is entered into by and between eXo&nbsp;Platform SAS, with its principal place of
						  business at PIBS, Le Prisme 56038 Vannes - France
						  (</span><span class="c0 c4">&ldquo;eXo&rdquo;</span><span class="c0">) and the
						  purchaser or user of eXo product and/or services that accepts the terms of this
						  Agreement (</span><span class="c0 c4">&ldquo;Customer&rdquo;</span><span class="c0">).
						  The effective date of this Agreement (<span class="c0 c4">"Effective Date"</span>) is the earlier of these two dates: 
						  the date of signature or acceptance of this Agreement by entering into an Order Form by the Customer or 
						  the date of use of eXo products and/or services by the Customer.</span></p>

				<p class="c15"><span class="c0">Whereas eXo and Customer desire to establish certain
						  terms and conditions under which Customer will, from time to time, be licensed software and
						  obtain services from eXo;</span></p>

				<p class="c15"><span class="c0">Now, therefore, for good and valuable consideration,
						  the receipt and sufficiency of which is hereby acknowledged, Customer and eXo agree as
						  follows:</span></p>

				<p class="c15"><span class="c0 c4">1. Definitions</span></p>

				<p class="c15"><span class="c0">Capitalized terms used in this Agreement are defined in
						  this Section 1 or the Section in which they are first used:</span></p>

				<p class="c15"><span class="c0 c4">1.1 &ldquo;Activation Key&rdquo;</span><span class="c0">&nbsp;means a file evidencing a grant of one or more Licenses by eXo to Customer
						  for the Term, and provided to Customer when the Subscription is purchased.</span></p>

				<p class="c15"><span class="c0 c4">1.2 &ldquo;CORE Processor&rdquo;</span><span class="c0">&nbsp;means the virtual or real unit that reads and executes program
						  instructions.</span></p>

				<p class="c15"><span class="c0 c4">1.3 &ldquo;Documentation&rdquo;</span> <span class="c0">means the standard end-user technical documentation and specifications that eXo
						  supplies with the Software, as revised by eXo from time to time by eXo. Advertising and
						  marketing materials are not Documentation.</span></p>

				<p class="c15"><span class="c0 c4">1.4 &ldquo;Error&rdquo;</span><span class="c0">&nbsp;means a reproducible failure of the Software to perform in substantial
						  conformity with its Documentation, and considered as such by eXo.</span></p>

				<p class="c15"><span class="c0 c4">1.5 &ldquo;Error&rdquo;</span><span class="c0">&nbsp;means an extension module or plugin published by eXo enhancing the Software 
						  with new functionalities.</span></p>

				<p class="c15"><span class="c0 c4">1.6 &ldquo;License&rdquo;</span><span class="c0">&nbsp;means a license granted, in accordance with a Subscription, by eXo to
						  Customer to allow installation and use of the Software.</span></p>
						  
				<p class="c15"><span class="c0 c4">1.7 &ldquo;License&rdquo;</span><span class="c0">&nbsp;means various third-party software components licensed under the terms of 
						  applicable license agreements, whether open source or not, included in the materials relating to such software. Third-Party Software is composed of individual 
						  software components, each of which has its own copyright and its own applicable license conditions.</span></p>

				<p class="c15"><span class="c0 c4">1.8 &ldquo;Order Form&rdquo;</span><span class="c0">&nbsp;means an order form, whether in written or electronic form, composed of one 
						  or multiple purchase orders, defining the Software, and/or the services which will be delivered to the Customer by eXo, in accordance with this Agreement and the 
						  specific conditions written in this Order Form.</span></p>

				<p class="c15"><span class="c0 c4">1.9 &ldquo;Software&rdquo;</span><span class="c0">&nbsp;means the tested and certified software 
						  in object code format provided by eXo to Customer, as specified on the Order Form, pursuant to this Agreement as of the Effective Date or a future date, including
						  any Documentation incorporated therein, and Updates to such software that eXo may provide to Customer from time to time as part of eXo Products Maintenance Program,  
						  as defined at <span class="c0 c25"><a class="c8" target="_blank" href="http://www.exoplatform.com/maintenance-program">http://www.exoplatform.com/maintenance-program</a></span>. 
						  For the avoidance of doubt and unless otherwise specified, 
						  Third-Party Software and/or eXo Add-Ons, that may be available through the Software, (a) do not form a part of the Software, (b) are solely governed by their own licenses  
						  and/or terms and conditions, (c) are not covered by this Agreement, and (d) may be downloaded and installed by the Customer under its sole responsibility and liability. As a limited 
						  exception to the aforesaid, eXo Add-Ons listed in <span class="c0 c4">Appendix 3 (&ldquo;eXo Official Add-Ons&rdquo;)</span> are governed and supported under the conditions of this 
						  Agreement under the same Subscription Service Level as the Subscription applicable to the Software.</span></p>

				<p class="c15"><span class="c0 c4">1.10 &ldquo;Subscription&rdquo;</span><span class="c0">&nbsp;means the license for the Software and , if applicable, the access to Support Services ordered 
						  and paid for by Customer and provided by eXo as specified on the Order Form. </span></p>


				<p class="c15"><span class="c0 c4">1.11 &ldquo;Support Services&rdquo;</span><span class="c0">&nbsp;means the support services provided
						  by eXo to Customer as part of the Subscription and as further defined in Section 2.6 and <span class="c0 c25"><a class="c8" target="_blank" href=
								  "http://www.exoplatform.com/SLA">http://www.exoplatform.com/SLA</a></span><span class="c0">&nbsp; and/or as may be specified on the applicable Order Form.</span>
				</p>
				
				<!--
				<p class="c15"><span class="c0 c4">1.11 &ldquo;Named Contact&rdquo;</span> <span class="c0">means an employee of Customer who is proficient on the Software.</span>
				</p>

				<p class="c15"><span class="c0 c4">1.7 &ldquo;Open Source Software&rdquo;</span>
						  <span class="c0">means various open source software components licensed under the terms
						  of applicable open source license agreements included in the materials relating to such
						  software.&nbsp; Open Source Software is composed of individual software components,
						  each of which has its own copyright and its own applicable license
						  conditions.</span></p>

				<p class="c15"><span class="c0 c4">1.13 &ldquo;Release&rdquo;</span><span class="c0">&nbsp;means a later version of the Software identified by a change in the first
						  digit to the left of the decimal point ((X).xx); Version Release means a later version
						  of the Software identified by a change in the first digit to the right of the decimal
						  point ((x).Xx); and Maintenance Release means a later version of the Software
						  identified by a change in the second digit to the right of the decimal point
						  ((x).xX).</span></p>
				-->

				<p class="c15"><span class="c0 c4">1.12 &ldquo;Term&rdquo;</span><span class="c0">&nbsp;means the period of time for the Subscription as specified in the Order Form.</span></p>

				<p class="c15"><span class="c0 c4">1.13 &ldquo;Update&rdquo;</span><span class="c0">&nbsp;means a Major Release, Minor Release or Maintenance Fix of the Software. 
				          <span class="c0 c4">&ldquo;Major Release&rdquo;</span> means a later version of the Software identified by a change in the first digit (X) of the 
				          identified update according to the (X.y.z) schema. 
				          <span class="c0 c4">&ldquo;Minor Release&rdquo;</span> means a later version of the Software identified by a change in the second digit (Y) of the 
				          identified update according to the (x.Y.z) schema. 
				          <span class="c0 c4">&ldquo;Maintenance Fix&rdquo;</span> means a new version of the Software identified by a change in the third digit (Z) of the 
				          identified update according to the (x.y.Z) schema.</span></p>
				          
				<p class="c15"><span class="c0 c4">1.14 &ldquo;Registered User&rdquo;</span><span class="c0">&nbsp;means  a physical or virtual person or program, named or anonymous, 
						  who establishes a network connection with the server on which the Software is installed, with the objective to make partial or complete use of the Software.</span></p>

				<p class="c15"><span class="c0 c4">2. General Terms</span></p>

				<p class="c15"><span class="c0 c4">2.1 Scope of Agreement.</span><span class=
																							   "c0">&nbsp; This Agreement governs all transactions between the parties with respect to
						  the Software and Services provided hereunder.</span></p>

				<p class="c15"><span class="c0 c4">2.2 Orders.</span><span class="c0">&nbsp; Customer
						  may from time to time place orders with eXo to purchase a Subscription at eXo&rsquo;s
						  then-current prices. Customer may transmit such orders to eXo by mail,
						  fax, email or other electronic channels. Customer may, for its convenience,
						  submit orders using its standard forms, but no terms, provisions or conditions of any
						  order document, acknowledgement or other business form that Customer may use in connection
						  with the acquisition or licensing of the Software will have any effect on the rights,
						  duties or obligations of the parties under, or otherwise modify, this Agreement,
						  regardless of any failure of eXo to object to such terms, provisions or
						  conditions. Any such additional or conflicting terms and conditions on any
						  Customer order document, acknowledgement or other business form are hereby rejected by
						  eXo.</span></p>

				<p class="c15"><span class="c0 c4">2.3 Acceptance of Orders.</span><span class="c0">&nbsp; eXo may accept orders in its sole discretion by sending to Customer an
						  Order Form confirming the particulars of the order.</span>
				</p>

				<p class="c15"><span class="c0 c4">2.4 Delivery.</span><span class="c0">&nbsp; Delivery
						  of the Software will be from eXo&rsquo;s Customer Portal web site. The Term is
						  specified in the Order Form. The Software will be deemed accepted by Customer upon delivery of the
						  Activation Key.&nbsp;</span></p>

				<p class="c15"><span class="c0 c4">2.5 Installation Services.</span><span class=
																								  "c0">&nbsp; Customer will be responsible for installing the Software on its computers
						  as permitted under this Agreement.Installation services may be purchased on at
						  time-and-materials basis at eXo&rsquo;s then-current rates as specified on the
						  applicable Order Form.</span></p>

				<p class="c15"><span class="c0 c4">2.6 Support Services.</span><span class=
																								  "c0">&nbsp; During the time that Customer has paid the applicable annual Subscription
						  fees, eXo will provide Customer with Support Services for the Software under the terms
						  outlined in the Support Policy as posted at </span><span class=
																												   "c0 c25"><a
						class="c8" target="_blank" href=
						"http://www.exoplatform.com/SLA">http://www.exoplatform.com/SLA</a></span><span class="c0">. Such Support Services are provided to Customer solely for Customer&rsquo;s
						  internal use, and Customer may not use the Software or Support Services to supply
						  any consulting, support or training services to any third party.</span></p>

				<p class="c15"><span class="c0 c4">2.7 Exclusions.</span><span class="c0">&nbsp; eXo
						  will have no obligation to correct Errors caused by:&nbsp; (a) improper installation of
						  the Software; (b) altered or modified Software, unless altered or modified by eXo; (c)
						  use of the Software in a manner inconsistent with its Documentation or this Agreement;
						  (d) any combination of the Software with hardware or software not specified in the
						  Documentation; or (e) defects in the Software due to accident, hardware malfunction,
						  abuse or improper use.</span></p>

				<p class="c15"><span class="c0 c4">2.8 Additional Services.</span><span class="c0">&nbsp; Should Customer request that eXo provide services in connection
						  with problems (a) caused by the factors listed in Section 2.7 or (b) that are otherwise
						  beyond the scope of the Support Services or this Agreement, Customer will pay for
						  such services eXo agrees to perform on a time-and-materials basis at eXo&rsquo;s
						  then-current rates.</span></p>

				<p class="c15"><span class="c0 c4">2.9 Customer Obligations.</span><span class="c0">&nbsp;</span></p>

				<p class="c15"><span class="c0 c4">2.9.1</span> <span class="c0">As a condition to
						  eXo&rsquo;s provision of the Support Services, Customer agrees to assure necessary
						  competence for use of the Software. Training courses (Appendix 2) provide the Customer’s technical crew with the set of knowledge required.</span></p>

				<p class="c15"><span class="c0 c4">2.9.2</span> <span class="c0">As a condition to
						  eXo&rsquo;s provision of the Support Services, Customer must assist eXo in
						  identifying and correcting any Errors, including executing reasonable diagnostic
						  routines in accordance with any instructions provided by eXo. Customer agrees to
						  provide eXo with such cooperation, materials, information, access and support which eXo
						  deems to be reasonably required to allow eXo to successfully provide the Support 
						  Services, including, without limitation, as may be set forth in an applicable Order
						  Form. Customer understands and agrees that eXo&rsquo;s obligations
						  hereunder are expressly conditioned upon Customer providing such cooperation,
						  materials, information, access and support.</span></p>

				<p class="c15"><span class="c0 c4">2.9.3</span> <span class="c0">Customer acknowledges
						  that in order for eXo to provide the Support Services, Customer may be required to
						  license and install certain third party software and provide certain third party
						  hardware that are not provided or licensed by eXo (&ldquo;Third Party
						  Products&rdquo;). eXo may provide Customer with links and instructions for
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
																									   "c0 c4">&nbsp;</span><span
						class="c0">install the Software licensed hereunder (in
						  object code format) with respect for the number of allowed CORE Processors and/or the limitation of Registered Users, 
						  as designated in the applicable Order Form, solely for Customer’s own business purposes;</span></p>

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
						  eXo&rsquo;s intellectual property or other rights;<br/>
						  (b) allow access or permit use of the Software by any users other than Customer&rsquo;s
						  employees, or authorized third-party contractors who are providing services to Customer
						  and agree in writing to abide by the terms of this Agreement, provided further that
						  Customer shall be liable for any failure by such employees and third-party contractors
						  to comply with the terms of this Agreement,<br/>
						  (c) create, develop, license, install, use, or deploy any third party software or
						  services to circumvent, enable, modify or provide access, permissions or rights which
						  violate the technical restrictions of the Software, any additional licensing terms
						  provided by eXo via product documentation, notification, and the terms of this
						  Agreement,<br/>
						  (d) modify or create derivative works based upon the Software,<br/>
						  (e) use the Software in connection with any business operation for which Customer
						  provides services to third parties, or<br/>
						  (f) disclose the results of any benchmark test of the Software to any third party
						  without eXo&rsquo;s prior written approval, unless otherwise expressly permitted
						  herein, provided, however, that the foregoing restriction shall apply to Customer only
						  if Customer is a software or hardware vendor, or Customer is performing testing or
						  benchmarking on the Software.</span></p>

				<p class="c15"><span class="c0 c4">3.4 Third-Party Software.&nbsp;</span> <span class="c0">The Third-Party Software 
						  is licensed under the terms of the applicable 
						  license conditions, whether open source or not, and/or copyright notices that can be found in the licenses file, 
						  the documentation or other materials accompanying the Third-Party Software. Copyrights to the Third-Party 
						  Software are held by copyright holders indicated in the copyright notices in the corresponding source files or 
						  in the licenses file or other materials accompanying the Third-Party Software.</span></p>

				<p class="c15"><span class="c0 c4">4. License Fees and Payment</span></p>

				<p class="c15"><span class="c0 c4">4.1 Subscription Fees.</span><span class="c0">&nbsp;
						  Customer shall pay all fees for each Subscription as specified on the applicable Order
						  Form. Customer may purchase additional Licenses via Subscription by placing any order
						  in accordance with Section 2.2.&nbsp; Any added Licenses will be subject to the
						  following:&nbsp; (i) added Licenses will be coterminous with the pre-existing Term
						  (either the initial Term or the renewal Term); (ii) the Subscription fees for the added
						  Licenses will be the then-current, generally applicable Subscription fee for such; and
						  (iii) any Licenses added in the middle of a billing period will be prorated for that
						  billing period.&nbsp; eXo reserves the right to modify its Subscription fees at any
						  time, upon at least thirty (30) days prior notice to Customer, which notice may be
						  provided by e-mail.</span></p>

				<p class="c15"><span class="c0 c4">4.2 Billing and Renewal.</span><span class="c0">&nbsp; eXo charges 
						  and collects in advance the Subscription fees. eXo will
						  automatically renew and issue an invoice each billing period on the subsequent
						  anniversary of the Subscription unless either party gives written notice of its intent
						  not to renew at least ninety (90) days prior to the end of the current contract
						  term. Upon any renewal, eXo&rsquo;s then-current terms and conditions for the
						  Support Services and this Agreement will apply. The renewal charge will be equal
						  to the then-current number of CORE Processors and/or Registered Users limitation times 
						  eXo&rsquo;s then-current list price 
						  Subscription fee at the time of renewal. Fees for any other services will be
						  charged on an as-quoted basis. All eXo supplied Software and Support
						  Services will only be delivered to Customer electronically through the Internet. Unless
						  otherwise specified on an Order Form, all invoices will be paid within thirty (30) days
						  from the date of the invoice. Fees for Support Services are non-refundable upon payment. 
						  Payments will be made without right of set-off or chargeback. All
						  payments must be made in the currency stated in the Order
						  Form. Late
						  payments will accrue interest at the rate of one and one half percent (1&frac12;%) per
						  month, or, if lower, the maximum rate permitted under applicable law. If payment of any
						  fee is overdue, eXo may also suspend provision of the Support Services until such
						  delinquency is corrected.</span></p>

				<p class="c15"><span class="c0 c4">4.3 Taxes.</span><span class="c0">&nbsp; The amounts
						  payable to eXo under this Agreement do not include any taxes, levies, or similar
						  governmental charges, however designated, including any related penalties and interest
						  (</span><span class="c0 c4">&ldquo;Taxes&rdquo;</span><span class="c0">).&nbsp;
						  Customer will pay (or reimburse eXo for the payment of) all Taxes except taxes on
						  eXo&rsquo;s net income, unless Customer provides eXo a valid state sales/use/excise tax
						  exemption certificate or direct pay permit. If Customer is required to pay any
						  withholding tax, charge or levy in respect of any payments due to eXo hereunder,
						  Customer agrees to gross up payments actually made such that eXo shall receive sums due
						  hereunder in full and free of any deduction for any such withholding tax, charge or
						  levy.&nbsp;</span></p>

				<p class="c15"><span class="c0 c4">4.4 Audit Rights.</span><span class="c0">&nbsp;
						  Customer will maintain accurate records as to its use of the Software as authorized by
						  this Agreement, for at least two (2) years from the last day on which Support 
						  Services expired for the applicable Software.&nbsp; eXo, or persons designated by eXo,
						  will, at any time during the period when Customer is obliged to maintain such records,
						  be entitled to audit such records and to ascertain completeness and accuracy, in order
						  to verify that the Software are used by Customer in accordance with the terms of this
						  Agreement and that Customer has paid the applicable Subscription fees and Support 
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
						  percent (5%) of the Subscription fees payable by Customer to eXo for the period
						  audited.</span></p>

				<p class="c15"><span class="c0 c4">5. Term and Termination</span></p>

				<p class="c15"><span class="c0 c4">5.1 Term.</span><span class="c0">&nbsp; Unless
						  otherwise stated in the applicable Order Form, the Term of this Agreement will begin on
						  the Effective Date and will continue for a firm period of three (3) years. Thereafter, this Agreement 
						  will be automatically renewed for additional terms of one (1) year each under standard list price and 
						  terms in effect at this date, unless notice to the contrary is given in writing ninety (90) days prior to such termination. </span></p>

				<p class="c15"><span class="c0 c4">5.2 Termination for Cause.</span><span class=
																								  "c0">&nbsp; Either party may terminate this Agreement for cause if the other party
						  materially breaches, but only by giving the breaching party written notice of
						  termination and specifying in such notice the alleged material breach.&nbsp; The
						  breaching party will have a grace period of thirty (30) days after such notice is
						  served to cure the breach described therein.&nbsp; If the breach is not cured within
						  the foregoing time period, this Agreement will automatically terminate upon the
						  conclusion of such period.&nbsp; Notwithstanding the foregoing, eXo, in its sole
						  discretion, may terminate this Agreement if Customer violates its obligations under
						  Sections 3 and/or 7.</span></p>

				<p class="c15"><span class="c0 c4">5.3 Effects of Termination.</span><span class="c0">&nbsp; Upon termination 
						  of this Agreement for any reason or expiration:&nbsp; (a) any amounts
						  owed to eXo under this Agreement before such termination or expiration will be immediately due and
						  payable; (b) all License rights granted in this Agreement before such termination or expiration and in 
						  any Order Form will
						  immediately terminate; (c) Customer must immediately stop all use of the Software; (d)
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
						  parties, Customer acknowledges and agrees that the Software, including its sequence,
						  structure, organization, and source code, constitute certain valuable intellectual
						  property rights including copyrights, trademarks, service marks, trade secrets,
						  patents, patent applications, contractual rights of non-disclosure or any other
						  intellectual property or proprietary right, arising of eXo and/or its
						  suppliers.&nbsp; The Software is licensed and not sold to Customer, and no title or
						  ownership to the Software or the intellectual property rights embodied therein passes
						  as a result of this Agreement or any act pursuant to this Agreement.&nbsp; The Software
						  and Documentation are the exclusive property of eXo and/or its suppliers, and all rights,
						  title and interest in and to such not expressly granted to Customer in this Agreement
						  are reserved. &nbsp;eXo owns all copies of the Software, in any form.&nbsp; Nothing in
						  this Agreement will be deemed to grant, under any legal theory, a
						  license under any of eXo&rsquo;s existing or future patents (or the existing or future
						  patents of its suppliers).</span></p>

				<p class="c15"><span class="c0 c4">6.2</span> <span class="c0">Customer acknowledges
						  that in the course of performing any Support Services, eXo may create software or
						  other works of authorship (collectively &ldquo;Work Product&rdquo;). Subject to
						  Customer&rsquo;s rights in the Customer Confidential Information, eXo shall own all
						  right, title and interest in such Work Products, including all intellectual property
						  rights therein and thereto.&nbsp; If any Work Product is delivered to Customer pursuant
						  to or in connection with the performance of Support Services (a
					&ldquo;Deliverable&rdquo;), eXo hereby grants to Customer a license to such Deliverable
						  under the same terms and conditions Customer&rsquo;s license to Software set forth in
						  Section 3 above.</span></p>

				<p class="c15"><span class="c0 c4">6.3</span> <span class="c0">Customer is not
						  obtaining any intellectual property right in or to any materials, works of authorship, software provided by eXo to
						  Customer in connection with the provision to Customer of Support Services
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

				<p class="c15"><span class="c0 c4">8.2 Support Services.&nbsp;</span> <span class="c0">
						  The Support Services shall be deemed to be accepted by Customer upon
						  delivery. eXo warrants that the Support Services to be performed hereunder will be
						  done in a workmanlike manner and shall conform to standards of the industry.
						  eXo&rsquo;s sole liability (and Customer&rsquo;s sole and exclusive remedy) for any
						  breach of this warranty shall be for eXo to re-perform the applicable Subscription
						  Services; provided that eXo is notified in writing of such non-conformity within three
						  (3) days following the performance of the relevant Support Services.</span></p>

				<p class="c15"><span class="c0 c4">8.3 Disclaimer.</span><span class="c0">&nbsp; THE
						  SOFTWARE AND ANY SUPPORT SERVICES PROVIDED HEREUNDER ARE PROVIDED &ldquo;AS
						  IS.&rdquo;&nbsp; EXCEPT FOR THE EXPRESS WARRANTIES PROVIDED IN SECTIONS 8.1 AND 8.2,
						  EXO MAKES NO OTHER WARRANTIES WITH RESPECT TO THE SOFTWARE, SUPPORT SERVICES OR
						  ANY OTHER MATERIAL, INFORMATION OR SERVICES PROVIDED HEREUNDER.&nbsp; EXO HEREBY
						  DISCLAIMS ALL OTHER WARRANTIES, WHETHER EXPRESS, IMPLIED OR STATUTORY, INCLUDING THE
						  IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, ACCURACY,
						  RESULT, EFFORT, TITLE AND NON-INFRINGEMENT.&nbsp; EXO DOES NOT WARRANT THAT ANY
						  SOFTWARE OR ANY SUPPORT SERVICES WILL BE PROVIDED ERROR FREE, WILL OPERATE WITHOUT
						  INTERRUPTION OR WILL FULFILL ANY OF CUSTOMER&rsquo;S PARTICULAR PURPOSES OR
						  NEEDS.&nbsp; CUSTOMER ACKNOWLEDGES THAT IT HAS RELIED ON NO WARRANTIES OTHER THAN THE
						  EXPRESS WARRANTIES SET FORTH IN SECTIONS 8.1 AND 8.2 AND THAT NO WARRANTIES ARE MADE BY
						  ANY OF EXO&rsquo;S SUPPLIERS OR DISTRIBUTORS.&nbsp; CUSTOMER ACKNOWLEDGES AND AGREES
						  THAT THE PRICES OFFERED UNDER THIS AGREEMENT REFLECT THESE NEGOTIATED WARRANTY
						  PROVISIONS.&nbsp; TO THE EXTENT THAT EXO CANNOT DISCLAIM ANY SUCH WARRANTY AS A MATTER
						  OF APPLICABLE LAW, THE SCOPE AND DURATION OF SUCH WARRANTY WILL BE THE MINIMUM
						  PERMITTED UNDER SUCH LAW. TO THE AVOIDANCE OF DOUBT, IN NO EVENT WILL THE APPLICABLE LAW 
						  HAS ANY EFFECT ON THE LIMITATION OF LIABILITY SET FORTH SECTION 9.</span></p>

				<p class="c15"><span class="c0 c4">9. Limitation of Liability.&nbsp;</span>
						  <span class="c0">Neither party will be liable to any other party for any indirect,
						  incidental, special, consequential, punitive or exemplary damages arising out of or
						  related to this Agreement under any legal theory, including but not limited to (I) lost
						  profits, lost data or business interruption, even if such party has been advised of,
						  knows of, or should know of the possibility of such damages, AND (II) ANY CLAIM
						  ATTRIBUTABLE TO ERRORS, OMISSIONS OR OTHER INACCURACIES IN OR DESTRUCTIVE PROPERTIES OF
						  THE SOFTWARE OR ANY SUPPORT SERVICES. &nbsp;REGARDLESS OF THE CAUSE OF ACTION,
						  WHETHER IN CONTRACT, TORT OR OTHERWISE. NEITHER PARTY&rsquo;S TOTAL CUMULATIVE
						  LIABILITY FOR ACTUAL DAMAGES ARISING OUT OF OR RELATED TO THIS AGREEMENT WILL EXCEED
						  THE TOTAL AMOUNT OF SUBSCRIPTION FEES THAT CUSTOMER HAS PAID FOR THE SOFTWARE OR
						  SUPPORT SERVICES GIVING RISE TO SUCH LIABILITY.&nbsp; NEITHER PARTY SHALL BRING
						  ANY CLAIM BASED ON THE SOFTWARE NOR THE SUPPORT SERVICES PROVIDED HEREUNDER MORE
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
						  any third party claim that the Software licensed hereunder infringes any copyrights 
						  registered or issued as of the Effective Date (&ldquo;Infringement
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
						  shall have no liability after the termination of the Agreement, and if the alleged infringement 
						  is based on (1) combination with
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

				<!--
				<p class="c15"><span class="c0 c4">11.1 Non-Solicitation.</span> <span class=
																							   "c0">Customer may not hire, or directly or indirectly solicit or employ, any employee
						  or contractor of eXo who is or was involved in the development, use or provision of
						  Support Services to Customer, without the prior written consent of eXo, for a
						  period of: (i) two (2) years after the termination of this Agreement, or (ii) during
						  the time the employee is employed by eXo and for a period of one (1) year thereafter,
						  whichever is later.</span></p>
				-->

				<p class="c15"><span class="c0 c4">11.1 &nbsp;Notices.</span><span class="c0">&nbsp;
						  All notices under this Agreement must be delivered in writing in person, by courier, or
						  by certified or registered mail (postage prepaid and return receipt requested) to the
						  other party at the address set forth in the applicable Order Form and will be effective
						  upon receipt or three (3) business days after being deposited in the mail as required
						  above, whichever occurs sooner.&nbsp; Either party may change its address by giving
						  written notice of the new address to the other party.</span></p>

				<p class="c15"><span class="c0 c4">11.2 Relationship of the Parties.</span><span class=
																										 "c0">&nbsp; The parties hereto are independent contractors.&nbsp; Nothing in this
						  Agreement shall be deemed to create an agency, employment, partnership, fiduciary or
						  joint venture relationship between the parties.&nbsp; Neither party has the power or
						  authority as agent, employee or in any other capacity to represent, act for, bind or
						  otherwise create or assume any obligation on behalf of the other party for any purpose
						  whatsoever.&nbsp; There are no third party beneficiaries to this
						  Agreement.<br/></span></p>
				<!--
				<p class="c15"><span class="c0 c4">11.4 Compliance with Export Control
						  Laws.</span><span class="c0">&nbsp; Customer acknowledges and agrees that it will
						  comply with all applicable export and import control laws and regulations of the United
						  States and the foreign jurisdiction in which the Software is used and, in particular,
						  Customer will not export or re-export Software without all required United States and
						  foreign government licenses.&nbsp; Customer will defend, indemnify, and hold harmless
						  eXo from any breach of the foregoing.</span></p>
				-->

				<p class="c15"><span class="c0 c4">11.3 Assignments.</span><span class="c0">&nbsp; Customer may
						  assign or transfer any of its rights or delegate any
						  of its duties under this Agreement (including its licenses with respect to the
						  Software) to any third party unless expressly authorized in writing by eXo.&nbsp; Any other attempted assignment or
						  transfer by Customer in violation of the foregoing will be void. Subject to the
						  foregoing, this Agreement will be binding upon and will inure to the benefit of the
						  parties and their respective successors and assigns.&nbsp;&nbsp;</span></p>
				<!--
				<p class="c15"><span class="c0 c4">11.6 U.S. Government End Users.</span><span class=
																									   "c0">&nbsp; The Software and any other software covered under this Agreement are
						  "commercial items" as that term is defined at 48 C.F.R. 2.101, consisting of
						  "commercial computer software" and "commercial computer software documentation" as such
						  terms are used in 48 C.F.R. 12.212.&nbsp; Consistent with 48 C.F.R. 12.212 and 48
						  C.F.R. 227.7202-1 through 227.7202-4, all U.S. Government end users acquire the
						  Software and any other software and documentation covered under this Agreement with
						  only those rights set forth therein.</span></p>
				-->

				<p class="c15"><span class="c0 c4">11.4 Governing Law and Venue.</span><span class="c0">&nbsp; This Agreement 
						  will be governed by the laws of France and the Paris Courts will have exclusive jurisdiction 
						  over any dispute arising out or connected with this Agreement. The parties 
						  hereto have expressly agreed that this Agreement will be construed in the French language.</span></p>

				<p class="c15"><span class="c0 c4">11.5 Marketing Activities.</span><span class=
																								  "c0">&nbsp; Customer agrees that eXo may from time to time identify Customer (with its
						  name, logo and/or trademark) as an eXo customer in or on its web site, sales and
						  marketing materials or press releases, subject to Customer&rsquo;s trademark and logo
						  usage guidelines provided by Customer.</span></p>

				<p class="c15"><span class="c0 c4">11.6 Remedies.</span><span class="c0">&nbsp; Except
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

				<p class="c15"><span class="c0 c4">11.7 Waivers.</span><span class="c0">&nbsp; All
						  waivers must be in writing.&nbsp; Any waiver or failure to enforce any provision of
						  this Agreement on one occasion will not be deemed a waiver of any other provision or of
						  such provision on any other occasion.</span></p>

				<p class="c15"><span class="c0 c4">11.8 Severability.</span><span class="c0">&nbsp; If
						  any provision of this Agreement is adjudicated to be unenforceable, such provision will
						  be changed and interpreted to accomplish the objectives of such provision to the
						  greatest extent possible under applicable law and the remaining provisions will
						  continue in full force and effect.&nbsp; Without limiting the generality of the
						  foregoing, Customer agrees that Section 9 will remain in effect notwithstanding the
						  unenforceability of any provision in Section 8.3.</span></p>

				<p class="c15"><span class="c0 c4">11.9 Force Majeure.</span><span class="c0">&nbsp;
						  Except for Customer's obligations to pay eXo hereunder, neither party shall be liable
						  to the other party for any failure or delay in performance caused by reasons beyond its
						  reasonable control to the extent the occurrence is caused by fires, floods, epidemics,
						  famines, earthquakes, hurricanes and other natural disasters or acts of God; regulation
						  or acts of any civilian or military authority or act of any self-regulatory authority;
						  wars, terrorism, riots, civil unrest, sabotage, theft or other criminal acts of third
						  parties.</span></p>

				<p class="c15"><span class="c0 c4">11.10 Entire Agreement.</span><span class=
																							   "c0">&nbsp; This Agreement (including each Order Form, and attachment thereto)
						  constitutes the entire agreement between the parties regarding the subject hereof and
						  supersedes all prior or contemporaneous agreements, understandings and communications,
						  whether written or oral.&nbsp; This Agreement may be amended only by a written document
						  signed by both parties.&nbsp; The terms of this Agreement will control over any
						  conflicting provisions in an Order Form or any standard terms and conditions set forth
						  on either party&rsquo;s form documents, including any purchase order or click-through
						  agreement contained on a Web site and any conflicting terms in any
					&ldquo;click-to-accept&rdquo; end user license agreement that may be embedded within
						  the Software, except for terms regarding Third-Party Software which are incorporated
						  herein by reference under Section 3.4 (&ldquo;Third-Party Software&rdquo;).</span></p>

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
						  (e.g., up to 64 or up to 256) or a number of Registered Users (e.g, up to 25 or up to 250) 
						  for which Customer is receiving Subscription Services.</span></p>

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

				<!--
				<table cellpadding="0" cellspacing="0" class="c26">
					<tbody>
					<tr class="c16">
						<td class="c18">
							<p class="c7"><span class="c0 c4">&nbsp;Enterprise Basic Subscription</span></p>

							<p class="c7 c6"></p>
						</td>

						<td class="c34">
							<p class="c9 c6"></p>

                            <ol class="c33" start="1">
								<li class="c9 c11"><span class="c0">Access to certified Production-ready Software</span></li>

								<li class="c9 c11"><span class="c0">Access to user, IT operation Documentation</span></li>

								<li class="c9 c11"><span class="c0">Certified Updates, patches and bug fixes through Maintenance benefits
                                    program   (As defined in section 5)</span></li>

								<li class="c9 c11"><span class="c0">Unit: up to 25, 50, 100 or 500 users</span></li>
							</ol>

							<p class="c9 c6"></p>

							<p class="c9 c6"></p>
						</td>
					</tr>
					</tbody>
				</table>
				-->

				<p class="c1 c6"></p>

				<table cellpadding="0" cellspacing="0" class="c26">
					<tbody>
					<tr class="c16">
						<td class="c38">
							<p class="c7"><span class="c0 c4">Enterprise Basic Subscription</span></p>
						</td>

						<td class="c32">
							<!--p class="c9 c6"></p-->

							<ol class="c19" start="1">
								<li class="c9 c11"><span class="c0">Access to certified Production-ready Software</span></li>

								<li class="c9 c11"><span class="c0">Multi-year support and Update policies</span></li>

                                <li class="c9 c11"><span class="c0">Access to Documentation and technical guides <span class="c9 c12">(development, installation, administration)</span></span></li>
                                
                                <li class="c9 c11"><span class="c0">Access to Basic Support Services <span class="c9 c12">(As defined in section 3)</span></span></li>             

                                <li class="c9 c11"><span class="c0">Sotfware certified updates, patches and bug fixes through Maintenance
                                    benefits program <span class="c9 c12">(As defined in section 5)</span></span></li>

                                <li class="c9 c11"><span class="c0">Access to professional services <span class="c9 c12">(As defined in section 6)</span></span></li>

                                <li class="c9 c11"><span class="c0">Authorized deployment typology: Single Server</span></li>

                                <li class="c9 c11"><span class="c0">Authorized environments: Pre-production, Production</span></li>                               

                                <li class="c9 c11"><span class="c0">Unit: Registered user</span></li>

                                <li class="c9 c11"><span class="c0">Registered users: Limited</span></li>


							</ol>

							<!--p class="c9 c6 c35"></p-->
						</td>
					</tr>
					</tbody>
				</table>

				<p class="c1 c6"></p>

                <table cellpadding="0" cellspacing="0" class="c26">
                    <tbody>
                    <tr class="c16">
                        <td class="c38">
                            <p class="c7"><span class="c0 c4">Enterprise Standard Subscription</span></p>

                        </td>

                        <td class="c32">
                            <!--<p class="c9 c6"></p>-->

                            <ol class="c19" start="1">
                                <li class="c9 c11"><span class="c0">Access to certified Production-ready Software</span></li>

                                <li class="c9 c11"><span class="c0">Multi-year support and Update policies</span></li>

                                <li class="c9 c11"><span class="c0">Access to Documentation and technical guides <span class="c9 c12">(development, installation, administration)</span></span></li>
                                
                                <li class="c9 c11"><span class="c0">Access to Standard Support Services <span class="c9 c12">(As defined in section 3)</span></span></li>             

                                <li class="c9 c11"><span class="c0">Sotfware certified updates, patches and bug fixes through Maintenance
                                    benefits program <span class="c9 c12">(As defined in section 5)</span></span></li>

                                <li class="c9 c11"><span class="c0">Access to professional services <span class="c9 c12">(As defined in section 6)</span></span></li>

                                <li class="c9 c11"><span class="c0">Authorized deployment typology: Multi Server, High Availability</span></li>

                                <li class="c9 c11"><span class="c0">Authorized environments: Pre-production, Production, Integration, Acceptance, 
                                									Staging Development
                                </span></li>

                                <li class="c9 c11"><span class="c0">Unit: Core Processor band</span></li>

                                <li class="c9 c11"><span class="c0">Registered users: Unlimited</span></li>

                                <li class="c9 c11"><span class="c0">Not included: Onsite localized service desk; Extended lifetime support (*)</span></li>

                            </ol>

                            <!--<p class="c9 c6 c35"></p>-->
                        </td>
                    </tr>
                    </tbody>
                </table>
                
                <p class="c1 c6"></p>

                <table cellpadding="0" cellspacing="0" class="c26">
                    <tbody>
                    <tr class="c16">
                        <td class="c38">
                            <p class="c7"><span class="c0 c4">Enterprise Premium Subscription</span></p>

                        </td>

                        <td class="c32">
                            <!--<p class="c9 c6"></p>-->

                            <ol class="c19" start="1">
                                <li class="c9 c11"><span class="c0">Access to certified Production-ready Software</span></li>

                                <li class="c9 c11"><span class="c0">Multi-year support and Update policies</span></li>

                                <li class="c9 c11"><span class="c0">Access to Documentation and technical guides <span class="c9 c12">(development, installation, administration)</span></span></li>
                                
                                <li class="c9 c11"><span class="c0">Access to Premium Support Services <span class="c9 c12">(As defined in section 3)</span></span></li>             

                                <li class="c9 c11"><span class="c0">Sotfware certified updates, patches and bug fixes through Maintenance
                                    benefits program <span class="c9 c12">(As defined in section 5)</span></span></li>

                                <li class="c9 c11"><span class="c0">Access to professional services <span class="c9 c12">(As defined in section 6)</span></span></li>

                                <li class="c9 c11"><span class="c0">Authorized deployment typology: Multi Server, High Availability</span></li>

                                <li class="c9 c11"><span class="c0">Authorized environments: Pre-production, Production, Integration, Acceptance, 
                                									Staging Development
                                </span></li>

                                <li class="c9 c11"><span class="c0">Unit: Core Processor band</span></li>

                                <li class="c9 c11"><span class="c0">Registered users: Unlimited</span></li>

                                <li class="c9 c11"><span class="c0">Not included: Onsite localized service desk; Extended lifetime support (*); Technical Account Manager</span></li>

                            </ol>

                            <!--<p class="c9 c6 c35"></p>-->
                        </td>
                    </tr>
                    </tbody>
                </table>

				<!--p class="c1 c6"></p-->
                <p class="c15"><span class="c0">(*) Extended Lifetime Support is available under nonstandard conditions as defined in an applicable Order Form. 
                        For the avoidance of doubt, no Extended Lifetime Support is included in any Subscription agreement and all 
                        Extended Lifetime Support offers require a baseline existing Subscription as defined in this agreement.</span></p>	

				<!--
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

								<li class="c9 c11"><span class="c0">Availability: eXo Business Days, excluding eXo holidays</span></li>

								<li class="c9 c11"><span class="c0">Target response time: Four (4) hours</span></li>

								<li class="c9 c11"><span class="c0">On-Site Coverage: &nbsp;Two (2) on-site technical reviews per year by eXo</span></li>

								<li class="c9 c11"><span class="c0">Customer &nbsp;Named Contacts : Two (2)</span></li>

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
				-->

				<p class="c1 c6"></p>

				<p class="c1"><span class="c0 c4">3. Support</span></p>

				<p class="c15"><span class="c0 c4">3.1 Support Procedures</span></p>

				<p class="c12"><span class="c0">Subscription Services are 
						  provided in the English and/or French languages only. eXo will respond according to SLAs 
						  as defined hereafter. eXo and Customer will specify initial
						  technical contacts, which may be updated upon request.</span></p>

				<ol class="c19" start="1">
					<li class="c12 c11"><span class="c0 c4">Level One Support</span><span class="c0">&nbsp;means technical acknowledgement of support requests, documentation of requests, 
							basic troubleshooting, and providing commons solutions. If no solution is found, a <span class="c0 c4">Level Two</span> escalation is made.</span></li>

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
						  in establishing the incident impact to the Customer upon incident receipt and will be used to
						  set expectations between Customer and eXo. Severities are established by eXo in
						  accordance with the Severity Level definitions below during escalation and are subject
						  to change during the life of each specific incident.</span></p>

				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">Incident severity levels (defined below) are utilized
						  in establishing the incident impact to the Customer upon incident receipt and will be used to
						  set expectations between Customer and eXo. Severities are established by eXo in
						  accordance with the Severity Level definitions below during escalation and are subject
						  to change during the life of each specific incident.</span></p>

				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">3.3 Mutual obligations</span></p>

				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">To help ensure a smooth transition during technical
						  collaboration or escalation, it is essential that all parties remain engaged until the
						  case is resolved or qualified with a lesser severity level. This includes at least:</span></p>
						  <ul>
						  	<li><span class="c6">Access to all relevant contact information and
						  technical activity to date, logs, technical parameters, and all information 
						  available to eXo Support Desk allowing for a deep analysis.</span></li>
						    <li><span class="c6">Execution of recommendations and procedures emitted by eXo Support Desk</span></li>
						  </ul>
						  	
				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">3.4 Support and Software Delivery</span></p>

				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">Unless otherwise set forth in an applicable Order Form,
						  (1) eXo will be the primary source for communication with Customer' and 
						  (2) The Software, Software Updates,
						  when and if available, will be delivered to Customer via eXo Network. Customer will
						  provide eXo with the number of named contacts as stated in the applicable Order Form. 
						  Customer will provide to eXo data, anecdotes, and other information
						  reasonably necessary to enable eXo to evaluate the level of customer service being
						  provided to 'Customer'.</span></p>

				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">3.5 Support Scope of Coverage</span></p>

				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">Support Services consists of assistance for
						  installation, usage, configuration and diagnosis on the applicable Software in a Production environment. 
						  Support Services are provided only according to the Supported Environments Policy listed </span>
						  <span class="c0 c25"><a class="c8" target="_blank" href="http://www.exoplatform.com/supported-environments">http://www.exoplatform.com/supported-environments</a>.</span></p> 
						  
				<p class="c12"><span class="c0">Support Services are not provided on environments outside of the scope referenced above. 
						  Support does not include assistance with code development, system and/or network design,
						  architectural design, upgrade for the Software or for third party software made available with eXo
						  Software. eXo does not provide maintenance and/or support for Software that has been
						  modified or that is running on hardware and/or third party software that is not supported.</span></p>

				<!--
				<p class="c1 c0"></p>

				<p class="c1"><span class="c0">Development Support consists of assistance for
						  installation, usage, configuration, code development guidelines, and diagnosis on the
						  applicable Software. Requests for architecture, design, development, prototyping,
						  deployments and upgrades &nbsp;are not included within the scope of Development
						  Support, but rather are available on a consulting basis under the terms of a separate
						  agreement</span></p>
				-->
				
				<!--p class="c1"><span class="c0"></span></p-->
				
				<p class="c12"><span class="c0">Summary of support services by subscription plan :</span></p>

				<!--table cellpadding="0" cellspacing="0" class="c53"-->
				<table cellpadding="0" cellspacing="0" class="c26">
					<tbody>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Subscription Plan</span></p></td>
							<td class="c2"><p class="c7"><span class="c0 c4">Basic</span></p></td>
							<td class="c2"><p class="c7"><span class="c0 c4">Standard</span></p></td>
							<td class="c2"><p class="c7"><span class="c0 c4">Premium</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Level 1 Support </span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Ticket Limit</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Limited</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Unlimited</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Unlimited</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Usage information</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Installation instructions</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Level 2 Support</span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Ticket Limit</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Limited</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Unlimited</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Unlimited</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Troubleshooting</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Issue management</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Workarounds and procedures</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Ongoing SLA</span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Level 3 Support</span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Maintenance versions</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Cumulative patches versions</span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">One-off patches</span></p></td>
							<td class="c2"><p class="c7"><span class="c0"></span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">V</span></p></td>
						</tr>
					</tbody>
				</table>
				
				<!--p class="c12"><span class="c0"></span></p-->
				
				<p class="c12"><span class="c0">For more details </span><span class="c0 c25"><a class="c8" target="_blank" href="http://www.exoplatform.com/sla">http://www.exoplatform.com/sla</a></span></p>
				
				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">3.6 Support Guidelines</span></p>

				<!--p class="c12 c6"></p-->

				<p class="c12"><span class="c0">eXo will use commercially reasonable efforts to provide
						  support in accordance with the guidelines set forth in Table below. eXo's Technical
						  Support standard business hours ("Standard Business Hours") are 8h-18h GMT, from Monday
						  to Friday, excluding eXo holidays.</span></p>

				<p class="c6 c12"></p>

				<p class="c12"><span class="c0 c4">Table: Support Guidelines</span></p>

				<p class="c6 c12"></p>

				<table cellpadding="0" cellspacing="0" class="c26">
					<tbody>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Subscription Plan</span></p></td>
							<td class="c2"><p class="c7"><span class="c0 c4">Basic</span></p></td>
							<td class="c2"><p class="c7"><span class="c0 c4">Standard</span></p></td>
							<td class="c2"><p class="c7"><span class="c0 c4">Premium</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Hours of Coverage</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Standard Business Hours</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Standard Business Hours</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">24x7</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Support Channel</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Web</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Web</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Web and Phone</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Tickets</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Limited</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Unlimited</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">Unlimited</span></p></td>
						</tr>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Number of Named Contacts</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">1</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">3</span></p></td>
							<td class="c2"><p class="c7"><span class="c0">5</span></p></td>
						</tr>
					</tbody>
				</table>						

                <p class="c6 c12"></p>

                <p class="c12"><span class="c0 c4">Target Response Times and Guidelines:</span></p>

                <table cellpadding="0" cellspacing="0" class="c26">
                    <tbody>
						<tr class="c16">
							<td class="c10"><p class="c9"><span class="c0 c4">Subscription Plan</span></p></td>
							<td class="c2"><p class="c7"><span class="c0 c4">Basic</span></p></td>
							<td class="c2"><p class="c7"><span class="c0 c4">Standard</span></p></td>
							<td class="c2"><p class="c7"><span class="c0 c4">Premium</span></p></td>
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
							<p class="c7"><span class="c0">x</span></p>
						</td>

						<td class="c2">
							<p class="c7"><span class="c0">1 Business Hour</span></p>
						</td>

						<td class="c2">
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
							<p class="c7"><span class="c0">x</span></p>
						</td>

						<td class="c2">
							<p class="c7"><span class="c0">4 Business Hours</span></p>
						</td>

						<td class="c13">
							<p class="c7"><span class="c0">4 Hours on a 24x7 basis</span></p>
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
							<p class="c7"><span class="c0">2 Business Days</span></p>
						</td>

						<td class="c13">
							<p class="c7"><span class="c0">1 Business Day</span></p>
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

						<td class="c13">
							<p class="c7"><span class="c0">2 Business Days</span></p>
						</td>
					</tr>
					</tbody>
				</table>

				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">3.7 Support Processes</span></p>

				<p class="c12"><span class="c0">Support Processes are available at
						  <span class="c0 c25"><a class="c8" target="_blank" href="http://www.exoplatform.com/support-operations">http://www.exoplatform.com/support-operations</a><span class="c6">.</span></p>

				<p class="c12 c6"></p>

				<p class="c12"><span class="c0 c4">4. Maintenance Benefits</span></p>

				<p class="c12 c6"></p>

				<p class="c12"><span class="c0">During the time that Customer has paid the applicable
						  annual Subscription fees, eXo shall provide to Customer copyrighted patches and Updates
						  for the installed Software (including any related Documentation) which are commercially
						  released. Maintenance benefits are ruled by the eXo maintenance program visible at
						  <span class="c0 c25"><a class="c8" target="_blank" href="http://www.exoplatform.com/maintenance-program">http://www.exoplatform.com/maintenance-program</a></span><span class="c6">.</span></p>

				<p class="c12 c6"></p>

				<p class="c7"><span class="c0 c4">Appendix 2: Additional Services</span></p>

				<p class="c9 c6 c14"></p>
				
				<p class="c12"><span class="c0">eXo provides services exclusively to customers covered by a 
					valid enterprise subscription plan. Details as to the services offered can be found here 
					<span class="c0 c25"><a class="c8" target="_blank" href="http://www.exoplatform.com/en/services/overview">http://www.exoplatform.com/company/en/services/overview</a></span><span class="c6">.</span></span></p>

				<p class="c12"><span class="c0">Terms and conditions governing eXo&rsquo;s services delivery processes are stated below. </span></p>

				<ol class="c21" start="1">
					<li class="c1 c27 c29"><span class="c0 c4">eXo Consulting Services</span></li>
				</ol>

				<ol class="c28" start="1">
					<li class="c1 c22"><span class="c0 c4">Scope of appliance</span></li>
				</ol>

				<p class="c1"><span class="c0">eXo consulting services (&ldquo;Consulting
						  Services&rdquo;) are meant to provide assistance to Customer on activities involving
						  the Software such as :</span></p>

				<ol class="c19" start="1">
					<li class="c9 c11 c14"><span class="c0">Technical and functional project design and
							specifications,</span></li>

					<li class="c9 c11 c14"><span class="c0">Migration and Updates</span></li>

					<li class="c9 c11 c14"><span class="c0">IT operations and expertise,</span></li>

					<li class="c9 c11 c14"><span class="c0">On-site technical or functional POCs (Proof
							Of Concept)</span></li>
				</ol>

				<p class="c9 c6 c14 c20"></p>

				<p class="c9 c14"><span class="c0">The Consulting Services do not include any
						  Subscription Services.</span></p>

				<p class="c9 c6 c14"></p>

				<ol class="c28" start="2">
					<li class="c1 c22"><span class="c0 c4">Performance and Deliverables</span></li>
				</ol>

				<p class="c9 c14"><span class="c0">eXo agrees to provide the Consulting Services and
						  Deliverables specified in an Order Form on the terms and conditions of this Appendix
						  and in accordance with&nbsp;the requirements, Deliverables description and delivery
						  dates in such Order Form. &nbsp;eXo shall perform the Consulting Services
						  professionally and diligently and will use its commercially reasonable efforts to
						  perform the Consulting Services in a timely manner.</span></p>

				<p class="c9 c6 c14"></p>

				<p class="c9 c14"><span class="c0">eXo and the Customer agree to scope the amount and
						  nature of Consulting Services needed in accordance with the involved
						  project.</span></p>

				<p class="c9 c14"><span class="c0">Consulting Services available scoping unit is the
					&ldquo;men.day&rdquo; and will depend on the seniority of the human resource profile
						  qualified by eXo to match the requirements of the Customer.</span></p>

				<p class="c9 c6 c14"></p>

				<ol class="c28" start="3">
					<li class="c1 c22"><span class="c0 c4">Responsibilities</span></li>
				</ol>

				<p class="c1"><span class="c0">As long as Consulting Services are an assistance
						  services, those are meant to be delivered under the operational responsibility of the
						  Customer.</span></p>

				<p class="c9 c14"><span class="c0">eXo agrees to provide the Customer with
						  reasonable advance notice in the event eXo expects a failure on its part to satisfy a
						  delivery date in an Order Form.</span></p>

				<p class="c1 c6 c36"></p>

				<ol class="c28" start="4">
					<li class="c1 c22"><span class="c0 c4">Ordering, invoicing and Payment</span></li>
				</ol>

				<p class="c1"><span class="c0">eXo&rsquo;s Consulting Services are meant to be ordered
						  prior to delivery and paid upon the timeframe specified in the related Order
						  Form.</span></p>

				<p class="c1"><span class="c0">Unless stated in the applicable Order Form, the delivery shall then occur within a timeframe of
						  three (3) months, starting on the effective date of the related Order Form. Any
						  remaining amount of Consulting Services past those three months will be lost
						  and invoiced as if delivered.</span></p>

				<p class="c1"><span class="c0">The Consulting Services will be agreed to be received
						  upon the validation by the Customer of a timesheet, which document will be shared
						  between service managers on a monthly basis.</span></p>

				<p class="c1"><span class="c0">Unless specified in the Order Form, additional fees such
						  as transport, lodging, third party materials including software licenses, hardware,
						  training, documentation needed to fulfill the delivery requirements are excluded from
						  the scope of the delivery and shall be provided or ordered separately by the
						  Customer.</span></p>

				<ol class="c21" start="2">
					<li class="c1 c27 c29"><span class="c0 c4">eXo Training Services</span></li>
				</ol>

				<ol class="c28" start="1">
					<li class="c1 c22"><span class="c0 c4">Scope of appliance</span></li>
				</ol>

				<p class="c1"><span class="c0">eXo&rsquo;s Training Services are meant to provide
						  training to Customer human resources under the following scope :</span></p>

				<ol class="c19" start="5">
					<li class="c9 c11"><span class="c0">&ldquo;Training Services&rdquo; means eXo&rsquo;s
							training courses, including eXo&rsquo;s publicly available courses and courses
							provided at a site designated by the Customer</span></li>
					<li class="c9 c11"><span class="c0">The Training Services do not include any
							Subscription Services.</span></li>
				</ol>

				<p class="c9 c6"></p>

				<ol class="c28" start="2">
					<li class="c1 c22"><span class="c0 c4">Performance and Deliverables</span></li>
				</ol>

				<p class="c9 c14"><span class="c0">eXo agrees to provide the Training Services and
						  Deliverables specified in an Order Form on the terms and conditions of this Appendix
						  and in accordance with&nbsp;the requirements, Deliverables description and delivery
						  dates in such Order Form.</span></p>

				<p class="c9 c6 c14"></p>

				<p class="c9 c14"><span class="c0">eXo and the Customer agree to scope the amount and
						  nature of Training Services needed in accordance with the involved project.</span></p>

				<p class="c9 c14"><span class="c0">Training Services available scoping unit is to be
						  selected among the Training Services offer visible at</span> 
						  <span class="c0 c25"><a class="c8" target="_blank" href="http://www.exoplatform.com/company/en/services/development-training">http://www.exoplatform.com/company/en/services/development-training</a></span>
				</p>
				
				<!--
				<p class="c9 c6 c14"><span class="c0 c25"><a class="c8" target="_blank" href=
						"http://www.exoplatform.com/company/en/services/development-training"></a></span></p>
				-->

				<ol class="c28" start="3">
					<li class="c1 c22"><span class="c0 c4">Equipment and Facilities</span></li>
				</ol>

				<p class="c9"><span class="c0">For on-site courses, Customer will supply the facility
						  and equipment as set forth in the Order Form. If eXo agrees to provide the training
						  facilities and hardware, Customer will be liable for any loss or destruction of this
						  equipment and hardware used in connection with the Training Services.</span></p>

				<p class="c9 c6"></p>

				<ol class="c28" start="4">
					<li class="c1 c22"><span class="c0 c4">Customer Responsibilities</span></li>
				</ol>

				<p class="c1"><span class="c0">Customer is responsible for assessing each
						  participant&rsquo;s suitability for the Training Services, enrollment in the
						  appropriate course(s) and Customer&rsquo;s participants&rsquo; attendance at scheduled
						  courses.</span></p>

				<ol class="c28" start="5">
					<li class="c1 c22"><span class="c0 c4">Rights to Training Materials</span></li>
				</ol>

				<p class="c9"><span class="c0">All intellectual property embodied in the training
						  products, materials, methodologies, software and processes, provided in connection with
						  the Training Services or developed during the performance of the Training Services
						  (collectively, the &ldquo;</span><span class="c0 c4">Training
						  Materials</span><span class="c0">&rdquo;) are the sole property of eXo and are
						  copyrighted by eXo unless otherwise indicated. Training Materials are provided solely
						  for the use of the participants and may not be copied or transferred without the prior
						  written consent of eXo. Training Materials are eXo&rsquo;s confidential and proprietary
						  information.</span></p>

				<p class="c9 c6 c14"></p>

				<ol class="c28" start="6">
					<li class="c1 c22"><span class="c0 c4">Ordering, invoicing and Payment</span></li>
				</ol>

				<p class="c1"><span class="c0">eXo&rsquo;s Training Services are meant to be ordered
						  and paid prior to delivery.</span></p>

				<p class="c1"><span class="c0">The delivery shall then occur within a timeframe of
						  three (3) months, starting with the date of appliance of the related Order Form. Any
						  remaining amount of Training Services past those three months will be
						  lost. Cancellation of Training Services are subject to eXo&rsquo;s Cancellation Policy, 
						  as set forth at </span><span class="c0 c25"><a class="c8" target="_blank" href="http://www.exoplatform.com/company/en/services/development-training">http://www.exoplatform.com/company/en/services/development-training</a></span></p>

				<p class="c1"><span class="c0">Unless specified in the Order Form, additional fees such
						  as transport, lodging, third party materials including software licenses, hardware,
						  training, documentation needed to fulfill the delivery requirements are excluded from
						  the scope of the delivery and shall be provided or ordered separately by the
						  Customer.</span></p>

				<p class="c9 c6 c14"></p>

				<!--
				<ol class="c28" start="7">
					<li class="c1 c22"><span class="c0 c4">Cancellation Policy</span></li>
				</ol>

				<p class="c1"><span class="c0">Class registrations are not confirmed until Purchase
						  Order is received. eXo reserves the right to cancel registrations if Purchase Order is
						  not received within 10 business days of registration submittal, and a minimum of 2
						  business days prior to the start date of the course.</span></p>

				<p class="c1"><span class="c0">All purchases of training are final and
						  non-refundable.</span></p>

				<p class="c1"><span class="c0">You may either reschedule or select credit toward a
						  future class up to 14 calendar days prior to the start date of the class you are
						  currently registered for. The credit must be used within 3 months of the original
						  course start date.</span></p>

				<p class="c1"><span class="c0">Course enrollment substitutions are acceptable any time
						  up to the course start date, but the eXo training coordinator must be notified prior to
						  the class start date.</span></p>

				<p class="c1"><span class="c0">eXo reserves the right to cancel any class. If a class
						  is cancelled, we will contact students by telephone and email to arrange for training
						  credit. Every effort will be made to reschedule a cancelled class or transfer
						  enrollments to a later date.</span></p><a href="#" target="_blank" name="id.43e09b9fd5fe" id=
						"id.43e09b9fd5fe"></a>
				-->

				<ol class="c21" start="3">
					<li class="c1 c27 c29"><span class="c0 c4">eXo Specific Development
							Services</span></li>
				</ol>

				<ol class="c28" start="1">
					<li class="c1 c22"><span class="c0 c4">Scope of appliance</span></li>
				</ol>

				<p class="c1"><span class="c0">eXo&rsquo;s specific development services
						  (&ldquo;Specific Development Services&rdquo;) are meant to provide Customer on project
						  development involving the Software such as :</span></p>

				<ol class="c19" start="7">
					<li class="c1 c11"><span class="c0">Specific development,</span></li>

					<li class="c1 c11"><span class="c0">Product extensions,</span></li>

					<li class="c1 c11"><span class="c0">Off-site POCs (Proof of concept),</span></li>
				</ol>

				<p class="c1"><span class="c0">The Specific Development Services don&rsquo;t include
						  any Subscription Services.</span></p>

				<ol class="c28" start="2">
					<li class="c1 c22"><span class="c0 c4">Performance and Deliverables</span></li>
				</ol>

				<p class="c9 c14"><span class="c0">eXo agrees to provide the Specific Development
						  Services and Deliverables specified in an Order Form on the terms and conditions of
						  this Appendix and in accordance with&nbsp;the requirements, Deliverables description
						  and delivery dates according to the technical offer appendix provided by eXo with such
						  Order Form. &nbsp;</span></p>

				<p class="c9 c14"><span class="c0">eXo and the Customer agree to scope the amount and
						  nature of Specific Development Services needed in accordance with the involved
						  project.</span></p>

				<p class="c9 c14"><span class="c0">Specific Development Services are generally tied to
						  a technical and organizational offer which will:</span></p>

				<ol class="c19" start="1">
					<li class="c9 c11 c14"><span class="c0">Specify the applicable documents or materials
							used to describe &nbsp;the Customer requirements and expected results</span></li>

					<li class="c9 c11 c14"><span class="c0">Specify the eXo&rsquo;s project management
							phases, Deliverables, commitments and means to fulfill the project requirements and
							warrant the result.</span></li>
				</ol>

				<p class="c9 c14 c20 c31"><span class="c0">Such an offer will be attached to an Order
						  Form and are subject to changes emitted through a Change Order, as set forth
						  below.</span></p>

				<p class="c9 c14 c20 c31"><span class="c0 c4">Change Order.</span> <span class=
																								 "c0">&nbsp;The parties may, upon mutual agreement in a written order, at any time, make
						  changes including deletions or additions, within the general scope of this Agreement,
						  to the Specific Development Services to be performed. &nbsp;If any such change causes
						  an increase or decrease in the time required for performance of any part of the
						  Specific Development Services, the parties will make an equitable adjustment in
						  delivery schedule and shall modify applicable Order Form accordingly.</span></p>

				<p class="c9 c6 c14"></p>

				<ol class="c28" start="3">
					<li class="c1 c22"><span class="c0 c4">Ordering, invoicing and Payment</span></li>
				</ol>

				<p class="c1"><span class="c0">eXo&rsquo;s Specific Development Services are meant to
						  be ordered prior to project start and paid in compliance with the pay schedule
						  specified in the related Order Form.</span></p>

				<p class="c1"><span class="c0">The Specific Development Services will be agreed to be
						  received upon the validation by the Customer of the Deliverables in accordance with the
						  delays, scope and the expected quality specified within the technical and
						  organizational offer attached to the Order Form.</span></p>

				<p class="c1"><span class="c0">Unless specified in the Order Form, additional fees such
						  as transport, lodging, third party materials including software licenses, hardware,
						  training, documentation needed to fulfill the delivery requirements are excluded from
						  the scope of the delivery and shall be provided or ordered separately by the
						  Customer.</span></p>

				<!--p class="c1 c6"></p-->
				
			<p class="c7"><span class="c0 c4">Appendix 3: eXo Official Add-ons</span></p>

			<table cellpadding="0" cellspacing="0" class="c26">
				<tbody>
					<tr class="c16">
						<td class="c10"><p class="c0"><span class="c0 c4">Add-on name</span></p></td>
						<td class="c2"><p class="c7"><span class="c0 c4">Introduces Specific Terms</span></p></td>
						<td class="c2"><p class="c7"><span class="c0 c4">Requires an active Enterprise Subscription</span></p></td>
						<td class="c2"><p class="c7"><span class="c0 c4">Requires an additional subscription fee</span></p></td>
					</tr>
					<tr class="c16">
						<td class="c10"><p class="c9"><span class="c0 c4">exo-acme-sample - eXo ACME Website Sample</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Basic, Standard or Premium</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
					</tr>
					<tr class="c16">
						<td class="c10"><p class="c9"><span class="c0 c4">exo-wai-sample - eXo WAI Sample</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Basic, Standard or Premium</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
					</tr>
					<tr class="c16">
						<td class="c10"><p class="c9"><span class="c0 c4">exo-chat - eXo Chat</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Basic, Standard or Premium</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
					</tr>
					<tr class="c16">
						<td class="c10"><p class="c9"><span class="c0 c4">exo-video-calls - eXo Video Calls</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Yes (*)</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Basic, Standard or Premium</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Yes</span></p></td>
					</tr>
					<tr class="c16">
						<td class="c10"><p class="c9"><span class="c0 c4">exo-cmis-addon - eXo CMIS Integration</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Basic, Standard or Premium</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
					</tr>
					<tr class="c16">
						<td class="c10"><p class="c9"><span class="c0 c4">exo-crash-tomcat - eXo CRaSH Add-on for Tomcat</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Basic, Standard or Premium</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
					</tr>
					<tr class="c16">
						<td class="c10"><p class="c9"><span class="c0 c4">exo-crash-jboss - eXo CRaSH Add-on for Jboss</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Standard or Premium</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
					</tr>
					<tr class="c16">
						<td class="c10"><p class="c9"><span class="c0 c4">exo-ide-addon - eXo IDE Add-on</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">Basic, Standard or Premium</span></p></td>
						<td class="c2"><p class="c7"><span class="c0">No</span></p></td>
					</tr>
				</tbody>
			</table>
			
			<p class="c15"><span class="c0"></span></p>
			
			<p class="c15"><span class="c0">(*) eXo Video Calls Add-on is governed and supported under the conditions of 
				the MSA and the SLA as provided under the same Subscription Service Level as the Subscription Service Level applicable to the 
				Software. For the avoidance of doubt and as long as the Subscription remains in force, eXo Video Calls Add-on will be deemed a part of 
				the Software as defined by Section 1.9 of the MSA. </span></p>
	
				
			</div>
			<div class="bottom clearfix">
				<form name="tcForm" action="<%= contextPath + "/terms-and-conditions-action"%>" method="post">
					<div class="pull-right">
						<button class="btn inactive" disabled="disabled" id="continueButton" onclick="validate();">Continue</button>
					</div>
					<div class="pull-left">
						<label class="uiCheckbox"><input type="checkbox" id="agreement" name="checktc" value="false" onclick="toggleState();" class="checkbox"/>
							<span>I agree with this terms and conditions agreement.</span>
						</label>
					</div>
					<script type="text/javascript" src="<%=contextPath%>/javascript/welcomescreens.js"></script>
				</form>
			</div>
		</div>
	</body>
</html>
