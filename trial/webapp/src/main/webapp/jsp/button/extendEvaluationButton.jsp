<%@page import="org.exoplatform.trial.TrialService"%>
<div class="Container ClearFix">
  <a class="Botton BlueRect FR" href="<%=TrialService.getExtendFormUrl()%>?pc=<%=TrialService.getProductCode() %>">Extend Evaluation</a>
  <span class="TextContainer">Contact eXo sales to request an extender evaluation license<br>Your product code is <span class="Gras"><%=TrialService.getProductCode() %></span></span>
  <span class="TriangleItem BlueIcon"></span>
</div>