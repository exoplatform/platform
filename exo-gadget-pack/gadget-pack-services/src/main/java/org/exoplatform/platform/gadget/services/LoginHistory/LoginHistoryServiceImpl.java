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
import java.util.Map;
import java.util.Set;

import org.exoplatform.platform.gadget.services.LoginHistory.storage.LoginHistoryStorage;


/**
 * Created by The eXo Platform SARL Author : Tung Vu Minh tungvm@exoplatform.com
 * Apr 21, 2011 6:19:21 PM
 */

public class LoginHistoryServiceImpl implements LoginHistoryService {
    private LoginHistoryStorage loginHistoryStorage;

    public LoginHistoryServiceImpl(LoginHistoryStorage loginHistoryStorage) {
        this.loginHistoryStorage = loginHistoryStorage;
    }

    /**
     * Get user's last login time
     */
    public long getLastLogin(String userId) throws Exception {
        return loginHistoryStorage.getLastLogin(userId);
    }

    /**
     * Get last logins
     *
     * @param numItems
     * @return List of {numItems} last login entries
     * @throws Exception
     */
    public List<LastLoginBean> getLastLogins(int numItems, String userIdFilter) throws Exception {
        return loginHistoryStorage.getLastLogins(numItems,userIdFilter);
    }

    /**
     * Add an entry to user login history
     *
     * @param userId
     * @param loginTime
     * @throws Exception
     */
    public void addLoginHistoryEntry(String userId, long loginTime) throws Exception {
        loginHistoryStorage.addLoginHistoryEntry(userId,loginTime);
    }

    /**
     * Get user login history
     *
     * @param userId
     * @return List of login history entries in range [fromTime..toTime] of user {userId}
     * @throws Exception
     */
    public List<LoginHistoryBean> getLoginHistory(String userId, long fromTime, long toTime) throws Exception {
        return loginHistoryStorage.getLoginHistory(userId,fromTime,toTime);
    }

    /**
     * Get user's login count per days in range [fromDate..toDate]
     */
    public List<LoginCounterBean> getLoginCountPerDaysInRange(String userId, long fromDate, long toDate) throws Exception {
        return getLoginCountPerDaysInRange(userId,fromDate,toDate);
    }

    /**
     * Get user login count per days in given week
     */
    public List<LoginCounterBean> getLoginCountPerDaysInWeek(String userId, long week) throws Exception {
        return loginHistoryStorage.getLoginCountPerDaysInWeek(userId,week);
    }

    /**
     * Get user login count per weeks in given month
     */
    public List<LoginCounterBean> getLoginCountPerWeeksInMonths(String userId, long fromMonth, int numOfMonths) throws Exception {
        return loginHistoryStorage.getLoginCountPerWeeksInMonths(userId,fromMonth,numOfMonths);
    }

    /**
     * Get user login count per months in given year
     */
    public List<LoginCounterBean> getLoginCountPerMonthsInYear(String userId, long year) throws Exception {
        return loginHistoryStorage.getLoginCountPerMonthsInYear(userId,year);
    }

    /**
     * Get the list of all users who are logged after fromTime
     *
     * @param fromTime
     * @return the list of user's name
     */
    public Set<String> getLastUsersLogin(long fromTime) throws Exception {
        return loginHistoryStorage.getLastUsersLogin(fromTime);
    }

    /**
     * An user is inactive if his last login is more than a number of days
     *
     * @param userId user's name
     * @param days the number of days to verify if user is active or not
     * @return
     */
    public boolean isActiveUser(String userId, int days) throws Exception {
        return loginHistoryStorage.isActiveUser(userId,days);
    }

    public Map<String, Integer> getActiveUsers(long fromTime) {
        return loginHistoryStorage.getActiveUsers(fromTime);
    }
    
    public long getBeforeLastLogin(String userId) throws Exception {
        return loginHistoryStorage.getBeforeLastLogin(userId);
    }
}

