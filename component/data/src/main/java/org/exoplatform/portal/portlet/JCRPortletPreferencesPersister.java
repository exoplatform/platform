/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.portlet;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;

import org.apache.commons.logging.Log;
import org.exoplatform.portal.config.Data;
import org.exoplatform.portal.config.JCRDataService;
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
public class JCRPortletPreferencesPersister extends JCRDataService implements PortletPreferencesPersister {
  
  @SuppressWarnings("unused")
  private transient Log log_;
  
  public JCRPortletPreferencesPersister(LogService lservice) {
    log_ = lservice.getLog(getClass()); 
  }

  public ExoPortletPreferences getPortletPreferences(WindowID windowID) throws Exception {
    Node parentNode = getDataServiceNode(windowID.getOwner(), PORTLE_TPREFERENCES, false);
    if(parentNode == null) return null;
    ExoWindowID exoWindowID = (ExoWindowID) windowID ;    
    Node node = getNode(parentNode, exoWindowID.getPersistenceId());
    if(node == null) return null;
    Data data = nodeToData(node);
    PortletPreferences portletPerferences = 
      (PortletPreferences) fromXML(data.getData(), PortletPreferences.class);
    return  portletPerferences.toExoPortletPreferences() ;
  }

  public void savePortletPreferences(WindowID windowID, ExoPortletPreferences exoPref) throws Exception {
    Node parentNode = getDataServiceNode(windowID.getOwner(), PORTLE_TPREFERENCES, true);
    ExoWindowID exoWindowID = (ExoWindowID) windowID ;
    PortletPreferences  preferences = new PortletPreferences(exoPref);
    Data data = portletPreferencesConfigToData(preferences);
    
    Node node = getNode(parentNode, exoWindowID.getPersistenceId());
    if(node == null) {
      node = parentNode.addNode(exoWindowID.getPortletApplicationName());
    }
    dataToNode(data,node);
    saveData(node, data, exoWindowID.getPersistenceId());
  }  
  
  private void saveData(Node parentNode, Data data, String name) throws Exception {
    Date time = Calendar.getInstance().getTime();
    data.setModifiedDate(time);
    if(data.getCreatedDate() == null) data.setCreatedDate(time);
    if(parentNode.hasNode(name)) {
      Node node = parentNode.getNode(name);
      dataToNode(data, node);
      node.save();
    } else {
      Node node = parentNode.addNode(name, DATA_NODE_TYPE);
      dataToNode(data, node);
      parentNode.save();
    }
    getSession().save();
  }
  
}