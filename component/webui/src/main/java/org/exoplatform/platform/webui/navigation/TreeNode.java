package org.exoplatform.platform.webui.navigation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.Described.State;
import org.exoplatform.portal.mop.navigation.NodeChangeListener;
import org.exoplatform.portal.mop.navigation.NodeState;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.webui.util.Util;

/**
 * A wrapper class of {@link UserNode} for manipulation in WebUI part
 * 
 * @author <a href="mailto:trong.tran@exoplatform.com">Trong Tran</a>
 * @version $Revision$
 */
public class TreeNode implements NodeChangeListener<UserNode>
{
   private Map<String, TreeNode> caches;

   private UserNavigation nav;

   private UserNode node;

   private TreeNode rootNode;

   private boolean deleteNode = false;

   private boolean cloneNode = false;

   private String id;

   private List<TreeNode> children;
   
   private Map<Locale, State> i18nizedLabels;

   public TreeNode(UserNavigation nav, UserNode node)
   {
      this(nav, node, null);
      this.rootNode = this;
      this.caches = new HashMap<String, TreeNode>();
      addToCached(this);
   }

   private TreeNode(UserNavigation nav, UserNode node, TreeNode rootNode)
   {
      this.rootNode = rootNode;
      this.nav = nav;
      this.node = node;
   }
   
   public List<TreeNode> getChildren()
   {
      if (children == null)
      {
         children = new LinkedList<TreeNode>();
         for (UserNode child : node.getChildren())
         {
            String key = child.getId() == null ? String.valueOf(child.hashCode()) : child.getId();
            TreeNode node = findNode(key);
            if (node == null)
            {
               throw new IllegalStateException("Can' find node " + child.getURI() + " in the cache");
            }
            children.add(node);
         }
      }
      return children;
   }

   public TreeNode getChild(String name)
   {
      UserNode child = node.getChild(name);
      if (child == null)
      {
         return null;
      }
      return findNode(child.getId() == null ? String.valueOf(child.hashCode()) : child.getId());
   }

   public boolean removeChild(TreeNode child)
   {
      children = null;
      if (child == null)
      {
         return false;
      }
      removeFromCached(child); 
      return node.removeChild(child.getName());
   }

   public TreeNode getParent()
   {
      UserNode parent = node.getParent();
      if (parent == null)
         return null;

      return findNode(parent.getId() == null ? String.valueOf(parent.hashCode()) : parent.getId());
   }

   public TreeNode getChild(int childIndex) throws IndexOutOfBoundsException
   {
      UserNode child = node.getChild(childIndex);
      if (child == null)
      {
         return null;
      }
      return findNode(child.getId() == null ? String.valueOf(child.hashCode()) : child.getId());
   }

   public TreeNode addChild(String childName)
   {
      children = null;
      UserNode child = node.addChild(childName);
      return addToCached(new TreeNode(nav, child, this.rootNode));
   }

   public void addChild(TreeNode child)
   {
      TreeNode oldParent = child.getParent();
      if (oldParent != null)
      {
         oldParent.children = null;
      }
      children = null; 
      this.node.addChild(child.getNode());
   }
   
   public void addChild(int index, TreeNode child)
   {
      TreeNode oldParent = child.getParent();
      if (oldParent != null)
      {
         oldParent.children = null;
      }
      children = null;
      node.addChild(index, child.getNode());
   }

   public TreeNode findNode(String nodeID)
   {
      return this.rootNode.caches.get(nodeID);
   }

   public UserNode getNode()
   {
      return node;
   }

   public UserNavigation getPageNavigation()
   {
      return nav;
   }

   public boolean isDeleteNode()
   {
      return deleteNode;
   }

   public void setDeleteNode(boolean deleteNode)
   {
      this.deleteNode = deleteNode;
   }

   public boolean isCloneNode()
   {
      return cloneNode;
   }

   public void setCloneNode(boolean b)
   {
      cloneNode = b;
   }

   public String getPageRef()
   {
      return node.getPageRef();
   }

   public String getId()
   {
      if (this.id == null)
      {
         this.id = node.getId() == null ? String.valueOf(node.hashCode()) : node.getId();
      }
      return this.id;
   }

   public String getURI()
   {
      return node.getURI();
   }

   public String getIcon()
   {
      return node.getIcon();
   }

   public void setIcon(String icon)
   {
      node.setIcon(icon);
   }

   public String getEncodedResolvedLabel()
   {
      if (getLabel() == null)
      {
         if (i18nizedLabels != null)
         {
            Locale locale = Util.getPortalRequestContext().getLocale();
            for (Locale key  : i18nizedLabels.keySet())
            {
               if (key.equals(locale))
               {
                 String encodedLabel = i18nizedLabels.get(key).getName();
                 return encodedLabel == null ? getName() : encodedLabel;
               }
            }
         }
      }
      String encodedLabel = node.getEncodedResolvedLabel();
      return encodedLabel == null ? getName() : encodedLabel;
   }

   public String getName()
   {
      return node.getName();
   }

   public void setName(String name)
   {
      node.setName(name);
   }

   public String getLabel()
   {
      return node.getLabel();
   }

   public void setLabel(String label)
   {
      node.setLabel(label);
   }

   public Visibility getVisibility()
   {
      return node.getVisibility();
   }

   public void setVisibility(Visibility visibility)
   {
      node.setVisibility(visibility);
   }

   public long getStartPublicationTime()
   {
      return node.getStartPublicationTime();
   }

   public void setStartPublicationTime(long startPublicationTime)
   {
      node.setStartPublicationTime(startPublicationTime);
   }

   public long getEndPublicationTime()
   {
      return node.getEndPublicationTime();
   }

   public void setEndPublicationTime(long endPublicationTime)
   {
      node.setEndPublicationTime(endPublicationTime);
   }

   public void setPageRef(String pageRef)
   {
      node.setPageRef(pageRef);
   }

   public String getResolvedLabel()
   {
      String resolvedLabel = node.getResolvedLabel();
      return resolvedLabel == null ? "" : resolvedLabel;
   }

   public boolean hasChildrenRelationship()
   {
      return node.hasChildrenRelationship();
   }

   public int getChildrenCount()
   {
      return node.getChildrenCount();
   }

   private TreeNode addToCached(TreeNode node)
   {
      if (node == null)
      {
         return null;
      }

      if (findNode(node.getId()) != null)
      {
         return node;
      }
      
      this.rootNode.caches.put(node.getId(), node);
      for (UserNode child : node.getNode().getChildren())
      {
         addToCached(new TreeNode(nav, child, this.rootNode));
      }
      return node;
   }

   private TreeNode removeFromCached(TreeNode node)
   {
      if (node == null)
      {
         return null;
      }

      this.rootNode.caches.remove(node.getId());
      if (node.hasChildrenRelationship())
      {
         for (TreeNode child : node.getChildren())
         {
            removeFromCached(child);
         }
      }
      return node;
   }

   @Override
   public void onAdd(UserNode target, UserNode parent, UserNode previous)
   {
      addToCached(new TreeNode(this.nav, target, this.rootNode));
      findNode(parent.getId()).children = null;
   }

   @Override
   public void onCreate(UserNode target, UserNode parent, UserNode previous, String name)
   {
   }

   @Override
   public void onRemove(UserNode target, UserNode parent)
   {
      removeFromCached(findNode(target.getId()));
      findNode(parent.getId()).children = null;
   }

   @Override
   public void onDestroy(UserNode target, UserNode parent)
   {
   }

   @Override
   public void onRename(UserNode target, UserNode parent, String name)
   {
   }

   @Override
   public void onUpdate(UserNode target, NodeState state)
   {
   }

   @Override
   public void onMove(UserNode target, UserNode from, UserNode to, UserNode previous)
   {
      TreeNode fromTreeNode = findNode(from.getId());
      TreeNode toTreeNode = findNode(to.getId());
      fromTreeNode.children = null;
      toTreeNode.children = null;
   }

   public void setI18nizedLabels(Map<Locale, State> labels)
   {
      this.i18nizedLabels = labels;
   }

   public Map<Locale, State> getI18nizedLabels()
   {
      return i18nizedLabels;
   }
}
