package org.exoplatform.platform.navigation.component.utils;

import org.exoplatform.social.webui.Utils;
/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 2/13/13
 */
public class NavigationUtils {

  /**
   * @deprecated use {@link Utils.getOwnerRemoteId()}
   */
    public static String getCurrentUser() {
        return Utils.getOwnerRemoteId();
    }
}
