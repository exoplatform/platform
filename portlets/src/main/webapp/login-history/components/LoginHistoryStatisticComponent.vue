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
      <div class="chart">
        <v-chart :options="chartOptions"/>
        <div class="uiContentBox">
          {{ $t('loginHistory.avgLoginPerDay') }}: {{ averageLogin }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import loginHistoryService from '../loginHistoryService';
import ECharts from 'vue-echarts';
import 'echarts/lib/chart/bar';

export default {
  components: {
    'v-chart': ECharts
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
        xAxis: {
          data: []
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        grid: {
          left: '3%',
          right: '3%'
        },
        series: [
          {
            type: 'bar',
            label: {
              show: true,
              position: 'insideTop'
            },
            itemStyle: {
              color: '#3398DB',
              opacity: 0.8
            },
            animationDurationUpdate: 0,
            data: []
          }
        ]
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
      this.chartOptions.xAxis.data = [];
      this.chartOptions.series[0].data = [];
      const stats = data[1];
      for(let i=0; i<stats.length; i++) {
        this.chartOptions.xAxis.data.push(new Date(stats[i].loginDate).toString('MMM dd, yyyy'));
        this.chartOptions.series[0].data.push(stats[i].loginCount > 0 ? stats[i].loginCount : null);
      }
    },
  }
};
</script>
<style scoped>
  .chart {
    width: 100%;
    height: 200px;
  }
  .echarts {
    width: 100%;
    height: 90%;
  }
</style>
