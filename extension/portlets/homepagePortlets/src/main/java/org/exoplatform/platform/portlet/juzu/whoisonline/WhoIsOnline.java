package org.exoplatform.platform.portlet.juzu.whoisonline;

import java.util.List;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 */
public interface WhoIsOnline {

    List<User> getFriends(String userId);

}
