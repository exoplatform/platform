package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInputItemSelector;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIGrid;
import org.exoplatform.webui.component.UIPageIterator;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.component.UISearch;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
  template = "app:/groovy/portal/webui/component/customization/UIPageBrowser.gtmpl" ,
  events = {
    @EventConfig(listeners = UIPageBrowser.DeleteActionListener.class, confirm = "UIPageBrowse.deletePage"),
    @EventConfig(listeners = UIPageBrowser.EditInfoActionListener.class),
    @EventConfig(listeners = UIPageBrowser.PreviewActionListener.class),
    @EventConfig(listeners = UIPageBrowser.AddNewActionListener.class)   
  }
)
public class UIPageBrowser extends UISearch {

  public static String[] BEAN_FIELD = {"pageId", "name", "accessPermission"} ;  
  public static String[] ACTIONS = {"Preview", "EditInfo", "Delete"} ; 
  
  private boolean showAddNewPage = false;
  protected String pageSelectedId_;
  
  private static List<SelectItemOption<String>> OPTIONS = new ArrayList<SelectItemOption<String>>(3);
  
  static{
    OPTIONS.add(new SelectItemOption<String>("Owner Type", "ownerType"));
    OPTIONS.add(new SelectItemOption<String>("Owner Id", "ownerId"));
    OPTIONS.add(new SelectItemOption<String>("Name", "name"));
  }

  private Query<Page> lastQuery_ ;  

  public UIPageBrowser() throws Exception {
    super(OPTIONS);
    UIGrid uiGrid = addChild(UIGrid.class, null, null) ;
    uiGrid.configure("pageId", BEAN_FIELD, ACTIONS) ;
    addChild(uiGrid.getUIPageIterator());
    uiGrid.getUIPageIterator().setRendered(false);
    
    defaultValue(null) ;
  }
  
  public Query getLastQuery() { return lastQuery_; }
  
  public void defaultValue(Query<Page> query) throws Exception {
    lastQuery_ = query ;
    DataStorage service = getApplicationComponent(DataStorage.class) ;
    if(lastQuery_ == null) lastQuery_ = new Query<Page>(null, null, null, Page.class) ;

    PageList pagelist = service.find(lastQuery_) ;
    pagelist.setPageSize(10);
    
    UIGrid uiGrid = findFirstComponentOfType(UIGrid.class) ;
    uiGrid.getUIPageIterator().setPageList(pagelist);
    addChild(uiGrid.getUIPageIterator());
    uiGrid.getUIPageIterator().setRendered(false);
    UIPageIterator pageIterator = uiGrid.getUIPageIterator();
    if(pageIterator.getAvailable() == 0 ) {
      UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
      uiApp.addMessage(new ApplicationMessage("UISearchForm.msg.empty", null)) ;
      
      Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
    }
  } 

  public void quickSearch(UIFormInputSet quickSearchInput) throws Exception {    
    UIFormStringInput input = (UIFormStringInput) quickSearchInput.getChild(0);
    UIFormSelectBox select = (UIFormSelectBox) quickSearchInput.getChild(1);
    String value = input.getValue();
    String selectBoxValue = select.getValue();
    Query<Page> query = new Query<Page>(null, null, null, Page.class) ;
    if(selectBoxValue.equals("ownerType")) query.setOwnerType(value) ;
    if(selectBoxValue.equals("ownerId")) query.setOwnerId(value) ;
    if(selectBoxValue.equals("name")) query.setName(value) ;
    defaultValue(query) ;
    if (this.<UIComponent>getParent() instanceof UIPopupWindow ) {
      UIPopupWindow popupWindow = getParent();
      popupWindow.setShow(true);
    }
  }
  
  public boolean isShowAddNewPage() { return showAddNewPage;  }
  
  public void setShowAddNewPage(boolean showAddNewPage) { this.showAddNewPage = showAddNewPage; }
  
  public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    UIForm uiForm  = getAncestorOfType(UIForm.class);
    String action =  null;
    if(uiForm != null){
      action =  uiForm.getSubmitAction();
    }else {
      action = context.getRequestParameter(UIForm.ACTION);
    }    
    if(action == null)  return;    
    Event<UIComponent> event = createEvent(action, Event.Phase.PROCESS, context) ;   
    if(event != null) event.broadcast()  ;    
  }
  
  @SuppressWarnings("unused")
  public void advancedSearch(UIFormInputSet advancedSearchInput) throws Exception {
  }  

  static  public class DeleteActionListener extends EventListener<UIPageBrowser> {
    public void execute(Event<UIPageBrowser> event) throws Exception {
      UIPageBrowser uiPageBrowser = event.getSource();
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      String id = pcontext.getRequestParameter(OBJECTID) ;
      UserPortalConfigService service = uiPageBrowser.getApplicationComponent(UserPortalConfigService.class) ;
      Page page = service.getPage(id, pcontext.getRemoteUser()) ;
      
      UIPortalApplication uiPortalApp = uiPageBrowser.getAncestorOfType(UIPortalApplication.class);
      if(page == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.null", new String[]{})) ;;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }
      
      if(!page.isModifiable()){
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-editPermission", new String[]{page.getName()})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
        return;
      }
      
      service.remove(page);
      uiPageBrowser.defaultValue(null);       
      pcontext.addUIComponentToUpdateByAjax(uiPageBrowser);
    }
  }
  
  static public class EditInfoActionListener extends EventListener<UIPageBrowser> {    
    public void execute(Event<UIPageBrowser> event) throws Exception {
      UIPageBrowser uiPageBrowser = event.getSource();
      UIPortalApplication uiPortalApp = uiPageBrowser.getAncestorOfType(UIPortalApplication.class);
      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      String id = pcontext.getRequestParameter(OBJECTID) ;
      UserPortalConfigService dao = uiPageBrowser.getApplicationComponent(UserPortalConfigService.class) ;
      Page page = dao.getPage(id, pcontext.getRemoteUser()) ;
      
      if(page == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.null", new String[]{})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
        return;
      }
    
      if(!page.isModifiable()) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-editPermission", new String[]{page.getName()})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
        return ;
      }
      
      UIPage uiPage =  uiPageBrowser.createUIComponent(pcontext, UIPage.class, null, null) ;
      PortalDataModelUtil.toUIPage(uiPage, page);
      
      if(Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
        UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
        UIPageForm uiPageForm = uiMaskWS.createUIComponent(UIPageForm.class, null, null);
        uiPageForm.setValues(uiPage);
        uiMaskWS.setUIComponent(uiPageForm);
        uiMaskWS.setShow(true);
        pcontext.addUIComponentToUpdateByAjax(uiMaskWS);
        return;
      }
      
      UIControlWorkspace uiControl =  uiPortalApp.findFirstComponentOfType(UIControlWorkspace.class) ;
      UIComponentDecorator uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID) ;
      UIPageManagement uiManagement = uiWorking.createUIComponent(UIPageManagement.class, null, null); 
      uiWorking.setUIComponent(uiManagement) ;
      
      uiManagement.setRenderedChildrenOfTypes(new Class[]{UIPageEditBar.class});
      UIPageEditBar uiEditBar = uiManagement.getChild(UIPageEditBar.class);
      uiEditBar.setUIPage(uiPage);
      uiEditBar.createEvent("EditPortlet", event.getExecutionPhase(), event.getRequestContext()).broadcast();
      
      /*UIPageBrowser uiPageBrowser = event.getSource();
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext(); 
      
      String id = pcontext.getRequestParameter(OBJECTID) ;
      UserPortalConfigService dao = uiPageBrowser.getApplicationComponent(UserPortalConfigService.class) ;
      Page page = dao.getPage(id, pcontext.getRemoteUser()) ;
      
      UIPortalApplication uiPortalApp = uiPageBrowser.getAncestorOfType(UIPortalApplication.class);      

      if(page == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.null", new String[]{})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
        return;
      }
      
      if(!page.isModifiable()) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-editPermission", new String[]{page.getName()})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
        return ;
      }
      
      UIPage uiPage =  uiPageBrowser.createUIComponent(pcontext, UIPage.class, null, null) ;
      PortalDataModelUtil.toUIPage(uiPage, page);
      
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      UIPageForm uiPageForm = uiMaskWS.createUIComponent(UIPageForm.class, null, null);
      uiPageForm.setValues(uiPage);
      uiMaskWS.setUIComponent(uiPageForm);
      uiMaskWS.setShow(true);
      pcontext.addUIComponentToUpdateByAjax(uiMaskWS);*/
    }
  }
  
  static public class PreviewActionListener extends EventListener<UIPageBrowser> {
    public void execute(Event<UIPageBrowser> event) throws Exception {
      UIPageBrowser uiPageBrowser = event.getSource() ;      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext(); 
      String id = pcontext.getRequestParameter(OBJECTID) ;
      UserPortalConfigService service = uiPageBrowser.getApplicationComponent(UserPortalConfigService.class) ;
      Page page = service.getPage(id, pcontext.getRemoteUser()) ;
      
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      if(page == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.null", new String[]{})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }
      
      if(Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-Preview", new String[]{page.getName()})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }
      
      UIPage uiPage =  uiPageBrowser.createUIComponent(event.getRequestContext(), UIPage.class,null,null) ;
      PortalDataModelUtil.toUIPage(uiPage, page);
      
      UIPagePreview uiPagePreview =  Util.showComponentOnWorking(uiPageBrowser, UIPagePreview.class);      
      uiPagePreview.setUIComponent(uiPage) ;
      uiPagePreview.setBackComponent(uiPageBrowser) ;
      
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(true);
    }
  }
  
  static public class AddNewActionListener extends EventListener<UIPageBrowser> {
    public void execute(Event<UIPageBrowser> event) throws Exception {
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UIPortalApplication uiApp = event.getSource().getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      UIPageForm uiPageForm = uiMaskWS.createUIComponent(UIPageForm.class, null, null);
      uiMaskWS.setUIComponent(uiPageForm);
      uiMaskWS.setShow(true);

      uiPageForm.getUIStringInput("ownerType").setValue(PortalConfig.USER_TYPE);
      uiPageForm.getUIStringInput("ownerId").setValue(prContext.getRemoteUser());      
      uiPageForm.removeChild(UIFormInputItemSelector.class);
      
      UIPageTemplateOptions uiTemplateConfig = uiPageForm.createUIComponent(UIPageTemplateOptions.class, null, null);    
      uiTemplateConfig.setRendered(false) ;
      uiPageForm.addUIFormInput(uiTemplateConfig) ;
      
      prContext.addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
 
}
