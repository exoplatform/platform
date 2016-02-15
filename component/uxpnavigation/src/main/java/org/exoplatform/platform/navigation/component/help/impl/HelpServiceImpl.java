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
package org.exoplatform.platform.navigation.component.help.impl;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.platform.navigation.component.help.HelpService;
import org.exoplatform.platform.navigation.component.help.Helper;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="kmenzli@exoplatform.com">Kmenzli</a>
 */
public class HelpServiceImpl implements HelpService {

    private static final Log LOG = ExoLogger.getExoLogger(HelpServiceImpl.class);

    private PropertiesParam props=null;

    public HelpServiceImpl(InitParams params) {

        if (params == null) {
            throw new IllegalStateException("given params are null in helpService");
        }

        this.props = params.getPropertiesParam("help.pages");

        if (props == null)
        {
            throw new IllegalStateException("params.getPropertiesParam() returns null in helpService");
        }
    }

    public String fetchHelpPage(String currentNavigation) {

        String helpPage = DEFAULT_HELP_PAGE;

        if (props != null) {

                helpPage = props.getProperty(currentNavigation);

        }
        if (!Helper.present(helpPage)) {

            return     getDefaultPageHelp();

        }

        return helpPage;
    }

    public String getDefaultPageHelp () {

        if (props != null) {

            if (Helper.present(props.getProperty("default"))) {
                 return props.getProperty("default") ;
            }
        }
        return DEFAULT_HELP_PAGE;

    }


}
