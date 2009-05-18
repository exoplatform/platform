/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.application.gadget.jcr;

import java.util.*;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.ValueParam;

/**
 * Created by The eXo Platform SAS Author : Pham Thanh Tung
 * thanhtungty@gmail.com Jun 18, 2008
 */
public class GadgetRegistryServiceImpl implements GadgetRegistryService {

  private static final String PATH                    = RegistryService.EXO_SERVICES + "/Gadgets";

  private static final String DEFAULT_DEVELOPER_GROUP = "/platform/administrators";

  private RegistryService     regService_;

  private DataMapper          mapper_                 = new DataMapper();

  private OrganizationService orgService;

  private String              gadgetDeveloperGroup    = null;

  private String              country;
  
  private String              language;
  
  private String              moduleId;
  
  private String              hostName;

  public GadgetRegistryServiceImpl(InitParams params, RegistryService service) throws Exception {
    regService_ = service;

    if (params != null) {
      PropertiesParam properties = params.getPropertiesParam("developerInfo");
      if (properties != null) {
        gadgetDeveloperGroup = properties.getProperty("developer.group");
      }
    }

    ValueParam gadgetCountry = params.getValueParam("gadgets.country");
    if (gadgetCountry != null) {
      country = gadgetCountry.getValue();
    }
    
    ValueParam gadgetLanguage = params.getValueParam("gadgets.language");
    if (gadgetLanguage != null) {
      language = gadgetLanguage.getValue();
    }
    
    ValueParam gadgetModuleId = params.getValueParam("gadgets.moduleId");
    if (gadgetModuleId != null) {
      moduleId = gadgetModuleId.getValue();
    }
    
    ValueParam gadgetHostName = params.getValueParam("gadgets.hostName");
    if (gadgetHostName != null) {
      hostName = gadgetHostName.getValue();
    }
    
    if (gadgetDeveloperGroup == null)
      gadgetDeveloperGroup = DEFAULT_DEVELOPER_GROUP;
  }

  public Gadget getGadget(String name) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    RegistryEntry entry;
    try {
      entry = regService_.getEntry(sessionProvider, PATH + "/" + name);
    } catch (PathNotFoundException pnfe) {
      return null;
    } finally {
      sessionProvider.close();
    }
    Gadget gadget = mapper_.toApplciation(entry.getDocument());
    return gadget;
  }

  public List<Gadget> getAllGadgets() throws Exception {
    return getAllGadgets(null);
  }

  public List<Gadget> getAllGadgets(Comparator<Gadget> sortComparator) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      Node regNode = regService_.getRegistry(sessionProvider).getNode();
      if (!regNode.hasNode(PATH)) {
        return new ArrayList<Gadget>();
      }
      NodeIterator itr = regNode.getNode(PATH).getNodes();
      List<Gadget> gadgets = new ArrayList<Gadget>();
      while (itr.hasNext()) {
        String entryPath = itr.nextNode().getPath().substring(regNode.getPath().length() + 1);
        RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath);
        Gadget gadget = mapper_.toApplciation(entry.getDocument());
        gadgets.add(gadget);
      }
      if (sortComparator != null)
        Collections.sort(gadgets, sortComparator);
      return gadgets;
    } finally {
      sessionProvider.close();
    }
  }

  public void saveGadget(Gadget gadget) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    RegistryEntry entry;
    try {
      entry = regService_.getEntry(sessionProvider, PATH + "/" + gadget.getName());
      mapper_.map(entry.getDocument(), gadget);
      regService_.recreateEntry(sessionProvider, PATH, entry);
    } catch (PathNotFoundException pnfe) {
      entry = new RegistryEntry(gadget.getName());
      mapper_.map(entry.getDocument(), gadget);
      regService_.createEntry(sessionProvider, PATH, entry);
    } finally {
      sessionProvider.close();
    }
  }

  public void removeGadget(String name) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      regService_.removeEntry(sessionProvider, PATH + "/" + name);
    } finally {
      sessionProvider.close();
    }
  }

  private OrganizationService getOrgService() {
    if (orgService == null) {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      orgService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
    }
    return orgService;
  }

  /**
   * return true is the user is defined as a gadget developer
   * 
   * @param username
   */
  public boolean isGadgetDeveloper(String username) {
    try {
      OrganizationService orgService = getOrgService();

      MembershipHandler memberShipHandler = orgService.getMembershipHandler();
      Collection<Membership> memberships = memberShipHandler.findMembershipsByUserAndGroup(username,
                                                                                           gadgetDeveloperGroup);
      if (memberships.size() > 0)
        return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;

  }

  public String getCountry() {
    return country;
  }

  public String getLanguage() {
    return language;
  }

  public String getModuleId() {
    return moduleId;
  }

  public String getHostName() {
    return hostName;
  }

}
