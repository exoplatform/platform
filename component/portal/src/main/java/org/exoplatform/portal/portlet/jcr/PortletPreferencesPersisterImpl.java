/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.portlet.jcr;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.exoplatform.portal.config.jcr.DataMapper;
import org.exoplatform.registry.JCRRegistryService;
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
  
  final public static String PORTLE_TPREFERENCES_TYPE = "exo:portletPreferences";
  final private static String PORTLET_PREFERENCES_SET_NODE = "portletPreferences" ;
  final public static String ID = "id" ;
  
  final private static String PORTAL_DATA = "MainPortalData" ;
  
  final private DataMapper mapper_ = new DataMapper();
  
  @SuppressWarnings("unused")
  private transient Log log_;
  
  private JCRRegistryService jcrRegService_;
  
  public PortletPreferencesPersisterImpl(JCRRegistryService jcrRegService, LogService lservice) {
    log_ = lservice.getLog(getClass()); 
    jcrRegService_ = jcrRegService;
  }

  public ExoPortletPreferences getPortletPreferences(WindowID windowID) throws Exception {
    String owner = windowID.getOwner();
    String [] components = owner.split("/");
    if(components.length < 2) return null; 
    String ownerType = components[0];
    String ownerId = components[1];
    Session session = jcrRegService_.getSession();
    Node appNode = jcrRegService_.getApplicationRegistryNode(session, ownerType);
    if(appNode == null || !appNode.hasNode(ownerId)) {
      session.logout();
      return null;
    }
    Node ownerNode = appNode.getNode(ownerId);
    if(!ownerNode.hasNode(PORTLET_PREFERENCES_SET_NODE)) {
      session.logout();
      return null;
    }
    Node portletPreNode = ownerNode.getNode(PORTLET_PREFERENCES_SET_NODE);
    ExoWindowID exoWindowID = (ExoWindowID) windowID ; 
    String name  = exoWindowID.getPersistenceId().replace('/', '_').replace(':', '_');
    if(!portletPreNode.hasNode(name)) {
      session.logout();
      return null;
    }
    Node node = portletPreNode.getNode(name);
    ExoPortletPreferences portletPreferences = mapper_.toPortletPreferences(node).toExoPortletPreferences() ;
    session.logout();
    return portletPreferences;
  }
  
  @SuppressWarnings("unused")
  public void savePortletPreferences(WindowID windowID, ExoPortletPreferences exoPref) throws Exception {
  }
  
}