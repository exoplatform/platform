/***************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
 ***************************************************************************/
package org.exoplatform.platform.gadget.services.newspaces;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.platform.gadget.services.newspaces.IntranetSpaceService;
import org.exoplatform.platform.gadget.services.newspaces.IntranetSpace;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="tung.do@exoplatform.com">Do Thanh Tung </a>
 * @version $Revision$
 */

@Path("intranetNewSpaceService")
public class NewSpaceRestService implements ResourceContainer {

  private static final Log   log                 = ExoLogger.getLogger(NewSpaceRestService.class);
  
  /** The Constant REGISTRATION is OPEN. */
  public final static String REGISTRATION_OPEN = "open";
  
  /** The Constant  REGISTRATION is VALIDATION. */
  public final static String REGISTRATION_VALIDATION = "validation";
  
  /** The Constant  REGISTRATION is CLOSE. */
  public final static String REGISTRATION_CLOSE = "close";
  
  /** The Constant VISIBILITY is PRIVATE. */
  public final static String VISIBILITY_PRIVATE = "private"; //visible
  
  /** The Constant VISIBILITY is HIDDEN. */
  public final static String VISIBILITY_HIDDEN = "hidden";

  public NewSpaceRestService() { }

/**
 * Get latest created spaces in maxtime days recently
 * @param maxtime
 * @return
 */
   @GET
   @Path("/space/latestCreatedSpace/{maxtime}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response latestCreatedSocialSpace(@PathParam("maxtime") int maxtime) {
     CacheControl cacheControl = new CacheControl();
     cacheControl.setNoCache(true);
     cacheControl.setNoStore(true);
     IntranetSpaceService intranetSpaceService = (IntranetSpaceService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IntranetSpaceService.class);
     List<IntranetSpace> listIntranetSpace= new ArrayList<IntranetSpace>(); 
     List<IntranetSpace> listResponseIntranetSpace= new ArrayList<IntranetSpace>(); 
     
     List<String> allGroupAndMembershipOfUser = getAllGroupAndMembershipOfUser();
     try
     {
       listIntranetSpace = intranetSpaceService.getLatestCreatedSpace(maxtime, allGroupAndMembershipOfUser);
       for (IntranetSpace space : listIntranetSpace) {
           if(space.getIsMember() || space.getIsInvitedUser() || space.getIsPendingUser()){
             listResponseIntranetSpace.add(space);
           }
           else if((space.getVisibility().equalsIgnoreCase(NewSpaceRestService.VISIBILITY_PRIVATE)) && 
               ((space.getRegistration().equalsIgnoreCase(NewSpaceRestService.REGISTRATION_OPEN)) || (space.getRegistration().equalsIgnoreCase(NewSpaceRestService.REGISTRATION_VALIDATION)))){
             //if space is Visible AND (Open OR Validation)
             listResponseIntranetSpace.add(space);
           }
       }
     }
     catch (Exception e)
     {
       log.error("has not any space", e);
     }
     List<Object> dataIntranetSpace = new ArrayList<Object>();

     dataIntranetSpace.add(listResponseIntranetSpace);
     MessageBean data = new MessageBean();
     data.setData(dataIntranetSpace);
     return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
   }

   public class MessageBean {
     private List<Object> data;

     public void setData(List<Object> list) {
       this.data = list;
     }
     public List<Object> getData() {
       return data;
     }

   }
   
   private List<String> getAllGroupAndMembershipOfUser() {
     List<String> listOfUser = new ArrayList<String>();
     try {
       Identity identity = ConversationState.getCurrent().getIdentity();
       listOfUser.add(identity.getUserId());
       Set<String> list = new HashSet<String>();
       list.addAll(identity.getGroups());
       for (MembershipEntry membership : identity.getMemberships()) {
         String value = membership.getGroup();
         list.add(value); // its groups
         value = membership.getMembershipType() + ":" + value;
         list.add(value);
       }
       listOfUser.addAll(list);
     } catch (Exception e) {
       log.warn("Failed to add all info of user.");
     }
     return listOfUser;
   }

}

