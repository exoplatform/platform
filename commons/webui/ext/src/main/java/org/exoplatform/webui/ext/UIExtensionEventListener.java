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
package org.exoplatform.webui.ext;

import java.util.Map;

import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * The event listener dedicated to UIExtension
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * May 05, 2009  
 */
public abstract class UIExtensionEventListener<T extends UIComponent> extends EventListener<T> {

  @Override
  public void execute(Event<T> event) throws Exception {
    Map<String, Object> context = createContext(event);
    UIComponent uiExtension = event.getSource();
    UIExtensionManager manager = uiExtension.getApplicationComponent(UIExtensionManager.class);
    if (manager.accept(getExtensionType(), event.getName(), context)) {
      processEvent(event);
    }
  }

  /**
   * All the filters passed so in this method, we can process the event 
   * without checking anything 
   * 
   * @param event the event to process
   * @throws Exception if an exception occurs
   */
  protected abstract void processEvent(Event<T> event) throws Exception;
  
  /**
   * Create the context from the given event
   * 
   * @param event the event to convert into a context
   * @return the context
   * @throws Exception if an exception occurs
   */
  protected abstract Map<String, Object> createContext(Event<T> event) throws Exception;
  
  /**
   * Gives the type of the extension
   * @return the type of the extension
   */
  protected abstract String getExtensionType();
}
