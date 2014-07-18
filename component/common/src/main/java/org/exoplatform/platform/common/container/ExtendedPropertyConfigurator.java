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
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Map;
import org.picocontainer.Startable;

/**
 * Allow set system properties.
 * <p/>
 * Example of configuration:
 * <p/>
 * <pre>
 * {@code
 * <component>
 *   <key>ExtendedPropertyConfigurator</key>
 *   <type>org.exoplatform.platform.common.container.ExtendedPropertyConfigurator</type>
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
 * <p/>
 * Note that <i>properties</i> and <i>properties.url</i> optional and can be omitted.
 * Note that if a path in <i>properties.url</i> doesn't exist it will be skipped with an info message
 *
 * @author pnedonosko
 * @author aheritier
 */
public class ExtendedPropertyConfigurator extends PropertyConfigurator implements Startable {

  /**
   * The logger.
   */
  private static final Log LOG = ExoLogger.getExoLogger(ExtendedPropertyConfigurator.class);

  /**
   * Constructor used by ExoContainer.
   *
   * @param {@link InitParams} params
   * @param {@link ConfigurationManager} confManager
   */
  public ExtendedPropertyConfigurator(InitParams params,
                                      ConfigurationManager confManager) {
    super(params, confManager);
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
            LOG.info("Configuration file " + path + " doesn't exist");
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