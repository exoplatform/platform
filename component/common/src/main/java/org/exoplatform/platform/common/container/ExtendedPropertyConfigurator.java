/*
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
package org.exoplatform.platform.common.container;

import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.PropertyConfigurator;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.util.ContainerUtil;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.Property;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import org.picocontainer.Startable;

/**
 * Allow set system properties.
 *
 * Example of configuration:
 *
 * <pre>
 * {@code
 * <component>
 *   <key>ExtendedPropertyConfigurator</key>
 *   <returnType>org.exoplatform.platform.container.ExtendedPropertyConfigurator</returnType>
 *   <init-params>
 *     <properties-param>
 *      <name>properties</name>
 *      <property name="myproperty" value="myvalue" />
 *     </properties-param>
 *     <values-param>
 *       <name>properties.urls</name>
 *       <value>cloud.properties</value>
 *       <value>cluster.properties</value>
 *     </values-param>
 *   </init-params>
 * </component>
 * }
 * </pre>
 *
 * Note that <i>properties</i> and <i>properties.url</i> optional and can be omitted.
 *
 * @author pnedonosko
 *
 */
public class ExtendedPropertyConfigurator implements Startable {

  /** The logger. */
  private static final Log LOG = ExoLogger.getExoLogger(ExtendedPropertyConfigurator.class);

  /**
   * Constructor used by ExoContainer. It depends on {@link PropertyConfigurator} to let basic
   * configuration to be loaded first.
   *
   * @param {@link InitParams} params
   * @param {@link ConfigurationManager} confManager
   * @param {@link PropertyConfigurator} basicConfigurator
   */
  public ExtendedPropertyConfigurator(InitParams params,
                                      ConfigurationManager confManager,
                                      PropertyConfigurator basicConfigurator) {

    PropertiesParam propertiesParam = params.getPropertiesParam("properties");
    if (propertiesParam != null) {
      StringBuilder log = new StringBuilder();
      for (Iterator<Property> i = propertiesParam.getPropertyIterator(); i.hasNext();) {
        Property property = i.next();
        String name = property.getName();
        String value = property.getValue();
        log.append('\t');
        log.append(name);
        log.append('=');
        log.append(value);
        log.append('\r');
        log.append('\n');
        PropertyManager.setProperty(name, value);
      }
      String msg = log.toString();
      if (msg.length() > 0) {
        LOG.info("Initialized properties from init param: \r\n" + msg);
      }
    }

    ValuesParam exts = params.getValuesParam("properties.urls");
    if (exts != null) {
      for (Object val : exts.getValues()) {
        String path = (String) val;
        if (path != null) {
          URL url = null;
          try {
            url = confManager.getURL(path);
            LOG.info("Using property file " + url + " to set configuration properties.");
            Map<String, String> props = ContainerUtil.loadProperties(url);
            if (props != null) {
              for (Map.Entry<String, String> entry : props.entrySet()) {
                String propertyName = entry.getKey();
                String propertyValue = entry.getValue();
                PropertyManager.setProperty(propertyName, propertyValue);
              }
            }
          } catch (Exception e) {
            LOG.error("Cannot load extension property file " + path
                + (url != null ? " resolved as " + url : ""), e);
          }
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
  }

}