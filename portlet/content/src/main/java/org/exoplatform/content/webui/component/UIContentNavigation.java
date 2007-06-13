/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.content.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.content.ContentDAO;
import org.exoplatform.portal.content.model.ContentNavigation;
import org.exoplatform.portal.content.model.ContentNode;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIDescription;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh  
 *          minhdv@exoplatform.com
 * Jul 6, 2006  
 * @version: $Id$
 */

@ComponentConfig(
    template =  "app:/groovy/content/webui/component/UIContentNavigation.gtmpl",
    events = {
        @EventConfig(listeners = UIContentNavigation.ChangeNodeActionListener.class),
        @EventConfig(listeners = UIContentNavigation.UpLevelActionListener.class),
        @EventConfig(listeners = UIContentNavigation.AddNodeActionListener.class),
        @EventConfig(listeners = UIContentNavigation.EditNodeActionListener.class),
        @EventConfig(listeners = UIContentNavigation.RemoveNodeActionListener.class, confirm = "UIContentNavigation.removeNode"),
        @EventConfig(listeners = UIContentNavigation.GetNodeInfoActionListener.class)
    }
)
public class UIContentNavigation extends UIContainer {

  private ContentNode selectedNode_ ;
  private ContentNode parentNode_ ; 
  private ContentNavigation nav_ ;
  private ContentNode grandNode_;
  
  void refresh() throws Exception {
    ContentDAO contentService = getApplicationComponent(ContentDAO.class) ;
    nav_ = contentService.get(Util.getUIPortal().getOwner());
    if(nav_ == null) {
      nav_ = new ContentNavigation();
      nav_.setOwner(Util.getUIPortal().getOwner());
    }
    if(nav_.getNodes() != null && nav_.getNodes().size() > 0){
      setSelectedNode(nav_.getNode(0).getId()); 
      return;
    }
    setSelectedNode(null);    
  }
  
  void save(ContentNode node) throws Exception {
    ContentDAO service = getApplicationComponent(ContentDAO.class) ; 
    ContentNode tempNode = selectedNode_;
    setSelectedNode(node.getId());
    if(parentNode_ != null){
      int idx = parentNode_.getChildren().indexOf(selectedNode_);
      parentNode_.getChildren().set(idx, node);
      node.setChildren((ArrayList<ContentNode>) selectedNode_.getChildren());
    }else if(selectedNode_ != null){
      int idx = nav_.getNodes().indexOf(selectedNode_);
      nav_.getNodes().set(idx, node);
      node.setChildren((ArrayList<ContentNode>) selectedNode_.getChildren());
    }else{
      if(tempNode == null) nav_.addNode(node); else tempNode.addChild(node);      
    }
    
    selectedNode_ = node;    
    service.save(nav_);
  }
  
  public ContentNode getSelectedNode() { return selectedNode_ ; } 
  
  public void setSelectedNode(String id) {
    parentNode_ = null ;
    grandNode_ = null ;
    selectedNode_ = findNode(id) ;   

    UIContentPortlet uiParent = getParent() ;
    UIContentWorkingArea uiWorkingArea = uiParent.getChild(UIContentWorkingArea.class);
    if(selectedNode_ == null){
      uiWorkingArea.setRenderedChild(UIDescription.class) ;
      return;
    }
    UIDetailContent uiDetail = uiWorkingArea.getChild(UIDetailContent.class) ;
    uiDetail.setContentNode(selectedNode_);
    uiWorkingArea.setRenderedChild(UIDetailContent.class) ;    
  }
  
  boolean isSelectedNode(ContentNode node){
    if(selectedNode_ == null) return false;
    return selectedNode_.getId().equals(node.getId());
  }
  
  List<ContentNode> getSibbingNodes() { 
    if(grandNode_ != null) return grandNode_.getChildren();
    if(parentNode_ != null) return parentNode_.getChildren();
    return nav_.getNodes();
  }
  
  public ContentNode getParentNode() { return parentNode_ ; }
  
  boolean isParentNode(ContentNode node){
    if(parentNode_ == null) return false;
    return parentNode_.getId().equals(node.getId());
  } 
  
  public ContentNavigation getContentNavigation() { return nav_; }

  private ContentNode findNode(String nodeId) {    
    if(nodeId == null || nav_.getNodes() == null) return null ;
    for(ContentNode node : nav_.getNodes()) {
      ContentNode value = findNode(nodeId, node);
      if(value != null) return value ;
    }  
    return null ; 
  }
  
  private ContentNode findNode(String nodeId, ContentNode node) {
    if(node.getId().equals(nodeId)) return node;
    List<ContentNode> children = node.getChildren() ;
    if(children == null) return null;
    for(ContentNode child : children) {
      if(child == null)  continue;
      ContentNode value = findNode(nodeId, child);
      if(value == null) continue;
      if(parentNode_ == null) parentNode_ = node;  else if(grandNode_ == null) grandNode_ = node;
      return value;
    }
    return null;
  }
  
  public boolean isLogon(){ 
    WebuiRequestContext  context = WebuiRequestContext.getCurrentInstance();
    if(context.getRemoteUser() == null || context.getRemoteUser().length() < 1 ) return false;
    return true;
  }

  static  public class ChangeNodeActionListener extends EventListener<UIContentNavigation> {
    public void execute(Event<UIContentNavigation> event) throws Exception {
      UIContentNavigation uiNav = event.getSource();      
      String id  = event.getRequestContext().getRequestParameter(OBJECTID);      
      uiNav.setSelectedNode(id) ;      
    }
  }

  static  public class UpLevelActionListener extends EventListener<UIContentNavigation> {
    public void execute(Event<UIContentNavigation> event) throws Exception {
      UIContentNavigation uiNav = event.getSource();
      ContentNode parentNode = uiNav.getParentNode() ;
      if(parentNode != null) uiNav.setSelectedNode(parentNode.getId()); 
      else uiNav.setSelectedNode(null);
    }
  }

  static  public class AddNodeActionListener extends EventListener<UIContentNavigation> {
    public void execute(Event<UIContentNavigation> event) throws Exception {     
//      UIContentNavigation uiNav = event.getSource();
//      UIContentPortlet uiParent = uiNav.getParent() ;
//      UIContentWorkingArea uiWorkingArea = uiParent.getChild(UIContentWorkingArea.class);
//      UIContentForm uiForm = uiWorkingArea.getChild(UIContentForm.class) ;
//      uiForm.setContentNode(null);
//      UIPortal uiPortal = Util.getUIPortal();
//      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
//      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;;
//      UIContentForm uiForm = uiMaskWS.createUIComponent(UIContentForm.class, null, null);
//      uiForm.setContentNode(null);
//      
//      UIContentNavigation uiNav = event.getSource();
//      ContentNode selectedNode = uiNav.getSelectedNode() ;
//      if(selectedNode == null)  return;
//      
//      uiMaskWS.setUIComponent(uiForm);
//      uiMaskWS.setWindowSize(640, 400);
//      uiMaskWS.setShow(true);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
//      Util.updateUIApplication(event);
      
      UIContentNavigation uiNav = event.getSource();
      UIContentPortlet uiParent = uiNav.getParent() ;
      UIContentWorkingArea uiWorkingArea = uiParent.getChild(UIContentWorkingArea.class);
      UIContentForm uiForm = uiWorkingArea.getChild(UIContentForm.class) ;
      uiForm.setContentNode(null);
      uiWorkingArea.setRenderedChild(UIContentForm.class) ;
      Class [] childrenToRender = {UIContentNavigation.class, UIContentWorkingArea.class };
      uiParent.setRenderedChildrenOfTypes(childrenToRender) ;
    }
  }
  

  static  public class EditNodeActionListener extends EventListener<UIContentNavigation> {
    public void execute(Event<UIContentNavigation> event) throws Exception {
      
//      UIContentNavigation uiNav = event.getSource();
//      UIPortal uiPortal = Util.getUIPortal();
//      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
//      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
//      
//      UIContentForm uiForm = uiMaskWS.createUIComponent(UIContentForm.class, null, null);
//      ContentNode selectedNode = uiNav.getSelectedNode() ;
//      if(selectedNode == null)  return;
//      uiForm.setContentNode(selectedNode);
//      uiMaskWS.setUIComponent(uiForm);      
//
//      uiMaskWS.setShow(true);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
//      Util.updateUIApplication(event);
      UIContentNavigation uiNav = event.getSource();
      UIContentPortlet uiParent = uiNav.getParent() ;
      UIContentWorkingArea uiWorkingArea = uiParent.getChild(UIContentWorkingArea.class);
      UIContentForm uiForm = uiWorkingArea.getChild(UIContentForm.class) ;
      ContentNode selectedNode = uiNav.getSelectedNode() ;
      if(selectedNode == null)  {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIContentNavigation.msg.EditNode", null)) ;
        
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      uiForm.setContentNode(selectedNode);
      uiWorkingArea.setRenderedChild(UIContentForm.class) ;
    }
  }

  static  public class RemoveNodeActionListener extends EventListener<UIContentNavigation> {
    public void execute(Event<UIContentNavigation> event) throws Exception {
      UIContentNavigation uiNav = event.getSource();
      ContentNode selectedNode = uiNav.getSelectedNode() ;
      if(selectedNode == null)  {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIContentNavigation.msg.EditNode", null)) ;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      ContentNode parentNode = uiNav.getParentNode() ;      
      List<ContentNode> children = null;
      if(parentNode != null) children = parentNode.getChildren();
      else children = uiNav.nav_.getNodes();   
      if(children == null) return;
      children.remove(uiNav.getSelectedNode());
      if(children.size() < 1) children = uiNav.nav_.getNodes();
      if(children.size() > 0) uiNav.setSelectedNode(children.get(0).getId());
      ContentDAO service = uiNav.getApplicationComponent(ContentDAO.class) ; 
      service.save(uiNav.getContentNavigation());
    }
  }

  static  public class GetNodeInfoActionListener extends EventListener<UIContentNavigation> {
    public void execute(Event<UIContentNavigation> event) throws Exception {
      
      UIContentPortlet uiParent = event.getSource().getParent() ;
      UIContentWorkingArea uiWorkingArea = uiParent.getChild(UIContentWorkingArea.class);
     uiWorkingArea.setRenderedChild(UIDescription.class) ;
    }
  }

}
