<template>
  <div>
    <input v-model="user" class="searchUser" name="titleInput" placeholder="Search user" type="text" @keyup.enter="doSearch()"/>
    <div class="lastLoginsContainerDiv">
      <div class="lastLoginsDataDiv">
        <div class="dataTables_wrapper" role="grid">
          <table class="uiGrid table table-hover table-striped dataTable">
            <thead>
              <tr role="row">
                <th class="sorting" role="columnheader" tabindex="0" aria-controls="lastLoginsTable" rowspan="1" colspan="1" style="width: 60px;" aria-label="User : activate to sort column ascending"><small>{{ $t('loginHistory.user') }}</small></th>
                <th class="sorting" role="columnheader" tabindex="0" aria-controls="lastLoginsTable" rowspan="1" colspan="1" style="width: 60px;" aria-label="Last Login  : activate to sort column ascending"><small>{{ $t('loginHistory.lastLogin') }}</small></th>
                <th class="sorting" role="columnheader" tabindex="0" aria-controls="lastLoginsTable" rowspan="1" colspan="1" style="width: 57px;" aria-sort="ascending" aria-label="Before that  : activate to sort column ascending"><small>{{ $t('loginHistory.beforeThat') }}</small></th>
              </tr>
            </thead>
            <tbody role="alert" aria-live="polite" aria-relevant="all">
              <tr v-for="login in loginItems" :id="login.userId" :key="login.userId">
                <td>
                  <a href="#" class="detailUser" @click="showStatisticForUser(login)"><small>{{ login.userName }}</small></a>
                </td>
                <td><small>{{ login.lastLogin | prettyTimeDiff }}</small></td>
                <td><small>{{ login.beforeLastLogin | prettyTimeDiff }}</small></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <exo-login-history-user-statistic v-if="selectedUser" :user="selectedUser"></exo-login-history-user-statistic>
  </div>
</template>
<script>
import loginHistoryService from '../loginHistoryService';

export default {
  data() {
    return {
      user: '',
      limit: 5,
      loginItems: [],
      interval: 0,
      selectedUser: null
    };
  },
  created() {
    this.doSearch();

    const FETCH_INTERVAL = 30000;
    this.interval = setInterval(() => this.doSearch(), FETCH_INTERVAL);
  },
  beforeDestroy() {
    clearInterval(this.interval);
  },
  methods: {
    doSearch() {
      let limit;
      let searchUser;

      if (this.user && this.user.trim().length > 0) {
        limit = '5';
        searchUser = this.user;
      } else {
        limit = '5';
        searchUser = '%';
      }

      this.getLoginItems(searchUser, limit);
    },
    showStatisticForUser(user) {
      this.selectedUser = user;
    },
    async getLoginItems(user, limit) {
      this.loginItems = await loginHistoryService.getLastLogins(user, limit);
    }
  }
};
</script>
