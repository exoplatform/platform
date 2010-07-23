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
package org.exoplatform.platform.migration.handlers.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.platform.migration.handlers.ComponentHandler;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;
import org.exoplatform.services.resources.ResourceBundleService;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform
 * haikel.thamri@exoplatform.com 21 juil. 2010
 */
public class ResourceBundleHandler implements ComponentHandler {

  private PortalContainer       portalContainer;

  private ResourceBundleService resourceBundleService;

  private Log                   log = ExoLogger.getLogger(this.getClass());

  public void invoke(Component component, String rootConfDir) {
    try {
      portalContainer = PortalContainer.getInstance();
      resourceBundleService = (ResourceBundleService) portalContainer.getComponentInstanceOfType(ResourceBundleService.class);
      preMarshallComponent(component, rootConfDir);
      Configuration configuration = new Configuration();
      configuration.addComponent(component);
      marshall(configuration, rootConfDir + File.separator + "portal" + File.separator
          + component.getKey() + ".xml");

    } catch (Exception ie) {
      // TODO Auto-generated catch block
      log.error("error in the invoke method", ie);
    }
  }

  private void preMarshallComponent(Component component, String rootConfDir) {
    try {
      String portalConfDir = rootConfDir + File.separator + "portal";
      Query query_ = new Query(null, null);
      PageList pageList = resourceBundleService.findResourceDescriptions(query_);
      List<ResourceBundleData> dataList = pageList.getAll();
      ArrayList<String> resourcesNames = new ArrayList<String>();
      for (ResourceBundleData rsrcBundleData : dataList) {
        resourcesNames.add(rsrcBundleData.getName());
        String[] tabName = rsrcBundleData.getName().split("\\.");
        String localeConfDir = portalConfDir;

        for (int i = 0; i + 1 < tabName.length; i++) {
          localeConfDir = localeConfDir + File.separator + tabName[i];
        }
        File localeConfFolder = new File(localeConfDir);
        localeConfFolder.mkdirs();
        File file = new File(localeConfDir + File.separator + tabName[tabName.length - 1] + "_"
            + rsrcBundleData.getLanguage() + ".properties");

        setContents(file, rsrcBundleData.getData());
      }

      ValuesParam initResources = component.getInitParams().getValuesParam("init.resources");
      ArrayList<String> initValues = initResources.getValues();
      ValuesParam portalResources = component.getInitParams().getValuesParam("portal.resources");
      ArrayList<String> portalValues = portalResources.getValues();
      ArrayList<String> values = new ArrayList<String>();
      values.addAll(initValues);
      values.addAll(portalValues);
      for (String name : resourcesNames) {
        boolean nameExist = false;
        for (String value : values) {
          if (value.equals(name))
            nameExist = true;
        }
        if (!nameExist) {
          initValues.add(name);

        }
      }

    } catch (Exception ie) {
      log.error("problem in the preMarshall Process", ie);
    }
  }

  private void setContents(File file, String data) throws IOException {
    BufferedWriter bw = null;
    try {
      // use buffering
      bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
      bw.write(data);
    } catch (Exception ie) {
      log.error("File cannot be written: ", ie);
    }

    finally {
      if (bw != null)
        bw.close();
    }
  }

  private void marshall(Object obj, String xmlPath) {
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      mctx.marshalDocument(obj, "UTF-8", null, new FileOutputStream(xmlPath));
    } catch (Exception ie) {
      log.error("Cannot convert the object to xml", ie);
    }
  }

}
