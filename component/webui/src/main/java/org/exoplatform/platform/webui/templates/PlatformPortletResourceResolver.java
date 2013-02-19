/**
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
package org.exoplatform.platform.webui.templates;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.resolver.PortletResourceResolver;
import org.exoplatform.resolver.ResourceResolver;

import java.io.InputStream;
import java.net.URL;

import javax.portlet.PortletContext;
/**
 *
 * @author <a href="kmenzli@exoplatform.com">Kmenzli</a>
 * @version $Revision$
 */
public class PlatformPortletResourceResolver extends PortletResourceResolver {
    public PlatformPortletResourceResolver(PortletContext context, String scheme)
    {
        super(context, scheme);
    }

    public URL getResource(String url) throws Exception {
        ResourceResolver resourceResolver = getPortalResourceResolver(url);
        if (resourceResolver != null)
        {
            URL res = resourceResolver.getResource(url);
            if (res != null)
            {
                return res;
            }
        }
        return super.getResource(url);
    }

    public InputStream getInputStream(String url) throws Exception
    {
        ResourceResolver resourceResolver = getPortalResourceResolver(url);
        if (resourceResolver != null)
        {
            InputStream inputStream = resourceResolver.getInputStream(url);
            if (inputStream != null)
            {
                return inputStream;
            }
        }
        return super.getInputStream(url);
    }

    public String getRealPath(String url) {
        ResourceResolver resourceResolver = getPortalResourceResolver(url);
        if (resourceResolver != null)
        {
            String path = resourceResolver.getRealPath(url);
            if (path != null)
            {
                return path;
            }
        }
        return super.getRealPath(url);
    }

    private ResourceResolver getPortalResourceResolver(String url)
    {
        PortalRequestContext context = Util.getPortalRequestContext();
        if (context != null)
        {
            return context.getResourceResolver(url);
        }
        return null;
    }
}
