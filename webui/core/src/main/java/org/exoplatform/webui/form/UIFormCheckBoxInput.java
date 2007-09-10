package org.exoplatform.webui.form;

import java.io.Writer;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
/**
 * Represents a checkbox field.
 * @param <T> The type of value that is expected
 */
@SuppressWarnings("hiding")
public class UIFormCheckBoxInput<T> extends UIFormInputBase<T>  {
  /**
   * Whether this checkbox is checked
   */
  private boolean checked = false;
  /**
   * A javascript expression that will be fired when the value changes (JS onChange event)
   */
  private String onchange_;
  private String componentEvent_ = null;

  @SuppressWarnings("unchecked")
  public UIFormCheckBoxInput(String name, String bindingExpression, T value) {
    super(name, bindingExpression, null);
    if(value != null) typeValue_ = (Class<T>)value.getClass();
    value_ = value;
    setId(name);
  }
  
  
  @SuppressWarnings("unchecked")
  public UIFormInput setValue(T value){
    if(value == null) return super.setValue(value);
    if(value instanceof Boolean){
      checked = ((Boolean)value).booleanValue();
    } else if(boolean.class.isInstance(value)){
      checked = boolean.class.cast(value);
    }
    typeValue_ = (Class<T>)value.getClass();
    return super.setValue(value);
  }
  
  public void setOnChange(String onchange){ onchange_ = onchange; }  
 
  public void setComponentEvent(String com){ componentEvent_ = com; }
  
  public void setOnChange(String event, String com){
    this.onchange_ = event; 
    this.componentEvent_ = com;
  } 
  
  public String renderOnChangeEvent(UIForm uiForm) throws Exception {
    if(componentEvent_ == null)  return uiForm.event(onchange_, null);
    return  uiForm.event(onchange_, componentEvent_ , (String)null);
  }

  final public boolean isChecked() { return checked; }  
  final public UIFormCheckBoxInput setChecked(boolean check) { 
    checked = check;
    return this ;
  } 
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context)  throws Exception {
    if (!isEnable()) return ;    
    if (input == null) checked = false; else checked = true;
    if(typeValue_ == Boolean.class || typeValue_ == boolean.class) {
      value_ = typeValue_.cast(checked);
    }
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;    
    w.write("<input type='checkbox' name='"); w.write(name); w.write("'") ;
    w.write(" value='"); 
    if(value_ != null)  w.write(String.valueOf(value_));
    w.write("' ");
    if(onchange_ != null) {
      UIForm uiForm = getAncestorOfType(UIForm.class) ;
      //TODO TrongTT: The onchange don't affect in IE. 
//      w.append(" onchange=\"").append(renderOnChangeEvent(uiForm)).append("\"");
      w.append(" onclick=\"").append(renderOnChangeEvent(uiForm)).append("\"");
    }
    if(checked) w.write(" checked ") ;
    if (!enable_)  w.write(" disabled ");    
    w.write(" class='checkbox'/>") ;
  }
 
}