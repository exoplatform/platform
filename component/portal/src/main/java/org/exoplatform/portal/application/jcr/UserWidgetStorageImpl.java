/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.jcr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jcr.Node;

import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.portal.application.UserWidgetStorage;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;

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
  private NodeHierarchyCreator nodeCreator ;

  public UserWidgetStorageImpl(NodeHierarchyCreator creator) throws Exception{   
    nodeCreator = creator ;
  }

  private Node createWidgetInstanceNode(SessionProvider sessionProvider, String userName, String widgetType, String instanceId) throws Exception{
    Node userApplicationsNode = nodeCreator.getUserApplicationNode(sessionProvider, userName) ;
    Node widgetsApp = getNode(userApplicationsNode, WIDGETS_REGETSTRY_NODE, null) ;
    Node widgetsTypeNode = getNode(widgetsApp, widgetType, null);
    Node node = getNode(widgetsTypeNode, instanceId, WIDGET_NODE_TYPE);
    userApplicationsNode.save();
    return node;
  }

  public void save(String userName, String widgetType, String instantId, Object data) throws Exception{
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node widgetNode = createWidgetInstanceNode(sessionProvider, userName, widgetType, instantId);
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
    sessionProvider.close() ;
  }
  
  public Object get(String userName, String widgetType, String instantId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node widgetNode = getWidgetNode(sessionProvider, userName, widgetType, instantId);
    if(widgetNode == null ) {
      sessionProvider.close() ;
      return null;
    }
    byte[] bytes =  IOUtil.getStreamContentAsBytes(widgetNode.getProperty(DATA).getStream());
    sessionProvider.close() ;
    return bytes;
  }

  public void delete(String userName, String widgetType, String instantId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node widgetNode = getWidgetNode(sessionProvider, userName, widgetType, instantId);
    if(widgetNode == null ) {
      sessionProvider.close() ;
      return;
    }
    Node parentNode = widgetNode.getParent();
    widgetNode.remove() ;
    parentNode.save();
    sessionProvider.close() ;
  }

  private Node getNode(Node parent, String name, String nodeType) throws Exception {
    if(parent.hasNode(name)){
      return parent.getNode(name);
    }
    Node node = null;
    if(nodeType == null) node= parent.addNode(name);
    else node = parent.addNode(name, nodeType);
    parent.save();
    return node;
  }

  private Node getWidgetNode(SessionProvider sessionProvider, String userName, String widgetType, String instantId) throws Exception {
    Node userAppsNode = nodeCreator.getUserApplicationNode(sessionProvider, userName) ;
    String instancePath = WIDGETS_REGETSTRY_NODE + "/" + widgetType + "/" + instantId ;
    if(userAppsNode.hasNode(instancePath)) return userAppsNode.getNode(instancePath) ;
    return null;
  }
}