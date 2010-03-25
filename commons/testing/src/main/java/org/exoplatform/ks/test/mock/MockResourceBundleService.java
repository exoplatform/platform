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
package org.exoplatform.ks.test.mock;

import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;
import org.exoplatform.services.resources.ResourceBundleService;

/**
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
public class MockResourceBundleService implements ResourceBundleService {

  public ResourceBundleData createResourceBundleDataInstance() {

    return null;
  }

  public PageList<ResourceBundleData> findResourceDescriptions(Query q) throws Exception {

    return null;
  }

  public ResourceBundle getResourceBundle(String name, Locale locale) {

    return null;
  }

  public ResourceBundle getResourceBundle(String[] name, Locale locale) {

    return null;
  }

  public ResourceBundle getResourceBundle(String name, Locale locale, ClassLoader cl) {

    return null;
  }

  public ResourceBundle getResourceBundle(String[] name, Locale locale, ClassLoader cl) {

    return null;
  }

  public ResourceBundleData getResourceBundleData(String id) throws Exception {

    return null;
  }

  public String[] getSharedResourceBundleNames() {

    return null;
  }

  public ResourceBundleData removeResourceBundleData(String id) throws Exception {

    return null;
  }

  public void saveResourceBundle(ResourceBundleData data) throws Exception {
  }

}
