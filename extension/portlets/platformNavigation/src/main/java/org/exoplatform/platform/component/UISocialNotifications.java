/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;

@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "app:/groovy/platformNavigation/portlet/UISocialNotificationsPortlet/UISocialNotifications.gtmpl",
  events = {
    @EventConfig(listeners = UISocialNotifications.UpdateProviderFrequencyActionListener.class),
    @EventConfig(listeners = UISocialNotifications.SaveActionListener.class),
    @EventConfig(listeners = UISocialNotifications.CancelActionListener.class)
  }
)
public class UISocialNotifications extends UIForm {
  
  private final static Log           LOG                      = ExoLogger.getExoLogger(UISocialNotifications.class);

  private static final String        NEW_USER                 = "NewUser";

  private static final String        CONNECTION_REQUEST       = "ConnectionRequest";

  private static final String        SPACE_INVITATION         = "SpaceInvitation";

  private static final String        REQUEST_JOIN_SPACE       = "RequestJoinSpace";

  private static final String        POSTED_ON_SPACE          = "PostedOnSpace";

  private static final String        MENTION_ME               = "MentionMe";

  private static final String        COMMENT_ON_MY_ACTIVITIES = "CommentOnMyActivities";

  private static final String        COMMA                    = ",";
  
  private Map<String, List<Boolean>> providers;

  public UISocialNotifications() throws Exception {
    setActions(new String[] {"Save", "Cancel"});
  }
  
  protected void init() {
    if (providers == null) {
      providers = new HashMap<String, List<Boolean>>();
    }
    List<String> pvds = Arrays.asList(NEW_USER, CONNECTION_REQUEST, SPACE_INVITATION, REQUEST_JOIN_SPACE, POSTED_ON_SPACE, MENTION_ME, COMMENT_ON_MY_ACTIVITIES);
    for (String pvd : pvds) {
      List<Boolean> frequencies = getFrequencies(pvd);
      if (frequencies == null) {
        frequencies = Arrays.asList(true, false, true, false);
      }
      providers.put(pvd, frequencies);
    }
  }
  
  protected Set<String> getProviders() {
    return providers.keySet();
  }
  
  protected List<Boolean> getFrequencies(String provider) {
    if (providers.containsKey(provider)) {
      return providers.get(provider);
    }
    return null;
  }
  
  protected void setProviderFrequency(String provider, int index) {
    List<Boolean> frequencies = getFrequencies(provider);
    if (frequencies != null) {
      boolean b = frequencies.get(index);
      frequencies.set(index, !b);
      providers.put(provider, frequencies);
    }
  }
  
  public static class UpdateProviderFrequencyActionListener extends EventListener<UISocialNotifications> {
    public void execute(Event<UISocialNotifications> event) throws Exception {
      String requestParam = event.getRequestContext().getRequestParameter(OBJECTID);
      UISocialNotifications notifications = event.getSource();
      String provider = requestParam.substring(0, requestParam.indexOf(COMMA));
      String index = requestParam.substring(provider.length() + 1);
      notifications.setProviderFrequency(provider, Integer.parseInt(index));
      event.getRequestContext().addUIComponentToUpdateByAjax(notifications);
    }
  }
  
  public static class SaveActionListener extends EventListener<UISocialNotifications> {
    public void execute(Event<UISocialNotifications> event) throws Exception {
      LOG.info("Save Action");
    }
  }

  public static class CancelActionListener extends EventListener<UISocialNotifications> {
    public void execute(Event<UISocialNotifications> event) throws Exception {
      UISocialNotifications notifications = event.getSource();
      event.getRequestContext().addUIComponentToUpdateByAjax(notifications);
      LOG.info("Cancel Action");
    }
  }
}
