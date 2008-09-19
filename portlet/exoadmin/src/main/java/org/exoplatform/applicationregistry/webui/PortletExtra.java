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

import java.util.List;

import org.exoplatform.services.portletcontainer.pci.PortletData;
import org.exoplatform.services.portletcontainer.pci.model.Description;
import org.exoplatform.services.portletcontainer.pci.model.DisplayName;
import org.exoplatform.services.portletcontainer.pci.model.ExoPortletPreferences;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Sep 18, 2008  
 */
public class PortletExtra {

  private String id_;
  private String name_;
  private String group_;
  private String type_;
  private PortletData portletData_;
  
  public PortletExtra(String fullName, String type, PortletData data) {
    String [] fragments = fullName.split("/");
    group_ = fragments[0];
    name_ = fragments[1];
    id_ = String.valueOf(fullName.hashCode()) ;
    type_ = type;
    portletData_ = data;
  }
  
  public String getId() { return id_; }
  
  public String getName() { return name_; }
  
  public String getPortletGroup() { return group_; }
  
  public String getType() { return type_; }
  
  public PortletData getPortletData() { return portletData_; }
  
  public String getDisplayName() {
    List<DisplayName> displaNames = portletData_.getDisplayName();
    if(displaNames == null || displaNames.isEmpty()) return name_;
    return displaNames.get(0).getDisplayName();    
  }
  
  public String getDescription() {
    List<Description> des = portletData_.getDescription();
    if(des == null || des.isEmpty()) return name_;
    return des.get(0).getDescription();
  }
  
  public ExoPortletPreferences getPortletPreferences() {
    return portletData_.getPortletPreferences();
  }
  
}