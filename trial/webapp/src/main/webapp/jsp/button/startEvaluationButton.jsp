<%@page import="org.exoplatform.trial.TrialService"%>
<div class="Container ClearFix">
  <a class="Botton GreenRect FR" href="/trial/UnlockServlet?rdate=<%=TrialService.computeRemindDateFromTodayBase64()%>">Start Evaluation</a>
  <span class="TextContainer">Full use of the product for a 30 days period </span>
  <span class="TriangleItem GreenIcon"></span>        
</div>