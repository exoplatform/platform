/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.content.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.content.ContentDAO;
import org.exoplatform.portal.content.model.ContentNode;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv@exoplatform.com
 * Jul 25, 2006  
 */

@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template =  "system:/groovy/webui/component/UIFormWithTitle.gtmpl",
  events = {
    @EventConfig(listeners = UIContentForm.SaveActionListener.class ),
    @EventConfig(listeners = UIContentForm.CancelActionListener.class,  phase = Phase.DECODE)
  }
)
public class UIContentForm extends UIForm {  
  
  final static public String FIELD_ID = "id" ;
  final static public String FIELD_URL = "url" ;
  final static public String FIELD_LABEL = "label" ;
  final static public String FIELD_DESCRIPTION = "description" ;
  final static public String FIELD_TYPE = "type" ;
  
  private ContentNode contentNode ;
  
  private  List<SelectItemOption<String>> option_ = new ArrayList<SelectItemOption<String>>();
  
  public UIContentForm() throws Exception {
    ContentDAO service = (ContentDAO) PortalContainer.getComponent(ContentDAO.class) ;
    List<String> types = service.getTypes() ;
    for(int i = 0 ; i < types.size() ; i++) {
      option_.add(new SelectItemOption<String>(types.get(i).toUpperCase(), types.get(i).toString())) ;
    }
    addUIFormInput(new UIFormStringInput(FIELD_ID, FIELD_ID, null));
    addUIFormInput(new UIFormStringInput(FIELD_URL, FIELD_URL, null));
    addUIFormInput(new UIFormStringInput(FIELD_LABEL, FIELD_LABEL, null).
                   addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormTextAreaInput(FIELD_DESCRIPTION, FIELD_DESCRIPTION, null)).
    addUIFormInput(new UIFormSelectBox(FIELD_TYPE, FIELD_TYPE, option_).
                   addValidator(EmptyFieldValidator.class));
  }
  
  public void setContentNode(ContentNode node) throws Exception { 
    contentNode = node;
    if(node != null) {
      invokeGetBindingBean(node) ;
      getUIStringInput(FIELD_ID).setEditable(false) ;
      return ;
    }
    getUIStringInput(FIELD_ID).setEditable(true).setValue(null) ;
    getUIStringInput(FIELD_URL).setValue(null) ;
    getUIStringInput(FIELD_LABEL).setValue(null) ;
    getUIStringInput(FIELD_DESCRIPTION).setValue(null);
  }
  
  public ContentNode getContentNode() { return contentNode; }

  static public class SaveActionListener extends EventListener<UIContentForm> {
    public void execute(Event<UIContentForm> event) throws Exception {
      UIContentForm uiForm = event.getSource() ;
      ContentNode contentNode = uiForm.getContentNode();         
      UIContentPortlet uiPortlet = uiForm.getAncestorOfType(UIContentPortlet.class) ;
      UIContentNavigation uiNav = uiPortlet.getChild(UIContentNavigation.class);
      
      if(contentNode == null) contentNode= new ContentNode();
      uiForm.invokeSetBindingBean(contentNode);
      
      if(contentNode.getId() == null || contentNode.getId().length() == 0){
        contentNode.setId(contentNode.getLabel());
      }
      
      uiNav.save(contentNode);
      uiNav.setSelectedNode(contentNode.getId());      
    }
  }
  
  static public class CancelActionListener extends EventListener<UIContentForm> {
    public void execute(Event<UIContentForm> event) throws Exception {
      UIContentForm uiForm = event.getSource() ;
      UIContentPortlet uiParent = uiForm.getAncestorOfType(UIContentPortlet.class) ;
      UIContentWorkingArea uiWorkingArea = uiParent.getChild(UIContentWorkingArea.class) ;
      uiWorkingArea.setRenderedChild(UIDetailContent.class) ;
    }
  }
  
}
