package org.exoplatform.webui.component;

import java.io.Writer;

import org.exoplatform.webui.application.RequestContext;

public class UIFormInputInfo extends UIFormInputBase<String> {
  
  public UIFormInputInfo(String name, String bindingExpression, String value) {
    super(name, bindingExpression, String.class);
    this.value_ = value ;
  }
    
  @SuppressWarnings("unused")
  public void decode(Object input,  RequestContext context) throws Exception {    
  }
  
  public void processRender(RequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    w.append("<span id=\"").append(getId()).append("\" class=\"").append(getId()).append("\">") ;
    if (value_ != null) w.write(value_);    
    w.write("</span>");
  }
  
}