/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal;

import java.io.Writer;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.webui.component.UITestForm;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIDropDownControl;
import org.exoplatform.webui.form.UIFormInputBase;

/**
 * Created by The eXo Platform SARL
 * Author : Hung Nguyen
 *          nguyenkequanghung@yahoo.com
 * Jun 6, 2006
 */

@ComponentConfig(
    template = "app:/groovy/UIFormWYSIWYGInput.gtmpl",
    events = {
      @EventConfig(listeners = UITestForm.TestLambkinActionListener.class)
    }
)
public class UIFormWYSIWYGInput extends UIFormInputBase<String> {
  
  final static String BASIC = "basic";
  final static String INPUT_ONLY = "input-only";
  
//@TODO htmlType not unsed
  
  private String htmlType ;
  public UIFormWYSIWYGInput() {
    this("test", null, "value Test") ;
  }
    
  public UIFormWYSIWYGInput(String name, String bindingExpression, String value) {
    super(name, bindingExpression, String.class);
    this.value_ = value ;
  }
  
  public UIFormWYSIWYGInput(String name, String bindingExpression, String value, String option) {
    super(name, bindingExpression, String.class);
    this.value_ = value ;
    htmlType = option ;
  }
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    value_ = (String) input;
    if(value_ != null && value_.length() == 0) value_ = null ;
  }
  
// @TODO portalName not used 
  public void processRenderssssssss(WebuiRequestContext context) throws Exception {
    String portalName = PortalContainer.getInstance().getPortalContainerInfo().getContainerName();
    Writer w =  context.getWriter() ;
    
    if (value_ == null) value_ = "" ;
    value_ = value_.replaceAll("'", "\\\\'");
    value_ = value_.replaceAll("[\r\n]", "");
    w.write("<textarea id=\"" + name + "\" name=\"" + name + "\">" + value_ + "</textarea>") ;
//    w.write("<script>");
    context.getJavascriptManager().addJavascript("eXo.ecm.ExoEditor.init('" + name + "',{});") ;
//    context.addJavascript(" var fckeditor" + name + " = new FCKeditor( '" + name + "' ); \n") ;
//    String basePath = "/" + portalName + "/FCKeditor/";
//    context.addJavascript(" fckeditor" + name + ".BasePath  = '" + basePath + "' ; \n");
//    if(htmlType != null && htmlType.equalsIgnoreCase(BASIC)){
//      context.addJavascript(" fckeditor"+ name +".ToolbarSet  = 'Basic' ; \n");
//    }else if(htmlType != null && htmlType.equalsIgnoreCase(INPUT_ONLY)){
//        context.addJavascript(" fckeditor" + name + ".ToolbarSet  = 'InputOnly' ; \n");
//        context.addJavascript(" fckeditor" + name + ".Height  = '50px' ; \n");
//    }else{
//      context.addJavascript(" fckeditor"+ name +".ToolbarSet  = 'Default' ; \n");
//      context.addJavascript(" fckeditor" + name + ".Height  = '400px' ; \n");
//    }
//    context.addJavascript(" fckeditor" + name + ".ReplaceTextarea() ;");    
//    w.write("</script>") ;
  }
}