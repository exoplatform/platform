<template>
  <div class="login-history-app uiGadgetThemes uiBox">
    <h6 class="title">{{ $t('loginHistory.title') }}</h6>
    <div class="uiContentBox">
      <div class="gadContent">
        <div class="uiTabSelectorCenter">
          <div class="btn-group">
            <button :class="{'active': activeView === 'login-history'}"
                    class="btn"
                    @click="changeView('login-history')">{{ $t('loginHistory.loginHistory') }}</button>
            <button :class="{'active': activeView === 'statistic'}"
                    class="btn"
                    @click="changeView('statistic')">{{ $t('loginHistory.statistic') }}</button>
            <button :class="{'active': activeView === 'history'}"
                    class="btn"
                    @click="changeView('history')">{{ $t('loginHistory.history') }}</button>
          </div>
        </div>
        <div class="content">
          <exo-login-history-last-logins v-if="activeView === 'login-history'"></exo-login-history-last-logins>
          <exo-login-history-statistic v-if="activeView === 'statistic'"></exo-login-history-statistic>
          <exo-login-history-histories v-if="activeView === 'history'"></exo-login-history-histories>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      activeView: 'login-history'
    };
  },
  methods: {
    changeView(viewName) {
      this.activeView = viewName;
    }
  }
};
</script>
