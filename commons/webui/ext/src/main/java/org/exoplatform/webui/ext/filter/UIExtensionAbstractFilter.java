/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.webui.ext.filter;

import java.util.Map;
import java.util.Set;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * 6 mai 2009  
 */
public abstract class UIExtensionAbstractFilter implements UIExtensionFilter {

  /**
   * The default key of the message to display in case of error
   */
  protected final String messageKey;
  
  /**
   * The flag used to indicate if the filter is mandatory
   */
  private final UIExtensionFilterType type;
  
  protected UIExtensionAbstractFilter() {
    this(null);
  }
  
  protected UIExtensionAbstractFilter(String messageKey) {
    this(messageKey, UIExtensionFilterType.REQUIRED);
  }
  
  protected UIExtensionAbstractFilter(String messageKey, UIExtensionFilterType type) {
    this.messageKey = messageKey;
    this.type = type;
  }
  
  /**
   * {@inheritDoc}
   */  
  public UIExtensionFilterType getType() {
    return type;
  }

  /**
   * Creates a popup to display the message
   */
  protected void createUIPopupMessages(Map<String, Object> context, String key, Object[] args, int type) {
    createUIPopupMessages(context, new ApplicationMessage(key, args, type));
  }

  /**
   * Creates a popup to display the message
   */
  protected void createUIPopupMessages(Map<String, Object> context, String key, Object[] args) {
    createUIPopupMessages(context, key, args, ApplicationMessage.WARNING);
  }

  /**
   * Creates a popup to display the message
   */
  protected void createUIPopupMessages(Map<String, Object> context, String key) {
    createUIPopupMessages(context, key, null);
  }

  /**
   * Creates a popup to display the message
   */
  private void createUIPopupMessages(Map<String, Object> context, ApplicationMessage message) {
    UIApplication uiApp = (UIApplication) context.get(UIApplication.class.getName());
    WebuiRequestContext requestContext = (WebuiRequestContext) context.get(WebuiRequestContext.class.getName());
    uiApp.addMessage(message);
    UIComponent uicomponent = uiApp.getUIPopupMessages();
    Set<UIComponent> sComponents = requestContext.getUIComponentToUpdateByAjax();
    if (sComponents == null || !sComponents.contains(uicomponent)) {
      requestContext.addUIComponentToUpdateByAjax(uicomponent);          
    }
  }
}
