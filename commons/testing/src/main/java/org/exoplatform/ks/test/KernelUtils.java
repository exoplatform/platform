/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ks.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.Property;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.container.xml.ValuesParam;

/**
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
public class KernelUtils {
  
  private KernelUtils() {
    // hidden
  }

  @SuppressWarnings("unchecked")
  public static <T>T getService(Class<? extends T> clazz) {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    return (T) container.getComponentInstanceOfType(clazz);
  }

  public static void addValueParam(InitParams params, String name, String value) {
    ValueParam param = new ValueParam();
    param.setName(name);
    param.setValue(value);
    params.addParameter(param);
   }
  
  public static void addObjectParam(InitParams params, String name, Object value) {
    ObjectParameter param = new ObjectParameter();
    param.setName(name);
    param.setObject(value);
    params.addParameter(param);
   }

  public static void addPropertiesParam(InitParams params, String name, Map<String, String> map) {
    PropertiesParam param = new PropertiesParam();
    param.setName(name);
    Iterator<String> it = map.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      Property prop = new Property(key, map.get(key));
      param.addProperty(prop);
    }
    params.addParameter(param);
  }

  public static void addValuesParam(InitParams params, String name, String... values) {
    ValuesParam param = new ValuesParam();
    param.setName(name);
    param.setValues(new ArrayList<String>(Arrays.asList(values)));
    params.addParameter(param);
  }

}
