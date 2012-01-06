<%@page import="org.exoplatform.trial.TrialService"%>
<div class="Container ClearFix">
  <a class="Botton GrayRect FR" href="/trial/UnlockServlet?dismiss=true">Dismiss</a>
  <span class="TextContainer">You have <span class="Gras"><%=TrialService.getNbDaysBeforeExpiration()%> days left</span> in your evaluation</span>
  <span class="TriangleItem GrayIcon"></span>
</div>
