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
package org.exoplatform.commons.testing;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.testng.annotations.BeforeClass;

/**
 * A base test class that allows to load an exo container with a selected set of
 * components. It is aimed at running lighter test cases than the
 * StandaloneContainer. <u>Example usage</u> :
 * 
 * <pre>
 * @ConfiguredBy({@ConfigurationUnit(scope = ContainerScope.ROOT, path = "conf/custom.xml"),@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/some.xml"),  @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/other.xml")})
 * </pre>
 * 
 * TODO : leverage GateIn testing framework. This is a temporary fork of GateIn
 * testing base classes.
 * 
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice
 *         Lamarque</a>
 * @version $Revision$
 */
public abstract class AbstractExoContainerTestCase {


  @BeforeClass
  public void startContainer() throws Exception {
    beforeContainerStart();
    initExoContainer();
    afterContainerStart();
  }
  
  
  protected void afterContainerStart() {
  }


  protected void beforeContainerStart() {
  }


  private void initExoContainer() throws ClassNotFoundException {
    //
    Set<String> rootConfigPaths = new HashSet<String>();
    rootConfigPaths.add("conf/root-configuration.xml");

    //
    Set<String> portalConfigPaths = new HashSet<String>();
    portalConfigPaths.add("conf/portal-configuration.xml");
    portalConfigPaths.add("conf/" + getClass().getSimpleName() + ".xml");

    //
    EnumMap<ContainerScope, Set<String>> configs = new EnumMap<ContainerScope, Set<String>>(ContainerScope.class);
    configs.put(ContainerScope.ROOT, rootConfigPaths);
    configs.put(ContainerScope.PORTAL, portalConfigPaths);

    //
    ConfiguredBy cfBy = getClass().getAnnotation(ConfiguredBy.class);

    if (cfBy != null) {
      for (ConfigurationUnit src : cfBy.value()) {
        configs.get(src.scope()).add(src.path());
      }
    }

    ContainerBuilder builder = new ContainerBuilder();

    Set<String> rootConfs = configs.get(ContainerScope.ROOT);
    for (String rootConf : rootConfs) {
      builder.withRoot(rootConf);
    }

    Set<String> portalConfs = configs.get(ContainerScope.PORTAL);
    for (String portalConf : portalConfs) {
      builder.withPortal(portalConf);
    }

    builder.build();
  
  }
  
   
  /**
   * Register a component to the container
   * @param key component key
   * @param instance component instance to register
   */
  protected <T, I extends T> void registerComponent(Class<T> key, I instance) {
    ExoContainerContext.getCurrentContainer().registerComponentInstance(key, instance);
  }

  /**
   * Get a component from current container
   * 
   * @param <T> type of component (key)
   * @param <U> type of component implementation (type)
   * @param key class of the registered component
   * @return
   */
  @SuppressWarnings("unchecked")
  protected <T, U extends T> U getComponent(Class<T> key) {
    // ExoContainer container = ExoContainerContext.getCurrentContainer();
    ExoContainer container = PortalContainer.getInstance();
    return (U) container.getComponentInstanceOfType(key);
  }
  
  /**
   * Replace a component implementation by registering it against the current container
   * @param key component key
   * @param instance component instance to register
   */
  protected <T, I extends T> void replaceComponent(Class<T> key, I instance) {
    ExoContainerContext.getCurrentContainer().unregisterComponent(key);
    registerComponent(key, instance);
  }
  


}
