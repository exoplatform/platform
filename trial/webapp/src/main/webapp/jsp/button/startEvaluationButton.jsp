<%@page import="org.exoplatform.trial.TrialService"%>
<div class="UIContainer">
	<span class="UIGreenBoxIcon"></span>
	<span class="UIGreenText FL">Full use of the product for a 30 days period</span>
	<a class="UIGreenSubmit FR" href="/trial/UnlockServlet?rdate=<%=TrialService.computeRemindDateFromTodayBase64()%>">Start Evaluation</a>
</div>