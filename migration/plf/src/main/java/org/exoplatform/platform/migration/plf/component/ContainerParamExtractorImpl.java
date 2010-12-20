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
package org.exoplatform.platform.migration.plf.component;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.migration.common.component.ContainerParamExtractor;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Dec
 * 20, 2010
 */
public class ContainerParamExtractorImpl implements ContainerParamExtractor {

  public String getContainerId(ExoContainer container) {
    if (!(container instanceof PortalContainer)) {
      return ExoContainerContext.getCurrentContainer().getContext().getName();
    }
    return container.getContext().getName();
  }

  public String getContainerRestContext(ExoContainer container) {
    if (!(container instanceof PortalContainer)) {
      return ExoContainerContext.getCurrentContainer().getContext().getRestContextName();
    }
    return container.getContext().getRestContextName();
  }

}
