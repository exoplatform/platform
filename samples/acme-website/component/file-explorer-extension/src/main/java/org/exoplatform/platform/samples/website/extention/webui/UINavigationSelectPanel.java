package org.exoplatform.platform.samples.website.extention.webui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.RepositoryException;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.ecm.webui.selector.UISelectable;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(template = "classpath:groovy/webui/component/explorer/extention/UISelectNavigationPanel.gtmpl", events = { @EventConfig(listeners = UINavigationSelectPanel.SelectActionListener.class) })
public class UINavigationSelectPanel extends UIContainer {

	private UserNavigation UserNavigation_;
	private UIPageIterator uiPageIterator_;
	private UserNode selectedUserNode_;

	public void setUserNavigation(UserNavigation node) {
		UserNavigation_ = node;
	}

	public UINavigationSelectPanel() throws Exception {
		uiPageIterator_ = addChild(UIPageIterator.class, null, "UICategoriesSelect");
	}

  private UserPortal getUserPortal() {
    UserPortal userPortal = Util.getUIPortalApplication().getUserPortalConfig().getUserPortal();
    return userPortal;
  }
  
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void updateGrid() throws Exception {
		ObjectPageList objPageList = new ObjectPageList(new ArrayList<UserNode>(getListSelectableNodes()), 10);
		uiPageIterator_.setPageList(objPageList);
	}

	public Collection<UserNode> getListSelectableNodes() throws Exception {
		List<UserNode> list = new ArrayList<UserNode>();
		if (UserNavigation_ == null && selectedUserNode_ == null)
			return list;
		else {
			if (selectedUserNode_ != null)
				return selectedUserNode_.getChildren();
		}
		return getNodes(UserNavigation_);

	}

  private Collection<UserNode> getNodes(UserNavigation navigation) {
    return getUserPortal().getNode(navigation, Scope.ALL, null, null).getChildren();
  }

	static public class SelectActionListener extends EventListener<UINavigationSelectPanel> {
		public void execute(Event<UINavigationSelectPanel> event) throws Exception {
			UINavigationSelectPanel uiDefault = event.getSource();
			UINavigationSelector uiNavigationSelector = uiDefault.getParent();
			String uri = event.getRequestContext().getRequestParameter(OBJECTID);
			((UISelectable) uiNavigationSelector.getSourceComponent()).doSelect(uiNavigationSelector.getReturnFieldName(), uri);
		}
	}

	public UserNavigation getUserNavigation() {
		return UserNavigation_;
	}

	public UIPageIterator getUIPageIterator() {
		return uiPageIterator_;
	}

	@SuppressWarnings("rawtypes")
	public List getSelectableNodes() throws Exception {
		return uiPageIterator_.getCurrentPageData();
	}

	public boolean isExceptedNodeType(UserNode node) throws RepositoryException {
		return false;
	}

	public UserNode getSelectedUserNode_() {
		return selectedUserNode_;
	}

	public void setSelectedUserNode_(UserNode selectedUserNode_) {
		this.selectedUserNode_ = selectedUserNode_;
	}
}
