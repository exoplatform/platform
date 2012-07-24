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
package org.exoplatform.platform.upgrade.plugins;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.common.admin.impl.TermsAndConditionsServiceImpl;
import org.exoplatform.platform.common.admin.TermsAndConditionsService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="kmenzli@exoplatform.com">Kmenzli</a>
 * @date 24/07/12
 */
public class UpgradeTermsAndConditionsPlugin extends UpgradeProductPlugin {
    private static final Log LOG = ExoLogger.getLogger(UpgradeTermsAndConditionsPlugin.class);

    private TermsAndConditionsService termsAnsConditionsService;

    public   UpgradeTermsAndConditionsPlugin (InitParams initParams, TermsAndConditionsService termsAnsConditionsService) {

        super(initParams);
        this.termsAnsConditionsService =termsAnsConditionsService;
    }
    @Override
    public void processUpgrade(String oldVersion, String newVersion) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Starting TermsAndConditions upgrade .....");
        }
        termsAnsConditionsService.checkTermsAndConditions();
        if (LOG.isInfoEnabled()) {
            LOG.info("TermsAndConditions data are migrated successfully");
        }
    }
    @Override
    public boolean shouldProceedToUpgrade(String previousVersion, String newVersion) {

        return !termsAnsConditionsService.isTermsAndConditionsChecked();

    }
}
