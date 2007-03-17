package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.organization.webui.component.UIPermissionSelector;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIGrid;
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
    @EventConfig(listeners = UIPageBrowser.SelectPageActionListener.class),
    @EventConfig(listeners = UIPageBrowser.PreviewActionListener.class),
    @EventConfig(listeners = UIPageBrowser.AddNewActionListener.class)   
  }
)
public class UIPageBrowser extends UISearch {

  public static String[] BEAN_FIELD = {"id", "owner", "viewPermission", "editPermission"} ;  
  public static String[] EDIT_ACTIONS = {"Preview", "EditInfo", "Delete"} ; //, "Preview"
  public static String[] SELECT_ACTIONS = {"SelectPage"} ;
  
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
    
    UIPageForm uiPageForm = addChild(UIPageForm.class, null, null) ;
    uiPageForm.setRendered(false) ;
    
    UIGrid uiGrid = addChild(UIGrid.class, null, null) ;
    uiGrid.configure("id", BEAN_FIELD, EDIT_ACTIONS) ;
    defaultValue(null) ;
  }

  public void defaultValue(Query query) throws Exception {
    lastQuery_ = query ;
    PortalRequestContext context = (PortalRequestContext) RequestContext.getCurrentInstance() ;
    PortalDAO service = getApplicationComponent(PortalDAO.class) ;

    if(lastQuery_ == null){
      lastQuery_ = new Query(context.getPortalOwner(), null, null, Page.class) ;
    }

    PageList pagelist = service.findDataDescriptions(lastQuery_) ;
    UIGrid uiGrid = findFirstComponentOfType(UIGrid.class) ;
    uiGrid.getUIPageIterator().setPageList(pagelist);
  } 

  public void quickSearch(UIFormInputSet quickSearchInput) throws Exception {    
    UIFormStringInput input = (UIFormStringInput) quickSearchInput.getChild(0);
    UIFormSelectBox select = (UIFormSelectBox) quickSearchInput.getChild(1);
    String name = input.getValue();
    if(name == null || name.equals("")) name = Util.getUIPortal().getOwner();
    String selectBoxValue = select.getValue();
    PortalRequestContext context = (PortalRequestContext) RequestContext.getCurrentInstance() ;
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
  
  public void processDecode(RequestContext context) throws Exception {   
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
      
      UserACL userACL = uiPageBrowser.getApplicationComponent(UserACL.class);
      String accessUser = Util.getPortalRequestContext().getRemoteUser();     
      if(page == null || !userACL.hasPermission(page.getOwner(), accessUser, page.getEditPermission())){
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
      
      String id = event.getRequestContext().getRequestParameter(OBJECTID) ;
      PortalDAO service = uiPageBrowser.getApplicationComponent(PortalDAO.class) ;
      Page page = service.getPage(id) ;
      
      UserACL userACL = uiPageBrowser.getApplicationComponent(UserACL.class);
      String accessUser = Util.getPortalRequestContext().getRemoteUser();     
      if(page == null || !userACL.hasPermission(page.getOwner(), accessUser, page.getEditPermission())){
        return;
      }
      
      UIPage uiPage =  uiPageBrowser.createUIComponent(event.getRequestContext(),UIPage.class,null,null) ;
      PortalDataModelUtil.toUIPage(uiPage, page, true);
      UIPageForm uiPageForm =  Util.showComponentOnWorking(uiPageBrowser, UIPageForm.class);
      uiPageForm.setValues(uiPage) ;
      uiPageForm.removeChild(UIPageTemplateOptions.class);
      uiPageForm.setBackUIComponent(uiPageBrowser) ;
      
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;
    }
  }
  
  static public class SelectPageActionListener extends EventListener<UIPageBrowser> {
    public void execute(Event<UIPageBrowser> event) throws Exception {
      UIPageBrowser uiPageBrowser = event.getSource() ;
      String id = event.getRequestContext().getRequestParameter(OBJECTID) ;
      PortalDAO service = uiPageBrowser.getApplicationComponent(PortalDAO.class) ;
      Page page = service.getPage(id) ;
      UIPageNodeForm uiParent = uiPageBrowser.getAncestorOfType(UIPageNodeForm.class) ;
      UIPageSelector uiPageSelector = uiParent.getChild(UIPageSelector.class) ;
      uiPageSelector.setPage(page) ;
      uiParent.setRenderedChild(UIPageSelector.class) ;
      
      Util.updateUIApplication(event);
    }
  }
  
  static public class PreviewActionListener extends EventListener<UIPageBrowser> {
    public void execute(Event<UIPageBrowser> event) throws Exception {
      UIPageBrowser uiPageBrowser = event.getSource() ;      
      String id = event.getRequestContext().getRequestParameter(OBJECTID) ;
      PortalDAO service = uiPageBrowser.getApplicationComponent(PortalDAO.class) ;
      Page page = service.getPage(id) ;
      
      UserACL userACL = uiPageBrowser.getApplicationComponent(UserACL.class);
      String accessUser = Util.getPortalRequestContext().getRemoteUser();     
      if(page == null || !userACL.hasPermission(page.getOwner(), accessUser, page.getViewPermission())){
        return;
      }
      
      UIPage uiPage =  uiPageBrowser.createUIComponent(event.getRequestContext(), UIPage.class,null,null) ;
      PortalDataModelUtil.toUIPage(uiPage, page, true);
      
      UIPagePreview uiPagePreview =  Util.showComponentOnWorking(uiPageBrowser, UIPagePreview.class);      
      uiPagePreview.setUIComponent(uiPage) ;
      uiPagePreview.setBackComponent(uiPageBrowser) ;
      
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext(); 
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setForceFullUpdate(true);
    }
  }
  
  static public class AddNewActionListener extends EventListener<UIPageBrowser> {
    public void execute(Event<UIPageBrowser> event) throws Exception {
      UIPageBrowser uiPageBrowser = event.getSource();
      UIPageForm uiPageForm =  Util.showComponentOnWorking(uiPageBrowser, UIPageForm.class);
      uiPageForm.setBackUIComponent(uiPageBrowser);
      UIPermissionSelector uiPermissionSelector = uiPageForm.getChild(UIPermissionSelector.class);    
      //TODO:  Look  like  this code  keep adding new  permission for ever
      uiPermissionSelector.createPermission("ViewPermission", null);
      uiPermissionSelector.createPermission("EditPermission", null);
      
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;
    }
  }
 
}
