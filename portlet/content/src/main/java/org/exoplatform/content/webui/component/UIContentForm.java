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
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIDescription;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;
import org.exoplatform.webui.form.validator.Validator;

@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template =  "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
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
    addUIFormInput(new UIFormStringInput(FIELD_URL, FIELD_URL, null).
                   addValidator(URLValidator.class));
    addUIFormInput(new UIFormStringInput(FIELD_LABEL, FIELD_LABEL, null).
                   addValidator(StringLengthValidator.class, 1, 20));
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
      //TODO: Tung.Pham replaced
      //-----------------------------
      UIContentForm uiForm = event.getSource() ;
      ContentNode contentNode = uiForm.getContentNode();         
      UIRSSReaderPortlet uiPortlet = uiForm.getAncestorOfType(UIRSSReaderPortlet.class) ;
      UIContentNavigation uiNav = uiPortlet.getChild(UIContentNavigation.class);
      
      if(contentNode == null) contentNode= new ContentNode();
      uiForm.invokeSetBindingBean(contentNode);

      if(contentNode.getId() == null || contentNode.getId().length() == 0){
        contentNode.setId(contentNode.getLabel());
      }
      if(!contentNode.getType().equals("desc")){
        if(contentNode.getUrl() == null || contentNode.getUrl().trim().length() == 0) {
          UIApplication uiApp = event.getRequestContext().getUIApplication() ;
          uiApp.addMessage(new ApplicationMessage("UIContentForm.msg.UrlNull", null, ApplicationMessage.WARNING)) ;
          return ;  
        }
      }
      
      if(contentNode != uiForm.getContentNode()) {
        ContentNode existingNode = uiNav.findNode(contentNode.getId()) ;
        if(existingNode != null) {
          UIApplication uiApp = event.getRequestContext().getUIApplication() ;
          uiApp.addMessage(new ApplicationMessage("UIContentForm.msg.SameNode", null, ApplicationMessage.WARNING)) ;
          return ;
        }
        uiNav.save(contentNode);
      } else {
        uiNav.save(contentNode);
        UIDetailContent uiDetail = uiPortlet.findFirstComponentOfType(UIDetailContent.class) ;
        if(uiDetail.getListItems().size() > 0) uiDetail.refresh(true) ;
      }
      
      //-----------------------------
      
//      UIContentForm uiForm = event.getSource() ;
//      ContentNode contentNode = uiForm.getContentNode();         
//      UIContentPortlet uiPortlet = uiForm.getAncestorOfType(UIContentPortlet.class) ;
//      UIContentNavigation uiNav = uiPortlet.getChild(UIContentNavigation.class);
//      
//      if(contentNode == null) contentNode= new ContentNode();
//      uiForm.invokeSetBindingBean(contentNode);
//      
//      if(contentNode.getId() == null || contentNode.getId().length() == 0){
//        contentNode.setId(contentNode.getLabel());
//      }
//      
//      try{
//        uiNav.save(contentNode);
//        uiNav.setSelectedNode(contentNode.getId());
//      }catch (Exception ex) {
//        ApplicationMessage msg = new ApplicationMessage(ex.getMessage(), null, ApplicationMessage.ERROR);
//        uiForm.getAncestorOfType(UIApplication.class).addMessage(msg) ;
//      }
    }
  }
  
  static public class CancelActionListener extends EventListener<UIContentForm> {
    public void execute(Event<UIContentForm> event) throws Exception {
      UIContentForm uiForm = event.getSource() ;
      UIRSSReaderPortlet uiParent = uiForm.getAncestorOfType(UIRSSReaderPortlet.class) ;
      UIContentWorkingArea uiWorkingArea = uiParent.getChild(UIContentWorkingArea.class) ;
      //TODO: Tung.Pham added
      //----------------------------
      UIContentNavigation uiNavi = uiParent.getChild(UIContentNavigation.class) ;
      if(uiNavi.getSelectedNode() == null) uiWorkingArea.setRenderedChild(UIDescription.class) ;
      else uiWorkingArea.setRenderedChild(UIDetailContent.class) ; 
      //----------------------------
    }
  }
  
  static public class URLValidator implements Validator {
    @SuppressWarnings("unchecked")
    public void validate(UIFormInput uiInput) throws Exception {
      String s = (String)uiInput.getValue();
//      System.out.println(" \n\n\nTest url: " + s);
      if(s == null || s.length() == 0) { return; }
      s=s.trim();
      if (!s.startsWith("http://") && !s.startsWith("shttp://")){ 
        if(!s.startsWith("//")) s = "//" + s;
        s = "http:" + s;
      }
      String[] k = s.split(":");
      if(k.length > 3) {
        Object[] args = { uiInput.getName(), uiInput.getBindingField() };
        throw new MessageException(new ApplicationMessage("URLValidator.msg.Invalid-config", args)) ;
      }
      for(int i = 0; i < s.length(); i ++){
        char c = s.charAt(i);
        //TODO: Tung.Pham modified
        //if (Character.isLetter(c) || Character.isDigit(c) || c=='_' || c=='-' || c=='.' || c==':' || c=='/' || c== '?' || c=='%'){
        if (Character.isLetter(c) || Character.isDigit(c) || isAllowedSpecialChar(c)) {
          continue;
        }
        Object[] args = { uiInput.getName(), uiInput.getBindingField() };
        throw new MessageException(new ApplicationMessage("URLValidator.msg.Invalid-Url", args)) ;
      }
      uiInput.setValue(s);
    }
    
    //TODO: Tung.Pham added
    private boolean isAllowedSpecialChar(char chr) {
      char[] allowedCharArray = {'_', '-', '.', ':', '/', '?', '=', '&', '%'} ;
      for(char ele : allowedCharArray) {
        if(chr == ele) return true ;
      }
      return false ;
    }
    
  }
}
