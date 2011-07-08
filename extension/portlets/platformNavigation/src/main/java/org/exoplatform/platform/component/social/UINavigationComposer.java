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
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.social.webui.composer.UIActivityComposerContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIFormTextAreaInput;

@ComponentConfig(lifecycle = UIFormLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIUserPlatformToolBarPortlet/UINavigationComposer.gtmpl", events = { @EventConfig(listeners = UINavigationComposer.PostMessageActionListener.class) })
public class UINavigationComposer extends org.exoplatform.social.webui.composer.UIComposer {

  public static final String ACTIVITY_TYPE = "DEFAULT_ACTIVITY";

  private UIFormTextAreaInput messageInput;
  private UIActivityComposerContainer composerContainer;
  private Identity ownerIdentity;
  private String defaultInput;

  public UINavigationComposer() throws Exception {
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
    ownerIdentity = Utils.getIdentityManager().getOrCreateIdentity(OrganizationIdentityProvider.NAME,
        Util.getPortalRequestContext().getRemoteUser(), true);
    defaultInput = getLabel("UIComposer.input.What_Are_You_Working_On");
  }

  public Identity getOwnerIdentity() {
    return this.ownerIdentity;
  }

  public String getAvatarURL() {
    String ownerAvatar = ownerIdentity.getProfile().getAvatarUrl();
    if (ownerAvatar == null || ownerAvatar.isEmpty()) {
      ownerAvatar = LinkProvider.PROFILE_DEFAULT_AVATAR_URL;
    }
    return ownerAvatar;
  }

  public String getDefaultInput() {
    return this.defaultInput;
  }

  public static class PostMessageActionListener extends EventListener<UINavigationComposer> {
    @Override
    public void execute(Event<UINavigationComposer> event) throws Exception {
      UINavigationComposer uiComposer = event.getSource();
      String message = (uiComposer.getMessage() == null) ? "" : uiComposer.getMessage();
      if (message.equals(uiComposer.getDefaultInput())) {
        message = "";
      }
      Utils.getActivityManager().saveActivity(uiComposer.getOwnerIdentity(), ACTIVITY_TYPE, message);
      UIFormTextAreaInput messageInput = uiComposer.getChild(UIFormTextAreaInput.class);
      messageInput.setValue("");
    }
  }
}