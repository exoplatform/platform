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
package org.exoplatform.platform.gadget.services.LoginHistory;

import java.util.List;



/**
 * Created by The eXo Platform SARL Author : Tung Vu Minh tungvm@exoplatform.com
 * Apr 21, 2011 6:19:21 PM
 */

public interface LoginHistoryService {
	public static String ALL_USERS = "AllUsers";
	
	public abstract long getLastLogin(String userId) throws Exception;
	
	public abstract List<LastLoginBean> getLastLogins(int numLogins, String userIdFilter) throws Exception;
	
	public abstract void addLoginHistoryEntry(String userId, long loginTime) throws Exception;

	public abstract List<LoginHistoryBean> getLoginHistory(String userId, long fromTime, long toTime) throws Exception;
	
	public abstract List<LoginCounterBean> getLoginCountPerDaysInWeek(String userId, long week) throws Exception;
	
	public abstract List<LoginCounterBean> getLoginCountPerWeeksInMonths(String userId, long fromMonth, int numOfMonths) throws Exception;
	
	public abstract List<LoginCounterBean> getLoginCountPerMonthsInYear(String userId, long year) throws Exception;
}