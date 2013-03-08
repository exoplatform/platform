package org.exoplatform.platform.common.account.setup.web;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.filter.Filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 3/4/13
 */
public class AccountSetupFilter implements Filter {

    private static final Log LOG = ExoLogger.getLogger(AccountSetupFilter.class);
    SettingService settingService ;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String edition = getPlatformEdition();
        boolean isDevMod = PropertyManager.isDevelopping();
        settingService = (SettingService) PortalContainer.getInstance().getComponentInstanceOfType(SettingService.class);
        boolean setupDone = false;
        SettingValue accountSetupNode = settingService.get(Context.GLOBAL, Scope.GLOBAL, AccountSetup.ACCOUNT_SETUP_NODE);
        if(accountSetupNode != null)
            setupDone = true;
        if((!setupDone)&&(!isDevMod)){
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendRedirect("/platform-extension/jsp/welcome-screens/accountSetup.jsp");
            return;
        }
        chain.doFilter(request, response);
    }

    private String getPlatformEdition() {
        try {
            Class<?> c = Class.forName("org.exoplatform.platform.edition.PlatformEdition");
            Method getEditionMethod = c.getMethod("getEdition");
            String platformEdition = (String) getEditionMethod.invoke(null);
            return platformEdition;
        } catch (Exception e) {
            LOG.error("An error occured while getting the platform edition information.", e);
        }
        return null;
    }
}