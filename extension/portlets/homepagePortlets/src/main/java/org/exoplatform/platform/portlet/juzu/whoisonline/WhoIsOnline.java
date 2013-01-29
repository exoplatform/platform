package org.exoplatform.platform.portlet.juzu.whoisonline;

import java.util.List;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public interface WhoIsOnline {

    List<User> getFriends(String userId);

}
