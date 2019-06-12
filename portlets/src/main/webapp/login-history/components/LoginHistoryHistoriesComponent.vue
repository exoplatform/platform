<template>
  <div>
    <div id="historyDiv" class="accordion">
      <div class="accordion-group">
        <div class="accordion-heading">
          <div class="accordion-toggle collapsed" data-parent="#historyDiv" data-toggle="collapse" href="#todayDiv">
            <a href="#"><i class="uiIconSelected pull-right"></i><span>{{ $t('loginHistory.today') }} ({{ today.length }})</span></a>
          </div>
        </div>
        <div id="todayDiv" class="accordion-body collapse">
          <div class="accordion-inner">
            <table>
              <tbody>
                <tr v-for="login in today" :key="login.userId">
                  <td v-if="!user" class="loginHistoryItem">
                    <a :id="login.userId" href="javascript:void(0)" class="customLink">{{ login.userName }}</a>
                  </td>
                  <td class="loginHistoryItemDetail">
                    {{ new Date(login.loginTime).toString("ddd dd MMM yyyy hh:mm tt") }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="accordion-group">
        <div class="accordion-heading">
          <div id="earlierThisWeekHeader" class="accordion-toggle collapsed" data-parent="#historyDiv" data-toggle="collapse" href="#earlierThisWeekDiv">
            <a href="#"><i class="uiIconSelected pull-right"></i><span>{{ $t('loginHistory.earlierThisWeek') }} ({{ week.length }})</span></a>
          </div>
        </div>
        <div id="earlierThisWeekDiv" class="accordion-body collapse">
          <div class="accordion-inner">
            <table>
              <tbody>
                <tr v-for="login in week" :key="login.userId">
                  <td v-if="!user" class="loginHistoryItem">
                    <a :id="login.userId" href="javascript:void(0)" class="customLink">{{ login.userName }}</a>
                  </td>
                  <td class="loginHistoryItemDetail">
                    {{ new Date(login.loginTime).toString("ddd dd MMM yyyy hh:mm tt") }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="accordion-group">
        <div class="accordion-heading">
          <div id="earlierThisMonthHeader" class="accordion-toggle collapsed" data-parent="#historyDiv" data-toggle="collapse" href="#earlierThisMonthDiv">
            <a href="#"><i class="uiIconSelected pull-right"></i><span>{{ $t('loginHistory.earlierThisMonth') }} ({{ month.length }})</span></a>
          </div>
        </div>
        <div id="earlierThisMonthDiv" class="accordion-body collapse">
          <div class="accordion-inner">
            <table>
              <tbody>
                <tr v-for="login in month" :key="login.userId">
                  <td v-if="!user" class="loginHistoryItem">
                    <a :id="login.userId" href="javascript:void(0)" class="customLink">{{ login.userName }}</a>
                  </td>
                  <td class="loginHistoryItemDetail">
                    {{ new Date(login.loginTime).toString("ddd dd MMM yyyy hh:mm tt") }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="accordion-group">
        <div class="accordion-heading">
          <div id="earlierHeader" class="accordion-toggle collapsed" data-parent="#historyDiv" data-toggle="collapse" href="#earlierDiv">
            <a href="#"><i class="uiIconSelected pull-right"></i><span>{{ $t('loginHistory.earlier') }} ({{ earlier.length }})</span></a>
          </div>
        </div>
        <div id="earlierDiv" class="accordion-body collapse">
          <div class="accordion-inner">
            <table>
              <tbody>
                <tr v-for="login in earlier" :key="login.userId">
                  <td v-if="!user" class="loginHistoryItem">
                    <a :id="login.userId" href="javascript:void(0)" class="customLink">{{ login.userName }}</a>
                  </td>
                  <td class="loginHistoryItemDetail">
                    {{ new Date(login.loginTime).toString("ddd dd MMM yyyy hh:mm tt") }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import loginHistoryService from '../loginHistoryService';

export default {
  props: {
    user: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      today: [],
      week: [],
      month: [],
      earlier: []
    };
  },
  watch: {
    user: function() {
      this.loadLoginHistories();
    }
  },
  created() {
    this.loadLoginHistories();
  },
  methods: {
    loadLoginHistories() {
      const user = this.user ? this.user : 'AllUsers';
      // TODAY
      loginHistoryService.getLoginHistories(user, Date.today().getTime(), Date.today().setTimeToNow().getTime()).then((resp) => {
        this.today = resp[1];
      });

      // This week
      loginHistoryService.getLoginHistories(user, Date.monday().getTime(), Date.today().getTime()).then((resp) => {
        this.week = resp[1];
      });

      // This month
      loginHistoryService.getLoginHistories(user, Date.today().moveToFirstDayOfMonth().getTime(), Date.monday().getTime()).then((resp) => {
        this.month = resp[1];
      });

      // Erlier
      loginHistoryService.getLoginHistories(user, 0, Date.today().moveToFirstDayOfMonth().getTime()).then((resp) => {
        this.earlier = resp[1];
      });
    }
  }
};
</script>
