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

import org.exoplatform.platform.gadget.services.ForumStatistics.ForumsWeeklyStatistic;

import java.util.List;

/**
 * @author <a href="tung.do@exoplatform.com">Do Thanh Tung </a>
 * @version 1.0
 */
public interface ForumsWeeklyStatisticService {

  public static final String EXO_START_DATE                = "exo:startDate";

  public static final String EXO_START_POSTS_COUNT_OF_WEEK = "exo:startPostsCountOfWeek";

  public static final String EXO_POSTS_COUNT_OF_WEEK       = "exo:postsCountOfWeek";

  public static final String EXO_LAST_STATISTIC_ENTRY      = "exo:lastStatEntry";

  public long getPostCountForumStatistic() throws Exception;

  public ForumsWeeklyStatistic getLastForumsWeeklyStatistic() throws Exception;

  public void saveForumsWeeklyStatistic(ForumsWeeklyStatistic forumsWeeklyStatistic) throws Exception;

  public List<ForumsWeeklyStatistic> getAllForumsWeeklyStatistic()throws Exception;
}
