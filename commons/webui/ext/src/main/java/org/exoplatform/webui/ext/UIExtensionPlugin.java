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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * This class allows you to dynamically define new extensions
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * May 04, 2009  
 */
public class UIExtensionPlugin extends BaseComponentPlugin {

  /**
   * Logger.
   */
  private static final Log LOG  = ExoLogger.getLogger(UIExtensionPlugin.class);

  /**
   * The initial parameter of this plugin
   */
  private final InitParams params;
  
  public UIExtensionPlugin(InitParams params) {
    this.params = params;
  }
  
  /**
   * @return all the extensions associated to this plugin 
   */
  public List<UIExtension> getExtensions() {
    Iterator<?> iterator = params.getObjectParamIterator();
    List<UIExtension> extensions = null;
    if (iterator != null) {
      while (iterator.hasNext()) {
        ObjectParameter o = (ObjectParameter) iterator.next();
        Object object = o.getObject();
        if (object instanceof UIExtension) {
          if (extensions == null) {
            extensions = new ArrayList<UIExtension>();            
          }
          extensions.add((UIExtension) object);          
        } else if(object != null) {
          LOG.warn("The object " + object + " should be of type UIExtension, it will be ignored");
        }
      }
    }
    return extensions;
  }
}
