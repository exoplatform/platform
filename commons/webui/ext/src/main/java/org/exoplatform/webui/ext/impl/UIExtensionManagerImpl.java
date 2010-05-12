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
package org.exoplatform.webui.ext.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.ext.UIExtension;
import org.exoplatform.webui.ext.UIExtensionManager;
import org.exoplatform.webui.ext.UIExtensionPlugin;
import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilterType;

/**
 * The default implementation of an extension manager
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * May 04, 2009  
 */
public class UIExtensionManagerImpl implements UIExtensionManager {

  /**
   * Logger.
   */
  private static final Log LOG  = ExoLogger.getLogger(UIExtensionManagerImpl.class);
  
  /**
   * All the registered extensions
   */
  private final ConcurrentMap<String, Map<String, UIExtension>> extensions = new ConcurrentHashMap<String, Map<String, UIExtension>>(); 
  
  /**
   * {@inheritDoc}
   */
  public boolean accept(String extensionType, String extensionName, Map<String, Object> context) {
    UIExtension extension = getUIExtension(extensionType, extensionName);
    if (extension == null) {
      LOG.warn("The extension type = " + extensionType + ", name = " + extensionName + " cannot be found");
    } else {
      return accept(extension, context, false);
    }
    return false;    
  }
  
  /**
   * {@inheritDoc}
   */
  public UIComponent addUIExtension(String extensionType,
                                 String extensionName,
                                 Map<String, Object> context,
                                 UIContainer parent) throws Exception {
    UIExtension extension = getUIExtension(extensionType, extensionName);
    if (extension == null) {
      LOG.warn("The extension type = " + extensionType + ", name = " + extensionName + " cannot be found");
    } else {
      String id = createComponentId(parent, extensionName);
      // If the component has already been added, first remove it
      parent.removeChildById(id);
      if (accept(extension, context, true)) {
        // The filters passed successfully
        UIComponent component = parent.createUIComponent(extension.getComponent(), null, id);
        parent.addChild(component);
        return component;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public UIComponent addUIExtension(UIExtension extension, Map<String, Object> context, UIContainer parent) throws Exception {
    return addUIExtension(extension.getType(), extension.getName(), context, parent);
  }
  
  /**
   * {@inheritDoc}
   */
  public void registerUIExtensionPlugin(UIExtensionPlugin extensionPlugin) {
    List<UIExtension> extensions = extensionPlugin.getExtensions();
    if (extensions != null) {
      for (UIExtension extension : extensions) {
        registerUIExtension(extension);
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void registerUIExtension(UIExtension extension) {
    Map<String, UIExtension> extensions = getUIExtensions(extension.getType(), true);
    extensions.put(extension.getName(), extension);
  }  
  
  /**
   * {@inheritDoc}
   */
  public List<UIExtension> getUIExtensions(String type) {
    Map<String, UIExtension> extensions = getUIExtensions(type, false);
    if (extensions != null) {
      List<UIExtension> lExtensions =  new ArrayList<UIExtension>(extensions.values());
      Collections.sort(lExtensions);
      return lExtensions;
    }
    return null;
  }
    
  /**
   * {@inheritDoc}
   */
  public UIExtension getUIExtension(String type, String name) {
    Map<String, UIExtension> extensions = getUIExtensions(type, false);
    if (extensions != null) {
      return extensions.get(name);
    }
    return null;
  }

  private Map<String, UIExtension> getUIExtensions(String extensionType, boolean create) {
    Map<String, UIExtension> mExtensions = extensions.get(extensionType);
    if (mExtensions == null && create) {
      extensions.putIfAbsent(extensionType, new ConcurrentHashMap<String, UIExtension>());
      mExtensions = extensions.get(extensionType);
    }
    return mExtensions;
  }
  
  protected boolean accept(UIExtension extension, Map<String, Object> context, boolean checkOnly) {
    return accept(extension.getExtendedFilters(), context, checkOnly) &&
           accept(extension.getComponentFilters(), context, checkOnly) &&
           extension.isEnable();
  }
  
  protected String createComponentId(UIContainer parent, String extensionName) {
    StringBuilder sb = new StringBuilder(128);
    sb.append(parent.getId());
    sb.append('_');
    sb.append(extensionName);
    sb.append('_');
    return sb.toString();
  }
  
  /**
   * Checks if the given filters accept the given context
   * @param checkOnly if <code>true</code> only the mandatory filters will be tested, otherwise
   * all the filters will be tested
   * @return <code>true</code> if all filters could pass otherwise it returns <code>false</code>
   */
  private boolean accept(List<UIExtensionFilter> filters, Map<String, Object> context, boolean checkOnly) {
    boolean result = true;
    if (filters != null) {
      for (int i = 0, length = filters.size(); i < length; i++) {
        UIExtensionFilter filter = filters.get(i);
        UIExtensionFilterType type = filter.getType();
        if (checkOnly && (type == null || !type.showExtensionOnlyIfOK())) {
          // only test the filter that are mandatory
          continue;
        }
        try {
          if (!filter.accept(context)) {
            onDeny(checkOnly, filter, context);
            if (type != null) {
              if (type.checkOtherFiltersOnlyIfOK()) {
                return false;
              } else if (type.acceptOnlyIfOK()) {
                result = false;
              }
            }
          }
        } catch (Exception e) {
          LOG.error("An execption occurs while applying the filter", e);
          onDeny(checkOnly, filter, context);
          if (type != null) {
            if (type.checkOtherFiltersOnlyIfOK()) {
              return false;
            } else if (type.acceptOnlyIfOK()) {
              result = false;
            }
          }
        }
      }
    }
    return result;    
  }
  
  private void onDeny(boolean checkOnly, UIExtensionFilter filter, Map<String, Object> context) {
    if (!checkOnly) {
      try {
        filter.onDeny(context);
      } catch (Exception e) {
        LOG.error("An execption occurs while calling the method onDeny of the filter", e);
      }
    }    
  }
}
