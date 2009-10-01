<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>

<div>
	
	<p>
		Send message:
		<input type="text" id="msg_<portlet:namespace/>"/> <a href="#" onclick="send_<portlet:namespace/>();">send</a>
		
	</p>
	
</div>

<script type="text/javascript">
	
	function send_<portlet:namespace/>() {
		var msg = document.getElementById("msg_<portlet:namespace/>").value;
		
		var query = "message=" + msg;
		var url = "<portlet:actionURL />".replace(/&amp;/g, "&");
		var request = new eXo.portal.AjaxRequest('POST', url, query);
		request.process();
		
	}

</script>