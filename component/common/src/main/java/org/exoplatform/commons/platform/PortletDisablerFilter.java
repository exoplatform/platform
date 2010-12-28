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

/**
 * This portlet filter ensures that the portlet is active. To be active, a
 * portlet must have its context name declared as a dependency of the current {@link PortalContainer}.
 */
public class PortletDisablerFilter implements RenderFilter {

  private static final String DISABLED_JSP = "/jsp/portlet-disabled.jsp";

  private static Log          LOG          = ExoLogger.getExoLogger(PortletDisablerFilter.class);

  private PortletContext              context;

  public void destroy() {
  }

  public void init(FilterConfig filterConfig) throws PortletException {
    context = filterConfig.getPortletContext();
  }

  /**
   * Serves {@link #DISABLED_JSP} if the portlet is not a valid dependency of the current portal container.
   */
  public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException,
                                                                                         PortletException {

    boolean isPortletActive = PortalContainer.isScopeValid(PortalContainer.getInstance(),
                                                           new FakeServletContext());

    if (isPortletActive) {
      chain.doFilter(request, response);

    } else {
      LOG.info("portlet" + context.getPortletContextName() + " is currently disabled.");
      PortletRequestDispatcher dispatcher = context.getRequestDispatcher(DISABLED_JSP);
      dispatcher.include(request, response);

    }
  }

  /**
   * a fake servelt context that gives this portlet servlet context name in
   * return to {@link #getServletContextName()}
   */
  @SuppressWarnings("unchecked")
  class FakeServletContext implements ServletContext {

    public Object getAttribute(String name) {
      return null;
    }


    public Enumeration getAttributeNames() {
      return null;
    }

    public ServletContext getContext(String uripath) {
      return null;
    }

    public String getInitParameter(String name) {
      return null;
    }

    public Enumeration getInitParameterNames() {
      return null;
    }

    public int getMajorVersion() {
      return 0;
    }

    public String getMimeType(String file) {
      return null;
    }

    public int getMinorVersion() {
      return 0;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
      return null;
    }

    public String getRealPath(String path) {
      return null;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
      return null;
    }

    public URL getResource(String path) throws MalformedURLException {
      return null;
    }

    public InputStream getResourceAsStream(String path) {
      return null;
    }

    public Set getResourcePaths(String path) {
      return null;
    }

    public String getServerInfo() {
      return null;
    }

    public Servlet getServlet(String name) throws ServletException {
      return null;
    }

    // HACK : we
    public String getServletContextName() {
      return context.getPortletContextName();
    }

    public Enumeration getServletNames() {
      return null;
    }

    public Enumeration getServlets() {
      return null;
    }

    public void log(String msg) {
    }

    public void log(Exception exception, String msg) {
    }

    public void log(String message, Throwable throwable) {
    }

    public void removeAttribute(String name) {
    }

    public void setAttribute(String name, Object object) {
    }

  }

}
