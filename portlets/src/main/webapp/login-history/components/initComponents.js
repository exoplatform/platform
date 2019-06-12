import LoginHistoryAppComponent from './LoginHistoryAppComponent.vue';
import LoginHistoryLastLoginsComponent from './LoginHistoryLastLoginsComponent.vue';
import LoginHistoryStatisticComponent from './LoginHistoryStatisticComponent.vue';
import LoginHistoryHistoriesComponent from './LoginHistoryHistoriesComponent.vue';
import LoginHistoryUserStatistic from './LoginHistoryUserStatistic.vue';

const components = {
  'exo-login-history-app': LoginHistoryAppComponent,
  'exo-login-history-last-logins': LoginHistoryLastLoginsComponent,
  'exo-login-history-statistic': LoginHistoryStatisticComponent,
  'exo-login-history-histories': LoginHistoryHistoriesComponent,
  'exo-login-history-user-statistic': LoginHistoryUserStatistic
};

for(const key in components) {
  Vue.component(key, components[key]);
}
