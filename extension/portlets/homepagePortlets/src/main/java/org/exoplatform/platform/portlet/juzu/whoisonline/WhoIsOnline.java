package org.exoplatform.platform.portlet.juzu.whoisonline;

import org.exoplatform.social.core.identity.model.Profile;

import java.util.List;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public interface WhoIsOnline {

    List<Profile> getFriends(String userId);

}
