<%@page import="org.exoplatform.trial.TrialService"%>
<div class="UIContainer">
	<span class="UIGrayBoxIcon"></span>
	<span class="UIGrayText FL">Your have <span class="UIGras"><%=TrialService.getNbDaysBeforeExpiration()%> days left</span> in you evaluation</span>
	<a class="UIGraySubmit FR" href="/trial/UnlockServlet?dismiss=true">Dismiss</a>
</div>