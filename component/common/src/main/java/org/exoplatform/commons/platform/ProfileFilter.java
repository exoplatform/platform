package org.exoplatform.commons.platform;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;

import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class ProfileFilter implements RenderFilter, ActionFilter {

  private static final String DISABLED_JSP = "/jsp/portlet-disabled.jsp";

  private static Log LOG = ExoLogger.getExoLogger(ProfileFilter.class);

  String             profiles;

  String             name;

  PortletContext     context;

  public void destroy() {
  }

  public void init(FilterConfig filterConfig) throws PortletException {
    profiles = filterConfig.getInitParameter("profiles");
    name = filterConfig.getPortletContext().getPortletContextName();
    context = filterConfig.getPortletContext();
  }

  public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException,
                                                                                         PortletException {
    if (profilesDisabled()) {

      LOG.info("portlet" + name + " is currently disabled. You need to enable it with -Dexo.profile="
          + profiles);
      PortletRequestDispatcher dispatcher = context.getRequestDispatcher(DISABLED_JSP);
      dispatcher.include(request, response);
    } else {
      chain.doFilter(request, response);
    }
  }

  public void doFilter(ActionRequest request, ActionResponse response, FilterChain chain) throws IOException,
                                                                                         PortletException {

    if (profilesDisabled()) {

      LOG.info("portlet" + name + " is currently disabled. You need to enable it with -Dexo.profile="
          + profiles);
    
      PortletRequestDispatcher dispatcher = context.getRequestDispatcher(DISABLED_JSP);
      dispatcher.include(request, response);
    } else {
      chain.doFilter(request, response);
    }
  }

  private boolean profilesDisabled() {
    if (profiles != null) {
      Collection<String> col = Arrays.asList(profiles.split(","));
      return !RootContainer.getProfiles().containsAll(col);
    }
    return true;
  }

}
