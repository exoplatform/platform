/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIDropDownControl;
import org.exoplatform.webui.core.UIDropDownItemSelector;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormMultiValueInputSet;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormUploadInput;
/**
 * Created by The eXo Platform SARL
 * Author : lxchiati  
 *          lebienthuy@gmail.com
 * Nov 23, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
      lifecycle = UIFormLifecycle.class,
      template =  "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
      events = {
        @EventConfig(listeners = UITestForm.SaveActionListener.class),
        @EventConfig(listeners = UITestForm.ResetActionListener.class),
        @EventConfig(listeners = UITestForm.CancelActionListener.class),
        @EventConfig(listeners = UITestForm.TestLambkinActionListener.class),
        @EventConfig(listeners = UITestForm.Onchange1ActionListener.class)
      }
  ),
  @ComponentConfig(
      type = UIDropDownControl.class,
      id = "lambkin",
      template = "system:/groovy/webui/core/UIDropDownControl.gtmpl",
      events = {
        @EventConfig(listeners = UITestForm.TestLambkinActionListener.class)
      }
  )
})
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
    UIFormUploadInput upload = new UIFormUploadInput("TestUpload", null); 
    UIFormMultiValueInputSet test =  new UIFormMultiValueInputSet(FIELD_DATE_TIME, FIELD_DATE_TIME);
    test.setType(UIFormDateTimeInput.class);
    addChild(upload);
    addChild(test);
    UIDropDownItemSelector uiDropDownItemSelector = addChild(UIDropDownItemSelector.class, null, null);
    uiDropDownItemSelector.setTitle("SelectContainer") ;
    uiDropDownItemSelector.setOnServer(true);
    uiDropDownItemSelector.setOnChange("ChangeOption");
    uiDropDownItemSelector.addItem("Tran the trong") ;
    uiDropDownItemSelector.addItem("Tran the tro'ng1") ;
    uiDropDownItemSelector.addItem("Tran the trong2") ;
    uiDropDownItemSelector.addItem("Tran the tro\"ng3") ;
    uiDropDownItemSelector.addItem("Tran the trong4") ;
    uiDropDownItemSelector.addItem("Tran the trong5") ;
    uiDropDownItemSelector.addItem("Tran the trong6") ;
    uiDropDownItemSelector.addItem("Tran the trong7") ;
    uiDropDownItemSelector.addItem("Tran the trong8") ;
    
    UIDropDownControl test1 = addChild(UIDropDownControl.class, null, null);
    
//    UIFormStringInput newtest = new UIFormStringInput(INTER_NUMBER_VALUE, INTER_NUMBER_VALUE, null);
//    newtest.addValidator(NumberFormatValidator.class);
//    addChild(newtest);
//
//    UIFormStringInput posittest = new UIFormStringInput(POSIT_NUMBER_VALUE, POSIT_NUMBER_VALUE, null);
//    posittest.addValidator(PositiveNumberFormatValidator.class);
//    addChild(posittest);
    
    List<SelectItemOption<String>> ls = new ArrayList<SelectItemOption<String>>() ;
    for(int i = 0; i < 40; i++) {
      ls.add(new SelectItemOption<String>("Select Box Item "+i+"", "select")) ;
    }
    ls.get(0).setSelected(true);
    UIFormSelectBox uiSelectBox = new UIFormSelectBox(FIELD_SELECT_BOX, FIELD_SELECT_BOX, ls) ;
    uiSelectBox.setMultiple(true);
    uiSelectBox.setSize(3);
//    uiSelectBox.setOnChange("Onchange");
//    UIFormSelectBox uiSelectBox1 = new UIFormSelectBox(FIELD_SELECT_BOX + "1", FIELD_SELECT_BOX + "1", ls) ;
//    uiSelectBox1.setOnChange("Onchange");
    addUIFormInput(uiSelectBox) ;
//    addUIFormInput(uiSelectBox1) ;
//    
//    UIFormRadioBoxInput radioBoxInput = new UIFormRadioBoxInput(FIELD_RADIO_BOX, FIELD_RADIO_BOX, ls);
//    UIFormCheckBoxInput aaaa = new UIFormCheckBoxInput(FIELD_TEXT_AREA, FIELD_TEXT_AREA, ls);
////    
//    addUIFormInput(radioBoxInput);
//    addUIFormInput(aaaa);
////    UIFormMultiValueInputSet uiFormMultiValue = new UIFormMultiValueInputSet("trong", "trong") ;
////    uiFormMultiValue.setType(UIFormUploadInput.class) ;
////    addUIFormInput(uiFormMultiValue) ;
////    addUIFormInput(new UIFormTextAreaInput(FIELD_TEXT_AREA, FIELD_TEXT_AREA, null));
//    addUIFormInput(new UIFormDateTimeInput(FIELD_HIDDEN_INPUT, FIELD_HIDDEN_INPUT, null).addValidator(DateTimeValidator.class));
//    addUIFormInput(new UIFormDateTimeInput(FIELD_DATE_TIME, FIELD_DATE_TIME, new Date()).addValidator(DateTimeValidator.class));
////    addUIFormInput(new UIFormHiddenInput(FIELD_HIDDEN_INPUT, FIELD_HIDDEN_INPUT, null));
//    addUIFormInput(new UIFormStringInput(FIELD_STRING_INPUT, FIELD_STRING_INPUT, null));
//    addUIFormInput(new UIFormUploadInput("upload", "upload")) ;
////    addUIFormInput(new UIFormUploadInput("upload2", "upload2")) ;
////    
   
////    addUIFormInput(test);
////
////    
////    UIFormMultiValueInputSet multiValueInputSet2 = new UIFormMultiValueInputSet("StringMultiValue", "StringMultiValue");
////    multiValueInputSet2.setType(UIFormStringInput.class);
////    addUIFormInput(multiValueInputSet2);
//    
//    UIFormMultiValueInputSet multiValueInputSet = new UIFormMultiValueInputSet(FIELD_MULTI_VALUE, FIELD_MULTI_VALUE);
//    multiValueInputSet.setType(UIFormStringInput.class);
//    addUIFormInput(multiValueInputSet);
    
    setActions(new String[]{"Save", "Reset", "Cancel"}) ;
  }
  
  public String getLabel(String arg0) throws Exception {
    return arg0;
  }
  
  @SuppressWarnings("unused")
  static  public class TestLambkinActionListener extends EventListener<UITestForm> {
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

  static  public class CancelActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
//      event.getRequestContext().addUIComponentToUpdateByAjax(event.getSource());
//      WebuiRequestContext rcontext = 
//        (WebuiRequestContext)event.getRequestContext().getParentAppRequestContext();
//      event.getRequestContext().getJavascriptManager().addJavascript("window.location=\"http://www.vietnamnet.vn\"");
//      System.out.println("\n\n\n == > "+rcontext +"\n\n");
//      HttpServletResponse response = rcontext.getResponse();      
//      System.out.println("\n\n\n == response ==> "+response +"\n\n");
//      response.sendRedirect("window.location=\"http://www.vietnamnet.vn\"");
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
//      formTest.getChild(UIFormSelectBox.class).setValue("sql");
//      formTest.getChild(UIFormRadioBoxInput.class).setValue("sql");
//      formTest.getChild(UIFormDateTimeInput.class).setDateValue(new Date());
//      formTest.getChild(UIFormTextAreaInput.class).setValue(null);
//      formTest.getUIStringInput(FIELD_STRING_INPUT).setValue("");
//      UIFormMultiValueInputSet multiValueInputSet = formTest.getChild(UIFormMultiValueInputSet.class);
//      multiValueInputSet.setValue(new ArrayList());
    }
  }
}
