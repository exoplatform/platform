/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.applicationregistry.webui;

import java.util.Comparator;
import java.util.List;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.services.portletcontainer.pci.model.Description;
import org.exoplatform.services.portletcontainer.pci.model.DisplayName;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Sep 11, 2008  
 */
public class Util {
  
  static final public String getDisplayNameValue(List<DisplayName> list, String defaultValue) {
    if(list == null || list.isEmpty()) return defaultValue;
    return list.get(0).getDisplayName();
  }
  
  static final public String getDescriptionValue(List<Description> list, String defaultValue) {
    if(list == null || list.isEmpty()) return defaultValue;
    return list.get(0).getDescription();
  }
  
  static public class CategoryComparator implements Comparator<ApplicationCategory> {

    public int compare(ApplicationCategory cate1, ApplicationCategory cate2) {
      return cate1.getDisplayName().compareTo(cate2.getDisplayName());
    }
    
  }
  
  static public class ApplicationComparator implements Comparator<Application> {

    public int compare(Application app1, Application app2) {
      return app1.getDisplayName().compareTo(app2.getDisplayName());
    }
    
  }
  
  static public class GadgetComparator implements Comparator<Gadget> {

    public int compare(Gadget gadget1, Gadget gadget2) {
      return gadget1.getTitle().compareTo(gadget2.getTitle());
    }
    
  }
  
}
