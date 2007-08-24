/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInputBase;
import org.exoplatform.webui.form.UIFormInputSet;
/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minh.dang@exoplatform.com
 * Sep 20, 2006
 */

public class UIFormInputWithActions extends UIFormInputSet {

  Map<String, List<ActionData>> actionField = new HashMap<String, List<ActionData>> () ;
  public UIFormInputWithActions(String id) {
    setId(id) ;
  }
  
  public void setActionField(String fieldName, List<ActionData> actions) throws Exception {
    actionField.put(fieldName, actions) ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    if(getComponentConfig() != null) {
      super.processRender(context) ;
      return ;
    }
    UIForm uiForm = getAncestorOfType(UIForm.class);
    Writer w = context.getWriter() ;
    w.write("<div class=\"UIFormInputSet " + uiForm.getId() + "\">") ;
    w.write("<table class=\"UIFormGrid\">") ;
    ResourceBundle res = context.getApplicationResourceBundle() ;
    
    for(UIComponent inputEntry :  getChildren()) {     
      String label ;
      try {
        label = uiForm.getLabel(res, inputEntry.getId());
        if(inputEntry instanceof UIFormInputBase) ((UIFormInputBase)inputEntry).setLabel(label);
      } catch(MissingResourceException ex){
        label = inputEntry.getId() ;
        System.err.println("\n "+uiForm.getId()+".label." + inputEntry.getId()+" not found value");
      }
      w.write("<tr>") ;
      w.write("<td class=\"FieldLabel\">") ; w.write(label); w.write("</td>") ;
      w.write("<td class=\"FieldComponent\">") ; renderUIComponent(inputEntry) ; 
      List<ActionData> actions = actionField.get(inputEntry.getName()) ;
      if(actions != null) {
        for(ActionData action : actions) {
          String actionLabel ;
          try{
            actionLabel = uiForm.getLabel(res, "action." + action.getActionName())  ;
          }catch(MissingResourceException ex) {
            actionLabel = action.getActionName() ;
            System.out.println("\n Key: '"+uiForm.getId()+".label.action." + action.getActionName() + "' not found");
          }
          String actionLink = ((UIComponent)getParent()).event(action.getActionListener()) ;
          w.write("<a title=\"" + actionLabel + "\" href=\"" + actionLink +"\">") ;
          if(action.getActionType() == ActionData.TYPE_ICON) {
            w.write("<img src=\"/eXoResources/skin/DefaultSkin/background/Blank.gif\" class=\"" + action.getCssIconClass()+"\"/>") ;
            if(action.isShowLabel) w.write(actionLabel) ;
          }else if(action.getActionType() == ActionData.TYPE_LINK){
            w.write(actionLabel) ;
          }
          w.write("</a>") ; w.write("&nbsp;") ; 
        }
      }
      w.write("</td>") ;
      w.write("</tr>") ;
    }
    w.write("</table>") ;
    w.write("</div>") ;    
  }
  
  static public class ActionData {
    final public static int TYPE_ICON = 0 ;
    final public static int TYPE_LINK = 1 ;
    
    private int actionType = 0 ;
    private String actionName ;
    private String actionListener ;
    private String cssIconClass = "AddNewNodeIcon" ;
    private boolean isShowLabel = false ;
    
    public void setActionType(int actionType) { this.actionType = actionType ; }
    public int getActionType() { return actionType; }
    
    public void setActionName(String actionName) { this.actionName = actionName; }
    public String getActionName() { return actionName; }
    
    public void setActionListener(String actionListener) { this.actionListener = actionListener; }
    public String getActionListener() { return actionListener; }
    
    public void setCssIconClass(String cssIconClass) { this.cssIconClass = cssIconClass; }
    public String getCssIconClass() { return cssIconClass; }
    
    public void setShowLabel(boolean isShowLabel) { this.isShowLabel = isShowLabel ; }
    public boolean isShowLabel() { return isShowLabel ; }
  }
}
