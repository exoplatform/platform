<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>

<div>
	<p>
		Received messages:
		<div id="received_<portlet:namespace/>">
			
		</div>
	</p>
	
	<p>
		Send message:
		<input type="text" id="msg_<portlet:namespace/>"/> <a href="#" onclick="send_<portlet:namespace/>();">send</a>
		
	</p>
	
</div>

<script type="text/javascript">
	
	Function.prototype.bind = function(object) {
	  var method = this;
	  return function() {
	    method.apply(object, arguments);
	  }
	}
	
	
	function send_<portlet:namespace/>() {
		var msg = document.getElementById("msg_<portlet:namespace/>").value;
		eXo.core.Topic.publish("<portlet:namespace/>", "/demo", msg);
	}
	
	function Listener_<portlet:namespace/>(){
		
	}
	
	Listener_<portlet:namespace/>.prototype.receiveMsg = function(event) {
		document.getElementById("received_<portlet:namespace/>").innerHTML = document.getElementById("received_<portlet:namespace/>").innerHTML + "<br />* " + event.senderId + ": " + event.message;
	}
	
	function init_<portlet:namespace/>() {
		var listener_<portlet:namespace/> = new Listener_<portlet:namespace/>();
		eXo.core.Topic.subscribe("/demo", listener_<portlet:namespace/>.receiveMsg.bind(listener_<portlet:namespace/>));
	}
	
	init_<portlet:namespace/>();
</script>