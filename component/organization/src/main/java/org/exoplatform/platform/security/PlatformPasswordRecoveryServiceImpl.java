/*
 * Copyright (C) 2015 eXo Platform SAS.
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

package org.exoplatform.platform.security;

import org.exoplatform.commons.api.notification.plugin.NotificationPluginUtils;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.services.mail.MailService;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.login.recovery.PasswordRecoveryServiceImpl;
import org.exoplatform.web.security.security.RemindPasswordTokenService;


/**
 * This class is override of {@link org.exoplatform.web.login.recovery.PasswordRecoveryServiceImpl}
 * It overrides 2 method: getSenderName() and getSenderEmail()
 * to get these information from "Notification administrator" page {@literal (Administration -> Portal -> Notifications)}
 *
 * This service will be configured in platform-extension.war to overwrite configuration in exogtn.
 *
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class PlatformPasswordRecoveryServiceImpl extends PasswordRecoveryServiceImpl {
    private final SettingService settingService;

    public PlatformPasswordRecoveryServiceImpl(OrganizationService orgService,
                                             MailService mailService,
                                             ResourceBundleService bundleService,
                                             RemindPasswordTokenService remindPasswordTokenService,
                                             WebAppController controller,
                                             SettingService settingService) {
        super(orgService, mailService, bundleService, remindPasswordTokenService, controller);
        this.settingService = settingService;
    }

    @Override
    protected String getSenderName() {
        SettingValue value = settingService.get(org.exoplatform.commons.api.settings.data.Context.GLOBAL, Scope.GLOBAL, NotificationPluginUtils.NOTIFICATION_SENDER_NAME);
        if (value == null) {
            return System.getProperty("exo.notifications.portalname", "eXo");
        } else {
            return (String)value.getValue();
        }
    }

    @Override
    protected String getSenderEmail() {
        SettingValue value = settingService.get(org.exoplatform.commons.api.settings.data.Context.GLOBAL, Scope.GLOBAL, NotificationPluginUtils.NOTIFICATION_SENDER_EMAIL);
        if (value == null) {
            return System.getProperty("gatein.email.smtp.from", "noreply@exoplatform.com");
        } else {
            return (String)value.getValue();
        }
    }
}
