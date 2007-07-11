package org.exoplatform.organization.webui.component;

import java.io.Writer;
import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
@ComponentConfig(
    events = {
      @EventConfig(listeners = UIListMembershipType.EditMembershipActionListener.class),
      @EventConfig(listeners = UIListMembershipType.DeleteMembershipActionListener.class, confirm = "UIListMembershipType.deleteMemberShip")
    }
)
public class UIListMembershipType extends UIContainer {
  
  private static String[] USER_BEAN_FIELD = {"name", "createdDate", "modifiedDate", "description"} ;
  private static String[] USER_ACTION = {"EditMembership", "DeleteMembership"} ;
  private String membershipSelected_;
  
	public UIListMembershipType() throws Exception { 
    UIGrid uiGrid = addChild(UIGrid.class, null, null);
    uiGrid.setId("UIGrid");
    uiGrid.configure("name", USER_BEAN_FIELD, USER_ACTION);
    //TODO: Tung.Pham added
    //--------------------------------------------------------
    uiGrid.getUIPageIterator().setId("UIListMembershipTypeIterator") ;
    //--------------------------------------------------------
    update(uiGrid);
	}
	
  public String getMembershipSelected(){ return membershipSelected_; }
  public void setMembershipSelected(String select) { membershipSelected_ = select; }
  
	public UIComponent getViewModeUIComponent() {	return null; }
  
  public String getName() { return "UIMembershipList" ; }
	
  @SuppressWarnings("unchecked")
	public void update(UIGrid uiGrid) throws Exception {
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    List memberships = (List)service.getMembershipTypeHandler().findMembershipTypes();
    PageList pagelist = new ObjectPageList(memberships,10);
    uiGrid.getUIPageIterator().setPageList(pagelist);
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    w.write("<div class=\"UIListMembershipType\">");
    renderChildren();
    w.write("</div>");
  }
	
	static  public class EditMembershipActionListener extends EventListener<UIListMembershipType> {
    public void execute(Event<UIListMembershipType> event) throws Exception {
    	UIListMembershipType uiMembership = event.getSource();
    	String name = event.getRequestContext().getRequestParameter(OBJECTID) ;
      
      OrganizationService service = uiMembership.getApplicationComponent(OrganizationService.class);
      MembershipType mt = service.getMembershipTypeHandler().findMembershipType(name);
      UIMembershipManagement uiMembershipManager = uiMembership.getParent();
      UIMembershipTypeForm uiForm = uiMembershipManager.getChild(UIMembershipTypeForm.class);
      uiForm.setMembershipType(mt);     
    }
	}
  
  static  public class DeleteMembershipActionListener extends EventListener<UIListMembershipType> {
    public void execute(Event<UIListMembershipType> event) throws Exception {
      UIListMembershipType uiMembership = event.getSource();
      String name = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIMembershipManagement membership = uiMembership.getParent() ;
      //TODO: Tung.Pham added
      //--------------------------------------
      UIMembershipTypeForm uiForm = membership.findFirstComponentOfType(UIMembershipTypeForm.class) ;
      MembershipType existMembershipType = uiForm.getMembershipType();
      if(existMembershipType != null && existMembershipType.getName().equals(name)) {
        UIApplication uiApp = event.getRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIMembershipList.msg.InUse", null)) ;
        return ;
      }
      //--------------------------------------
      OrganizationService service = uiMembership.getApplicationComponent(OrganizationService.class);
      MembershipType membershipType = service.getMembershipTypeHandler().findMembershipType(name) ;
      service.getMembershipTypeHandler().removeMembershipType(name,true);
      membership.deleteOptions(membershipType) ;
      UIGrid uiGrid = uiMembership.findComponentById("UIGrid");
      uiMembership.update(uiGrid);
      
    }
  }
  
}
