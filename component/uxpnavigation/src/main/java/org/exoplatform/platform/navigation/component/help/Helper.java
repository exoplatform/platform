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
package org.exoplatform.platform.navigation.component.help;


import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="kmenzli@exoplatform.com">Kmenzli</a>
 * @date 05/11/12
 */

public class Helper {

    private static final Log LOG = ExoLogger.getExoLogger(Helper.class);
    public static final String DEFAULT_HELP_PAGE= "http://docs.exoplatform.com";

    public static boolean present (String theString) {
        boolean present = false;
        if (theString != null && theString.length()!=0) {
            present = true;
        }
        return present;
    }

    public static String getCurrentNavigation() {
        try {

            return  Util.getUIPortal().getNavPath().getName();

        } catch (Exception E) {

            LOG.warn("Can not load the currentNavigation ",E);
            E.printStackTrace();
            return null;
        }

    }
}
