/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.portlet.jcr;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.exoplatform.portal.config.jcr.DataMapper;
import org.exoplatform.services.jcr.RepositoryService;
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
  
  final public static String PORTLET_TPREFERENCES = "portletPreferences";
  final public static String PORTLE_TPREFERENCES_TYPE = "exo:portletPreferences";
  final public static String ID = "id" ;
  
  final private static String PORTAL_APP = "PortalApp" ;
  
  final private static String NT_FOLDER_TYPE = "nt:folder" ;
  
  final private DataMapper mapper_ = new DataMapper();
  
  @SuppressWarnings("unused")
  private transient Log log_;
  
  final private static String WORKSPACE = "production" ;
  
  private  RepositoryService service_ ;
  
  public PortletPreferencesPersisterImpl(RepositoryService jcrService, LogService lservice) {
    log_ = lservice.getLog(getClass()); 
    service_ = jcrService;
  }

  public ExoPortletPreferences getPortletPreferences(WindowID windowID) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node rootNode = create(session.getRootNode().getNode(PORTAL_APP), windowID.getOwner());
    if(rootNode == null || !rootNode.hasNode(PORTLET_TPREFERENCES)) return null;
    Node portletPreNode = rootNode.getNode(PORTLET_TPREFERENCES);
    if(portletPreNode == null) return null;
    ExoWindowID exoWindowID = (ExoWindowID) windowID ; 
    String name  = exoWindowID.getPersistenceId().replace('/', '_').replace(':', '_');
    if(!portletPreNode.hasNode(name)) return null;
    Node node = portletPreNode.getNode(name);
    return mapper_.toPortletPreferences(node).toExoPortletPreferences() ;
  }
  
  private Node create(Node parent, String name) throws Exception {
    if(parent.hasNode(name)) return parent.getNode(name);    
    Node node = parent.addNode(name, NT_FOLDER_TYPE);
    parent.save();
    return node;    
  }
  
  @SuppressWarnings("unused")
  public void savePortletPreferences(WindowID windowID, ExoPortletPreferences exoPref) throws Exception {
  }
  
}