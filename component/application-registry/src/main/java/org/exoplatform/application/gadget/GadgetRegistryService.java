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
package org.exoplatform.application.gadget;

import java.util.Comparator;
import java.util.List;

/**
 * This service is used to register the gadget with portal. Developer uses this
 * service to manage list of gadgets.
 * <p>
 * Created by The eXo Platform SAS<br/> Jun 18, 2008<br/>
 * </p>
 */
public interface GadgetRegistryService {

  /**
   * Gets the gadget from database by name.
   * 
   * @param name the name of gadget
   * @return the gadget or null if not found
   * @throws Exception
   */
  public Gadget getGadget(String name) throws Exception;

  /**
   * Gets all of available gadgets from the database.
   * 
   * @return a list of gadgets
   * @throws Exception
   */
  public List<Gadget> getAllGadgets() throws Exception;

  /**
   * Gets all of available gadgets from the database.</br> The list of gadgets
   * are sorted.
   * 
   * @param sortComparator The comparator is used to control the order of
   *          gadgets
   * @return a list of gadgets
   * @throws Exception
   */
  public List<Gadget> getAllGadgets(Comparator<Gadget> sortComparator) throws Exception;

  /**
   * Adds the gadget to the database. If the gadget is existing, it will be
   * updated.
   * 
   * @param app the gadget that is saved to database
   * @throws Exception
   */
  public void saveGadget(Gadget gadget) throws Exception;

  /**
   * Removes the gadget from the database.
   * 
   * @param id the id of gadget
   * @throws Exception
   */
  public void removeGadget(String name) throws Exception;

  public boolean isGadgetDeveloper(String username);

  public String getCountry();

  public String getLanguage();

  public String getModuleId();

  public String getHostName();
}
