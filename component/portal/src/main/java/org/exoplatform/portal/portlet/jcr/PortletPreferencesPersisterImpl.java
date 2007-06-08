/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.portlet.jcr;

import org.apache.commons.logging.Log;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.portlet.PortletPreferences;
import org.exoplatform.services.log.LogService;
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
  
  
  @SuppressWarnings("unused")
  private transient Log log_;
  
  private DataStorage dataStorage_;
  
  public PortletPreferencesPersisterImpl(DataStorage dataStorage, LogService lservice) {
    log_ = lservice.getLog(getClass()); 
    dataStorage_ = dataStorage;
  }

  public ExoPortletPreferences getPortletPreferences(WindowID windowID) throws Exception {
    PortletPreferences portletPreferences = dataStorage_.getPortletPreferences(windowID);
    return portletPreferences == null ? null : portletPreferences.toExoPortletPreferences();
  }
  
  @SuppressWarnings("unused")
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