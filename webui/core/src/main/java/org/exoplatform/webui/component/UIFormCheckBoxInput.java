package org.exoplatform.webui.component;

import java.io.Writer;

import org.exoplatform.webui.application.WebuiRequestContext;

@SuppressWarnings("hiding")
public class UIFormCheckBoxInput<T> extends UIFormInputBase<T>  {
  
  private boolean checked = false;
  private String onchange_;

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
  
  protected String renderOnChangeEvent(UIForm uiForm) throws Exception {
    return uiForm.event(onchange_, null);
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
      w.append(" onchange=\"").append(renderOnChangeEvent(uiForm)).append("\"");
    }
    if(checked) w.write(" checked ") ;
    if (!enable_)  w.write(" disabled ");    
    w.write(" class='checkbox'/>") ;
  }
 
}