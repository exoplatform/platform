/***************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
 ***************************************************************************/
package org.exoplatform.platform.gadget.services.ForumStatistics;

import org.exoplatform.services.jcr.util.IdGenerator;

import java.util.Date;

/**
 * Forums Weekly Statistic Bean.
 * 
 * @author <a href="tung.do@exoplatform.com">Do Thanh Tung </a>
 * @version 1.0
 */
public class ForumsWeeklyStatistic {
  public static final String WEEK_STATISTIC_ID = "WeeklyStatistic";
  private String id;
  
  private Date   startDate;            // the datetime when week begin

  private Long   startPostsCountOfWeek;// total posts in the time when week begin

  private Long   postsCountOfWeek;     // total posts from startPostsCountOfWeek to end of week (or to now if the week is current week)

  private Date   lastStatEntry;        // the last time that postsCountOfWeek was update.

  public ForumsWeeklyStatistic() {
    id = WEEK_STATISTIC_ID +  IdGenerator.generate() ;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Long getStartPostsCountOfWeek() {
    return startPostsCountOfWeek;
  }

  public void setStartPostsCountOfWeek(Long startPostsCountOfWeek) {
    this.startPostsCountOfWeek = startPostsCountOfWeek;
  }

  public Long getPostsCountOfWeek() {
    return postsCountOfWeek;
  }

  public void setPostsCountOfWeek(Long postsCountOfWeek) {
    this.postsCountOfWeek = postsCountOfWeek;
  }

  public Date getLastStatEntry() {
    return lastStatEntry;
  }

  public void setLastStatEntry(Date lastStatEntry) {
    this.lastStatEntry = lastStatEntry;
  }

}
