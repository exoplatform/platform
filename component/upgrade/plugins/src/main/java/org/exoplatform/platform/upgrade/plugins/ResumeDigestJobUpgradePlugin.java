package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.scheduler.JobSchedulerService;

public class ResumeDigestJobUpgradePlugin extends UpgradeProductPlugin {

  private JobSchedulerService schedulerService;

  private static final Log LOG = ExoLogger.getLogger(ResumeDigestJobUpgradePlugin.class);


  public ResumeDigestJobUpgradePlugin(JobSchedulerService schedulerService, InitParams initParams) {
    super(initParams);
    this.schedulerService = schedulerService;
  }


  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    try {
      schedulerService.resumeJob("NotificationDailyJob", "Notification");
      schedulerService.resumeJob("NotificationWeeklyJob", "Notification");
    } catch (Exception e) {
      LOG.error("Error when resuming daily and weekly job",e);
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion, previousVersion);
  }

}
