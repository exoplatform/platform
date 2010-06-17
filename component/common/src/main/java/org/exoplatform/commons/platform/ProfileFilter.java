package org.exoplatform.commons.platform;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class ProfileFilter implements RenderFilter {

  private static final String DISABLED_JSP = "/jsp/portlet-disabled.jsp";

  private static Log          LOG          = ExoLogger.getExoLogger(ProfileFilter.class);


  PortletContext              context;

  public void destroy() {
  }

  public void init(FilterConfig filterConfig) throws PortletException {
    context = filterConfig.getPortletContext();
  }

  public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException,
                                                                                         PortletException {

    boolean isPortletActive = PortalContainer.isScopeValid(PortalContainer.getInstance(),
                                                           new FakeServletContext());

    if (isPortletActive) {
      chain.doFilter(request, response);

    } else {

      LOG.info("portlet" + context.getPortletContextName()
          + " is currently disabled. You need to enable it with -Dexo.profile=");
      PortletRequestDispatcher dispatcher = context.getRequestDispatcher(DISABLED_JSP);
      dispatcher.include(request, response);

    }
  }

  /**
   * a fake servelt context that gives this portlet servlet context name in
   * return to {@link #getServletContextName()}
   * 
   * @author patricelamarque
   */
  class FakeServletContext implements ServletContext {

    public Object getAttribute(String name) {
      // TODO Auto-generated method stub
      return null;
    }

    public Enumeration getAttributeNames() {
      // TODO Auto-generated method stub
      return null;
    }

    public ServletContext getContext(String uripath) {
      // TODO Auto-generated method stub
      return null;
    }

    public String getInitParameter(String name) {
      // TODO Auto-generated method stub
      return null;
    }

    public Enumeration getInitParameterNames() {
      // TODO Auto-generated method stub
      return null;
    }

    public int getMajorVersion() {
      // TODO Auto-generated method stub
      return 0;
    }

    public String getMimeType(String file) {
      // TODO Auto-generated method stub
      return null;
    }

    public int getMinorVersion() {
      // TODO Auto-generated method stub
      return 0;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
      // TODO Auto-generated method stub
      return null;
    }

    public String getRealPath(String path) {
      // TODO Auto-generated method stub
      return null;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
      // TODO Auto-generated method stub
      return null;
    }

    public URL getResource(String path) throws MalformedURLException {
      // TODO Auto-generated method stub
      return null;
    }

    public InputStream getResourceAsStream(String path) {
      // TODO Auto-generated method stub
      return null;
    }

    public Set getResourcePaths(String path) {
      // TODO Auto-generated method stub
      return null;
    }

    public String getServerInfo() {
      // TODO Auto-generated method stub
      return null;
    }

    public Servlet getServlet(String name) throws ServletException {
      // TODO Auto-generated method stub
      return null;
    }

    // HACK : we
    public String getServletContextName() {
      return context.getPortletContextName();
    }

    public Enumeration getServletNames() {
      // TODO Auto-generated method stub
      return null;
    }

    public Enumeration getServlets() {
      // TODO Auto-generated method stub
      return null;
    }

    public void log(String msg) {
      // TODO Auto-generated method stub

    }

    public void log(Exception exception, String msg) {
      // TODO Auto-generated method stub

    }

    public void log(String message, Throwable throwable) {
      // TODO Auto-generated method stub

    }

    public void removeAttribute(String name) {
      // TODO Auto-generated method stub

    }

    public void setAttribute(String name, Object object) {
      // TODO Auto-generated method stub

    }

  }

}
