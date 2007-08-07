/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.widget.jcr;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.registry.ApplicationRegistry;
import org.exoplatform.registry.JCRRegistryService;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          thuy.le@exoplatform.com
 * Aug 6, 2007  
 */
public class UserWidgetDataService {
  
  public static final String WIDGETS_REGETSTRY_NODE = "widgets";
  public static final String DATA = "data";
  public static final String WIDGET_NODE_TYPE = "exo:widget";
  private JCRRegistryService jcrRegService_;
  public UserWidgetDataService(JCRRegistryService jcrRegService) throws Exception{   
    jcrRegService_ = jcrRegService;
  }
  
  private Node createWidgetAppNode(String userName, String widgetType, String instantId, boolean overwrite) throws Exception{
    Node appsNode = jcrRegService_.createApplicationRegistry(userName, new ApplicationRegistry(WIDGETS_REGETSTRY_NODE), false);
    Node widgetsTypeNode = createNode(appsNode, widgetType, false, null);
    Node node = createNode(widgetsTypeNode, instantId, false, WIDGET_NODE_TYPE);
    appsNode.save();
    return node;
  }
  
  public void save(String userName, String widgetType, String instantId, String data) throws Exception{
    Node widgetNode = createWidgetAppNode(userName, widgetType, instantId, false);
    widgetNode.setProperty(DATA, data);
    widgetNode.save();
    Session session = jcrRegService_.getSession();
    session.save();
    session.logout();
  }
  
  public void remove(String userName, String widgetType, String instantId)throws Exception{
    Node widgetNode = getWidgetNode(userName, widgetType, instantId);
    if(widgetNode == null ) return;
    Node parentNode = widgetNode.getParent();
    parentNode.save();
    Session session = jcrRegService_.getSession();
    session.save();
    session.logout();
  }
  
  public String getWidgetData(String userName, String widgetType, String instantId)throws Exception{
    Node widgetNode = getWidgetNode(userName, widgetType, instantId); 
    if(widgetNode == null) return null;
    return widgetNode.getProperty(DATA).getString();
  }
  
  private Node createNode(Node appsNode, String name, boolean overwrite, String nodeType) throws Exception {
    Session session = jcrRegService_.getSession();
    if( appsNode.hasNode(name)){
      if(!overwrite){
        session.logout();
        return appsNode.getNode(name);
      }
      appsNode.getNode(name).remove();
      appsNode.save();
    }
    Node node = null;
    if(nodeType == null) node= appsNode.addNode(name);
    else node = appsNode.addNode(name, nodeType);
    appsNode.save();
    session.save();
    session.logout();
    return node;
  }
  
  public Node getWidgetNode(String userName, String widgetType, String instantId) throws Exception {
    Node appsNode = jcrRegService_.createApplicationRegistry(userName, new ApplicationRegistry(WIDGETS_REGETSTRY_NODE), false);
    if(appsNode.hasNode(widgetType + "/" + instantId)) {
       return appsNode.getNode(widgetType + "/" + instantId);
    } 
    return null;
  }
}