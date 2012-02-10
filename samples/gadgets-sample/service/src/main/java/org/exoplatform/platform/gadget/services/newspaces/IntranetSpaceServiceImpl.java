/*
 * Copyright (C) 2003-2011 eXo Platform.
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
package org.exoplatform.platform.gadget.services.newspaces;

import org.exoplatform.commons.utils.ISO8601;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.gadget.services.newspaces.IntranetSpaceService;
import org.exoplatform.platform.gadget.services.newspaces.IntranetSpace;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

/**
 * 
 * @author <a href="tungdt@exoplatform.com">Do Thanh Tung </a>
 * @version $Revision$
 */
public class IntranetSpaceServiceImpl implements IntranetSpaceService {

  private static final Log log = ExoLogger.getLogger(IntranetSpaceServiceImpl.class);
  private RepositoryService _repoService;
  private final String SOCIAL_WORKSPACE ="social";
  private final String SPACE_HOME = "production/soc:spaces";

  private static final String JCR_ROOT = "/jcr:root";
  /**
   * IntranetSpaceServiceImpl constructor
   * @param dataLocation
   * @throws Exception
   */
  public IntranetSpaceServiceImpl(RepositoryService repoService) throws Exception{
	this._repoService = repoService;
	  
  }

  private Node getSpaceHome() throws Exception {
	  
    if(this._repoService != null){
		SessionProvider sProvider = SessionProvider.createSystemProvider();
		
		try {
			Session session = this.getSession(sProvider);
			Node rootNode = session.getRootNode();
			return rootNode.getNode(SPACE_HOME);
		}
		catch (Exception e) {
			log.error("Can not get get SpaceHome", e);
			sProvider.close();
		}
    }
	return null;
  }

  /**
   * 
   * @see org.exoplatform.intranet.component.social.IntranetSpaceService#getLatestCreatedSpace()
   */
  public List<IntranetSpace> getLatestCreatedSpace(int maxday, List<String> allGroupAndMembershipOfUser) {
    
    if (maxday <=0) maxday = 10;
    String userName = (allGroupAndMembershipOfUser!=null && allGroupAndMembershipOfUser.size()>0)? allGroupAndMembershipOfUser.get(0):null;
    
    List<IntranetSpace> listSpaces = new ArrayList<IntranetSpace>();
	SessionProvider sProvider = SessionProvider.createSystemProvider();
	
    try {

      Node spaceHomeNode = getSpaceHome();
      Calendar pastTime = Calendar.getInstance();
      pastTime.setTime(new Date(new Date().getTime() - maxday*24*60*60*1000)); // the days ago

      //query here
      QueryManager qm = spaceHomeNode.getSession().getWorkspace().getQueryManager();
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(JCR_ROOT).append(spaceHomeNode.getPath()).append("//element(*,soc:spacedefinition)");
      stringBuffer.append("[@exo:dateCreated >= xs:dateTime('" + ISO8601.format(pastTime)+ "')]");

      stringBuffer.append(" order by @exo:dateCreated descending");
      String pathQuery = stringBuffer.toString();
      Query query = qm.createQuery(pathQuery, Query.XPATH);
      QueryResult result = query.execute();
      //end query

      NodeIterator iter = result.getNodes();
      IntranetSpace space;
      while (iter.hasNext()) {
        Node spaceNode = iter.nextNode();
        space = new IntranetSpace();
        space.setDisplayName(spaceNode.getProperty("soc:displayName").getString());
        space.setDescription(spaceNode.getProperty("soc:description").getString());
        space.setUrl(spaceNode.getProperty("soc:url").getString());
        space.setCreatedDate(spaceNode.getProperty("exo:dateCreated").getDate().getTime());
        space.setRegistration(spaceNode.getProperty("soc:registration").getString());
        space.setVisibility(spaceNode.getProperty("soc:visibility").getString());
        
        //check user is member of space
        String groupId= spaceNode.getProperty("soc:groupId").getString();
        if(allGroupAndMembershipOfUser.contains(groupId)){
          space.setIsMember(true);
        }
        
        //check user is invitedUsers of space
        if(spaceNode.hasProperty("soc:invitedMembersId")){
          String[] invitedUsers = convertValuesToStrings(spaceNode.getProperty("soc:invitedMembersId").getValues());
          for (String user : invitedUsers) {
            if (user.equals(userName)){
              space.setIsInvitedUser(true);
              break;
            }
          }
        }
        
        //check user is pendingUsers of space
        if(spaceNode.hasProperty("soc:pendingMembersId")){
          String[] pendingUsers = convertValuesToStrings(spaceNode.getProperty("soc:pendingMembersId").getValues());
          for (String user : pendingUsers) {
            if (user.equals(userName)){
              space.setIsPendingUser(true);
              break;
            }
          }
        }

        //get space avatar
        String avartarUrl = null;
        PortalContainer portalContainer = PortalContainer.getInstance();
        IdentityManager iManager = (IdentityManager) portalContainer.getComponentInstanceOfType(IdentityManager.class);
        Identity spaceIdentity = iManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, spaceNode.getProperty("soc:name").getString(), true);
        Profile profile = spaceIdentity.getProfile();
        avartarUrl =profile.getAvatarUrl();
        space.setAvatarURL(avartarUrl);
        
        listSpaces.add(space);
        
      }
      return listSpaces;
    } catch (Exception e) {
      log.error("Can not get Latest Created Space", e);
      return null;
    } finally {
      sProvider.close();
    }

  }
  
  private Session getSession(SessionProvider sessionProvider) throws Exception {
	ManageableRepository currentRepo = this._repoService.getCurrentRepository();
	return sessionProvider.getSession(SOCIAL_WORKSPACE, currentRepo);
  }
 
  
  private String [] convertValuesToStrings(Value[] values) throws Exception {
    if(values.length == 1) return new String[]{values[0].getString()};
    String[] strArrays = new String[values.length];
    for(int i = 0; i < values.length; ++i) {
      strArrays[i] = values[i].getString();
    }
    return strArrays;
  }

}