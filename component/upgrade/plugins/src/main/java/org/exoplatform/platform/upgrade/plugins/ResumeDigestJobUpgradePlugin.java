package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.service.storage.MailNotificationStorage;
import org.exoplatform.commons.notification.impl.NotificationContextImpl;
import org.exoplatform.commons.notification.impl.jpa.email.JPAMailNotificationStorage;
import org.exoplatform.commons.notification.impl.jpa.email.dao.MailDigestDAO;
import org.exoplatform.commons.notification.job.NotificationJob;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.scheduler.JobSchedulerService;


/**
 * Upgrade plugin to resume daily and weekly digest jobs.
 * This jobs have been paused during migration of notification from JCR to RDBMS
 * This UP resume both jobs
 * In addition, if the migration comes from version 5.2.0+, we completly delete stored digest.
 * In fact, during the time the jobs were paused, the digest were stored in DB.
 * If we don't delete it, the first start will create old for old digests.
 *
 * Finally, if we upgrade from a version before 5.2.0, we don't delete digest :
 * The jobs were not paused, and there is no "old" digests stored.
 */

public class ResumeDigestJobUpgradePlugin extends UpgradeProductPlugin {

  private JobSchedulerService schedulerService;
  private MailNotificationStorage mailNotificationStorage;

  private static final Log LOG = ExoLogger.getLogger(ResumeDigestJobUpgradePlugin.class);


  public ResumeDigestJobUpgradePlugin(JobSchedulerService schedulerService, MailNotificationStorage mailNotificationStorage, InitParams initParams) {
    super(initParams);
    this.mailNotificationStorage=mailNotificationStorage;
    this.schedulerService = schedulerService;
  }


  @Override
  public void processUpgrade(String oldVersion, String newVersion) {


    try {
      //Force remove digest items only source migration version is after 5.2.0
      //Before this version, there is no problem of blocking digest, so no need of delete it
      if (VersionComparator.isAfter(oldVersion, "5.2.0") ||
              VersionComparator.isSame(oldVersion, "5.2.0")) {
        mailNotificationStorage.deleteAllDigests();
      }

      schedulerService.resumeJob("NotificationDailyJob", "Notification");
      schedulerService.resumeJob("NotificationWeeklyJob", "Notification");
    } catch (Exception e) {
      LOG.error("Error when resuming daily and weekly job",e);
      throw new RuntimeException("An error occurred when resuming daily and weekly job");
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion, previousVersion);
  }

}
