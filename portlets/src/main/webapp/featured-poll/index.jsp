<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="javax.portlet.ResourceURL" %>
<%@ page import="javax.portlet.PortletURL" %>

<portlet:defineObjects />

<%
PortletURL actionURL = renderResponse.createActionURL();
String pollId = (String)renderRequest.getAttribute("pollId");
%>

<div id="featured-poll-app">
    <script>
        require(['PORTLET/portlets/FeaturedPoll'], function(app) {
            app.init('<%=pollId%>', '<%=actionURL%>');
        });
    </script>
</div>
