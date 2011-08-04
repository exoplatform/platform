package org.exoplatform.platform.common.portlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
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
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.platform.common.module.ModuleRegistry;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * This portlet filter ensures that the portlet is active. To be active, a
 * portlet must have its context name declared as a dependency of the
 * current {@link PortalContainer}.
 */
public class PortletDisablerFilter implements RenderFilter {

  private static final String PORTLET_DISABLED_DEFAULT_MESSAGE = "Portlet disabled.";

  private static final String DISABLED_HTML_FILE_PATH = "war:/../portlet-disabled.html";

  private static Log LOG = ExoLogger.getExoLogger(PortletDisablerFilter.class);

  private PortletContext context;

  private String messageContent = null;

  public void destroy() {}

  public void init(FilterConfig filterConfig) throws PortletException {
    context = filterConfig.getPortletContext();
  }

  /**
   * Serves {@link #DISABLED_JSP} if the portlet is not a valid dependency
   * of the current portal container.
   */
  public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException, PortletException {
    boolean isPortletActive = PortalContainer.isScopeValid(PortalContainer.getInstance(), new FakeServletContext());
    if (isPortletActive) {
      chain.doFilter(request, response);
    } else {
      PortletConfig portletConfig = (PortletConfig) request.getAttribute("javax.portlet.config");
      String portletName = portletConfig.getPortletName();
      String portletID = context.getPortletContextName() + "/" + portletName;
      LOG.info("The portlet '" + portletID + "' is currently disabled.");
      if (messageContent == null || messageContent.isEmpty()) {
        ConfigurationManager confManager = (ConfigurationManager) PortalContainer.getComponent(ConfigurationManager.class);
        try {
          InputStream inputStream = confManager.getInputStream(DISABLED_HTML_FILE_PATH);
          messageContent = convertStreamToString(inputStream);
        } catch (Exception exception) {
          messageContent = PORTLET_DISABLED_DEFAULT_MESSAGE;
          LOG.error("Cannot read message for disabled portlet", exception);
        }
      }
      ModuleRegistry moduleRegistry = (ModuleRegistry) PortalContainer.getComponent(ModuleRegistry.class);
      String portletDisplayName = moduleRegistry.getDisplayName(portletName, request.getLocale());
      String html = mergeDisabledContent(moduleRegistry, messageContent, portletDisplayName, portletName, portletID);
      response.getWriter().write(html);
    }
  }

  /**
   * Replace some variables into HTML content
   * 
   * @param content
   * @return
   */
  public String mergeDisabledContent(ModuleRegistry moduleRegistry, String content, String portletDisplayName,
      String portletName, String portletID) {
    String result = content;

    result = result.replaceAll("ACTIVE_PROFILES", PortalContainer.getProfiles().toString());
    result = result.replaceAll("APP_NAME", portletDisplayName);
    result = result.replaceAll("APP_ID", portletName);
    result = result.replaceAll("PROFILE", moduleRegistry.getModulesForPortlet(portletID).toString());

    return result;
  }

  public String convertStreamToString(InputStream is) throws IOException {
    /*
     * To convert the InputStream to String we use the Reader.read(char[]
     * buffer) method. We iterate until the Reader return -1 which means
     * there's no more data to read. We use the StringWriter class to
     * produce the string.
     */
    if (is != null) {
      Writer writer = new StringWriter();

      char[] buffer = new char[1024];
      try {
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
          writer.write(buffer, 0, n);
        }
      } finally {
        is.close();
      }
      return writer.toString();
    } else {
      return "";
    }
  }

  /**
   * a fake servlet context that gives this portlet servlet context name in
   * return to {@link #getServletContextName()}
   */
  class FakeServletContext implements ServletContext {

    public Object getAttribute(String name) {
      return null;
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getAttributeNames() {
      return null;
    }

    public ServletContext getContext(String uripath) {
      return null;
    }

    public String getInitParameter(String name) {
      return null;
    }

    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings("rawtypes")
    public Enumeration getServletNames() {
      return null;
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getServlets() {
      return null;
    }

    public void log(String msg) {}

    public void log(Exception exception, String msg) {}

    public void log(String message, Throwable throwable) {}

    public void removeAttribute(String name) {}

    public void setAttribute(String name, Object object) {}

  }

}
