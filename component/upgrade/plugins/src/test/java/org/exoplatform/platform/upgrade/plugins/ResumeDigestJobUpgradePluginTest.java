package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.service.storage.MailNotificationStorage;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.scheduler.JobSchedulerService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ResumeDigestJobUpgradePluginTest {

    @Test
    public void testResumeDigestJobUpgradePluginFrom510To520() throws Exception {
        //Given
        JobSchedulerService schedulerService = mock(JobSchedulerService.class);
        MailNotificationStorage mailNotificationStorage = mock(MailNotificationStorage.class);

        // When
        ResumeDigestJobUpgradePlugin plugin = new ResumeDigestJobUpgradePlugin(schedulerService,
                mailNotificationStorage, new InitParams());
        plugin.processUpgrade("5.1.0", "5.2.0");

        // Then
        verify(mailNotificationStorage, times(0)).removeMessageAfterSent(any(NotificationContext.class));
        verify(schedulerService,times(2)).resumeJob(anyString(),anyString());
    }

    @Test
    public void testResumeDigestJobUpgradePluginFrom520To600() throws Exception {
        //Given
        JobSchedulerService schedulerService = mock(JobSchedulerService.class);
        MailNotificationStorage mailNotificationStorage = mock(MailNotificationStorage.class);

        // When
        ResumeDigestJobUpgradePlugin plugin = new ResumeDigestJobUpgradePlugin(schedulerService,
                mailNotificationStorage, new InitParams());
        plugin.processUpgrade("5.2.0", "6.0.0");

        // Then
        verify(mailNotificationStorage, times(2)).removeMessageAfterSent(any(NotificationContext.class));
        verify(schedulerService,times(2)).resumeJob(anyString(),anyString());
    }

    @Test
    public void testResumeDigestJobUpgradePluginFrom510To600() throws Exception {
        //Given
        JobSchedulerService schedulerService = mock(JobSchedulerService.class);
        MailNotificationStorage mailNotificationStorage = mock(MailNotificationStorage.class);

        // When
        ResumeDigestJobUpgradePlugin plugin = new ResumeDigestJobUpgradePlugin(schedulerService,
                mailNotificationStorage, new InitParams());
        plugin.processUpgrade("5.1.0", "6.0.0");

        // Then
        verify(mailNotificationStorage, times(0)).removeMessageAfterSent(any(NotificationContext.class));
        verify(schedulerService,times(2)).resumeJob(anyString(),anyString());
    }
}
