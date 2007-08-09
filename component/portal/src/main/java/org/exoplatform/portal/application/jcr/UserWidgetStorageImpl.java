/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.jcr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.commons.utils.IOUtil;
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

  private Node createWidgetAppNode(Session session, String userName, String widgetType, String instantId) throws Exception{
    jcrRegService_.createUserHome(userName, false);
    jcrRegService_.createApplicationRegistry(userName, new ApplicationRegistry(WIDGETS_REGETSTRY_NODE), false);
    Node appsNode = jcrRegService_.getApplicationRegistryNode(session, userName, WIDGETS_REGETSTRY_NODE);
    Node widgetsTypeNode = getNode(session,appsNode, widgetType, null);
    Node node = getNode(session, widgetsTypeNode, instantId, WIDGET_NODE_TYPE);
    appsNode.save();
    return node;
  }

  public void save(String userName, String widgetType, String instantId, Object data) throws Exception{
    Session session = jcrRegService_.getSession();
    Node widgetNode = createWidgetAppNode(session, userName, widgetType, instantId);
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
    session.save();
    session.logout();
  }
  
  public Object get(String userName, String widgetType, String instantId) throws Exception {
    Session session = jcrRegService_.getSession();
    Node widgetNode = getWidgetNode(session, userName, widgetType, instantId);
    if(widgetNode == null ) {
      session.logout();
      return null;
    }
    byte[] bytes =  IOUtil.getStreamContentAsBytes(widgetNode.getProperty(DATA).getStream());
    session.logout();
    return bytes;
  }

  public void delete(String userName, String widgetType, String instantId) throws Exception {
    Session session = jcrRegService_.getSession();
    Node widgetNode = getWidgetNode(session, userName, widgetType, instantId);
    if(widgetNode == null ) {
      session.logout();
      return;
    }
    Node parentNode = widgetNode.getParent();
    parentNode.save();
    session.save();
    session.logout();
  }

  private Node getNode(Session session ,Node appsNode, String name, String nodeType) throws Exception {
    if(appsNode.hasNode(name)){
      return appsNode.getNode(name);
    }
    Node node = null;
    if(nodeType == null) node= appsNode.addNode(name);
    else node = appsNode.addNode(name, nodeType);
    appsNode.save();
    session.save();
    return node;
  }

  private Node getWidgetNode(Session session, String userName, String widgetType, String instantId) throws Exception {
    if( jcrRegService_.getUserNode(session, userName) == null) return null;
    Node appsNode = jcrRegService_.getApplicationRegistryNode(session, userName, WIDGETS_REGETSTRY_NODE);
    if(appsNode == null ) return null;
    if(appsNode.hasNode(widgetType + "/" + instantId)) {
      return appsNode.getNode(widgetType + "/" + instantId);
    } 
    return null;
  }
}