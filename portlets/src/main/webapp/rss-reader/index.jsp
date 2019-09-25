<%@ page import="org.exoplatform.platform.portlet.rss.model.FeedItem" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%
    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
    ResourceBundle resource = portletConfig.getResourceBundle(renderRequest.getLocale());
    List<FeedItem> feedEntries = (List<FeedItem>)renderRequest.getAttribute("feedEntries");
    String feedFavicon = (String)renderRequest.getAttribute("feedFavicon");
%>
<div class="feedContainer uiRssAggregator uiBox">
    <div class="uiContentBox">
        <% if (feedEntries.size() == 0) { %>
        <div><%=resource.getString("noFeedItem")%></div>
        <% } else {
        for (FeedItem item : feedEntries) { %>
        <div class="item" style="border-bottom: 1px solid #eeeeee; margin-bottom: 15px; padding-bottom: 5px">
            <div class="feed-title">
                <% if (feedFavicon != null && !feedFavicon.isEmpty()) { %>
                <img src="<%=feedFavicon%>" alt="" border="0" align="absmiddle" style="height:16;width:16;" onerror="this.style.visibility='hidden';">&nbsp;&nbsp;
                <% } %>
                <a class="titlelink" href="<%= item.getLink()%>">
                    <%=item.getTitle()%>
                </a>
            </div>
            <div class="more" style="display: none;">
                <div class="date"><%= (item.getDate() != null ? df.format(item.getDate()) : "") %></div>
                <div class="desc"><%=item.getSummary()%></div>
                <div class="link text-right"><a href="<%=item.getLink()%>" target="_blank"><%=resource.getString("viewLink")%> &raquo;</a></div>
            </div>
        </div>

        <%}
        } %>
    </div>
</div>
