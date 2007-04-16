package org.exoplatform.webui.component;

import java.io.Writer;

import org.exoplatform.webui.application.WebuiRequestContext;

public class UIFormTextAreaInput extends UIFormStringInput {
  
  private int rows = -1;
  private int columns = -1;
  
  public UIFormTextAreaInput(String name, String bindingExpression, String value) {
    super(name, bindingExpression, value);
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    String value = getValue() ;
    if(value == null) value = getDefaultValue();
    w.write("<textarea class='textarea' name='") ; w.write(getName());
    if(readonly_) w.write(" readonly ");
    if(!enable_)  w.write(" disabled ");
    if(rows > -1) w.write(" rows=\""); w.write(rows); w.write("\"");
    if(columns > -1) w.write(" cols=\""); w.write(columns); w.write("\"");
    w.write("'>");
    if(value != null) w.write(value) ;  
    w.write("</textarea>");
  }

  public int getColumns() { return columns; }

  public void setColumns(int columns) { this.columns = columns; }

  
  public int getRows() { return rows; }

  public void setRows(int rows) { this.rows = rows; }
  
}
