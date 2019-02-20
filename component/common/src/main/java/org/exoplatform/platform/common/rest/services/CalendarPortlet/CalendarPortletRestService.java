/*
 * Copyright (C) 2019 eXo Platform SAS.
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
package org.exoplatform.platform.common.rest.services.CalendarPortlet;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.exoplatform.calendar.service.*;
import org.exoplatform.calendar.ws.CalendarRestApi;
import org.exoplatform.calendar.ws.bean.CalendarResource;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.rest.api.EntityBuilder;
import org.exoplatform.social.rest.api.RestUtils;

import org.json.JSONObject;

import java.util.*;

@Path("portlet/homePage/calendar")
@Produces(MediaType.APPLICATION_JSON)
public class CalendarPortletRestService implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(CalendarPortletRestService.class);
    private final static String HOME_PAGE_CALENDAR_SETTINGS = "IntranetHomePageCalendarSettings";

    private CalendarService calendarService;
    private SettingService settingService;
    private OrganizationService organizationService;

    public CalendarPortletRestService(CalendarService calendarService, SettingService settingService,
                                      OrganizationService organizationService) {
        this.calendarService = calendarService;
        this.settingService = settingService;
        this.organizationService = organizationService;
    }

    /**
     * Get calendar portlet settings
     *
     */
    @GET
    @Path("settings")
    @RolesAllowed("users")
    @ApiOperation(value = "Gets calendar portlet settings",
            httpMethod = "GET",
            response = Response.class,
            notes = "This returns calendar portlet settings")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Request fulfilled")})
    public Response getDispNonDispCalendars(@Context UriInfo uriInfo) throws Exception {
        String username = ConversationState.getCurrent().getIdentity().getUserId();
        String defaultCalendarLabel = "Default";
        Iterator itr1 = getAllCal(username).iterator();
        String[] nonDisplayedCalendarList = getNonDisplayedCalendarIds();
        List<CalendarResource> calendarDisplayedList = new ArrayList<>();
        List<CalendarResource> calendarNonDisplayedList = new ArrayList<>();
        while (itr1.hasNext()) {
            org.exoplatform.calendar.service.Calendar c = (org.exoplatform.calendar.service.Calendar) itr1.next();
            if(c.getGroups()==null) {
                if (c.getId().equals(Utils.getDefaultCalendarId(username)) && c.getName().equals(calendarService.getDefaultCalendarName())) {
                    c.setName(defaultCalendarLabel);
                }
            }
            if (containsCalendarId(nonDisplayedCalendarList, c.getId())) {
                calendarNonDisplayedList.add(new CalendarResource(c, getBasePath(uriInfo)));
            } else {
                calendarDisplayedList.add(new CalendarResource(c, getBasePath(uriInfo)));
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("allDisplayedCals", calendarDisplayedList);
        jsonObject.put("nonDisplayedCals", calendarNonDisplayedList);

        return EntityBuilder.getResponse(jsonObject.toString(), uriInfo, RestUtils.getJsonMediaType(), Response.Status.OK);
    }

    private boolean containsCalendarId(String[] calendarIds, String id) {
        int i = 0;
        if (calendarIds != null) {
            while (i < calendarIds.length) {
                if ((calendarIds[i] != null) && (calendarIds[i].equals(id))) {
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    /**
     * Posts calendar portlet settings
     *
     */
    @POST
    @Path("settings")
    @RolesAllowed("users")
    @ApiOperation(value = "sets calendar portlet settings",
            httpMethod = "POST",
            response = Response.class,
            notes = "This sets calendar portlet settings")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Request fulfilled")})
    public Response saveSettings(@Context UriInfo uriInfo, String calIds) throws Exception {
        settingService.remove(org.exoplatform.commons.api.settings.data.Context.USER, Scope.APPLICATION, HOME_PAGE_CALENDAR_SETTINGS);
        settingService.set(org.exoplatform.commons.api.settings.data.Context.USER, Scope.APPLICATION, HOME_PAGE_CALENDAR_SETTINGS, SettingValue.create("NonDisplayedCalendar:" + calIds));

        return EntityBuilder.getResponse("", uriInfo, RestUtils.getJsonMediaType(), Response.Status.OK);
    }

    private List getAllCal(String username) throws Exception {
        List<org.exoplatform.calendar.service.Calendar> calList = calendarService.getUserCalendars(username, true);
        List<GroupCalendarData> lgcd = calendarService.getGroupCalendars(getUserGroups(username), true, username);
        List<String> calIds = new ArrayList<String>();
        for (GroupCalendarData g : lgcd) {
            for (org.exoplatform.calendar.service.Calendar c : g.getCalendars()) {
                if (!calIds.contains(c.getId())) {
                    calIds.add(c.getId());
                    calList.add(c);
                }
            }
        }
        return calList;
    }

    private String[] getNonDisplayedCalendarIds() {
        SettingValue settingNode = settingService.get(org.exoplatform.commons.api.settings.data.Context.USER, Scope.APPLICATION, HOME_PAGE_CALENDAR_SETTINGS);
        if ((settingNode != null) && (settingNode.getValue().toString().split(":").length == 2)) {
            return settingNode.getValue().toString().split(":")[1].split(",");
        }
        return new String[]{};
    }

    private String getBasePath(UriInfo uriInfo) {
        StringBuilder path = new StringBuilder(uriInfo.getBaseUri().toString());
        path.append(CalendarRestApi.CAL_BASE_URI);
        return path.toString();
    }

    private String[] getUserGroups(String username) throws Exception {
        String [] groupsList;
        Object[] objs = organizationService.getGroupHandler().findGroupsOfUser(username).toArray();
        groupsList = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            groupsList[i] = ((Group) objs[i]).getId();
        }
        return groupsList;
    }
}
