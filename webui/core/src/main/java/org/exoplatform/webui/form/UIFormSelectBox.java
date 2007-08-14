package org.exoplatform.webui.form;

import java.io.Writer;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.model.SelectItemOption;

public class UIFormSelectBox extends UIFormStringInput {
  
  private int size_ = 1 ;
  
  private List<SelectItemOption<String>> options_ ;
  private String onchange_;
  private boolean multiple_ = false;
  
	public UIFormSelectBox(String name, String bindingExpression, List<SelectItemOption<String>> options) {
    super(name, bindingExpression, null);
    setOptions(options);
	}
  
  final public UIFormSelectBox setSize(int i) { 
    size_ = i ; 
    return this ;
  }
  
  public boolean isMultiple() { return multiple_; }

  public void setMultiple(boolean value) { this.multiple_ = value; }  
  
  final public List<SelectItemOption<String>> getOptions() { return options_ ; }
  
  final public UIFormSelectBox setOptions(List<SelectItemOption<String>> options) { 
    options_ = options ; 
    if(options_ == null || options_.size() < 1) return this;
    value_ = options_.get(0).getValue();
    return this ;
  } 
  
  public void setOnChange(String onchange){ onchange_ = onchange; }    
  
  @SuppressWarnings("deprecation")
  public UIFormSelectBox setDisabled(boolean disabled) {
    setEnable(!disabled);
    return this;
  }
  
//  protected String renderOnChangeAction(UIForm uiform) throws Exception {
//    StringBuilder builder = new StringBuilder();
//    builder.append(" onchange=\"javascript:eXo.webui.UIForm.submitForm('").
//            append("").append("','").append(onchange_).append("');\" ");
//    return builder.toString();
//  }
  
  protected String renderOnChangeEvent(UIForm uiForm) throws Exception {
    return uiForm.event(onchange_, (String)null);
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    ResourceBundle res = context.getApplicationResourceBundle() ;
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    String formId =  null ;
    if(uiForm.getId().equals("UISearchForm")) formId = uiForm.<UIComponent>getParent().getId() ;
    else formId = uiForm.getId() ;
   
    Writer w =  context.getWriter() ;
    w.write("<select class=\"selectbox\" name=\""); w.write(name); w.write("\"") ;
    if(onchange_ != null) {
      w.append(" onchange=\"").append(renderOnChangeEvent(uiForm)).append("\"");
    }
    
    if(multiple_)  w.write(" multiple ");
    
//    if(size_ > 1)  w.write(" multiple=\"true\" size=\"" + size_ + "\"");    if need control multiple values then can add variable "multiple" to implement 
    if(size_ > 1)  w.write(" size=\"" + size_ + "\"");
    
    if (!enable_)  w.write(" disabled ");
    
    w.write(">\n") ;
    
    for(int i=0; i < options_.size(); i++) {
      String label = options_.get(i).getLabel() ;
      try {
        label = res.getString(formId + ".label.option." + options_.get(i)) ;
      } catch(MissingResourceException ex) { }
      
      if (getValue() != null && options_.get(i).getValue().equals(getValue())) {
        w.write("<option selected=\"selected\" value=\""); w.write(options_.get(i).getValue()); w.write("\">"); 
      }  else {
        w.write("<option value=\""); w.write(options_.get(i).getValue()); w.write("\">"); 
      }
      w.write(label); w.write("</option>\n");
    }
    
    w.write("</select>\n") ;
  }

}