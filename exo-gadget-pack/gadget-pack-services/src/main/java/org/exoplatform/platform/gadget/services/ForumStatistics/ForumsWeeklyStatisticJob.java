/**
 * Copyright (C) ${year} eXo Platform SAS.
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
package org.exoplatform.platform.gadget.services.ForumStatistics;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.platform.gadget.services.ForumStatistics.ForumsWeeklyStatisticService;
import org.exoplatform.platform.gadget.services.ForumStatistics.ForumsWeeklyStatistic;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Job service with a periodic action in order to store Forums Weekly Statistics in JCR.
 * 
 * @author <a href="tung.do@exoplatform.com">Do Thanh Tung </a>
 * @version 1.0
 */
public class ForumsWeeklyStatisticJob implements Job {
  private static final Log log = ExoLogger.getLogger(ForumsWeeklyStatisticJob.class);

  public ForumsWeeklyStatisticJob() {
  }
  
/**
 * 
 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
 */
  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {
      ExoContainer containerContext = ExoContainerContext.getCurrentContainer();
      ForumsWeeklyStatisticService service = (ForumsWeeklyStatisticService) containerContext.getComponentInstanceOfType(ForumsWeeklyStatisticService.class);
      ForumsWeeklyStatistic weeklyStatistic = service.getLastForumsWeeklyStatistic();

      if (isAddNew(weeklyStatistic)) {
        weeklyStatistic = new ForumsWeeklyStatistic();
        weeklyStatistic.setStartDate(new Date());
        weeklyStatistic.setLastStatEntry(new Date());
        weeklyStatistic.setPostsCountOfWeek(service.getPostCountForumStatistic());
        weeklyStatistic.setStartPostsCountOfWeek(service.getPostCountForumStatistic());
      } else {
        weeklyStatistic.setLastStatEntry(new Date());
        weeklyStatistic.setPostsCountOfWeek((service.getPostCountForumStatistic() > weeklyStatistic.getStartPostsCountOfWeek()) ? 
                                                   service.getPostCountForumStatistic() - weeklyStatistic.getStartPostsCountOfWeek() : 0);
      }

      service.saveForumsWeeklyStatistic(weeklyStatistic);

    } catch (Exception e) {
      log.error("Failed to update ForumsWeeklyStatistic", e);
    }
  }

  /**
   * Check if a Forums Week Statistic the one that begin the week.
   * Forums Week Statistics are new if the current date/time is the beginning of new week.
   * 
   * @param weeklyStatistic
   * @return true if the Forums Week statistic is the beginning of the week, wrong if not.
   */
  private boolean isAddNew(ForumsWeeklyStatistic weeklyStatistic) {
    if (weeklyStatistic == null)
      return true;
    Calendar now = GregorianCalendar.getInstance();
    Calendar startDate = GregorianCalendar.getInstance();
    startDate.setTime(weeklyStatistic.getStartDate());
    return (now.get(Calendar.WEEK_OF_YEAR) != startDate.get(Calendar.WEEK_OF_YEAR));
  }
}
