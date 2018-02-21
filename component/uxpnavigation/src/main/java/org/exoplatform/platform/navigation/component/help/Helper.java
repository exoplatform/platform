/**
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.platform.navigation.component.help;

import org.exoplatform.platform.navigation.component.utils.DashboardUtils;
import org.exoplatform.platform.navigation.component.utils.NavigationUtils;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.webui.Utils;

/**
 * @author <a href="kmenzli@exoplatform.com">Kmenzli</a>
 */

public class Helper {

    private static final Log LOG = ExoLogger.getExoLogger(Helper.class);
    public static final String DEFAULT_HELP_ID = "default";

    public static boolean present (String theString) {
        boolean present = false;
        if (theString != null && theString.length()!=0) {
            present = true;
        }
        return present;
    }

    public static String getCurrentNavigation(SpaceService spaceService){
        try {
            String nav=Util.getUIPortal().getNavPath().getName();
            String url = Util.getPortalRequestContext().getRequest().getRequestURL().toString();
            if((url.contains("/:spaces:"))||(url.contains("/spaces/")))   {
                if(url.contains("documents"))  {
                    return "space:document";
                }
                else if(url.contains("wiki")) {
                    return "space:wiki";
                }
                else if(url.contains("tasks")) {
                    return "space:tasks";
                }
                else if((url.contains("answer"))||(url.contains("faq")) || (url.contains("poll"))) {
                    return "space:faq_annswer";
                }
                else if(url.contains("calendar")) {
                    return "space:calendar";
                }
                else if(url.contains("forum")) {
                    return "space:forum";
                }
                else if(nav.equals("settings")) {
                    return "space:manager";
                }
                else {
                        String spaceUrl=getSelectedPageNode().getURI();
                        Space space = spaceService.getSpaceByUrl(spaceUrl);
                        if (space != null) {
                            if(space.getPrettyName().equals(nav)){
                                return "space:activity_stream";
                            }
                        } else {
                            return DEFAULT_HELP_ID;
                        }
                    }
                }
            else if(url.contains("wiki")&&(isProfileOwner())){
                return "personnal:wiki";
            }
            else if((url.contains("profile"))&&(isProfileOwner())){
                return "personnal:profile";
            }
            else if((url.contains("connections"))&&(isProfileOwner())){
                return "personnal:connections";
            }
            else if((url.contains("activities"))&&(isProfileOwner())){
              return "personnal:activities";
            }
            else if((url.contains("notifications"))&&(isProfileOwner())){
              return "personnal:notifications";
            }
            else if(url.contains("all-spaces")){
                return "personnal:all-spaces";
            }
            else if((nav!=null)&&(nav.equals("home"))){
             if((SiteKey.portal(getCurrentPortal())!=null) &&(SiteKey.portal(getCurrentPortal()).getName().equals("intranet"))){
                    return "Company Context Home";
              }
            }
            else if((nav!=null)&&(nav.equals("calendar"))){
                return "Company Context Calendar";
            }
            else if((nav!=null)&&(nav.equals("forum"))){
                return "Company Context Forum";
            }
            else if((nav!=null)&&(nav.equals("wiki"))){
                return "Company Context Wiki";
            }
            else if((nav!=null)&&(nav.equals("tasks"))){
                return "Company Context Tasks";
            }
            else if((nav!=null)&&(nav.equals("documents"))){
                return "Company Context Documents";
            }
            else if((nav!=null)&&((nav.equals("FAQ"))||(nav.equals("answers")))){
                return "Company Context FAQ:Answers";
            }
            else if((nav!=null)&&((nav.equals("connexions")))){
                return "Company Context Connections";
            }
            SiteType siteType = Util.getUIPortal().getSiteType();
            if (siteType != null && siteType.equals(SiteType.USER)) {
              String dashboardUrl = DashboardUtils.getDashboardURL();
              if(url.contains(dashboardUrl.substring(0, dashboardUrl.lastIndexOf("/")))){
                  return "dashboard";
              }
            }
            return DEFAULT_HELP_ID;
        } catch (Exception E) {
            LOG.warn("Can not load the currentNavigation ", E);
            return null;
        }
    }
    public static boolean isProfileOwner() {
        return Utils.getViewerRemoteId().equals(NavigationUtils.getCurrentUser());
    }
    public static String getCurrentPortal()
    {
        return Util.getPortalRequestContext().getPortalOwner();
    }
    public static UserNode getSelectedPageNode() throws Exception {
        return Util.getUIPortal().getSelectedUserNode();
    }
}
