package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.organization.webui.component.UIPermissionSelector;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
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
    @EventConfig(listeners = UIPageBrowser.DeleteActionListener.class),
    @EventConfig(listeners = UIPageBrowser.EditInfoActionListener.class),
    @EventConfig(listeners = UIPageBrowser.PreviewActionListener.class),
    @EventConfig(listeners = UIPageBrowser.AddNewActionListener.class)   
  }
)
public class UIPageBrowser extends UISearch {

  public static String[] BEAN_FIELD = {"id", "owner", "viewPermission", "editPermission"} ;  
  public static String[] ACTIONS = {"Preview", "EditInfo", "Delete"} ; 
  
  private boolean showAddNewPage = false;
  
  private static List<SelectItemOption<String>> OPTIONS = new ArrayList<SelectItemOption<String>>(3);
  
  static{
    OPTIONS.add(new SelectItemOption<String>("Owner", "owner"));
    OPTIONS.add(new SelectItemOption<String>("View Permission", "viewPermission"));
    OPTIONS.add(new SelectItemOption<String>("Edit Permission", "editPermission"));
  }

  private Query lastQuery_ ;  

  public UIPageBrowser() throws Exception {
    super(OPTIONS);
    UIGrid uiGrid = addChild(UIGrid.class, null, null) ;
    uiGrid.configure("id", BEAN_FIELD, ACTIONS) ;
    defaultValue(null) ;
    addChild(uiGrid.getUIPageIterator());
    uiGrid.getUIPageIterator().setRendered(false);
  }
  
  public Query getLastQuery() { return lastQuery_; }
  
  public void defaultValue(Query query) throws Exception {
    lastQuery_ = query ;
    PortalRequestContext context = (PortalRequestContext) WebuiRequestContext.getCurrentInstance() ;
    PortalDAO service = getApplicationComponent(PortalDAO.class) ;

    if(lastQuery_ == null) lastQuery_ = new Query(context.getPortalOwner(), null, null, Page.class) ;

    PageList pagelist = service.findDataDescriptions(lastQuery_) ;
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
    String name = input.getValue();
    if(name == null || name.equals("")) name = Util.getUIPortal().getOwner();
    String selectBoxValue = select.getValue();
    PortalRequestContext context = (PortalRequestContext) WebuiRequestContext.getCurrentInstance() ;
    Query query = new Query(context.getPortalOwner(), null, null, Page.class) ;
    if(selectBoxValue.equals("owner")) query.setOwner(name) ;
    if(selectBoxValue.equals("viewPermission")) query.setViewPermission(name) ;
    if(selectBoxValue.equals("editPermission")) query.setEditPermission(name) ;
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
      String id = event.getRequestContext().getRequestParameter(OBJECTID) ;
      PortalDAO service = uiPageBrowser.getApplicationComponent(PortalDAO.class) ;
      Page page = service.getPage(id) ;
      
      UIPortalApplication uiPortalApp = uiPageBrowser.getAncestorOfType(UIPortalApplication.class);
      
      if(page == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.null", new String[]{})) ;;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }
      
      UserACL userACL = uiPageBrowser.getApplicationComponent(UserACL.class);
      String accessUser = Util.getPortalRequestContext().getRemoteUser();     
      if(!userACL.hasPermission(page.getOwner(), accessUser, page.getEditPermission())){
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-editPermission", new String[]{page.getName()})) ;;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
        return;
      }
      
      service.removePage(id);
      uiPageBrowser.defaultValue(null);       
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageBrowser);
    }
  }
  
  static public class EditInfoActionListener extends EventListener<UIPageBrowser> {    
    public void execute(Event<UIPageBrowser> event) throws Exception {
      UIPageBrowser uiPageBrowser = event.getSource();
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext(); 
      
      String id = pcontext.getRequestParameter(OBJECTID) ;
      PortalDAO dao = uiPageBrowser.getApplicationComponent(PortalDAO.class) ;
      Page page = dao.getPage(id) ;
      
      UIPortalApplication uiPortalApp = uiPageBrowser.getAncestorOfType(UIPortalApplication.class);      

      if(page == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.null", new String[]{})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
        return;
      }
      
      UserACL userACL = uiPageBrowser.getApplicationComponent(UserACL.class);
      String accessUser = pcontext.getRemoteUser();     
      String editPermission = page.getEditPermission();
      
      if(!userACL.hasPermission(page.getOwner(), accessUser, editPermission)) {
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
      pcontext.addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
  static public class PreviewActionListener extends EventListener<UIPageBrowser> {
    public void execute(Event<UIPageBrowser> event) throws Exception {
      UIPageBrowser uiPageBrowser = event.getSource() ;      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext(); 
      String id = pcontext.getRequestParameter(OBJECTID) ;
      PortalDAO service = uiPageBrowser.getApplicationComponent(PortalDAO.class) ;
      Page page = service.getPage(id) ;
      
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      if(page == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.null", new String[]{})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }
      
      if("Desktop".equals(page.getFactoryId())) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-Preview", new String[]{page.getName()})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
        return;
      }
      
      UserACL userACL = uiPageBrowser.getApplicationComponent(UserACL.class);
      String accessUser = pcontext.getRemoteUser();
      
      if(!userACL.hasPermission(page.getOwner(), accessUser, page.getViewPermission())){
        uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.Invalid-viewPermission", new String[]{page.getName()})) ;;
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

      uiPageForm.getUIStringInput("owner").setValue(prContext.getRemoteUser());      
      uiPageForm.getUIStringInput("owner").setEditable(false);
      UIPermissionSelector uiPermissionSelector = uiPageForm.getChild(UIPermissionSelector.class);    
      uiPermissionSelector.createPermission("ViewPermission", null);
      uiPermissionSelector.createPermission("EditPermission", null);
      uiPageForm.removeChild(UIFormInputItemSelector.class);
      
      UIPageTemplateOptions uiTemplateConfig = uiPageForm.createUIComponent(UIPageTemplateOptions.class, null, null);    
      uiTemplateConfig.setRendered(false) ;
      uiPageForm.addUIFormInput(uiTemplateConfig) ;
      
      prContext.addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
 
}
