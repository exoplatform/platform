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

import java.util.List;
import java.util.Map;

import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;

/**
 * This class is used to manage all the extensions available into the system.
 * The main target is to first add the ability to add new extension dynamically without
 * changing anything in the source code.  
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * May 04, 2009  
 */
public interface UIExtensionManager {

  /**
   * First check if the given extension exists, if so it checks if the extension can
   * be added according (all filters that are mandatory) to the given context, 
   * if so it adds the extension to the parent and returns the extension otherwise 
   * it returns <code>null</code>
   * 
   * @param extensionType the type of the extension, usually it is the FQN of the component 
   * that displays the extension
   * @param extensionName the name of the extension to add
   * @param context the context to check in order to know if the extension can be added 
   * @param parent the parent component to which the extension must be added
   * @return the component related to the extension if it can be added, <code>null</code>
   * otherwise
   * @throws Exception if an error occurs
   */
  public UIComponent addUIExtension(String extensionType, String extensionName, Map<String, Object> context, UIContainer parent) throws Exception;

  /**
   * It checks if the extension can be added (all filters that are mandatory) according 
   * to the given context, if so it adds the extension to the parent and returns the 
   * extension otherwise it returns <code>null</code>
   * 
   * @param extension the extension to add
   * @param context the context to check in order to know if the extension can be added
   * @param parent the parent component to which the extension must be added
   * @return the component related to the extension if it can be added, <code>null</code>
   * otherwise
   * @throws Exception if an error occurs
   */
  public UIComponent addUIExtension(UIExtension extension, Map<String, Object> context, UIContainer parent) throws Exception;
  
  /**
   * Checks if all the filters pass, if one filter doesn't pass the method UIExtension.onDeny will be called
   * 
   * @param extensionType the type of the extension, usually it is the FQN of the component 
   * that displays the extension
   * @param extensionName the name of the extension to add
   * @param context the context to check
   * @return <code>true</code> if all the filter could pass, <code>false</code> otherwise
   */
  public boolean accept(String extensionType, String extensionName, Map<String, Object> context);
  
  /**
   * Gives all the extensions related to the given type
   * 
   * @param type the type of the extension, usually it is the FQN of the component 
   * that displays the extension
   * @return a list of all the extensions that belongs to the given owner 
   */
  public List<UIExtension> getUIExtensions(String type);

  /**
   * Give the extension corresponding to the given criteria
   * 
   * @param type the type of the extension, usually it is the FQN of the component 
   * that displays the extension
   * @param name the name of the extension to get
   * @return the corresponding UIExtension if it exists, <code>null</code> otherwise
   */
  public UIExtension getUIExtension(String type, String name);
  
  /**
   * Register a new extension
   * 
   * @param extension the extension to register
   */
  public void registerUIExtension(UIExtension extension);
  
  /**
   * Register all the extensions defined into the UIExtensionPlugin
   * 
   * @param extensionPlugin the plugin to treat
   */
  public void registerUIExtensionPlugin(UIExtensionPlugin extensionPlugin);
}
