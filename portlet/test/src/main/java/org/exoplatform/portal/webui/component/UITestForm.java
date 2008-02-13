/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.portal.account.UIAccountSetting;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIDropDownControl;
import org.exoplatform.webui.core.UITabPane;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.core.lifecycle.UIContainerLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormMultiValueInputSet;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.UIFormUploadInput;
import org.exoplatform.webui.form.UIFormWYSIWYGInput;
import org.exoplatform.webui.organization.UIAccountForm;
/**
 * Created by The eXo Platform SARL
 * Author : lxchiati  
 *          lebienthuy@gmail.com
 * Nov 23, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
      lifecycle = UIContainerLifecycle.class,
//      template =  "system:/groovy/webui/core/UITabPane.gtmpl",
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
public class UITestForm extends UIContainer { 

//  public static final String FIELD_SELECT_BOX = "selectBox" ;
//  public static final String FIELD_RADIO_BOX = "radioBox" ;
//  public static final String FIELD_TEXT_AREA = "textArea" ;
//  public static final String FIELD_HIDDEN_INPUT = "hiddenInput" ;
//  public static final String FIELD_STRING_INPUT = "stringInput" ;
//  public static final String FIELD_DATE_TIME = "dateTime" ;
//  public static final String FIELD_MULTI_VALUE = "multiValue" ;
//  
//  public static final String INTER_NUMBER_VALUE = "IntegerNumber" ;
//  public static final String  POSIT_NUMBER_VALUE = "PositiveNumber" ;
//
//  private Collection sibblingsGroup_ ;

  public UITestForm() throws Exception {
	UITabPane abc = addChild(UITabPane.class, null, null) ;
 	abc.addChild(UIAccountForm.class,null,"UIAccountForm1") ;
	abc.addChild(UIAccountForm.class,null,"UIAccountForm2") ;
	abc.setSelectedTab("UIAccountForm1") ;
//    UITree tree = addChild(UITree.class, null, "UITree");
//    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
//    sibblingsGroup_ = service.getGroupHandler().findGroups(null);
//    
//    tree.setSibbling((List)sibblingsGroup_);
//    tree.setIcon("Icon GroupAdminIcon");
//    tree.setSelectedIcon("Icon PortalIcon");
//    tree.setBeanIdField("id");
//    //tree.setBeanLabelField("groupName");
//    tree.setBeanLabelField("label");
//    setSelectedTab(tree.getId()) ;
//    UIFormUploadInput upload = new UIFormUploadInput("TestUpload", null); 
//    UIFormMultiValueInputSet test =  new UIFormMultiValueInputSet(FIELD_DATE_TIME, FIELD_DATE_TIME);
//    test.setType(UIFormDateTimeInput.class);
//    addChild(upload);
//    addUIFormInput(new UIFormWYSIWYGInput("trongtran", "trongtran", "trongtran the torng", false)) ;
//    addChild(test);
//    UIDropDownItemSelector uiDropDownItemSelector = addChild(UIDropDownItemSelector.class, null, null);
//    uiDropDownItemSelector.setTitle("SelectContainer") ;
//    uiDropDownItemSelector.setOnServer(true);
//    uiDropDownItemSelector.setOnChange("ChangeOption");
//    uiDropDownItemSelector.addItem("Tran the trong") ;
//    uiDropDownItemSelector.addItem("Tran the tro'ng1") ;
//    uiDropDownItemSelector.addItem("Tran the trong2") ;
//    uiDropDownItemSelector.addItem("Tran the tro\"ng3") ;
//    uiDropDownItemSelector.addItem("Tran the trong4") ;
//    uiDropDownItemSelector.addItem("Tran the trong5") ;
//    uiDropDownItemSelector.addItem("Tran the trong6") ;
//    uiDropDownItemSelector.addItem("Tran the trong7") ;
//    uiDropDownItemSelector.addItem("Tran the trong8") ;
//    
//    UIDropDownControl test1 = addChild(UIDropDownControl.class, null, null);
    
//    UIFormStringInput newtest = new UIFormStringInput(INTER_NUMBER_VALUE, INTER_NUMBER_VALUE, null);
//    newtest.addValidator(NumberFormatValidator.class);
//    addChild(newtest);
//
//    UIFormStringInput posittest = new UIFormStringInput(POSIT_NUMBER_VALUE, POSIT_NUMBER_VALUE, null);
//    posittest.addValidator(PositiveNumberFormatValidator.class);
//    addChild(posittest);
    
//    List<SelectItemOption<String>> ls = new ArrayList<SelectItemOption<String>>() ;
//    for(int i = 0; i < 40; i++) {
//      ls.add(new SelectItemOption<String>("Select Box Item "+i+"", "select"+i)) ;
//    }
//    ls.get(0).setSelected(true);
//    UIFormSelectBox uiSelectBox = new UIFormSelectBox(FIELD_SELECT_BOX, FIELD_SELECT_BOX, ls) ;
//    uiSelectBox.setMultiple(true);
//    uiSelectBox.setSize(3);
//    uiSelectBox.setOnChange("Onchange");
//    UIFormSelectBox uiSelectBox1 = new UIFormSelectBox(FIELD_SELECT_BOX + "1", FIELD_SELECT_BOX + "1", ls) ;
//    uiSelectBox1.setOnChange("Onchange");
//    addUIFormInput(uiSelectBox) ;
//    addUIFormInput(new UIFormSelectBox("test", "test", ls).setSize(3)) ;
//    
//    UIFormRadioBoxInput radioBoxInput = new UIFormRadioBoxInput(FIELD_RADIO_BOX, FIELD_RADIO_BOX, ls);
//    UIFormCheckBoxInput aaaa = new UIFormCheckBoxInput(FIELD_TEXT_AREA, FIELD_TEXT_AREA, ls);
////    
//    addUIFormInput(radioBoxInput);
//    addUIFormInput(aaaa);
//    UIFormMultiValueInputSet uiFormMultiValue = new UIFormMultiValueInputSet("trong", "trong") ;
//    uiFormMultiValue.setType(UIFormUploadInput.class) ;
//    addUIFormInput(uiFormMultiValue) ;
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
//    
//    setActions(new String[]{"Save", "Reset", "Cancel"}) ;
  }
  
  public String getLabel(String arg0) throws Exception {
    return arg0;
  }
  
  @SuppressWarnings("unused")
  static  public class TestLambkinActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
//      System.out.println("\n\n\n\n\n == > onchange action : " + event.getSource().getId() + "\n\n");
////      UIFormSelectBox uiSelectBox = event.getSource().getChild(UIFormSelectBox.class);
////      System.out.println(">>>>>>>>>>>>>>>>>> " + uiSelectBox.getValue() + "\n\n");
    }
  }
  static  public class Onchange1ActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
//      System.out.println("\n\n\n\n\n == > onchange action 1 : " + event.getSource().getId() + "\n\n");
////      UIFormSelectBox uiSelectBox = event.getSource().getChild(UIFormSelectBox.class);
////      System.out.println(">>>>>>>>>>>>>>>>>> " + uiSelectBox.getValue() + "\n\n");
    }
  }

  static  public class CancelActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
////      event.getRequestContext().addUIComponentToUpdateByAjax(event.getSource());
////      WebuiRequestContext rcontext = 
////        (WebuiRequestContext)event.getRequestContext().getParentAppRequestContext();
////      event.getRequestContext().getJavascriptManager().addJavascript("window.location=\"http://www.vietnamnet.vn\"");
////      System.out.println("\n\n\n == > "+rcontext +"\n\n");
////      HttpServletResponse response = rcontext.getResponse();      
////      System.out.println("\n\n\n == response ==> "+response +"\n\n");
////      response.sendRedirect("window.location=\"http://www.vietnamnet.vn\"");
    }
  }

  static  public class SaveActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
////     System.out.println("\n\n\n\nHello the world\n\n\n\n");
////      UITestForm uiForm = event.getSource();
////      System.out.println(uiForm.<UIFormInput>getUIInput("trongtran").getValue()) ;
////      String selectChoise = uiForm.getUIFormSelectBox(FIELD_SELECT_BOX).getValue() ;
////      System.out.println("Select box: " + selectChoise);     
////      String radioChoise = uiForm.getChild(UIFormRadioBoxInput.class).getValue() ;
////      System.out.println("Radio Choise: " + radioChoise);     
////      String textArea = uiForm.getChild(UIFormTextAreaInput.class).getValue() ;
////      System.out.println("TextArea: " + textArea);     
////      String dateString = uiForm.getChild(UIFormDateTimeInput.class).getValue() ;
////      System.out.println("Date/Time: " + dateString);   
////
////      String stringHidden = uiForm.getChild(UIFormHiddenInput.class).getValue();
////      System.out.println("Hidden: " + stringHidden);
////
////      String stringInput = uiForm.getUIStringInput(FIELD_STRING_INPUT).getValue();
////      System.out.println("String: " + stringInput);
////
////      UIFormMultiValueInputSet multiValueInputSet = uiForm.getChild(UIFormMultiValueInputSet.class);
////      Class classType = multiValueInputSet.getUIFormInputBase();
////      System.out.println("Type in MultiValue: " + classType.getName());
////      List<?> listvalue = multiValueInputSet.getValue();
////
////      for(int i = 0; i < listvalue.size(); i++) {       
////        System.out.println("Value at position "+ i+ ":" + listvalue.get(i) );
////      }
////      
////      UIFormUploadInput uiUpload = uiForm.getChild(UIFormUploadInput.class);
////      byte[] data = uiUpload.getUploadData();
////      if(data == null) return;
////      System.out.println("===========================> upload data : "+uiUpload.getUploadResource().getFileName()+" with : "+data.length);
////      System.out.println("===========================> uploaded data type : "+uiUpload.getUploadResource().getMimeType()  );
    }
  }
//  
  static  public class ResetActionListener extends EventListener<UITestForm> {
    public void execute(Event<UITestForm> event) throws Exception {
////      UITestForm formTest = event.getSource();
////      formTest.getChild(UIFormSelectBox.class).setValue("sql");
////      formTest.getChild(UIFormRadioBoxInput.class).setValue("sql");
////      formTest.getChild(UIFormDateTimeInput.class).setDateValue(new Date());
////      formTest.getChild(UIFormTextAreaInput.class).setValue(null);
////      formTest.getUIStringInput(FIELD_STRING_INPUT).setValue("");
////      UIFormMultiValueInputSet multiValueInputSet = formTest.getChild(UIFormMultiValueInputSet.class);
////      multiValueInputSet.setValue(new ArrayList());
    }
  }
}
