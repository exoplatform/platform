/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.portal.application.jcr;

import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.WindowID;
import org.exoplatform.services.portletcontainer.pci.model.ExoPortletPreferences;
import org.exoplatform.services.portletcontainer.persistence.PortletPreferencesPersister;
/**
 * Jul 16, 2004 
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: PortletPreferencesPersisterImpl.java,v 1.4 2004/08/11 02:22:16 tuan08 Exp $
 */
public class PortletPreferencesPersisterImpl implements PortletPreferencesPersister {
  
  private DataStorage dataStorage_;
  
  public PortletPreferencesPersisterImpl(DataStorage dataStorage) {
    dataStorage_ = dataStorage;
  }

  public ExoPortletPreferences getPortletPreferences(WindowID windowID) throws Exception {
    PortletPreferences portletPreferences = dataStorage_.getPortletPreferences(windowID);
    return portletPreferences == null ? null : portletPreferences.toExoPortletPreferences();
  }
  
  public void savePortletPreferences(WindowID windowID, ExoPortletPreferences exoPref) throws Exception {
    PortletPreferences portletPreferences = new PortletPreferences(exoPref);
    ExoWindowID exoWindowID = (ExoWindowID) windowID;
    portletPreferences.setWindowId(exoWindowID.getPersistenceId());
    String owner = windowID.getOwner();
    String [] components = owner.split("#");
    if(components.length < 2) throw new Exception("WindowId is invalid "+windowID);
    portletPreferences.setOwnerType(components[0]);
    portletPreferences.setOwnerId(components[1]);
    dataStorage_.save(portletPreferences);
  }
  
}