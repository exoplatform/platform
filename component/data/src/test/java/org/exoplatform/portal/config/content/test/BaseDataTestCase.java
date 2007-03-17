/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.content.test;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.portal.config.JCRPortalDAO;
import org.exoplatform.portal.portlet.HibernatePortletPreferencesPersister;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.log.LogService;
import org.exoplatform.services.portletcontainer.persistence.PortletPreferencesPersister;
import org.exoplatform.test.BasicTestCase;
/**
 * Created by The eXo Platform SAS
 * Author : Xuan Hoa Pham
 *          hoapham@exoplatform.com
 * 					phamvuxuanhoa@gmail.com
 * Dec 12, 2006  
 */
public class BaseDataTestCase extends BasicTestCase {
  
  final protected static String NT_UNSTRUCTURED = "nt:unstructured".intern() ;
  final protected static String NT_FOLDER = "nt:folder".intern() ;
  final protected static String NT_FILE = "nt:file".intern() ;   
    
  final protected static String SYSTEM_WS = "systemWS".intern() ;
  protected PortletPreferencesPersister porletPreferencesService_;
  protected Node defaultRoot_;
  protected Node systemNode_;
  protected ManageableRepository repository_;
  protected PortalContainer manager_;  
  protected Session sysSessionOnDefault_ ;  
  protected JCRPortalDAO service_ ;
  
  public void setUp() throws Exception{
    
    LogService logService = (LogService) RootContainer.getInstance().getComponentInstanceOfType(
               LogService.class); 

    logService.setLogLevel("org.exoplatform.services.jcr", LogService.DEBUG, true);     
    
    manager_ = PortalContainer.getInstance() ;
    if(System.getProperty("java.security.auth.login.config") == null)
      System.setProperty("java.security.auth.login.config", "src/resource/login.conf" );


    RepositoryService repositoryService = (RepositoryService) manager_.getComponentInstanceOfType(
                      RepositoryService.class);
        
    repository_ = repositoryService.getRepository();
    service_ = (JCRPortalDAO)manager_.
                               getComponentInstanceOfType(JCRPortalDAO.class) ;
    porletPreferencesService_ = (PortletPreferencesPersister) manager_.
                                getComponentInstanceOfType(HibernatePortletPreferencesPersister.class);
    sysSessionOnDefault_ = repository_.getSystemSession(SYSTEM_WS) ;  
    defaultRoot_ = sysSessionOnDefault_.getRootNode();    
    
    Node home = defaultRoot_.addNode("home") ;    
    Node userNode =home.addNode("exo");    
    Node nameService = userNode.addNode("services");
    Node portalHot = nameService.addNode("portal");
    portalHot.addNode("data");
    
    Node portalConfig = portalHot.addNode("portalConfig");
    

    Node pageNode = portalConfig.addNode("page");
    Node navigationNode = pageNode.addNode("navigation");
    navigationNode.addNode("portlet");
    
    defaultRoot_.save() ;
    sysSessionOnDefault_.save();
  }
}