/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.component.social;

import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.application.PeopleService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.social.webui.composer.UIActivityComposerContainer;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIFormTextAreaInput;

@ComponentConfig(lifecycle = UIFormLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIUserPlatformToolBarPortlet/UIComposer.gtmpl", events = { @EventConfig(listeners = UIComposer.PostMessageActionListener.class) })
public class UIComposer extends org.exoplatform.social.webui.composer.UIComposer {

  public static final String ACTIVITY_TYPE = "DEFAULT_ACTIVITY";

  private UIFormTextAreaInput messageInput;
  private UIActivityComposerContainer composerContainer;

  public UIComposer() throws Exception {
    // add textbox for inputting message
    messageInput = getChild(UIFormTextAreaInput.class);
    messageInput.setId("navigationComposerInput");
    messageInput.setName("navigationComposerInput");
    messageInput.setBindingField("navigationComposerInput");

    // add composer container
    composerContainer = getChild(UIActivityComposerContainer.class);
    composerContainer.setId("ActivityComposerContainer");

    setPostContext(PostContext.USER);
    this.isActivityStreamOwner(true);
  }

  public static class PostMessageActionListener extends EventListener<UIComposer> {
    @Override
    public void execute(Event<UIComposer> event) throws Exception {
      UIComposer uiComposer = event.getSource();
      String message = (uiComposer.getMessage() == null) ? "" : uiComposer.getMessage();

      String defaultInput = "";
      if (uiComposer.getPostContext() == PostContext.SPACE) {
        defaultInput = event.getRequestContext().getApplicationResourceBundle()
            .getString(uiComposer.getId() + ".input.Write_Something");
      } else {
        defaultInput = event.getRequestContext().getApplicationResourceBundle()
            .getString(uiComposer.getId() + ".input.What_Are_You_Working_On");
      }

      if (message.equals(defaultInput)) {
        message = "";
      }

      UIFormTextAreaInput messageInput = uiComposer.getChild(UIFormTextAreaInput.class);
      messageInput.setValue("");
      // post activity via the current activity composer
      WebuiRequestContext requestContext = event.getRequestContext();
      Identity ownerIdentity = Utils.getIdentityManager().getOrCreateIdentity(OrganizationIdentityProvider.NAME,
          Util.getPortalRequestContext().getRemoteUser());
      ExoSocialActivity activity = new ExoSocialActivityImpl(Utils.getViewerIdentity().getId(), PeopleService.PEOPLE_APP_ID,
          message, null);
      activity.setType(ACTIVITY_TYPE);
      Utils.getActivityManager().saveActivity(ownerIdentity, activity);
    }
  }
}