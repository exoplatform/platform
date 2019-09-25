<template>
  <fieldset class="userForm">
    <legend class="StyleLegend">{{ user.userName }}</legend>
    <div class="userContent clearfix">
      <div class="leftContent pull-left">
        <div :class="{selectIteam: activeView === 'statistic'}" class="iteamIcon">
          <a href="#" @click="changeView('statistic')">
            <i class="iconStastics"></i><span v-if="activeView === 'statistic'" class="caret"></span>
          </a>
        </div>
        <div :class="{selectIteam: activeView === 'history'}" class="iteamIcon">
          <a href="#" @click="changeView('history')">
            <i class="iconDetail"></i><span v-if="activeView === 'history'" class="caret"></span>
          </a>
        </div>
      </div>
      <div class="rightContent">
        <exo-login-history-statistic v-if="activeView === 'statistic'" :user="user.userId"></exo-login-history-statistic>
        <exo-login-history-histories v-if="activeView === 'history'" :user="user.userId"></exo-login-history-histories>
      </div>
    </div>
  </fieldset>
</template>
<script>
export default {
  props: {
    user: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      activeView: 'statistic'
    };
  },
  methods: {
    changeView(viewName) {
      this.activeView = viewName;
    }
  }
};
</script>
