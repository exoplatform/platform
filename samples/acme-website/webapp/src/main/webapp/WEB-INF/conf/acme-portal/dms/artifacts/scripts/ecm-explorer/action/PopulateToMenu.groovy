/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.exoplatform.services.cms.scripts.CmsScript;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;
/**
 * Created by The eXo Platform SARL
 * Author : Anouar Chattouna
 *          anouar.chattouna@exoplatform.com
 * Jan 28, 2011 03:01:09 PM
 */

public class PopulateToHomePageMenu implements CmsScript {
  
  private RepositoryService repositoryService_ ;
  private SessionProviderService seProviderService_;
  private static final Log LOG  = ExoLogger.getLogger("PopulateToHomePageMenu");
  
  public PopulateToHomePageMenu(RepositoryService repositoryService, SessionProviderService sessionProviderService) {
    repositoryService_ = repositoryService ;
	seProviderService_ = sessionProviderService ;
  }

  public void execute(Object context) {
    Map variables = (Map) context;
	String repository = (String)variables.get("repository");
    String srcWorkspace = (String) context.get("srcWorkspace");
    String nodePath = (String) context.get("nodePath");
	Session session = null;
	//gets the action variables
	String navigationNode = variables.get("exo:navigationNode").toString();
    String index = variables.get("exo:index").toString();
	boolean clickable = variables.get("exo:clickable");
	String page = variables.get("exo:page").toString();
	String childrenPage = variables.get("exo:childrenPage").toString();

	try {
    	ManageableRepository manageableRepository = repositoryService_.getRepository(repository);
    	SessionProvider sessionProvider = seProviderService_.getSessionProvider(null);
    	if (sessionProvider == null) {
    		sessionProvider = seProviderService_.getSystemSessionProvider(null);
    	}
		session = sessionProvider.getSession(srcWorkspace, manageableRepository);
		//session = repositoryService_.getDefaultRepository().getSystemSession(srcWorkspace);
		Node node = (Node) session.getItem(nodePath);
		//node type should be navigableType
		boolean navigableType = node.isNodeType("exo:taxonomy") || node.isNodeType("exo:taxonomyLink") || node.isNodeType("exo:webContent") || node.isNodeType("exo:product");
		//node should allow exo:navigable mixin addition
		boolean canAddMixin = node.canAddMixin("exo:navigable");

		if(canAddMixin && navigableType){
			if(!node.isNodeType("exo:navigable")){
				//add mixin exo:navigation
				node.addMixin("exo:navigable");
				Node parentNode = node.getParent();
				if(parentNode.isNodeType("exo:navigable")){
					node.setProperty("exo:clickable", true);
					node.save();
				}else{
					node.setProperty("exo:navigationNode", navigationNode);
					node.setProperty("exo:index", Long.parseLong(index));
					node.setProperty("exo:clickable", false);
					node.setProperty("exo:page", page);
					node.setProperty("exo:pageParamId", "folder-id");
					node.setProperty("exo:childrenPage", childrenPage);
					node.setProperty("exo:childrenPageParamId", "content-id");
					node.save();
					if(node.hasNodes()) {
						propagateVisibility(node, true);
						}
				}
				session.save();
			}
		}
    } catch(Exception e) {
		/*
		if(session != null) {
			session.logout();        
		}
		*/
		LOG.error("Error while trying to add the mixin exo:navigable " + e.getMessage());
		//e.printStackTrace() ;
    }
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
					boolean navigableType = childNode.isNodeType("exo:taxonomy") || childNode.isNodeType("exo:taxonomyLink") || childNode.isNodeType("exo:webContent") || childNode.isNodeType("exo:product");
					if (!hasNavigableMixinType && navigableType) {
						if (childNode.canAddMixin("exo:navigable")) {
							childNode.addMixin("exo:navigable");
						} else {
							//uiApp.addMessage(new ApplicationMessage("UISingleExternalMetadataForm.msg.can-not-add",	null));
							//event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
							return;
						}
	
						//childNode.setProperty("exo:navigationNode", "");
						//childNode.setProperty("exo:index", 1000);
						childNode.setProperty("exo:clickable", true);
						//childNode.setProperty("exo:page", "");
						//childNode.setProperty("exo:pageParamId", "");
						//childNode.setProperty("exo:childrenPage", "");
						//childNode.setProperty("exo:childrenPageParamId", "");							
						childNode.save();
						
						if(childNode.isNodeType("exo:taxonomy")) {
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

  public void setParams(String[] params) {}

}