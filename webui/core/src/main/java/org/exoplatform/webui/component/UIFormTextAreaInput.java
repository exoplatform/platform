package org.exoplatform.webui.component;

import java.io.Writer;

import org.exoplatform.webui.application.RequestContext;

public class UIFormTextAreaInput extends UIFormStringInput {
  
  public UIFormTextAreaInput(String name, String bindingExpression, String value) {
    super(name, bindingExpression, value);
  }
  
  public void processRender(RequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    String value = getValue() ;
    if(value == null) value = getDefaultValue();
    w.write("<textarea class='textarea' name='") ; w.write(getName()); w.write("'>");
    if(value != null) w.write(value) ;  
    w.write("</textarea>");
  }
  
}
