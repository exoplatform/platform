<%@page import="org.exoplatform.trial.TrialService"%>
<div class="UIContainer">
	<span class="UIBlueBoxIcon"></span>
	<span class="UIBlueTextTwoLine FL">Contact eXo sales to request an extender evaluation license<br>Your product code is <span class="UIGras"><%=TrialService.getProductCode() %></span></span>
	<a class="UIBlueSubmit FR" href="<%=TrialService.getExtendFormUrl()%>?pc=<%=TrialService.getProductCode() %>">Extend Evaluation</a>
</div>