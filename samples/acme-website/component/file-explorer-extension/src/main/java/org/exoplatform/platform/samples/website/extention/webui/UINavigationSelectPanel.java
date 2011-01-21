package org.exoplatform.platform.samples.website.extention.webui;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.ecm.webui.selector.UISelectable;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(template = "classpath:groovy/webui/component/explorer/extention/UISelectNavigationPanel.gtmpl", events = { @EventConfig(listeners = UINavigationSelectPanel.SelectActionListener.class) })
public class UINavigationSelectPanel extends UIContainer {

	private PageNavigation pageNavigation_;
	private UIPageIterator uiPageIterator_;
	private PageNode selectedPageNode_;

	public void setPageNavigation(PageNavigation node) {
		pageNavigation_ = node;
	}

	public UINavigationSelectPanel() throws Exception {
		uiPageIterator_ = addChild(UIPageIterator.class, null, "UICategoriesSelect");
		// updateGrid();

	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public void updateGrid() throws Exception {
		ObjectPageList objPageList = new ObjectPageList(getListSelectableNodes(), 10);
		uiPageIterator_.setPageList(objPageList);
	}

	public List<PageNode> getListSelectableNodes() throws Exception {
		List<PageNode> list = new ArrayList<PageNode>();
		if (pageNavigation_ == null && selectedPageNode_ == null)
			return list;
		else {
			if (selectedPageNode_ != null)
				return selectedPageNode_.getChildren();
		}
		return pageNavigation_.getNodes();

	}

	static public class SelectActionListener extends EventListener<UINavigationSelectPanel> {
		public void execute(Event<UINavigationSelectPanel> event) throws Exception {
			UINavigationSelectPanel uiDefault = event.getSource();
			UINavigationSelector uiNavigationSelector = uiDefault.getParent();
			String uri = event.getRequestContext().getRequestParameter(OBJECTID);
			((UISelectable) uiNavigationSelector.getSourceComponent()).doSelect(uiNavigationSelector.getReturnFieldName(), uri);
		}
	}

	public PageNavigation getPageNavigation() {
		return pageNavigation_;
	}

	public UIPageIterator getUIPageIterator() {
		return uiPageIterator_;
	}

	@SuppressWarnings("rawtypes")
	public List getSelectableNodes() throws Exception {
		return uiPageIterator_.getCurrentPageData();
	}

	public boolean isExceptedNodeType(PageNode node) throws RepositoryException {
		return false;
	}

	public PageNode getSelectedPageNode_() {
		return selectedPageNode_;
	}

	public void setSelectedPageNode_(PageNode selectedPageNode_) {
		this.selectedPageNode_ = selectedPageNode_;
	}
}
