/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.exoplatform.portal.component.customization.UIAddApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormDateTimeInput;
import org.exoplatform.webui.component.UIFormHiddenInput;
import org.exoplatform.webui.component.UIFormMultiValueInputSet;
import org.exoplatform.webui.component.UIFormRadioBoxInput;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.UIFormUploadInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : lxchiati  
 *          lebienthuy@gmail.com
 * Nov 23, 2006  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/component/UIFormWithTitle.gtmpl",
    events = {
      @EventConfig(listeners = UITestForm.SaveActionListener.class),
      @EventConfig(listeners = UITestForm.ResetActionListener.class),
      @EventConfig(listeners = UITestForm.CancelActionListener.class),
      @EventConfig(listeners = UITestForm.OnchangeActionListener.class)
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


  public UITestForm() throws Exception {  
//    addChild(UIAddApplication.class, null, null).setRendered(true);
//    List<SelectItemOption<String>> ls = new ArrayList<SelectItemOption<String>>() ;
//    ls.add(new SelectItemOption<String>("SQL", "sql")) ;
//    ls.add(new SelectItemOption<String>("xPath", "xpath")) ;
//    
//    UIFormSelectBox uiSelectBox = new UIFormSelectBox(FIELD_SELECT_BOX, FIELD_SELECT_BOX, ls) ;
//    uiSelectBox.setOnChange("Onchange");
//    UIFormRadioBoxInput radioBoxInput = new UIFormRadioBoxInput(FIELD_RADIO_BOX, FIELD_RADIO_BOX, ls);
//    
//    addUIFormInput(uiSelectBox) ;
//    addUIFormInput(radioBoxInput);
//    UIFormMultiValueInputSet uiFormMultiValue = new UIFormMultiValueInputSet("trong", "trong") ;
//    uiFormMultiValue.setType(UIFormUploadInput.class) ;
//    addUIFormInput(uiFormMultiValue) ;
//    addUIFormInput(new UIFormTextAreaInput(FIELD_TEXT_AREA, FIELD_TEXT_AREA, null));
//    addUIFormInput(new UIFormDateTimeInput(FIELD_DATE_TIME, FIELD_DATE_TIME, null));
//    addUIFormInput(new UIFormHiddenInput(FIELD_HIDDEN_INPUT, FIELD_HIDDEN_INPUT, null));
//    addUIFormInput(new UIFormStringInput(FIELD_STRING_INPUT, FIELD_STRING_INPUT, null));
//    addUIFormInput(new UIFormUploadInput("upload", "upload")) ;
//    addUIFormInput(new UIFormUploadInput("upload2", "upload2")) ;
//    
    UIFormMultiValueInputSet test =  new UIFormMultiValueInputSet(FIELD_DATE_TIME, FIELD_DATE_TIME);
    test.setType(UIFormDateTimeInput.class);
    addUIFormInput(test);
//
//    
//    UIFormMultiValueInputSet multiValueInputSet2 = new UIFormMultiValueInputSet("StringMultiValue", "StringMultiValue");
//    multiValueInputSet2.setType(UIFormStringInput.class);
//    addUIFormInput(multiValueInputSet2);
    
    UIFormMultiValueInputSet multiValueInputSet = new UIFormMultiValueInputSet(FIELD_MULTI_VALUE, FIELD_MULTI_VALUE);
    multiValueInputSet.setType(UIFormStringInput.class);
    addUIFormInput(multiValueInputSet);
    
    setActions(new String[]{"Save", "Reset", "Cancel"}) ;
  }
  
  @SuppressWarnings("unused")
  static  public class OnchangeActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
      System.out.println("\n\n\n\n\n == > onchange action \n\n");
      UIFormSelectBox uiSelectBox = event.getSource().getChild(UIFormSelectBox.class);
      System.out.println(">>>>>>>>>>>>>>>>>> " + uiSelectBox.getValue() + "\n\n");
    }
  }

  static  public class CancelActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
      event.getRequestContext().addUIComponentToUpdateByAjax(event.getSource());
      
      WebuiRequestContext rcontext = 
        (WebuiRequestContext)event.getRequestContext().getParentAppRequestContext();
      event.getRequestContext().getJavascriptManager().addJavascript("window.location=\"http://www.vietnamnet.vn\"");
      System.out.println("\n\n\n == > "+rcontext +"\n\n");
      HttpServletResponse response = rcontext.getResponse();      
      System.out.println("\n\n\n == response ==> "+response +"\n\n");
      response.sendRedirect("window.location=\"http://www.vietnamnet.vn\"");
//      
    }
  }

  static  public class SaveActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
      UITestForm uiForm = event.getSource();
      String selectChoise = uiForm.getUIFormSelectBox(FIELD_SELECT_BOX).getValue() ;
      System.out.println("Select box: " + selectChoise);     
      String radioChoise = uiForm.getChild(UIFormRadioBoxInput.class).getValue() ;
      System.out.println("Radio Choise: " + radioChoise);     
      String textArea = uiForm.getChild(UIFormTextAreaInput.class).getValue() ;
      System.out.println("TextArea: " + textArea);     
      String dateString = uiForm.getChild(UIFormDateTimeInput.class).getValue() ;
      System.out.println("Date/Time: " + dateString);   

      String stringHidden = uiForm.getChild(UIFormHiddenInput.class).getValue();
      System.out.println("Hidden: " + stringHidden);

      String stringInput = uiForm.getUIStringInput(FIELD_STRING_INPUT).getValue();
      System.out.println("String: " + stringInput);

      UIFormMultiValueInputSet multiValueInputSet = uiForm.getChild(UIFormMultiValueInputSet.class);
      Class classType = multiValueInputSet.getUIFormInputBase();
      System.out.println("Type in MultiValue: " + classType.getName());
      List<?> listvalue = multiValueInputSet.getValue();

      for(int i = 0; i < listvalue.size(); i++) {       
        System.out.println("Value at position "+ i+ ":" + listvalue.get(i) );
      }
      
      UIFormUploadInput uiUpload = uiForm.getChild(UIFormUploadInput.class);
      byte[] data = uiUpload.getUploadData();
      if(data == null) return;
      System.out.println("===========================> upload data : "+uiUpload.getUploadResource().getFileName()+" with : "+data.length);
      System.out.println("===========================> uploaded data type : "+uiUpload.getUploadResource().getMimeType()  );
    }
  }
  
  static  public class ResetActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
      UITestForm formTest = event.getSource();
      formTest.getChild(UIFormSelectBox.class).setValue("sql");
      formTest.getChild(UIFormRadioBoxInput.class).setValue("sql");
      formTest.getChild(UIFormDateTimeInput.class).setDateValue(new Date());
      formTest.getChild(UIFormTextAreaInput.class).setValue(null);
      formTest.getUIStringInput(FIELD_STRING_INPUT).setValue("");
      UIFormMultiValueInputSet multiValueInputSet = formTest.getChild(UIFormMultiValueInputSet.class);
      multiValueInputSet.setValue(new ArrayList());
    }
  }
}
