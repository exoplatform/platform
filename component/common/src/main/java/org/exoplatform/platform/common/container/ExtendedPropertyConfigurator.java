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

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import org.picocontainer.Startable;

/**
 * Allow set system properties.
 * <br>
 * Example of configuration:
 * <pre>
 * {@code
 * <component>
 *   <key>ExtendedPropertyConfigurator</key>
 *   <type>org.exoplatform.platform.common.container.ExtendedPropertyConfigurator</type>
 *   <init-params>
 *     <properties-param>
 *      <name>MyPropertySet1</name>
 *      <property name="myproperty" value="myvalue" />
 *     </properties-param>
 *     <properties-param profile="myProfile">
 *      <name>MyPropertySet1</name>
 *      <property name="myproperty" value="myvalue2" />
 *     </properties-param>
 *     <properties-param>
 *      <name>MyPropertySet1</name>
 *      <property name="myproperty2" value="myothervalue" />
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
 * <br>
 * Note that <i>properties-param</i> entries and <i>properties.url</i> optional and can be omitted.
 * Note that if a path in <i>properties.url</i> doesn't exist it will be skipped with an info message
 *
 * @author pnedonosko
 * @author aheritier
 * @author nfilotto
 */
public class ExtendedPropertyConfigurator extends PropertyConfigurator implements Startable {

  /**
   * The logger.
   */
  private static final Log LOG = ExoLogger.getExoLogger(ExtendedPropertyConfigurator.class);

  /**
   * Constructor used by ExoContainer.
   *
   * @param params
   * @param confManager
   */
  public ExtendedPropertyConfigurator(InitParams params,
                                      ConfigurationManager confManager) {
    super(loadPropertiesParams(params), confManager);
    ValuesParam exts = params.getValuesParam("properties.urls");
    if (exts != null) {
      for (Object val : exts.getValues()) {
        String path = (String) val;
        if (path != null) {
          URL url = null;
          try {
            url = confManager.getURL(path);
            // Test if the Url is available before going further
            // We don't want an error reported by ContainerUtil.loadProperties when it doesn't exist
            url.openStream().close();
            LOG.info("Using property file " + url + " to set configuration properties.");
            Map<String, String> props = ContainerUtil.loadProperties(url);
            if (props != null) {
              for (Map.Entry<String, String> entry : props.entrySet()) {
                String propertyName = entry.getKey();
                String propertyValue = entry.getValue();
                PropertyManager.setProperty(propertyName, propertyValue);
              }
            }
          } catch (FileNotFoundException fne) {
            LOG.info("Skipping configuration file " + path + ". File doesn't exist.");
          } catch (Exception e) {
            LOG.error("Cannot load extension property file " + path
                          + (url != null ? " resolved as " + url : ""), e);
          }
        }
      }
    }
  }

  /**
   * Register all properties defined as <i>properties-param</i> in the {@link org.exoplatform.commons.utils.PropertyManager}.
   * <i>properties</i> entry isn't processed here but done by the parent {@link org.exoplatform.container.PropertyConfigurator}.
   * @param params The list of InitParams to register
   * @return The <i>params</i> list passed in parameter
   */
  private static InitParams loadPropertiesParams(InitParams params) {
    if (params != null) {
      Iterator<PropertiesParam> it = params.getPropertiesParamIterator();
      while (it.hasNext()) {
        PropertiesParam propertiesParam = it.next();
        LOG.debug("Going to initialize properties from init param");
        for (Iterator<Property> i = propertiesParam.getPropertyIterator(); i.hasNext(); ) {
          Property property = i.next();
          String name = property.getName();
          String value = property.getValue();
          LOG.debug("Adding property from init param " + name + " = " + value);
          PropertyManager.setProperty(name, value);
        }
        if ("properties".equals(propertiesParam.getName()))
          it.remove();
      }
    }
    return params;
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
