package org.exoplatform.platform.samples.website.extention.webui;

import javax.jcr.Node;

import org.exoplatform.ecm.webui.tree.UIBaseNodeTreeSelector;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.webui.config.annotation.ComponentConfig;

@ComponentConfig(template = "classpath:groovy/webui/component/explorer/extention/UINavigationSelector.gtmpl")
public class UINavigationSelector extends UIBaseNodeTreeSelector {

	public UINavigationSelector() throws Exception {
		addChild(UINavigationTreeBuilder.class, null, UINavigationTreeBuilder.class.getSimpleName() + hashCode());
		addChild(UINavigationSelectPanel.class, null, null);
	}

	public void init() throws Exception {

		UINavigationTreeBuilder builder = getChild(UINavigationTreeBuilder.class);
		UINavigationSelectPanel uiNavigationSelectPanel = getChild(UINavigationSelectPanel.class);
		uiNavigationSelectPanel.setUserNavigation(builder.getEdittedNavigation());
		uiNavigationSelectPanel.updateGrid();

	}

	public void onChange(final UserNode currentNode, Object context) throws Exception {

		UINavigationSelectPanel uiNavigationSelectPanel = getChild(UINavigationSelectPanel.class);
		uiNavigationSelectPanel.setSelectedUserNode_(currentNode);
		uiNavigationSelectPanel.updateGrid();
	}

	public void activate() throws Exception {
		// TODO Auto-generated method stub

	}

	public void deActivate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChange(Node currentNode, Object context) throws Exception {
		// TODO Auto-generated method stub

	}

}
