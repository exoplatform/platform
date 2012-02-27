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
package org.exoplatform.platform.gadget.services.LoginHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;


/**
 * Created by The eXo Platform SARL Author : Tung Vu Minh tungvm@exoplatform.com
 * Apr 21, 2011 6:19:21 PM
 */

@Path("loginhistory")
public class LoginHistoryRestService implements ResourceContainer {
	private static final Log log = ExoLogger.getLogger(LoginHistoryRestService.class);

	private static final CacheControl cacheControl;
	static {
		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
		cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		cacheControl.setNoStore(true);
	}
	
	/**
	 * Get user login history
	 * 
	 * REST service URL: /loginhistory/{userId}/{fromTime}/{toTime}
	 * 
	 * @return: Login history entries from {fromTime} to {toTime} (in JSON data format) of user {userId}
	 */		
	@GET
	@Path("/loginhistory/{userId}/{fromTime}/{toTime}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginhistory(@PathParam("userId") String userId, @PathParam("fromTime") Long fromTime, @PathParam("toTime") Long toTime) throws Exception {
		try {
			LoginHistoryService _loginHistoryService = (LoginHistoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LoginHistoryService.class);		
			List<LoginHistoryBean> loginHis = _loginHistoryService.getLoginHistory(userId, fromTime, toTime);
									
			List<Object> loginHisData = new ArrayList<Object>();
			loginHisData.add(loginHis.size());
			loginHisData.add(loginHis);
			
			MessageBean data = new MessageBean();
			data.setData(loginHisData);
			return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		}
		catch (Exception e) {
			log.debug("Error in get user login history REST service: " + e.getMessage(), e);
			return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
		}
	}

	/**
	 * Get login count statistic in a week
	 * 
	 * REST service URL: /loginhistory/weekstats/{userId}/{week}
	 * 
	 * @return: List of login count per days in week {week} (in JSON data format) of user {userId}
	 */		
	@GET
	@Path("/weekstats/{userId}/{week}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response weekstats(@PathParam("userId") String userId, @PathParam("week") String week) throws Exception {
		try {
			LoginHistoryService _loginHistoryService = (LoginHistoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LoginHistoryService.class);		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<LoginCounterBean> loginCounts = _loginHistoryService.getLoginCountPerDaysInWeek(userId, sdf.parse(week).getTime());
						
			List<Object> loginCountsData = new ArrayList<Object>();
			
			loginCountsData.add(loginCounts.size());
			loginCountsData.add(loginCounts);
			
			MessageBean data = new MessageBean();
			data.setData(loginCountsData);
			return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		}
		catch (Exception e) {
			log.debug("Error in weekstats REST service: " + e.getMessage(), e);
			return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
		}
	}

	/**
	 * Get login count statistic in months
	 * 
	 * REST service URL: /loginhistory/monthstats/{userId}/{fromMonth/{numOfMonth}}
	 * 
	 * @return: List of login count per weeks in {numOfMonths} months start from {fromMonth} (in JSON data format) of user {userId}
	 */		
	@GET
	@Path("/monthstats/{userId}/{fromMonth}/{numOfMonths}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response monthstats(@PathParam("userId") String userId, @PathParam("fromMonth") String fromMonth, @PathParam("numOfMonths") int numOfMonths) throws Exception {
		try {
			LoginHistoryService _loginHistoryService = (LoginHistoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LoginHistoryService.class);		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<LoginCounterBean> loginCounts = _loginHistoryService.getLoginCountPerWeeksInMonths(userId, sdf.parse(fromMonth).getTime(), numOfMonths);
						
			List<Object> loginCountsData = new ArrayList<Object>();
			
			loginCountsData.add(loginCounts.size());
			loginCountsData.add(loginCounts);
			
			MessageBean data = new MessageBean();
			data.setData(loginCountsData);
			return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		}
		catch (Exception e) {
			log.debug("Error in monthstats REST service: " + e.getMessage(), e);
			return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
		}
	}

	/**
	 * Get login count statistic in a year
	 * 
	 * REST service URL: /loginhistory/yearstats/{userId}/{year}
	 * 
	 * @return: List of login count per months in year {year} (in JSON data format) of user {userId}
	 */		
	@GET
	@Path("/yearstats/{userId}/{year}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response yearstats(@PathParam("userId") String userId, @PathParam("year") String year) throws Exception {
		try {
			LoginHistoryService _loginHistoryService = (LoginHistoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LoginHistoryService.class);		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<LoginCounterBean> loginCounts = _loginHistoryService.getLoginCountPerMonthsInYear(userId, sdf.parse(year).getTime());
						
			List<Object> loginCountsData = new ArrayList<Object>();
			
			loginCountsData.add(loginCounts.size());
			loginCountsData.add(loginCounts);
			
			MessageBean data = new MessageBean();
			data.setData(loginCountsData);
			return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		}
		catch (Exception e) {
			log.debug("Error in yearstats REST service: " + e.getMessage(), e);
			return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
		}
	}
		
	/**
	 * Get last {numItems} login history entries
	 * 
	 * REST service URL: /loginhistory/lastlogins/{numItems}/{userIdFilter}
	 * 
	 * @return: The list of last {numItems} login history entries (filtered by {userIdFilter}) in JSON data format
	 */		
	@GET
	@Path("/lastlogins/{numItems}/{userIdFilter}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lastlogins(@PathParam("numItems") int numItems, @PathParam("userIdFilter") String userIdFilter) throws Exception {
		try {
			LoginHistoryService _loginHistoryService = (LoginHistoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LoginHistoryService.class);		
			List lastLogins = _loginHistoryService.getLastLogins(numItems, userIdFilter);
			
			MessageBean data = new MessageBean();
			data.setData(lastLogins);
			return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		}
		catch (Exception e) {
			log.debug("Error in get last logins REST service: " + e.getMessage(), e);
			return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
		}
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
}
