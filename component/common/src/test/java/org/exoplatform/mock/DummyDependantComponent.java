/*
 * Copyright (C) 2003-2015 eXo Platform SAS.
 *
 * This file is part of UXPaaS :: PLF :: Services.
 *
 * UXPaaS :: PLF :: Services is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * UXPaaS :: PLF :: Services software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with UXPaaS :: PLF :: Services; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.mock;

import org.exoplatform.services.naming.InitialContextInitializer;
import org.picocontainer.Startable;

/**
 * Dummy Startable component to make sure the datasource is bound before others
 * components use it.
 */
public class DummyDependantComponent implements Startable {

  private final InitialContextInitializer jndiInitializer;

  public DummyDependantComponent(InitialContextInitializer jndiInitializer) {
    this.jndiInitializer = jndiInitializer;
  }

  @Override
  public void start() {
    // nothing
    System.out.println("Start here");
  }

  @Override
  public void stop() {
    // nothing
  }

}
