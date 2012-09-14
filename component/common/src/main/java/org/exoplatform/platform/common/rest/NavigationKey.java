/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.platform.common.rest;

import org.exoplatform.portal.mop.SiteKey;

/**
 * @author <a href="mailto:kmenzli@exoplatform.com">Khemais MENZLI</a>
 * @version $Revision$
 * Date: 14/09/12
 */
public class NavigationKey {
    private SiteKey siteKey;
    private String navUri;

    public NavigationKey(SiteKey siteKey)
    {
        this.siteKey = siteKey;
    }

    public NavigationKey(SiteKey siteKey, String navUri)
    {
        this.siteKey = siteKey;
        this.navUri = navUri;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public String getNavUri()
    {
        return navUri;
    }
}
