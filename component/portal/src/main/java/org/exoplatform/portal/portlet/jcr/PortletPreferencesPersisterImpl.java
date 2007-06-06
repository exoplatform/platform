/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.portlet.jcr;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.exoplatform.portal.config.jcr.DataMapper;
import org.exoplatform.portal.config.model.PortalConfig;
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
  final private static String EXO_DATA_TYPE = "exo:data" ;
  
  final private static String PORTAL = "portal" ;
  
  final private static String PORTAL_DATA = "MainPortalData" ;
  final private static String USER_DATA = "UserPortalData";
  final private static String GROUP_DATA = "SharedPortalData";
  
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
    String [] components = owner.split("#");
    if(components.length < 2) return null;
    String ownerType = components[0];
    String ownerId = components[1];
    Session session = jcrRegService_.getSession();
    Node portletPrefSetNode = getSetNode(session, PORTLET_PREFERENCES_SET_NODE, ownerType, ownerId);  
    if(portletPrefSetNode == null) {
      session.logout();
      return null;
    }
    ExoWindowID exoWindowID = (ExoWindowID) windowID ; 
    String name  = exoWindowID.getPersistenceId().replace('/', '_').replace(':', '_');
    if(!portletPrefSetNode.hasNode(name)) {
      session.logout();
      return null;
    }
    Node node = portletPrefSetNode.getNode(name);
    ExoPortletPreferences portletPreferences = mapper_.toPortletPreferences(node).toExoPortletPreferences() ;
    session.logout();
    return portletPreferences;
  }
  
  
  private Node getSetNode(Session session, String set, String ownerType, String ownerId) throws Exception {
    Node portalNode = getDataNode(session, ownerType, ownerId);
    if(portalNode == null || !portalNode.hasNode(set)) return  null;
    return portalNode.getNode(set) ;    
  }

  private Node getDataNode(Session session, String ownerType, String ownerId) throws Exception {
    if(ownerType.equals(PortalConfig.PORTAL_TYPE)) {
      Node appNode = jcrRegService_.getApplicationRegistryNode(session, PORTAL_DATA);
      if(appNode.hasNode(ownerId)) return appNode.getNode(ownerId);
      return null;
    }
    
    if(ownerType.equals(PortalConfig.USER_TYPE)){
      Node node = jcrRegService_.getApplicationRegistryNode(session, ownerId, USER_DATA);
      if(node == null || !node.hasNode(ownerId)) return null;
      node = node.getNode(ownerId);
      if(!node.hasNode(EXO_DATA_TYPE)) return null;
      node = node.getNode(EXO_DATA_TYPE);
      if(node.hasNode(PORTAL)) return node.getNode(PORTAL);
      return null;
    } 
    
    if(ownerType.equals(PortalConfig.GROUP_TYPE)){
      Node node = jcrRegService_.getApplicationRegistryNode(session, GROUP_DATA);
      String [] groups = ownerId.split("/");
      for(String group : groups) {
        if(group.trim().length() < 1) continue;
        if(!node.hasNode(group)) return null;
        node = node.getNode(group);
      }
      if(!node.hasNode(EXO_DATA_TYPE)) return null;
      node = node.getNode(EXO_DATA_TYPE);
      if(node.hasNode(PORTAL)) return node.getNode(PORTAL);
    }
    
    return null;
  }
  
  @SuppressWarnings("unused")
  public void savePortletPreferences(WindowID windowID, ExoPortletPreferences exoPref) throws Exception {
  }
  
}