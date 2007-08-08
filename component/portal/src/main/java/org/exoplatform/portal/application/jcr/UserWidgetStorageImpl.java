/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.jcr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.portal.application.UserWidgetStorage;
import org.exoplatform.registry.ApplicationRegistry;
import org.exoplatform.registry.JCRRegistryService;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          thuy.le@exoplatform.com
 * Aug 6, 2007  
 */
public class UserWidgetStorageImpl implements UserWidgetStorage {

  public static final String WIDGETS_REGETSTRY_NODE = "widgets";
  public static final String DATA = "data";
  public static final String WIDGET_NODE_TYPE = "exo:widget";
  private JCRRegistryService jcrRegService_;

  public UserWidgetStorageImpl(JCRRegistryService jcrRegService) throws Exception{   
    jcrRegService_ = jcrRegService;
  }

  private Node createWidgetAppNode(String userName, String widgetType, String instantId) throws Exception{
    Node appsNode = jcrRegService_.createApplicationRegistry(userName, new ApplicationRegistry(WIDGETS_REGETSTRY_NODE), false);
    Node widgetsTypeNode = getNode(appsNode, widgetType, null);
    Node node = getNode(widgetsTypeNode, instantId, WIDGET_NODE_TYPE);
    appsNode.save();
    return node;
  }

  public void save(String userName, String widgetType, String instantId, Object data) throws Exception{
    Node widgetNode = createWidgetAppNode(userName, widgetType, instantId);
    InputStream  inputStream;
    if(data instanceof byte[]) {
      inputStream  = new ByteArrayInputStream((byte[])data);
    } else if (data instanceof InputStream){
      inputStream = (InputStream) data;
    } else {
      inputStream  = new ByteArrayInputStream(data.toString().getBytes());
    }
    widgetNode.setProperty(DATA, inputStream);
    widgetNode.save();
    Session session = jcrRegService_.getSession();
    session.save();
    session.logout();
  }
  
  public Object get(String userName, String widgetType, String instantId) throws Exception {
      return null;
  }

  public void delete(String userName, String widgetType, String instantId) throws Exception {
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

  private Node getNode(Node appsNode, String name, String nodeType) throws Exception {
    Session session = jcrRegService_.getSession();
    if(appsNode.hasNode(name)){
      session.logout();
      return appsNode.getNode(name);
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