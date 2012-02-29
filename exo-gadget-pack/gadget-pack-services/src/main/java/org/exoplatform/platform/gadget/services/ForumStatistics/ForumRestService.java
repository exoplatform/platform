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
package org.exoplatform.platform.gadget.services.ForumStatistics;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.forum.service.ForumStatistic;
import org.exoplatform.platform.gadget.services.ForumStatistics.ForumsIntranetService;
import org.exoplatform.platform.gadget.services.ForumStatistics.ForumsWeeklyStatisticService;
import org.exoplatform.platform.gadget.services.ForumStatistics.ForumsWeeklyStatistic;
import org.exoplatform.platform.gadget.services.ForumStatistics.TopicBean;
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
 * REST service that serve Forums Data.
 * 
 * @author <a href="tung.do@exoplatform.com">Do Thanh Tung </a>
 * @version 1.0
 */

@Path("forumsService")
public class ForumRestService implements ResourceContainer {
  
  private static final Log LOG= ExoLogger.getLogger(ForumRestService.class);
                                                                       
   public ForumRestService() { }
   
   /**
    * Get Forums Global statistic as JSON format.
    * 
    * REST service: URL is /forumsService/forums/statistic
    * 
    * @return global Forums Statistis as JSON data format.
    */
  @GET
  @Path("/forums/statistic/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response forumsStatistic() {
     CacheControl cacheControl = new CacheControl();
     cacheControl.setNoCache(true);
     cacheControl.setNoStore(true);
     ForumService forumService = (ForumService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ForumService.class);
     ForumStatistic forumStatistic = new ForumStatistic();   
     try
     {
       forumStatistic= forumService.getForumStatistic();
     }
     catch (Exception e)
     {
       LOG.error("forumStatistic not found", e);
     }
     List<Object> dataForumStatistic = new ArrayList<Object>();

     dataForumStatistic.add(forumStatistic);
     MessageBean data = new MessageBean();
     data.setData(dataForumStatistic);
     return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
  }
  
  /**
   * Get Forums Weekly Statistics as JSON data format.
   * 
   * REST service URL: /forumsService/forums/weeklystatistic
   * 
   * @return Forums weekly statistic as JSON data format.
   */
  @GET
  @Path("/forums/weeklystatistic/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response forumsWeeklyStatistic() {
     CacheControl cacheControl = new CacheControl();
     cacheControl.setNoCache(true);
     cacheControl.setNoStore(true);
     ForumsWeeklyStatisticService forumsWeeklyStatisticService = (ForumsWeeklyStatisticService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ForumsWeeklyStatisticService.class);
     List<ForumsWeeklyStatistic> listForumsWeeklyStatistic= new ArrayList<ForumsWeeklyStatistic>(); 
     try
     {
       listForumsWeeklyStatistic= forumsWeeklyStatisticService.getAllForumsWeeklyStatistic();
     }
     catch (Exception e)
     {
       LOG.error("forumWeeklyStatistic not found", e);
     }
     List<Object> dataForumStatistic = new ArrayList<Object>();

     dataForumStatistic.add(listForumsWeeklyStatistic);
     MessageBean data = new MessageBean();
     data.setData(dataForumStatistic);
     return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
  }
  
  /**
   * Get top {maxcount} of vote rating forums topics
   * @param maxcount is maximum of vote rating forums topics that will be retrieved
   * @return top {maxcount} of vote rating forums topics as JSON format
   */
  @GET
  @Path("/forums/toprate/{maxcount}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response forumsTopRated(@PathParam("maxcount") int maxcount) {
    CacheControl cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    
    ForumsIntranetService intranetService = (ForumsIntranetService) 
                                               ExoContainerContext.getCurrentContainer()
                                                 .getComponentInstanceOfType(ForumsIntranetService.class);
    try {
      List<TopicBean> beanList = intranetService.getTopRateTopicByUser(getAllGroupAndMembershipOfUser(), maxcount);
     
      List<Object> listData = new ArrayList<Object>(); 
      listData.add(beanList);
      MessageBean data = new MessageBean();
      data.setData(listData);
      return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    } catch (Exception e) {
      LOG.debug("Failed to get top voted rating topics");
    }
    return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
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
      LOG.warn("Failed to add all info of user.");
    }
    return listOfUser;
  }

}

