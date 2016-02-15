package org.exoplatform.platform.navigation.component.breadcrumb.impl;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.navigation.component.breadcrumb.UserNavigationHandlerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 */
public class UserNavigationHandlerServiceImpl implements UserNavigationHandlerService {

    List<String> userNavigationuri;
    private static final Log LOG = ExoLogger.getLogger(UserNavigationHandlerServiceImpl.class);

    public UserNavigationHandlerServiceImpl(InitParams initParams) {
        userNavigationuri = initParams.getValuesParam("user.navigation.uri").getValues();
    }


    public List<String> loadUserNavigation() {
        return userNavigationuri;
    }
}
