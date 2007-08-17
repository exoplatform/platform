package org.exoplatform.webui.form;

import java.io.Writer;

import org.exoplatform.webui.application.WebuiRequestContext;
/**
 * Represents a info text element
 *
 */
public class UIFormInputInfo extends UIFormInputBase<String> {
  
  public UIFormInputInfo(String name, String bindingExpression, String value) {
    super(name, bindingExpression, String.class);
    this.value_ = value ;
  }
    
  @SuppressWarnings("unused")
  public void decode(Object input,  WebuiRequestContext context) throws Exception {    
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    w.append("<span id=\"").append(getId()).append("\" class=\"").append(getId()).append("\">") ;
    if (value_ != null) w.write(value_);    
    w.write("</span>");
  }
  
}