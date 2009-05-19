package org.exoplatform.webui.organization.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIPopupComponent;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Tuan
 *          phamtuanchip@gmail.com
 * Dec 11, 2007  
 * Modified: dang.tung
 *           tungcnw@gmail.com 
 * Nov 22, 2008           
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/organization/account/UIUserSelector.gtmpl",
    events = {
      @EventConfig(listeners = UIUserSelector.AddActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIUserSelector.AddUserActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIUserSelector.SearchActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIUserSelector.SearchGroupActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIUserSelector.SelectGroupActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIUserSelector.FindGroupActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIUserSelector.ShowPageActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIUserSelector.CloseActionListener.class, phase = Phase.DECODE)
    }
)

public class UIUserSelector extends UIForm implements UIPopupComponent { 
  final public static String FIELD_KEYWORD = "Quick Search".intern() ;
  final public static String FIELD_FILTER = "filter".intern() ;
  final public static String FIELD_GROUP = "group".intern() ;
  public static String USER_NAME = "userName";
  public static String LAST_NAME = "lastName";
  public static String FIRST_NAME = "firstName";
  public static String EMAIL = "email";

  protected Map<String, User> userData_ = new HashMap<String, User>() ;
  private boolean isShowSearch_ = false ;
  private boolean isShowSearchGroup = true;
  private boolean isShowSearchUser = true;
  protected String groupId_ = null ;
  protected Collection<String> pars_ ;
  public UIPageIterator uiIterator_ ;

  private String selectedUsers;
  private boolean multi = true;
  
  public UIUserSelector() throws Exception {  
    addUIFormInput(new UIFormStringInput(FIELD_KEYWORD, FIELD_KEYWORD, null)) ;
    addUIFormInput(new UIFormSelectBox(FIELD_FILTER, FIELD_FILTER, getFilters())) ;
    addUIFormInput(new UIFormStringInput(FIELD_GROUP, FIELD_GROUP, null)) ;
    isShowSearch_ = true ;
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    ObjectPageList objPageList = new ObjectPageList(service.getUserHandler().findUsers(new Query()).getAll(), 10) ;
    uiIterator_ = new UIPageIterator() ;
    uiIterator_.setPageList(objPageList) ;
    uiIterator_.setId("UISelectUserPage") ;
    
    // create group selector
    UIPopupWindow uiPopup = addChild(UIPopupWindow.class, null, "UIPopupGroupSelector");
    uiPopup.setWindowSize(540, 0);
    UIGroupSelector uiGroup = createUIComponent(UIGroupSelector.class, null, null);
    uiPopup.setUIComponent(uiGroup);
    uiGroup.setId("GroupSelector");
    uiGroup.getChild(UITree.class).setId("TreeGroupSelector");
    uiGroup.getChild(UIBreadcumbs.class).setId("BreadcumbsGroupSelector");
  }
  
  @SuppressWarnings("unchecked")
  public List<User> getData() throws Exception {
    if(getMulti()) {
      for(Object obj : uiIterator_.getCurrentPageData()){
        User user = (User)obj ;
        if(getUIFormCheckBoxInput(user.getUserName()) == null)
          addUIFormInput(new UIFormCheckBoxInput<Boolean>(user.getUserName(),user.getUserName(), false)) ;
      }
    }
    return new ArrayList<User>(uiIterator_.getCurrentPageData());
  }
  
  public String getSelectedUsers() { return selectedUsers ; }
  
  public void setSelectedUsers(String selectedUsers) { this.selectedUsers = selectedUsers ;} 
  
  public void setMulti(boolean multi) { this.multi = multi; }
  
  public boolean getMulti() { return multi; }
  
  public UIPageIterator  getUIPageIterator() {  return uiIterator_ ; }

  public long getAvailablePage(){ return uiIterator_.getAvailablePage() ;}
  
  public long getCurrentPage() { return uiIterator_.getCurrentPage();}
  
  // update data, review later
//  public void init(Collection<String> pars) throws Exception{
//    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
//    ObjectPageList objPageList = new ObjectPageList(service.getUserHandler().getUserPageList(0).getAll(), 10) ;
//    uiIterator_.setPageList(objPageList) ;
//    pars_ = pars ;
//  }
  
  private List<SelectItemOption<String>> getFilters() throws Exception {
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    options.add(new SelectItemOption<String>(USER_NAME, USER_NAME)) ;
    options.add(new SelectItemOption<String>(LAST_NAME, LAST_NAME)) ;
    options.add(new SelectItemOption<String>(FIRST_NAME, FIRST_NAME)) ;
    options.add(new SelectItemOption<String>(EMAIL, EMAIL)) ;
    return options;
  }

  public String[] getActions() { return new String[]{"Add", "Close"}; }
  
  public void activate() throws Exception {}
  public void deActivate() throws Exception {} 
  
  public String getLabel(String id) {
    try {
      return super.getLabel(id) ;
    } catch (Exception e) {
      return id ;
    }
  }
  public void setShowSearch(boolean isShowSearch) {
    this.isShowSearch_ = isShowSearch;
  }
  public boolean isShowSearch() {
    return isShowSearch_;
  }
  
  public void setShowSearchGroup(boolean isShowSearchGroup) {
    this.isShowSearchGroup = isShowSearchGroup;
  }
  public boolean isShowSearchGroup() {
    return isShowSearchGroup;
  }
  
  public void setShowSearchUser(boolean isShowSearchUser) {
    this.isShowSearchUser = isShowSearchUser;
  }
  
  public void search (String keyword, String filter, String groupId) throws Exception{
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    Query q = new Query() ;
    if(keyword != null && keyword.trim().length() != 0) {
      if(keyword.indexOf("*")<0){
        if(keyword.charAt(0)!='*') keyword = "*"+keyword ;
        if(keyword.charAt(keyword.length()-1)!='*') keyword += "*" ;
      }
      keyword = keyword.replace('?', '_') ;
      if(USER_NAME.equals(filter)) {
        q.setUserName(keyword) ;
      } 
      if(LAST_NAME.equals(filter)) {
        q.setLastName(keyword) ;
      }
      if(FIRST_NAME.equals(filter)) {
        q.setFirstName(keyword) ;
      }
      if(EMAIL.equals(filter)) {
        q.setEmail(keyword) ;
      }
    }
    List results = new CopyOnWriteArrayList() ;
    results.addAll(service.getUserHandler().findUsers(q).getAll()) ;
    // remove if user doesn't exist in selected group
    MembershipHandler memberShipHandler = service.getMembershipHandler();
    
    if(groupId != null && groupId.trim().length() != 0) {
      for(Object user : results) {
        if(memberShipHandler.findMembershipsByUserAndGroup(((User)user).getUserName(), groupId).size() == 0) {
          results.remove(user);
        }
      }
    }
    ObjectPageList objPageList = new ObjectPageList(results, 10) ;
    uiIterator_.setPageList(objPageList);
  }

  public boolean isShowSearchUser() {
    return isShowSearchUser;
  }
  
  public String getSelectedGroup() {
    return getUIStringInput(FIELD_GROUP).getValue();
  }
  public void setSelectedGroup(String selectedGroup) {
    getUIStringInput(FIELD_GROUP).setValue(selectedGroup);
  }
  static  public class AddActionListener extends EventListener<UIUserSelector> {
    @SuppressWarnings("unchecked")
    public void execute(Event<UIUserSelector> event) throws Exception {
      UIUserSelector uiForm = event.getSource();
      StringBuilder sb = new StringBuilder() ;
      int count = 0;
      for(Object o : uiForm.uiIterator_.getCurrentPageData()) {
        User u = (User)o ;
        UIFormCheckBoxInput input = uiForm.getUIFormCheckBoxInput(u.getUserName()) ;
        if(input != null && input.isChecked()) {
          count++ ;
          if(sb.toString() != null && sb.toString().trim().length() != 0) sb.append(",") ;
          sb.append(u.getUserName()) ;
        }
      }
      if(count == 0) {
        UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class) ;
        uiApp.addMessage(new ApplicationMessage("UIUserSelector.msg.user-required",null)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return ;
      }
      uiForm.setSelectedUsers(sb.toString());
      uiForm.<UIComponent>getParent().broadcast(event, event.getExecutionPhase());
    }  
  }
  
  //TODO maybe check duplicate user in method:
  //OrganizationService.getUserHandler().findUsersByGroup(groupId)
  public PageList removeDuplicate(PageList users) throws Exception {
    List after = new ArrayList();
    for(Object u : users.getAll()) {
      if(after.contains(u)) continue;
      after.add(u);
    }
    return new ObjectPageList(after, 10);
  }
  
  static  public class AddUserActionListener extends EventListener<UIUserSelector> {
    public void execute(Event<UIUserSelector> event) throws Exception {
      UIUserSelector uiForm = event.getSource();
      String userName = event.getRequestContext().getRequestParameter(OBJECTID);
      uiForm.setSelectedUsers(userName);
      uiForm.<UIComponent>getParent().broadcast(event, event.getExecutionPhase()) ;
    }  
  }

  protected void updateCurrentPage(int page) throws Exception{
    uiIterator_.setCurrentPage(page) ;
  }
  public void setKeyword(String value) {
    getUIStringInput(FIELD_KEYWORD).setValue(value) ;
  }
  
  static  public class SelectGroupActionListener extends EventListener<UIGroupSelector> {   
    public void execute(Event<UIGroupSelector> event) throws Exception {
      UIGroupSelector uiSelectGroupForm = event.getSource();
      UIUserSelector uiSelectUserForm = uiSelectGroupForm.<UIComponent>getParent().getParent(); 
      String groupId = event.getRequestContext().getRequestParameter(OBJECTID);
      uiSelectUserForm.setSelectedGroup(groupId);
      OrganizationService service = uiSelectGroupForm.getApplicationComponent(OrganizationService.class);
      PageList users = uiSelectUserForm.removeDuplicate(service.getUserHandler().findUsersByGroup(groupId));
      uiSelectUserForm.uiIterator_.setPageList(users);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSelectUserForm) ;
    }
  }
  
  static  public class FindGroupActionListener extends EventListener<UIUserSelector> {   
    public void execute(Event<UIUserSelector> event) throws Exception {
      UIUserSelector uiSelectUserForm = event.getSource();
      String groupId = uiSelectUserForm.getSelectedGroup();
      uiSelectUserForm.setSelectedGroup(groupId);
      OrganizationService service = uiSelectUserForm.getApplicationComponent(OrganizationService.class);
      if(groupId != null && groupId.trim().length() != 0){
        PageList users = uiSelectUserForm.removeDuplicate(service.getUserHandler().findUsersByGroup(groupId));
        uiSelectUserForm.uiIterator_.setPageList(users);
      }
      else {
        uiSelectUserForm.uiIterator_.setPageList(service.getUserHandler().findUsers(new Query()));
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSelectUserForm) ;
    }
  }
  
  @SuppressWarnings("unchecked")
  static  public class SearchActionListener extends EventListener<UIUserSelector> {
    public void execute(Event<UIUserSelector> event) throws Exception {
      UIUserSelector uiForm = event.getSource() ;
      
      String keyword = uiForm.getUIStringInput(FIELD_KEYWORD).getValue();
      String filter = uiForm.getUIFormSelectBox(FIELD_FILTER).getValue();
      String groupId = uiForm.getSelectedGroup();
      uiForm.search(keyword, filter, groupId);
      if(filter == null || filter.trim().length() == 0) return;
      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ;
    }
  }

  
 
  
  static  public class CloseActionListener extends EventListener<UIUserSelector> {
    public void execute(Event<UIUserSelector> event) throws Exception {
      UIUserSelector uiForm = event.getSource();
      uiForm.<UIComponent>getParent().broadcast(event, event.getExecutionPhase()) ;
    }
  }
  
  static  public class SearchGroupActionListener extends EventListener<UIUserSelector> {
    public void execute(Event<UIUserSelector> event) throws Exception {
      UIUserSelector uiForm = event.getSource();
      uiForm.getChild(UIPopupWindow.class).setShow(true);
    }
  }
  static  public class ShowPageActionListener extends EventListener<UIUserSelector> {
    public void execute(Event<UIUserSelector> event) throws Exception {
      UIUserSelector uiSelectUserForm = event.getSource() ;
      int page = Integer.parseInt(event.getRequestContext().getRequestParameter(OBJECTID)) ;
      uiSelectUserForm.updateCurrentPage(page) ; 
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSelectUserForm);           
    }
  }
}
