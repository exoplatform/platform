/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.platform.webui.navigation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.Described.State;
import org.exoplatform.portal.mop.description.DescriptionService;
import org.exoplatform.portal.mop.navigation.NavigationError;
import org.exoplatform.portal.mop.navigation.NavigationServiceException;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.portal.UIPortalComposer;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIEditInlineWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.gatein.common.util.ParameterValidation;

/** Copied by The eXo Platform SARL Author May 28, 2009 3:07:15 PM */
@ComponentConfigs({
   @ComponentConfig(template = "system:/groovy/portal/webui/navigation/UINavigationNodeSelector.gtmpl", events = {
      @EventConfig(listeners = UINavigationNodeSelector.ChangeNodeActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.NodeModifiedActionListener.class)}),
   @ComponentConfig(id = "NavigationNodePopupMenu", type = UIRightClickPopupMenu.class, template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl", events = {
      @EventConfig(listeners = UINavigationNodeSelector.AddNodeActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.EditPageNodeActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.EditSelectedNodeActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.CopyNodeActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.CutNodeActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.CloneNodeActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.PasteNodeActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.MoveUpActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.MoveDownActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.DeleteNodeActionListener.class, confirm = "UIPageNodeSelector.deleteNavigation")}),
   @ComponentConfig(id = "UINavigationNodeSelectorPopupMenu", type = UIRightClickPopupMenu.class, template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl", events = {
      @EventConfig(listeners = UINavigationNodeSelector.AddNodeActionListener.class),
      @EventConfig(listeners = UINavigationNodeSelector.PasteNodeActionListener.class)})})
public class UINavigationNodeSelector extends UIContainer
{
   private UserNavigation edittedNavigation;

   /**
    * This field holds transient copy of edittedTreeNodeData, which is used when
    * user pastes the content to a new tree node
    */
   private TreeNode copyOfTreeNodeData;

   private TreeNode rootNode;

   private UserPortal userPortal;

   private UserNodeFilterConfig filterConfig;
   
   private Map<UserNode, Map<Locale, State>> i18nizedLabels;

   private static final Scope NODE_SCOPE = Scope.GRANDCHILDREN;

   public UINavigationNodeSelector() throws Exception
   {
      UIRightClickPopupMenu rightClickPopup =
         addChild(UIRightClickPopupMenu.class, "UINavigationNodeSelectorPopupMenu", null).setRendered(true);
      rightClickPopup.setActions(new String[]{"AddNode", "PasteNode"});

      UITree uiTree = addChild(UITree.class, null, "TreeNodeSelector");
      uiTree.setIcon("DefaultPageIcon");
      uiTree.setSelectedIcon("DefaultPageIcon");
      uiTree.setBeanIdField("Id");
      uiTree.setBeanChildCountField("childrenCount");
      uiTree.setBeanLabelField("encodedResolvedLabel");
      uiTree.setBeanIconField("icon");

      UIRightClickPopupMenu uiPopupMenu =
         createUIComponent(UIRightClickPopupMenu.class, "NavigationNodePopupMenu", null);
      uiPopupMenu.setActions(new String[]{"AddNode", "EditPageNode", "EditSelectedNode", "CopyNode", "CloneNode",
         "CutNode", "DeleteNode", "MoveUp", "MoveDown"});
      uiTree.setUIRightClickPopupMenu(uiPopupMenu);
      
      i18nizedLabels = new HashMap<UserNode, Map<Locale,State>>();
   }

   public void setI18NizedLabels(Map<UserNode, Map<Locale, State>> labels)
   {
      this.i18nizedLabels = labels;
   }
   
   public Map<UserNode, Map<Locale, State>> getI18NizedLabels()
   {
      return this.i18nizedLabels;
   }
   
   /**
    * Init the UITree wrapped in UINavigationNodeSelector
    * 
    * @throws Exception
    */
   public void initTreeData() throws Exception
   {
      if (edittedNavigation == null || userPortal == null)
      {
         throw new IllegalStateException("edittedNavigation and userPortal must be initialized first");
      }

      try
      {
         this.rootNode =
            new TreeNode(edittedNavigation, userPortal.getNode(edittedNavigation, NODE_SCOPE, filterConfig, null));
         
         TreeNode node = this.rootNode;
         if (this.rootNode.getChildren().size() > 0)
         {
            node = rebaseNode(this.rootNode.getChild(0), NODE_SCOPE);
            if (node == null)
            {
               initTreeData();
               return;
            }
         }
         selectNode(node);
      }
      catch (Exception ex)
      {
         // Navigation deleted --> close the editor
         this.rootNode = null;
         
         WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
         context.getUIApplication().addMessage(
            new ApplicationMessage("UINavigationNodeSelector.msg." + NavigationError.NAVIGATION_NO_SITE.name(), null,
               ApplicationMessage.ERROR));

         UIPopupWindow popup = getAncestorOfType(UIPopupWindow.class);
         popup.createEvent("ClosePopup", Phase.PROCESS, context).broadcast();
         
         PortalRequestContext prContext = Util.getPortalRequestContext();
         UIWorkingWorkspace uiWorkingWS = Util.getUIPortalApplication().getChild(UIWorkingWorkspace.class);
         prContext.addUIComponentToUpdateByAjax(uiWorkingWS);
         prContext.setFullRender(true);
      }      
   }

   public TreeNode selectNode(TreeNode node) throws Exception
   {
      if (node == null)
      {
         return null;
      }

      UITree tree = getChild(UITree.class);
      tree.setSelected(node);
      if (node.getId().equals(rootNode.getId()))
      {
         tree.setChildren(null);
         tree.setSibbling(node.getChildren());
         tree.setParentSelected(node);
      }
      else
      {
         TreeNode parentNode = node.getParent();
         tree.setChildren(node.getChildren());
         tree.setSibbling(parentNode.getChildren());
         tree.setParentSelected(parentNode);
      }
      return node;
   }

   public TreeNode rebaseNode(TreeNode treeNode, Scope scope) throws Exception
   {
      if (treeNode == null || treeNode.getNode() == null)
      {
         return null;
      }

      UserNode userNode = treeNode.getNode();
      if (userNode.getId() == null)
      {
         // Transient node
         return treeNode;
      }

      userPortal.rebaseNode(userNode, scope, getRootNode());           
      //this line return null if node has been deleted
      return findNode(treeNode.getId());
   }

   public void save()
   {
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
      try 
      {
         userPortal.saveNode(getRootNode().getNode(), null);
         DescriptionService descriptionService = getApplicationComponent(DescriptionService.class);
         Map<UserNode, Map<Locale, State>> i18nizedLabels = this.i18nizedLabels;
         
         for (UserNode userNode  : i18nizedLabels.keySet())
         {
            Map<Locale, State> labels = i18nizedLabels.get(userNode);
            
            descriptionService.setDescriptions(userNode.getId(), labels);
         }
      }
      catch (NavigationServiceException ex)
      {           
         context.getUIApplication().addMessage(
            new ApplicationMessage("UINavigationNodeSelector.msg." + ex.getError().name(), null, ApplicationMessage.ERROR));            
      }
   }
   
   public TreeNode getCopyNode()
   {
      return copyOfTreeNodeData;
   }

   public void setCopyNode(TreeNode copyNode)
   {
      this.copyOfTreeNodeData = copyNode;
   }

   public TreeNode getRootNode()
   {
      return rootNode;
   }

   public void setUserPortal(UserPortal userPortal) throws Exception
   {
      this.userPortal = userPortal;
      setFilterConfig(UserNodeFilterConfig.builder().withReadWriteCheck().build());
   }

   private void setFilterConfig(UserNodeFilterConfig config)
   {
      this.filterConfig = config;
   }

   public void setEdittedNavigation(UserNavigation nav) throws Exception
   {
      this.edittedNavigation = nav;
   }

   public UserNavigation getEdittedNavigation()
   {
      return this.edittedNavigation;
   }

   public TreeNode findNode(String nodeID)
   {
      if (getRootNode() == null)
      {
         return null;
      }
      return getRootNode().findNode(nodeID);
   }

   static public abstract class BaseActionListener<T> extends EventListener<T>
   {
      protected TreeNode rebaseNode(TreeNode node, UINavigationNodeSelector selector) throws Exception
      {
         return rebaseNode(node, UINavigationNodeSelector.NODE_SCOPE, selector);
      }
      
      protected TreeNode rebaseNode(TreeNode node, Scope scope, UINavigationNodeSelector selector) throws Exception
      {
         WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
         TreeNode rebased = selector.rebaseNode(node, scope);
         if (rebased == null)
         {
            context.getUIApplication().addMessage(new ApplicationMessage("UINavigationNodeSelector.msg.staleData", null,
               ApplicationMessage.WARNING));
            selector.selectNode(selector.getRootNode());
            context.addUIComponentToUpdateByAjax(selector);
         }
         return rebased;
      }
      
      protected void handleError(NavigationError error, UINavigationNodeSelector selector) throws Exception
      {
         selector.initTreeData();
         if (selector.getRootNode() != null)
         {
            WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
            UIApplication uiApp = context.getUIApplication();
            uiApp.addMessage(new ApplicationMessage("UINavigationNodeSelector.msg." + error.name(), null,
               ApplicationMessage.ERROR));
         }                 
      }
   }

   static public class ChangeNodeActionListener extends BaseActionListener<UITree>
   {
      public void execute(Event<UITree> event) throws Exception
      {
         WebuiRequestContext context = event.getRequestContext();
         UINavigationNodeSelector uiNodeSelector = event.getSource().getParent();

         String nodeID = context.getRequestParameter(OBJECTID);
         TreeNode node = uiNodeSelector.findNode(nodeID);

         try
         {
            node = rebaseNode(node, uiNodeSelector);
         }
         catch (NavigationServiceException ex)
         {
            handleError(ex.getError(), uiNodeSelector);
            return;
         }

         uiNodeSelector.selectNode(node);
         context.addUIComponentToUpdateByAjax(uiNodeSelector);
      }
   }

   static public class AddNodeActionListener extends BaseActionListener<UIRightClickPopupMenu>
   {
      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         WebuiRequestContext context = event.getRequestContext();
         UIRightClickPopupMenu uiPopupMenu = event.getSource();
         UINavigationNodeSelector uiNodeSelector = uiPopupMenu.getAncestorOfType(UINavigationNodeSelector.class);

         String nodeID = context.getRequestParameter(UIComponent.OBJECTID);
         TreeNode node;
         if (ParameterValidation.isNullOrEmpty(nodeID))
         {
            node = uiNodeSelector.getRootNode();
         }
         else
         {
            node = uiNodeSelector.findNode(nodeID);            
         }
         
         try
         {
            node = rebaseNode(node, uiNodeSelector);
            if (node == null) return;
         }
         catch (NavigationServiceException ex)
         {
            handleError(ex.getError(), uiNodeSelector);
            return;
         }

         UIPopupWindow uiManagementPopup = uiNodeSelector.getAncestorOfType(UIPopupWindow.class);
         UIPageNodeForm uiNodeForm = uiManagementPopup.createUIComponent(UIPageNodeForm.class, null, null);
         uiNodeForm.setValues(null);
         uiManagementPopup.setUIComponent(uiNodeForm);

         uiNodeForm.setSelectedParent(node);
         UserNavigation edittedNavigation = uiNodeSelector.getEdittedNavigation();
         uiNodeForm.setContextPageNavigation(edittedNavigation);
         uiManagementPopup.setWindowSize(800, 500);
         event.getRequestContext().addUIComponentToUpdateByAjax(uiManagementPopup.getParent());
      }
   }

   static public class NodeModifiedActionListener extends BaseActionListener<UINavigationNodeSelector>
   {
      @Override
      public void execute(Event<UINavigationNodeSelector> event) throws Exception
      {
         UINavigationNodeSelector uiNodeSelector = event.getSource();

         try
         {
            rebaseNode(uiNodeSelector.getRootNode(), uiNodeSelector);
         }
         catch (NavigationServiceException ex)
         {
            handleError(ex.getError(), uiNodeSelector);
         }
      }
   }

   static public class EditPageNodeActionListener extends EventListener<UIRightClickPopupMenu>
   {
      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         // get nodeID
         String nodeID = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);

         // get UINavigationNodeSelector
         UIRightClickPopupMenu uiPopupMenu = event.getSource();
         UINavigationNodeSelector uiNodeSelector = uiPopupMenu.getAncestorOfType(UINavigationNodeSelector.class);

         // get Selected Node
         TreeNode selectedPageNode = uiNodeSelector.findNode(nodeID);

         UIPortalApplication uiApp = Util.getUIPortalApplication();
         if (selectedPageNode == null || selectedPageNode.getPageRef() == null)
         {
            uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.notAvailable", null));
            return;
         }

         UserPortalConfigService userService = uiNodeSelector.getApplicationComponent(UserPortalConfigService.class);

         // get selected page
         String pageId = selectedPageNode.getPageRef();
         Page selectPage = (pageId != null) ? userService.getPage(pageId) : null;
         if (selectPage != null)
         {
            UserACL userACL = uiApp.getApplicationComponent(UserACL.class);
            if (!userACL.hasEditPermission(selectPage))
            {
               uiApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.UserNotPermission", new String[]{pageId}, 1));
               return;
            }

            uiApp.setModeState(UIPortalApplication.APP_BLOCK_EDIT_MODE);
            // uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
            // uiWorkingWS.addChild(UIPortalComposer.class, "UIPageEditor",
            // null);

            UIWorkingWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
            UIPortalToolPanel uiToolPanel =
               uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class).setRendered(true);
            uiWorkingWS.setRenderedChild(UIEditInlineWorkspace.class);

            UIPortalComposer portalComposer =
               uiWorkingWS.findFirstComponentOfType(UIPortalComposer.class).setRendered(true);
            portalComposer.setShowControl(true);
            portalComposer.setEditted(false);
            portalComposer.setCollapse(false);
            portalComposer.setId("UIPageEditor");
            portalComposer.setComponentConfig(UIPortalComposer.class, "UIPageEditor");

            uiToolPanel.setShowMaskLayer(false);
            uiToolPanel.setWorkingComponent(UIPage.class, null);
            UIPage uiPage = (UIPage)uiToolPanel.getUIComponent();

            if (selectPage.getTitle() == null)
               selectPage.setTitle(selectedPageNode.getLabel());

            // convert Page to UIPage
            PortalDataMapper.toUIPage(uiPage, selectPage);
            Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS);
            Util.getPortalRequestContext().setFullRender(true);
         }
         else
         {
            uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.notAvailable", null));
         }
      }
   }

   static public class EditSelectedNodeActionListener extends BaseActionListener<UIRightClickPopupMenu>
   {
      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         WebuiRequestContext context = event.getRequestContext();
         UIRightClickPopupMenu popupMenu = event.getSource();
         UINavigationNodeSelector uiNodeSelector = popupMenu.getAncestorOfType(UINavigationNodeSelector.class);

         String nodeID = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
         TreeNode node = uiNodeSelector.findNode(nodeID);
         try
         {
            node = rebaseNode(node, uiNodeSelector);
            if (node == null) return;
         }
         catch (NavigationServiceException ex)
         {
            handleError(ex.getError(), uiNodeSelector);
            return;
         }

         UIApplication uiApp = context.getUIApplication();
         UserPortalConfigService service = uiApp.getApplicationComponent(UserPortalConfigService.class);
         String pageId = node.getPageRef();
         Page page = (pageId != null) ? service.getPage(pageId) : null;
         if (page != null)
         {
            UserACL userACL = uiApp.getApplicationComponent(UserACL.class);
            if (!userACL.hasPermission(page))
            {
               uiApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.UserNotPermission", new String[]{pageId}, 1));
               return;
            }
         }
         
         DescriptionService descriptionService = popupMenu.getApplicationComponent(DescriptionService.class);
         
         if (node.getI18nizedLabels() == null)
         {
            Map<Locale, State> labels = descriptionService.getDescriptions(nodeID);
            
            node.setI18nizedLabels(labels);
         }
         
         UIPopupWindow uiManagementPopup = uiNodeSelector.getAncestorOfType(UIPopupWindow.class);
         UIPageNodeForm uiNodeForm = uiApp.createUIComponent(UIPageNodeForm.class, null, null);
         uiManagementPopup.setUIComponent(uiNodeForm);

         UserNavigation edittedNav = uiNodeSelector.getEdittedNavigation();
         uiNodeForm.setContextPageNavigation(edittedNav);
         uiNodeForm.setValues(node);
         uiNodeForm.setSelectedParent(node.getParent());
         uiManagementPopup.setWindowSize(800, 500);
         event.getRequestContext().addUIComponentToUpdateByAjax(uiManagementPopup.getParent());
      }
   }

   static public class CopyNodeActionListener extends BaseActionListener<UIRightClickPopupMenu>
   {
      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         WebuiRequestContext context = event.getRequestContext();
         UINavigationNodeSelector uiNodeSelector = event.getSource().getAncestorOfType(UINavigationNodeSelector.class);
         event.getRequestContext().addUIComponentToUpdateByAjax(uiNodeSelector);

         String nodeID = context.getRequestParameter(UIComponent.OBJECTID);
         TreeNode node = uiNodeSelector.findNode(nodeID);
         try
         {
            node = rebaseNode(node, Scope.ALL, uiNodeSelector);
            if (node == null) return;
         }
         catch (NavigationServiceException ex)
         {
            handleError(ex.getError(), uiNodeSelector);
            return;
         }

         node.setDeleteNode(false);
         uiNodeSelector.setCopyNode(node);
         event.getSource().setActions(
            new String[]{"AddNode", "EditPageNode", "EditSelectedNode", "CopyNode", "CloneNode", "CutNode",
               "PasteNode", "DeleteNode", "MoveUp", "MoveDown"});
      }
   }

   static public class CutNodeActionListener extends BaseActionListener<UIRightClickPopupMenu>
   {
      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         WebuiRequestContext context = event.getRequestContext();
         UINavigationNodeSelector uiNodeSelector = event.getSource().getAncestorOfType(UINavigationNodeSelector.class);
         context.addUIComponentToUpdateByAjax(uiNodeSelector);

         String nodeID = context.getRequestParameter(UIComponent.OBJECTID);
         TreeNode node = uiNodeSelector.findNode(nodeID);
         try
         {
            node = rebaseNode(node, Scope.SINGLE, uiNodeSelector);
            if (node == null) return;
         }
         catch (NavigationServiceException ex)
         {
            handleError(ex.getError(), uiNodeSelector);
            return;
         }

         if (node != null && Visibility.SYSTEM.equals(node.getVisibility()))
         {
            context.getUIApplication().addMessage(
               new ApplicationMessage("UINavigationNodeSelector.msg.systemnode-move", null));
            return;
         }

         node.setDeleteNode(true);
         uiNodeSelector.setCopyNode(node);
         event.getSource().setActions(
            new String[]{"AddNode", "EditPageNode", "EditSelectedNode", "CopyNode", "CloneNode", "CutNode",
               "PasteNode", "DeleteNode", "MoveUp", "MoveDown"});
      }
   }

   static public class CloneNodeActionListener extends CopyNodeActionListener
   {
      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         super.execute(event);
         UINavigationNodeSelector uiNodeSelector = event.getSource().getAncestorOfType(UINavigationNodeSelector.class);
         TreeNode currNode = uiNodeSelector.getCopyNode();
         String nodeID = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
         if (currNode != null && currNode.getId().equals(nodeID))
            currNode.setCloneNode(true);
      }
   }

   static public class PasteNodeActionListener extends BaseActionListener<UIRightClickPopupMenu>
   {
      private UINavigationNodeSelector uiNodeSelector;

      private DataStorage dataStorage;

      private UserPortalConfigService service;

      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         WebuiRequestContext context = event.getRequestContext();
         UIRightClickPopupMenu uiPopupMenu = event.getSource();
         uiNodeSelector = uiPopupMenu.getAncestorOfType(UINavigationNodeSelector.class);
         context.addUIComponentToUpdateByAjax(uiNodeSelector);

         String nodeID = context.getRequestParameter(UIComponent.OBJECTID);
         TreeNode targetNode = uiNodeSelector.findNode(nodeID);
         TreeNode sourceNode = uiNodeSelector.getCopyNode();
         if (sourceNode == null)
            return;

         try
         {
            targetNode = rebaseNode(targetNode, uiNodeSelector);
            if (targetNode == null) return;
         }
         catch (NavigationServiceException ex)
         {
            handleError(ex.getError(), uiNodeSelector);
            return;
         }

         if (sourceNode.getId().equals(targetNode.getId()))
         {
            context.getUIApplication().addMessage(
               new ApplicationMessage("UIPageNodeSelector.msg.paste.sameSrcAndDes", null));
            return;
         }

         if (isExistChild(targetNode, sourceNode))
         {
            context.getUIApplication()
               .addMessage(new ApplicationMessage("UIPageNodeSelector.msg.paste.sameName", null));
            return;
         }

         UITree uitree = uiNodeSelector.getChild(UITree.class);
         UIRightClickPopupMenu popup = uitree.getUIRightClickPopupMenu();
         popup.setActions(new String[]{"AddNode", "EditPageNode", "EditSelectedNode", "CopyNode", "CutNode",
            "CloneNode", "DeleteNode", "MoveUp", "MoveDown"});
         uiNodeSelector.setCopyNode(null);         

         if (uiNodeSelector.findNode(sourceNode.getId()) == null)
         {
            context.getUIApplication().addMessage(
               new ApplicationMessage("UINavigationNodeSelector.msg.copiedNode.deleted", null, ApplicationMessage.WARNING));
            uiNodeSelector.selectNode(uiNodeSelector.getRootNode());
            return;
         }
         
         if (sourceNode.isDeleteNode())
         {
            targetNode.addChild(sourceNode);
            uiNodeSelector.selectNode(targetNode);
            return;
         }

         service = uiNodeSelector.getApplicationComponent(UserPortalConfigService.class);
         dataStorage = uiNodeSelector.getApplicationComponent(DataStorage.class);
         pasteNode(sourceNode, targetNode, sourceNode.isCloneNode());
         uiNodeSelector.selectNode(targetNode);
      }

      private TreeNode pasteNode(TreeNode sourceNode, TreeNode parent, boolean isClone) throws Exception
      {
         TreeNode node = parent.addChild(sourceNode.getName());
         node.setLabel(sourceNode.getLabel());
         node.setVisibility(sourceNode.getVisibility());
         node.setIcon(sourceNode.getIcon());
         node.setStartPublicationTime(sourceNode.getStartPublicationTime());
         node.setEndPublicationTime(sourceNode.getEndPublicationTime());

         if (isClone)
         {
            String pageName = "page" + node.hashCode();
            node.setPageRef(clonePageFromNode(sourceNode, pageName, sourceNode.getPageNavigation().getKey()));
         }
         else
         {
            node.setPageRef(sourceNode.getPageRef());
         }

         for (TreeNode child : sourceNode.getChildren())
         {
            pasteNode(child, node, isClone);
         }

         return node;
      }

      private String clonePageFromNode(TreeNode node, String pageName, SiteKey siteKey) throws Exception
      {
         String pageId = node.getPageRef();
         if (pageId != null)
         {
            Page page = service.getPage(pageId);
            if (page != null)
            {
               page = dataStorage.clonePage(pageId, siteKey.getTypeName(), siteKey.getName(), pageName);
               return page.getPageId();
            }
         }
         return null;
      }

      private boolean isExistChild(TreeNode parent, TreeNode child)
      {
         return parent != null && parent.getChild(child.getName()) != null;
      }
   }

   static public class MoveUpActionListener extends BaseActionListener<UIRightClickPopupMenu>
   {
      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         moveNode(event, -1);
      }

      protected void moveNode(Event<UIRightClickPopupMenu> event, int i) throws Exception
      {
         WebuiRequestContext context = event.getRequestContext();
         UINavigationNodeSelector uiNodeSelector = event.getSource().getAncestorOfType(UINavigationNodeSelector.class);
         context.addUIComponentToUpdateByAjax(uiNodeSelector.getParent());

         String nodeID = context.getRequestParameter(UIComponent.OBJECTID);
         TreeNode targetNode = uiNodeSelector.findNode(nodeID);
         // This happen when browser's not sync with server
         if (targetNode == null)
            return;

         TreeNode parentNode = targetNode.getParent();
         try
         {
            parentNode = rebaseNode(parentNode, uiNodeSelector);
            if (parentNode == null) return;
            // After update the parentNode, maybe targetNode has been deleted or moved
            TreeNode temp = parentNode.getChild(targetNode.getName()); 
            if (temp == null || !temp.getId().equals(targetNode.getId()))
            {
               context.getUIApplication().addMessage(new ApplicationMessage("UINavigationNodeSelector.msg.staleData", null,
                  ApplicationMessage.WARNING));
               uiNodeSelector.selectNode(uiNodeSelector.getRootNode());
               context.addUIComponentToUpdateByAjax(uiNodeSelector);
               return;
            }
         }
         catch (NavigationServiceException ex)
         {
            handleError(ex.getError(), uiNodeSelector);
            return;
         }

         Collection<TreeNode> children = parentNode.getChildren();

         int k;
         for (k = 0; k < children.size(); k++)
         {
            if (parentNode.getChild(k).getId().equals(targetNode.getId()))
            {
               break;
            }
         }

         if (k == 0 && i == -1)
         {
            return;
         }
         if (k == children.size() - 1 && i == 2)
         {
            return;
         }
          
         parentNode.addChild(k + i, targetNode);
         
         //These lines help to refresh the tree
         TreeNode selectedNode = uiNodeSelector.getSelectedNode();
         uiNodeSelector.selectNode(parentNode);
         uiNodeSelector.selectNode(selectedNode);
      }
   }

   static public class MoveDownActionListener extends UINavigationNodeSelector.MoveUpActionListener
   {
      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         super.moveNode(event, 2);
      }
   }

   static public class DeleteNodeActionListener extends EventListener<UIRightClickPopupMenu>
   {
      public void execute(Event<UIRightClickPopupMenu> event) throws Exception
      {
         WebuiRequestContext pcontext = event.getRequestContext();
         UINavigationNodeSelector uiNodeSelector = event.getSource().getAncestorOfType(UINavigationNodeSelector.class);
         pcontext.addUIComponentToUpdateByAjax(uiNodeSelector);

         String nodeID = pcontext.getRequestParameter(UIComponent.OBJECTID);
         TreeNode childNode = uiNodeSelector.findNode(nodeID);
         if (childNode == null)
         {
            return;
         }
         TreeNode parentNode = childNode.getParent();

         if (Visibility.SYSTEM.equals(childNode.getVisibility()))
         {
            UIApplication uiApp = pcontext.getUIApplication();
            uiApp.addMessage(new ApplicationMessage("UINavigationNodeSelector.msg.systemnode-delete", null));
            return;
         }

         parentNode.removeChild(childNode);
         uiNodeSelector.selectNode(parentNode);
      }
   }

   public TreeNode getSelectedNode()
   {
      return getChild(UITree.class).getSelected();
   }
}
