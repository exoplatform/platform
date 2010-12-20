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
package org.exoplatform.platform.migration.common.handler.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.platform.migration.common.constants.Constants;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;
import org.exoplatform.services.resources.ResourceBundleService;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform haikel.thamri@exoplatform.com 21 juil. 2010
 */
public class ResourceBundleHandler extends ComponentHandler {

  public ResourceBundleHandler(InitParams initParams) {
    super.setTargetComponentName(ResourceBundleService.class.getName());
  }

  public Entry invoke(Component component, ExoContainer container) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(out);

    writeResourceBundles(component, zos, container);

    Configuration configuration = new Configuration();
    configuration.addComponent(component);
    zos.putNextEntry(new ZipEntry(component.getKey() + ".xml"));
    zos.write(toXML(configuration));
    zos.closeEntry();
    zos.close();

    Entry entry = new Entry(component.getKey());
    entry.setType(EntryType.ZIP);
    entry.setContent(out.toByteArray());
    return entry;
  }

  private void writeResourceBundles(Component component, ZipOutputStream zos, ExoContainer container) throws Exception {
    ResourceBundleService resourceBundleService = (ResourceBundleService) container.getComponentInstanceOfType(ResourceBundleService.class);
    Query query_ = new Query(null, null);
    List<ResourceBundleData> dataList = resourceBundleService.findResourceDescriptions(query_).getAll();
    ArrayList<String> resourcesNames = new ArrayList<String>();
    for (ResourceBundleData rsrcBundleData : dataList) {
      resourcesNames.add(rsrcBundleData.getName());
      String resourceBundleName = rsrcBundleData.getName().replaceAll("\\.", "/");
      zos.putNextEntry(new ZipEntry(resourceBundleName + "_" + rsrcBundleData.getLanguage() + Constants.RESURCE_BUNDLE_FILE_PROPERTIES));
      zos.write(rsrcBundleData.getData().getBytes());
      zos.closeEntry();
    }

    ValuesParam initResources = component.getInitParams().getValuesParam("init.resources");
    ArrayList<String> initValues = initResources.getValues();
    ValuesParam portalResources = component.getInitParams().getValuesParam("portal.resource.names");
    ArrayList<String> portalValues = portalResources.getValues();

    Set<String> values = new HashSet<String>();
    values.addAll(initValues);
    values.addAll(portalValues);
    values.addAll(resourcesNames);
    initResources.setValues(new ArrayList<String>(values));
  }

}
