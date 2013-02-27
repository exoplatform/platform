/**
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.platform.webui.templates;

import org.exoplatform.portal.application.PortalApplication;
import org.exoplatform.resolver.ApplicationResourceResolver;

import javax.servlet.ServletConfig;

public class PlatformPortalApplication extends PortalApplication {
    public PlatformPortalApplication(ServletConfig config) {
        super(config);
        ApplicationResourceResolver resolver = new ApplicationResourceResolver();
        resolver.addResourceResolver(new PlatformServletResourceResolver(config.getServletContext(), "war:"));
        resolver.addResourceResolver(new PlatformServletResourceResolver(config.getServletContext(), "app:"));
        resolver.addResourceResolver(new PlatformServletResourceResolver(config.getServletContext(), "system:"));
        resolver.addResourceResolver(new PlatformServletResourceResolver(config.getServletContext().getContext("/eXoResources"),
                "resources:"));
        setResourceResolver(resolver);
    }
}
