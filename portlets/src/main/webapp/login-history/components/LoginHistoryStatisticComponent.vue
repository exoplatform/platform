<template>
  <div class="login-history-statistic">
    <div class="statisticDiv">
      <div class="clearfix selectWeek">
        <button class="pull-right btn"> {{ $t('loginHistory.week') }} <i class="uiIconMiniArrowDown"></i></button>
        <div class="btn-group">
          <button class="btn" data-placement="bottom" rel="tooltip" title="Preview" @click="prev()">&nbsp;<i class="uiIconArrowLeft"></i>&nbsp;</button>
          <button class="btn">{{ $t('loginHistory.week') }} {{ weekNumber }}</button>
          <button class="btn" data-placement="bottom" rel="tooltip" title="Next" @click="next()">&nbsp;<i class="uiIconArrowRight"></i>&nbsp;</button>
        </div>
      </div>
      <div v-if="chartData.length > 0" class="chart">
        <GChart
          :data="chartData"
          :options="chartOptions"
          type="ColumnChart"/>
        <div style="margin-top: 10px; margin-left: 12px;">
          {{ $t('loginHistory.avgLoginPerDay') }}: {{ averageLogin }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import loginHistoryService from '../loginHistoryService';
import { GChart } from 'vue-google-charts';
export default {
  components: {
    GChart
  },
  props: {
    user: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      startDate: null,
      weekNumber: -1,
      averageLogin: -1,
      chartData: [],
      chartOptions: {
        chartArea: {
          left: 20,
          right: 10
        },
        legend: {
          position: 'none'
        }
      }
    };
  },
  watch: {
    user: function() {
      this.getDailyStas(this.startDate.getTime());
    }
  },
  created() {
    this.startDate = Date.today().is().sunday() ? Date.last().monday() : Date.monday();
    this.weekNumber = this.startDate.getWeek();
    this.getDailyStas(this.startDate.getTime());
  },
  methods: {
    prev() {
      this.startDate = this.startDate.last().monday();
      this.weekNumber = this.startDate.getWeek();
      this.getDailyStas(this.startDate.getTime());
    },
    next() {
      this.startDate = this.startDate.next().monday();
      this.weekNumber = this.startDate.getWeek();
      this.getDailyStas(this.startDate.getTime());
    },
    async getDailyStas(currentWeek) {
      const data = await loginHistoryService.getDailyStats(this.user ? this.user : 'AllUsers', currentWeek);

      this.weekNumber = new Date(currentWeek).getWeek() ;

      const totalLogin = data[1].reduce((total, item) => {
        return total + (item.loginCount > -1 ? parseInt(item.loginCount) : 0);
      }, 0);
      const loginCount = data[1].reduce((total, item) => {
        return total + (item.loginCount > -1 ? 1 : 0);
      }, 0);

      const TWO = 2;
      this.averageLogin = loginCount > 0 ? (totalLogin/loginCount).toFixed(TWO) : 0;

      // Draw chart
      this.chartData = data[1].map(item => {
        return [
          new Date(item.loginDate).toString('ddd'),
          item.loginCount > -1 ? item.loginCount : 0,
          item.loginCount > 0 ? item.loginCount : null,
          new Date(item.loginDate).toString('MMM dd, yyyy')
        ];
      });

      // Unshift the axes
      this.chartData.unshift(['Day', 'Value', { role: 'annotation' }, {role: 'tooltip'}]);
    },
  }
};
</script>
<style scoped>
  .chart{
    width: 100%;
  }
</style>
