package org.exoplatform.platform.common.portlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;

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

  private ModuleRegistry moduleRegistry = null;

  private Map<String, String> disabledPortletMessages = new HashMap<String, String>();

  public void init(FilterConfig filterConfig) throws PortletException {
    context = filterConfig.getPortletContext();
  }

  /**
   * Serves {@link #DISABLED_JSP} if the portlet is not a valid dependency
   * of the current portal container.
   */
  public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException, PortletException {
    PortletConfig portletConfig = (PortletConfig) request.getAttribute("javax.portlet.config");
    String portletName = portletConfig.getPortletName();
    String portletID = context.getPortletContextName() + "/" + portletName;

    if (getModuleRegistry().isPortletActive(portletID)) {
      chain.doFilter(request, response);
    } else {
      LOG.info("The portlet '" + portletID + "' is currently disabled.");

      // Get the message from cache
      String html = getPortletSpecificMessage(request, portletName, portletID);
      response.getWriter().print(html);
    }
  }

  public void destroy() {}

  private String getPortletSpecificMessage(RenderRequest request, String portletName, String portletID) {
    String html = disabledPortletMessages.get(portletID);
    if (html == null || html.isEmpty()) {
      String portletDisplayName = getModuleRegistry().getDisplayName(portletName, request.getLocale());
      html = mergeDisabledContent(getModuleRegistry(), getDisablerMessage(), portletDisplayName, portletName, portletID);
      disabledPortletMessages.put(portletID, html);
    }
    return html;
  }

  private String getDisablerMessage() {
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
    return messageContent;
  }

  /**
   * Replace some variables into HTML content
   * 
   * @param content
   * @return specific message for the selected portlet, after some
   *         replacements
   */
  private String mergeDisabledContent(ModuleRegistry moduleRegistry, String content, String portletDisplayName,
      String portletName, String portletID) {
    String result = content;

    result = result.replaceAll("ACTIVE_PROFILES", PortalContainer.getProfiles().toString());
    result = result.replaceAll("APP_NAME", portletDisplayName);
    result = result.replaceAll("APP_ID", portletName);
    result = result.replaceAll("PROFILE", moduleRegistry.getModulesForPortlet(portletID).toString());

    return result;
  }

  /**
   * @param is
   *          the InputStream
   * @return the String content of the input stream
   * @throws IOException
   */
  private String convertStreamToString(InputStream is) throws IOException {
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
   * @return ModuleRegistry component
   */
  private ModuleRegistry getModuleRegistry() {
    if (moduleRegistry == null) {
      moduleRegistry = (ModuleRegistry) PortalContainer.getComponent(ModuleRegistry.class);
    }
    return moduleRegistry;
  }

}
