/*
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
 */
package org.exoplatform.platform.portlet;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.social.common.UserListAccess;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.core.space.SpaceException;
import org.exoplatform.social.core.space.SpaceUtils;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * {@link UISpaceSummaryInfoPortlet} used as a portlet displaying space summary. <br />
 * Created by The eXo Platform MEA
 * 
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar Chattouna</a>
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platform/portlet/UISpaceSummaryInfoPortlet.gtmpl")
public class UISpaceSummaryInfoPortlet extends UIPortletApplication {

  private Space space;
  private boolean isSpace = true;
  private List<User> administratorsList;
  private final UIPageIterator iteratorAdministrators;
  private final String ITERATOR_ADMINISTRATORS_ID = "UIIteratorLeader";
  private final Integer ITEMS_PER_PAGE = 5;
  private Log log = ExoLogger.getLogger(this.getClass());

  public UISpaceSummaryInfoPortlet() throws Exception {
    iteratorAdministrators = createUIComponent(UIPageIterator.class, null, ITERATOR_ADMINISTRATORS_ID);
    addChild(iteratorAdministrators);
    space = getApplicationComponent(SpaceService.class).getSpaceByUrl(SpaceUtils.getSpaceUrl());
    if (space == null) {
      isSpace = false;
      log.error("No Space Found. Please check the portlet's SPACE_URL preference");
    }
  }

  /**
   * Gets space display name.
   * 
   * @return space display name or an empty String
   */
  public String getSpaceDisplayName() {
    if (space != null) {
      return space.getDisplayName();
    } else {
      return "";
    }
  }

  /**
   * Gets space description.
   * 
   * @return space description or an empty String
   */
  public String getSpaceDescription() {
    if (space != null) {
      return space.getDescription();
    } else {
      return "";
    }
  }

  /**
   * Gets space image source url.
   * 
   * @return image source url or an empty String
   * @throws Exception 
   */
  public String getSpaceImageSource() throws Exception {
    String imgSrc = null;
    if (space != null) {
      imgSrc = space.getAvatarUrl();
//      if(imgSrc == null){
//        imgSrc = LinkProvider.buildAvatarImageUri(space.getAvatarAttachment());
//      }
      return imgSrc;
    }
    return "";
  }

  /**
   * Gets the number of members of a space.
   * 
   * @return number of members
   */
  public int getSpaceMembersNumber() throws SpaceException {
    return SpaceUtils.countMembers(space);
  }

  /**
   * Gets the full URL of a space.
   * 
   * @return the full URL
   */
  public String getSpaceFullURL() {
    return Util.getPortalRequestContext().getPortalURI() + space.getUrl();
  }

  /**
   * gets current URI
   * 
   * @return current URI
   */
  public String getURI() {
    String nodePath = Util.getPortalRequestContext().getNodePath();
    String uriPath = Util.getPortalRequestContext().getRequestURI();
    return uriPath.replaceAll(nodePath, "");
  }

  /**
   * gets identity by userId
   * 
   * @param userId
   * @return user identity
   */
  public Identity getIdentity(String userId) {
    return getApplicationComponent(IdentityManager.class).getOrCreateIdentity("organization", userId, true);
  }

  /**
   * gets the administrators list
   * 
   * @return administrators list
   * @throws Exception
   */

  @SuppressWarnings("unchecked")
  public List<User> getAdministrators() throws Exception {
    initAdministrators();
    int currentPage = iteratorAdministrators.getCurrentPage();
    LazyPageList<User> pageList = new LazyPageList<User>(new UserListAccess(administratorsList), ITEMS_PER_PAGE);
    iteratorAdministrators.setPageList(pageList);
    int pageCount = iteratorAdministrators.getAvailablePage();
    if (pageCount >= currentPage) {
      iteratorAdministrators.setCurrentPage(currentPage);
    } else if (pageCount < currentPage) {
      iteratorAdministrators.setCurrentPage(currentPage - 1);
    }
    return iteratorAdministrators.getCurrentPageData();
  }

  /**
   * initialize administrators, called from {@link #getAdministratorsList()}
   * 
   * @throws Exception
   */
  public void initAdministrators() throws Exception {
    administratorsList = new ArrayList<User>();
    OrganizationService orgSrc = getApplicationComponent(OrganizationService.class);
    UserHandler userHandler = orgSrc.getUserHandler();
    String[] managers = space.getManagers();
    if (managers != null) {
      for (String name : managers) {
        administratorsList.add(userHandler.findUserByName(name));
      }
    }
  }

  public List<User> getAdministratorsList() throws Exception {
    initAdministrators();
    return administratorsList;
  }

  public void setAdministratorsList(List<User> administratorsList) {
    this.administratorsList = administratorsList;
  }

  public UIPageIterator getIteratorAdministrators() {
    return iteratorAdministrators;
  }

  public boolean isSpace() {
    return isSpace;
  }
}