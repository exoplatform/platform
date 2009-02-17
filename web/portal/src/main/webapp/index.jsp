<%@ page import="org.exoplatform.container.PortalContainer"%>
<%@ page import="org.exoplatform.container.RootContainer"%>
<%@ page import="org.exoplatform.portal.config.UserPortalConfigService"%>
<%
	RootContainer rootContainer = RootContainer.getInstance() ;
  PortalContainer manager = rootContainer.getPortalContainer(session.getServletContext().getServletContextName()) ;
  UserPortalConfigService userPortalConfigService = (UserPortalConfigService) manager.getComponentInstanceOfType(UserPortalConfigService.class) ;
	response.sendRedirect(request.getContextPath() + "/public/"+userPortalConfigService.getDefaultPortal()+"/");
%>

