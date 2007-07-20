/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import java.io.Writer;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormMultiValueInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormUploadInput;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.NameValidator;
/**
 * Created by The eXo Platform SARL
 * Author : lxchiati  
 *          lebienthuy@gmail.com
 * Nov 23, 2006  
 */
@ComponentConfig(
    id = "cocojambo",
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/core/UITabPane.gtmpl",
    events = {
      @EventConfig(listeners = UITestForm.SaveActionListener.class),
      @EventConfig(listeners = UITestForm.ResetActionListener.class),
      @EventConfig(listeners = UITestForm.SetVisibleActionListener.class),
      @EventConfig(listeners = UITestForm.Onchange1ActionListener.class)
    }
)
public class UITestForm extends UIForm { 

  public static final String FIELD_SELECT_BOX = "selectBox" ;
  public static final String FIELD_RADIO_BOX = "radioBox" ;
  public static final String FIELD_TEXT_AREA = "textArea" ;
  public static final String FIELD_HIDDEN_INPUT = "hiddenInput" ;
  public static final String FIELD_STRING_INPUT = "stringInput" ;
  public static final String FIELD_DATE_TIME = "dateTime" ;
  public static final String FIELD_MULTI_VALUE = "multiValue" ;
  
  public static final String INTER_NUMBER_VALUE = "IntegerNumber" ;
  public static final String  POSIT_NUMBER_VALUE = "PositiveNumber" ;


  public UITestForm() throws Exception {  
    UIFormInputSet tab1 = new UIFormInputSet("TabN1") ;
    tab1.addUIFormInput(new UIFormStringInput("name", "name", null)).
         addUIFormInput(new UIFormStringInput("age", "age", null)).
         addUIFormInput(new UIFormStringInput("gender", "gender", null));
    addChild(tab1);
    
    UIFormMultiValueInputSet test =  new UIFormMultiValueInputSet(FIELD_DATE_TIME, FIELD_DATE_TIME);
    test.setType(UIFormDateTimeInput.class);
    UIFormInputSet tab2 = new UIFormInputSet( "tabN2") ;
    tab2.addChild(test);
    tab2.setRendered(false);
    addChild(tab2);
  
//    List<SelectItemOption<String>> ls = new ArrayList<SelectItemOption<String>>() ;
//    for(int i = 0; i < 40; i++) {
//      ls.add(new SelectItemOption<String>("Select Box Item "+i+"", "select")) ;
//    }
//    UIFormSelectBox uiSelectBox = new UIFormSelectBox(FIELD_SELECT_BOX, FIELD_SELECT_BOX, ls) ;
//    
//    addUIFormInput(uiSelectBox) ;
//
//    
    setActions(new String[]{"Save", "Reset"}) ;
  }
  
  public void processRender( WebuiRequestContext context) throws Exception {
    Writer write = context.getWriter();
    write.append("<div class=\"UIForm\">");
    write.append("  <div class=\"HorizontalLayout\">");
    write.append("    <div class=\"FormContainer\">");
    super.processRender(context);
    write.append("    </div>");
    write.append("  </div>");
    write.append("</div>");
  }
   
  public String getLabel(String arg0) throws Exception {
    return arg0;
  }
  
  @SuppressWarnings("unused")
  static  public class SetVisibleActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
      System.out.println("\n\n\n\n\n == > onchange action : " + event.getSource().getId() + "\n\n");
//      UIFormSelectBox uiSelectBox = event.getSource().getChild(UIFormSelectBox.class);
//      System.out.println(">>>>>>>>>>>>>>>>>> " + uiSelectBox.getValue() + "\n\n");
    }
  }
  static  public class Onchange1ActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
      System.out.println("\n\n\n\n\n == > onchange action 1 : " + event.getSource().getId() + "\n\n");
//      UIFormSelectBox uiSelectBox = event.getSource().getChild(UIFormSelectBox.class);
//      System.out.println(">>>>>>>>>>>>>>>>>> " + uiSelectBox.getValue() + "\n\n");
    }
  }

  static  public class SaveActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
//     System.out.println("\n\n\n\nHello the world\n\n\n\n");
//      UITestForm uiForm = event.getSource();
//      String selectChoise = uiForm.getUIFormSelectBox(FIELD_SELECT_BOX).getValue() ;
//      System.out.println("Select box: " + selectChoise);     
//      String radioChoise = uiForm.getChild(UIFormRadioBoxInput.class).getValue() ;
//      System.out.println("Radio Choise: " + radioChoise);     
//      String textArea = uiForm.getChild(UIFormTextAreaInput.class).getValue() ;
//      System.out.println("TextArea: " + textArea);     
//      String dateString = uiForm.getChild(UIFormDateTimeInput.class).getValue() ;
//      System.out.println("Date/Time: " + dateString);   
//
//      String stringHidden = uiForm.getChild(UIFormHiddenInput.class).getValue();
//      System.out.println("Hidden: " + stringHidden);
//
//      String stringInput = uiForm.getUIStringInput(FIELD_STRING_INPUT).getValue();
//      System.out.println("String: " + stringInput);
//
//      UIFormMultiValueInputSet multiValueInputSet = uiForm.getChild(UIFormMultiValueInputSet.class);
//      Class classType = multiValueInputSet.getUIFormInputBase();
//      System.out.println("Type in MultiValue: " + classType.getName());
//      List<?> listvalue = multiValueInputSet.getValue();
//
//      for(int i = 0; i < listvalue.size(); i++) {       
//        System.out.println("Value at position "+ i+ ":" + listvalue.get(i) );
//      }
//      
//      UIFormUploadInput uiUpload = uiForm.getChild(UIFormUploadInput.class);
//      byte[] data = uiUpload.getUploadData();
//      if(data == null) return;
//      System.out.println("===========================> upload data : "+uiUpload.getUploadResource().getFileName()+" with : "+data.length);
//      System.out.println("===========================> uploaded data type : "+uiUpload.getUploadResource().getMimeType()  );
    }
  }
  
  static  public class ResetActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
//      UITestForm formTest = event.getSource();
    }
  }
}
