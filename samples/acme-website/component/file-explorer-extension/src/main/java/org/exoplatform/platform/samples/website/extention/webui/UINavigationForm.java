package org.exoplatform.platform.samples.website.extention.webui;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.exoplatform.ecm.webui.component.explorer.UIJCRExplorer;
import org.exoplatform.ecm.webui.selector.UISelectable;
import org.exoplatform.wcm.webui.core.UIPopupWindow;
import org.exoplatform.wcm.webui.selector.page.UIPageSelector;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIPopupComponent;
import org.exoplatform.webui.core.UIPopupContainer;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormRadioBoxInput;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.ext.UIFormInputSetWithAction;
import org.exoplatform.webui.form.validator.NumberFormatValidator;

@ComponentConfig(lifecycle = UIFormLifecycle.class, template = "classpath:groovy/webui/component/explorer/extention/UINavigationForm.gtmpl", events = {
		@EventConfig(listeners = UINavigationForm.SaveActionListener.class),
		@EventConfig(listeners = UINavigationForm.CancelActionListener.class, phase = Phase.DECODE),
		@EventConfig(listeners = UINavigationForm.SelectListTargetPageActionListener.class, phase = Phase.DECODE),
		@EventConfig(listeners = UINavigationForm.SelectDetailTargetPageActionListener.class, phase = Phase.DECODE),
		@EventConfig(listeners = UINavigationForm.SelectNavigationNodeActionListener.class, phase = Phase.DECODE),
		@EventConfig(listeners = UINavigationForm.RemoveListTargetPageActionListener.class, phase = Phase.DECODE),
		@EventConfig(listeners = UINavigationForm.RemoveDetailTargetPageActionListener.class, phase = Phase.DECODE),
		@EventConfig(listeners = UINavigationForm.RemoveNavigationNodeActionListener.class, phase = Phase.DECODE) })
public class UINavigationForm extends UIForm implements UIPopupComponent, UISelectable {

	// private static final Log logger =
	// ExoLogger.getExoLogger(UINavigationForm.class);

	/** The Constant NODE. */
	public static final String NODE = "Node";

	/** The Constant IS_VISIBLE. */
	public static final String IS_VISIBLE = "Visible";

	/** The Constant NAVIGATION_NODE_STRING_INPUT. */
	public static final String NAVIGATION_NODE_STRING_INPUT = "NavigationNode";

	/** The Constant NAVIGATION_NODE_INPUT_SET. */
	public static final String NAVIGATION_NODE_INPUT_SET = "NavigationNodeInputSet";

	/** The Constant NAVIGATION_NODE_SELECTOR_POPUP_WINDOW. */
	public final static String NAVIGATION_SELECTOR_POPUP_WINDOW = "NavigationSelectorPopupWindow";

	/** The Constant INDEX. */
	public static final String INDEX = "Index";

	/** The Constant NAVIGATION_CONTROLS. */
	public static final String NAVIGATION_CONTROLS = "NavigationControls";

	/** The Constant IS_CLICKABLE. */
	public static final String IS_CLICKABLE = "Clickable";

	/** The Constant LIST_TARGET_PAGE_STRING_INPUT. */
	public final static String LIST_TARGET_PAGE_STRING_INPUT = "ListTargetPageFormStringInput";

	/** The Constant LIST_TARGET_PAGE_INPUT_SET. */
	public final static String LIST_TARGET_PAGE_INPUT_SET = "ListTargetPageFormInputSet";

	/** The Constant LIST_TARGET_PAGE_SELECTOR_POPUP_WINDOW. */
	public final static String LIST_TARGET_PAGE_SELECTOR_POPUP_WINDOW = "ListTargetPageSelectorPopupWindow";

	/** The Constant LIST_SHOW_CLV_BY_STRING_INPUT. */
	public static final String LIST_SHOW_CLV_BY_STRING_INPUT = "ListShowCLVByStringInput";

	/** The Constant DETAIL_TARGET_PAGE_STRING_INPUT. */
	public final static String DETAIL_TARGET_PAGE_STRING_INPUT = "DetailTargetPageStringInput";

	/** The Constant DETAIL_TARGET_PAGE_INPUT_SET. */
	public final static String DETAIL_TARGET_PAGE_INPUT_SET = "DetailTargetPageInputSet";

	/** The Constant DETAIL_TARGET_PAGE_SELECTOR_POPUP_WINDOW. */
	public final static String DETAIL_TARGET_PAGE_SELECTOR_POPUP_WINDOW = "DetailTargetPageSelectorPopupWindow";

	/** The Constant DETAIL_SHOW_CLV_BY_STRING_INPUT. */
	public static final String DETAIL_SHOW_CLV_BY_STRING_INPUT = "DetailShowCLVByStringInput";

	/** The Constant LIST. */
	public static final String LIST = "List";

	/** The Constant DETAIL. */
	public static final String DETAIL = "Detail";

	/** The popup id. */
	private String popupId;

	String nameValue_ = null;

	private boolean isVisible = false;

	/** The index value. */
	private long index_ = 1000;

	/** The navigation node value. */
	private String navigationNode_ = "";

	/** The clickable value. */
	private boolean isClickable = false;

	/** The list targetPage value. */
	private String listTargetPage_ = "";

	/** The list parameter name value. */
	private String listShowClvBy_ = "";

	/** The detail targetPage value. */
	private String detailTargetPage_ = "";

	/** The detail parameter name value. */
	private String detailShowClvBy_ = "";

	/** The currentNode value. */
	private Node currentNode;

	public UINavigationForm() throws Exception {
	}

	/**
	 * initializes fields
	 * 
	 * @throws Exception
	 */
	public void initParams() throws Exception {

		// get current node

		// name field
		if (currentNode.hasProperty("exo:title")) {
			nameValue_ = currentNode.getProperty("exo:title").getValue().getString();
		}
		if (currentNode.hasNode("jcr:content")) {
			Node content = currentNode.getNode("jcr:content");
			if (content.hasProperty("dc:title")) {
				try {
					nameValue_ = content.getProperty("dc:title").getValues()[0].getString();
				} catch (Exception e) {
					nameValue_ = null;
				}
			}
		}

		if (nameValue_ == null)
			nameValue_ = currentNode.getName();

		boolean hasNavigableMixinType = currentNode.isNodeType("exo:navigable");
		if (hasNavigableMixinType) {
		  isVisible = true;
			if (currentNode.hasProperty("exo:index")) {
				index_ = currentNode.getProperty("exo:index").getLong();
			}

			if (currentNode.hasProperty("exo:navigationNode")) {
				navigationNode_ = currentNode.getProperty("exo:navigationNode").getString();
			}

			if (currentNode.hasProperty("exo:clickable")) {
				if (currentNode.getProperty("exo:clickable").getBoolean()) {
				  isClickable = true;
				} else
				  isClickable = false;
			}

			if (currentNode.hasProperty("exo:page")) {
				listTargetPage_ = currentNode.getProperty("exo:page").getString();
			}

			if (currentNode.hasProperty("exo:pageParamId")) {
				listShowClvBy_ = currentNode.getProperty("exo:pageParamId").getString();
			}

			if (currentNode.hasProperty("exo:childrenPage")) {
				detailTargetPage_ = currentNode.getProperty("exo:childrenPage").getString();
			}

			if (currentNode.hasProperty("exo:childrenPageParamId")) {
				detailShowClvBy_ = currentNode.getProperty("exo:childrenPageParamId").getString();
			}

		}

	}

	public void activate() throws Exception {
		UIJCRExplorer uiExplorer = getAncestorOfType(UIJCRExplorer.class);
		currentNode = uiExplorer.getCurrentNode();
		initParams();

		/** node field */
		UIFormStringInput uiFormNameValueStringInput = new UIFormStringInput(NODE, NODE, nameValue_);
		uiFormNameValueStringInput.setEditable(false);

		/** visible field */
		UIFormCheckBoxInput<Boolean> uiFormVisibleValueCheckBoxInput = new UIFormCheckBoxInput<Boolean>(IS_VISIBLE, IS_VISIBLE, false);
		uiFormVisibleValueCheckBoxInput.setChecked(isVisible);

		/** navigation node field */
		UIFormStringInput uiFormNavigationNodeValueStringInput = new UIFormStringInput(NAVIGATION_NODE_STRING_INPUT, NAVIGATION_NODE_STRING_INPUT,
				navigationNode_);
		uiFormNavigationNodeValueStringInput.setEditable(false);

		UIFormInputSetWithAction navigationNodeInputSet = new UIFormInputSetWithAction(NAVIGATION_NODE_INPUT_SET);
		navigationNodeInputSet.setActionInfo(NAVIGATION_NODE_STRING_INPUT, new String[] { "SelectNavigationNode", "RemoveNavigationNode" });
		navigationNodeInputSet.addUIFormInput(uiFormNavigationNodeValueStringInput);

		/** index field */
		UIFormStringInput uiFormIndexValueStringInput = new UIFormStringInput(INDEX, INDEX, String.valueOf(index_));
		uiFormIndexValueStringInput.addValidator(NumberFormatValidator.class);

		/** clickable field */
		UIFormCheckBoxInput<Boolean> uiFormClickableValueCheckBoxInput = new UIFormCheckBoxInput<Boolean>(IS_CLICKABLE, IS_CLICKABLE, false);
		uiFormClickableValueCheckBoxInput.setChecked(isClickable);

		/** TARGET PAGE */
		UIFormStringInput uiFormTargetPageValueStringInput = new UIFormStringInput(LIST_TARGET_PAGE_STRING_INPUT, LIST_TARGET_PAGE_STRING_INPUT,
				listTargetPage_);
		uiFormTargetPageValueStringInput.setEditable(false);

		UIFormInputSetWithAction targetPageInputSet = new UIFormInputSetWithAction(LIST_TARGET_PAGE_INPUT_SET);

		targetPageInputSet.setActionInfo(LIST_TARGET_PAGE_STRING_INPUT, new String[] { "SelectListTargetPage", "RemoveListTargetPage" });
		targetPageInputSet.addUIFormInput(uiFormTargetPageValueStringInput);

		/** SHOW CLV BY */

		UIFormStringInput uiFormShowClvByValueStringInput = new UIFormStringInput(LIST_SHOW_CLV_BY_STRING_INPUT, LIST_SHOW_CLV_BY_STRING_INPUT,
				listShowClvBy_);

		/** DETAIL_TARGET PAGE */

		UIFormStringInput uiFormDetailTargetPageValueStringInput = new UIFormStringInput(DETAIL_TARGET_PAGE_STRING_INPUT,
				DETAIL_TARGET_PAGE_STRING_INPUT, detailTargetPage_);
		uiFormDetailTargetPageValueStringInput.setEditable(false);
		UIFormInputSetWithAction detailTargetPageInputSet = new UIFormInputSetWithAction(DETAIL_TARGET_PAGE_INPUT_SET);
		detailTargetPageInputSet.setActionInfo(DETAIL_TARGET_PAGE_STRING_INPUT, new String[] { "SelectDetailTargetPage", "RemoveDetailTargetPage" });
		detailTargetPageInputSet.addUIFormInput(uiFormDetailTargetPageValueStringInput);

		/** DETAIL_SHOW CLV BY */

		UIFormStringInput uiFormDetailShowClvByValueStringInput = new UIFormStringInput(DETAIL_SHOW_CLV_BY_STRING_INPUT,
				DETAIL_SHOW_CLV_BY_STRING_INPUT, detailShowClvBy_);
		/*
		if (!navigationNode_.equals("")) {
			uiFormIndexValueStringInput.setEnable(false);
			uiFormClickableValueRadioBoxInput.setEnable(false);
		}
		*/
		addChild(uiFormNameValueStringInput);
		addChild(uiFormVisibleValueCheckBoxInput);
		addChild(navigationNodeInputSet);
		addChild(uiFormIndexValueStringInput);
		addChild(uiFormClickableValueCheckBoxInput);
		addChild(targetPageInputSet);
		addChild(uiFormShowClvByValueStringInput);
		addChild(detailTargetPageInputSet);
		addChild(uiFormDetailShowClvByValueStringInput);

		setActions(new String[] { "Save", "Cancel" });

	}

	/**
   * 
   */
	public void deActivate() throws Exception {

	}

	/**
	 * Sets the popup id.
	 * 
	 * @param popupId
	 *            the new popup id
	 */
	public void setPopupId(String popupId) {
		this.popupId = popupId;
	}

	/**
	 * Save action
	 * @author Thomas Delhoménie
	 *
	 */
	static public class SaveActionListener extends EventListener<UINavigationForm> {
		public void execute(Event<UINavigationForm> event) throws Exception {
			UINavigationForm uiNavigationForm = event.getSource();
			UIJCRExplorer uiExplorer = uiNavigationForm.getAncestorOfType(UIJCRExplorer.class);
			UIApplication uiApp = uiNavigationForm.getAncestorOfType(UIApplication.class);
			// retrieve current node
			Node node = uiExplorer.getCurrentNode();
			// if current node is locked...
			if (uiExplorer.nodeIsLocked(node)) {
				uiApp.addMessage(new ApplicationMessage("UIPopupMenu.msg.node-locked", null));
				event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
				return;
			}

			
			boolean isVisible = (Boolean) uiNavigationForm.<UIFormCheckBoxInput<Boolean>> getUIInput(IS_VISIBLE).getValue();
			
			boolean hasNavigableMixinType = node.isNodeType("exo:navigable");
			if (isVisible) {
				if (!hasNavigableMixinType) {
					if (node.canAddMixin("exo:navigable")) {
						node.addMixin("exo:navigable");
					} else {
						uiApp.addMessage(new ApplicationMessage("UISingleExternalMetadataForm.msg.can-not-add",	null));
						event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
						return;
					}

				}
				
				String navigationNode = uiNavigationForm.getUIStringInput(NAVIGATION_NODE_STRING_INPUT).getValue();
				node.setProperty("exo:navigationNode", navigationNode);

				// if navigation node is selected, default values of
				// "index and clickable" are saved
				long index = 1000;
				boolean isClickable = true;
				String listTargetPage = uiNavigationForm.getUIStringInput(LIST_TARGET_PAGE_STRING_INPUT).getValue();
				String paramListTargetPage = uiNavigationForm.getUIStringInput(LIST_SHOW_CLV_BY_STRING_INPUT).getValue();

				if (navigationNode.equals("")) {
					index = Long.parseLong(uiNavigationForm.getUIStringInput(INDEX).getValue());
					isClickable = (Boolean)uiNavigationForm.<UIFormCheckBoxInput<Boolean>> getUIInput(IS_CLICKABLE).getValue();
				}

				node.setProperty("exo:index", index);
				node.setProperty("exo:clickable", isClickable);
				node.setProperty("exo:page", listTargetPage);
				node.setProperty("exo:pageParamId", paramListTargetPage);

				String detailTargetPage = uiNavigationForm.getUIStringInput(DETAIL_TARGET_PAGE_STRING_INPUT).getValue();
				node.setProperty("exo:childrenPage", detailTargetPage);

				String detailParamName = uiNavigationForm.getUIStringInput(DETAIL_SHOW_CLV_BY_STRING_INPUT).getValue();
				node.setProperty("exo:childrenPageParamId", detailParamName);
			} else {
				if (hasNavigableMixinType) {
					node.removeMixin("exo:navigable");
				}
			}
			node.save();
			
			propagateVisibility(node, isVisible);
			
			node.getSession().save();

			// close window
			uiExplorer.cancelAction();
		}
		
		/**
		 * Recursive method to make all children visible or not
		 * @param node
		 * @param visible
		 */
		private void propagateVisibility(Node node, boolean visible) throws RepositoryException {
			if(node.hasNodes()) {
				// loop over child nodes...
				NodeIterator itChildNodes = node.getNodes();
				while(itChildNodes.hasNext()) {
					Node childNode = itChildNodes.nextNode();
					
					boolean hasNavigableMixinType = childNode.isNodeType("exo:navigable");
					if (visible) {
						boolean folderType = childNode.isNodeType("nt:folder")
												|| childNode.isNodeType("exo:taxonomy");
						boolean navigableType = folderType
												|| childNode.isNodeType("exo:webContent")
												|| childNode.isNodeType("exo:product")
												|| childNode.isNodeType("exo:taxonomyLink");
						
						if (!hasNavigableMixinType && navigableType) {
							if (childNode.canAddMixin("exo:navigable")) {
								childNode.addMixin("exo:navigable");
							} else {
								//uiApp.addMessage(new ApplicationMessage("UISingleExternalMetadataForm.msg.can-not-add",	null));
								//event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
								return;
							}
		
							childNode.setProperty("exo:navigationNode", "");
							childNode.setProperty("exo:index", 1000);
							childNode.setProperty("exo:clickable", true);
							childNode.setProperty("exo:page", "");
							childNode.setProperty("exo:pageParamId", "");
							childNode.setProperty("exo:childrenPage", "");
							childNode.setProperty("exo:childrenPageParamId", "");
							
							childNode.save();
							
							if(folderType) {
								propagateVisibility(childNode, visible);
							}
						}
					} else {
						if (hasNavigableMixinType) {
							childNode.removeMixin("exo:navigable");
							childNode.save();
						}
						
						propagateVisibility(childNode, visible);
					}	
				}
			}
		}
	}

	static public class CancelActionListener extends EventListener<UINavigationForm> {
		public void execute(Event<UINavigationForm> event) throws Exception {
			UIJCRExplorer uiExplorer = event.getSource().getAncestorOfType(UIJCRExplorer.class);
			uiExplorer.cancelAction();
		}
	}

	/**
	 * Action to select navigation node
	 */
	public static class SelectNavigationNodeActionListener extends EventListener<UINavigationForm> {
		public void execute(Event<UINavigationForm> event) throws Exception {

			UINavigationForm uiNavigationForm = event.getSource();
			UINavigationSelector uiNavigationSelector = uiNavigationForm.createUIComponent(UINavigationSelector.class, null, null);
			uiNavigationSelector.setSourceComponent(uiNavigationForm, new String[] { NAVIGATION_NODE_STRING_INPUT });
			UIJCRExplorer uiExplorer = event.getSource().getAncestorOfType(UIJCRExplorer.class);
			UIPopupContainer popupContainer = uiExplorer.getChild(UIPopupContainer.class);
			uiNavigationSelector.init();
			popupContainer.removeChildById(NAVIGATION_SELECTOR_POPUP_WINDOW);
			UIPopupWindow popupWindow = popupContainer.addChild(UIPopupWindow.class, null, NAVIGATION_SELECTOR_POPUP_WINDOW);
			popupWindow.setUIComponent(uiNavigationSelector);
			popupWindow.setWindowSize(800, 0);
			popupWindow.setShow(true);
			popupWindow.setRendered(true);
			popupWindow.setResizable(true);
			WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
			requestContext.addUIComponentToUpdateByAjax(popupContainer);
			uiNavigationForm.setPopupId(NAVIGATION_SELECTOR_POPUP_WINDOW);

		}
	}

	/**
	 * Action to select target page
	 */
	public static class SelectListTargetPageActionListener extends EventListener<UINavigationForm> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.exoplatform.webui.event.EventListener#execute(org.exoplatform
		 * .webui.event.Event)
		 */
		public void execute(Event<UINavigationForm> event) throws Exception {
			UINavigationForm uiNavigationForm = event.getSource();
			UIPageSelector pageSelector = uiNavigationForm.createUIComponent(UIPageSelector.class, null, null);
			pageSelector.setSourceComponent(uiNavigationForm, new String[] { LIST_TARGET_PAGE_STRING_INPUT });
			UIJCRExplorer uiExplorer = event.getSource().getAncestorOfType(UIJCRExplorer.class);
			UIPopupContainer popupContainer = uiExplorer.getChild(UIPopupContainer.class);
			popupContainer.removeChildById(LIST_TARGET_PAGE_SELECTOR_POPUP_WINDOW);
			UIPopupWindow popupWindow = popupContainer.addChild(UIPopupWindow.class, null, LIST_TARGET_PAGE_SELECTOR_POPUP_WINDOW);
			popupWindow.setUIComponent(pageSelector);
			popupWindow.setWindowSize(800, 0);
			popupWindow.setShow(true);
			popupWindow.setRendered(true);
			popupWindow.setResizable(true);
			WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
			requestContext.addUIComponentToUpdateByAjax(popupContainer);
			uiNavigationForm.setPopupId(LIST_TARGET_PAGE_SELECTOR_POPUP_WINDOW);
		}
	}

	/**
	 * Action to select detail target page
	 */
	public static class SelectDetailTargetPageActionListener extends EventListener<UINavigationForm> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.exoplatform.webui.event.EventListener#execute(org.exoplatform
		 * .webui.event.Event)
		 */
		public void execute(Event<UINavigationForm> event) throws Exception {
			UINavigationForm uiNavigationForm = event.getSource();
			UIPageSelector pageSelector = uiNavigationForm.createUIComponent(UIPageSelector.class, null, null);
			pageSelector.setSourceComponent(uiNavigationForm, new String[] { DETAIL_TARGET_PAGE_STRING_INPUT });
			UIJCRExplorer uiExplorer = event.getSource().getAncestorOfType(UIJCRExplorer.class);
			UIPopupContainer popupContainer = uiExplorer.getChild(UIPopupContainer.class);
			popupContainer.removeChildById(DETAIL_TARGET_PAGE_SELECTOR_POPUP_WINDOW);
			UIPopupWindow popupWindow = popupContainer.addChild(UIPopupWindow.class, null, DETAIL_TARGET_PAGE_SELECTOR_POPUP_WINDOW);
			popupWindow.setUIComponent(pageSelector);
			popupWindow.setWindowSize(800, 0);
			popupWindow.setShow(true);
			popupWindow.setRendered(true);
			popupWindow.setResizable(true);
			WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
			requestContext.addUIComponentToUpdateByAjax(popupContainer);
			uiNavigationForm.setPopupId(DETAIL_TARGET_PAGE_SELECTOR_POPUP_WINDOW);
		}
	}

	/**
	 * Action to remove navigation node
	 */
	public static class RemoveNavigationNodeActionListener extends EventListener<UINavigationForm> {
		public void execute(Event<UINavigationForm> event) throws Exception {
			UINavigationForm uiNavigationForm = event.getSource();
			uiNavigationForm.getUIStringInput(NAVIGATION_NODE_STRING_INPUT).setValue("");
			// if NAVIGATION_NODE_STRING_INPUT is not selected other fields must
			// be enabled
//			uiNavigationForm.getUIStringInput(INDEX).setEnable(true);
//			((UIFormCheckBoxInput) uiNavigationForm.getUIInput(IS_CLICKABLE)).setEnable(true);
//			uiNavigationForm.getUIStringInput(LIST_TARGET_PAGE_STRING_INPUT).setEnable(true);
//			uiNavigationForm.getUIStringInput(DETAIL_SHOW_CLV_BY_STRING_INPUT).setEnable(true);
			event.getRequestContext().addUIComponentToUpdateByAjax(uiNavigationForm);
		}
	}

	/**
	 * Action to remove detail target page
	 */
	public static class RemoveDetailTargetPageActionListener extends EventListener<UINavigationForm> {
		public void execute(Event<UINavigationForm> event) throws Exception {
			UINavigationForm uiNavigationForm = event.getSource();
			uiNavigationForm.getUIStringInput(DETAIL_TARGET_PAGE_STRING_INPUT).setValue("");
			event.getRequestContext().addUIComponentToUpdateByAjax(uiNavigationForm);
		}
	}

	/**
	 * Action to remove target page
	 */
	public static class RemoveListTargetPageActionListener extends EventListener<UINavigationForm> {
		public void execute(Event<UINavigationForm> event) throws Exception {
			UINavigationForm uiNavigationForm = event.getSource();
			uiNavigationForm.getUIStringInput(LIST_TARGET_PAGE_STRING_INPUT).setValue("");
			event.getRequestContext().addUIComponentToUpdateByAjax(uiNavigationForm);
		}
	}

	/**
	 * Method called when a field value is selected in a popup window (navigation node, list page, ...)
	 */
	public void doSelect(String selectField, Object value) throws Exception {
		if (selectField != null && value != null) {
			String sValue = (String) value;
			// if NAVIGATION_NODE_STRING_INPUT is selected other fields must be
			// disabled
			/*
			if (selectField == NAVIGATION_NODE_STRING_INPUT) {
				getUIStringInput(INDEX).setEnable(false);
				((UIFormCheckBoxInput) getUIInput(IS_CLICKABLE)).setChecked(false);
				((UIFormCheckBoxInput) getUIInput(IS_CLICKABLE)).setEnable(false);
				getUIStringInput(LIST_TARGET_PAGE_STRING_INPUT).setEnable(false);
				getUIStringInput(DETAIL_SHOW_CLV_BY_STRING_INPUT).setEnable(false);
			}
			*/
			getUIStringInput(selectField).setValue(sValue);
		}
		UIJCRExplorer uiExplorer = this.getAncestorOfType(UIJCRExplorer.class);
		UIPopupContainer popupContainer = uiExplorer.getChild(UIPopupContainer.class);
		popupContainer.removeChildById(popupId);
	}

}
