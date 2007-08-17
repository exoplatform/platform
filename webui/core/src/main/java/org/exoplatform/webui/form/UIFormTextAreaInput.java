package org.exoplatform.webui.form;

import java.io.Writer;

import org.exoplatform.webui.application.WebuiRequestContext;
/**
 * Represents a textarea element
 * The value is stored in UIFormInputBase
 */
public class UIFormTextAreaInput extends UIFormStringInput {
  /**
   * number of rows
   */
  private int rows = -1;
  /**
   * number of columns
   */
  private int columns = -1;
  
  public UIFormTextAreaInput(String name, String bindingExpression, String value) {
    super(name, bindingExpression, value);
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    String value = getValue() ;
    if(value == null) value = getDefaultValue();
    w.append("<textarea class='textarea' name='").append(getName()).append("'");
    if(readonly_) w.write(" readonly ");
    if(!enable_)  w.write(" disabled ");
    if(rows > -1) w.append(" rows=\"").append(String.valueOf(rows)).append("\"");
    if(columns > -1) w.append(" cols=\"").append(String.valueOf(columns)).append("\"");
    w.write(">");
    if(value != null) w.write(value) ;  
    w.write("</textarea>");
  }

  public int getColumns() { return columns; }

  public void setColumns(int columns) { this.columns = columns; }
  
  public int getRows() { return rows; }

  public void setRows(int rows) { this.rows = rows; }
  
}
