<template>
  <div class="calendarPortlet ">
    <div class="calendarPortletData uiBox">
      <h6 v-show="!isSettings" class="title clearfix">
        <a v-exo-tooltip.bottom.body="$t('prev.day')" href="#" class="actionIcon prevDate pull-left" @click="nbclick--; initCalendar()">
          <i class="uiIconMiniArrowLeft uiIconLightGray"></i>
        </a>
        <a v-exo-tooltip.bottom.body="$t('next.day')" href="#" class="actionIcon nextDate pull-right" @click="nbclick++; initCalendar()">
          <i class="uiIconMiniArrowRight uiIconLightGray"></i>
        </a>
        <div class="currentDateContainer">
          <center><a href="#">{{ dateLabel }}{{ date_act }}</a></center>
        </div>
      </h6>
      <a v-exo-tooltip.bottom.body="$t('settings.label')" v-show="spaceId === '' && !isSettings" class="settingsLink actionIcon pull-right" @click="renderSettings()"><i class="uiIconSetting uiIconLightGray"></i> </a>
      <div v-if="isSettings && spaceId === ''" id="manage" class="tab-pane fade in active">
        <exo-calendar-settings :displayed="allDisplayedCals" :nondisplayed="nonDisplayedCals" @savedCalendar="eventSavedCalendar"></exo-calendar-settings>
      </div>
      <!-- events -->
      <div v-show="displayedCalendars.length > 0 && eventsDisplayedList.length > 0 && !isSettings" id="CalendarContainer" class="events uiContentBox">
        <div class="eventTitle">
          <span v-for="cal in displayedCalendars" :key="cal" :class="['calendarName ' + cal.color]" :id="cal.id" :title="cal.name">{{ cal.name }}</span>
        </div>
        <ul class="eventsList">
          <li v-for="event in eventsDisplayedList" :key="event" :class="getEventCssClass(event)" :id="event.id">
            <div :class="calendarDisplayedMap[event.calendar.substring(event.calendar.lastIndexOf('/')+1)].color">
              <div class="clearfix itemColor" >
                <div class="pull-left eventSummary">
                  <a :href="['/portal/intranet/calendar/details/' + event.id]" v-html="event.subject"></a>
                </div>
                <div class="pull-right time">
                  <div v-if="getEventDuration(event) === 86399999">
                    <span>{{ $t('all.day.label') }}</span>
                  </div>
                  <div v-else-if="getEventDuration(event) > 86399999">
                    <span>{{ event.from }}</span> -
                    <span>{{ event.to }}</span>
                  </div>
                  <div v-else>
                    <span> {{ event.from }} - {{ event.to }} </span>
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
      <!--end events-->
      <!-- tasks -->
      <h6 v-show="tasksDisplayedList.length > 0 && !isSettings" class="title taskTitle">{{ $t('tasks.calendar.label') }}</h6>
      <div v-show="tasksDisplayedList.length > 0 && !isSettings" class="tasks uiContentBox">
        <ul class="tasksList">
          <li v-for="task in tasksDisplayedList" :key="task" :class="getTaskCssClass(task)">
            <a :href="['/portal/intranet/calendar/details/' + task.id]" class="eventSummary" v-html="task.name">
            </a>
          </li>
        </ul>
      </div>
      <!-- end tasks -->
    </div>
  </div>
</template>

<script>
  import * as calendarServices from '../calendarServices';
  import { exoConstants } from '../../js/eXoConstants.js';

  export default {
    data() {
      return {
        displayedCalendars: [],
        allDisplayedCals: [],
        nonDisplayedCals: [],
        eventsDisplayedList: [],
        tasksDisplayedList: [],
        calendarDisplayedMap: [],
        dateLabel: this.$t("today.label") + ": ",
        date_act: '',
        spaceId: `${exoConstants.SPACE_ID}`,
        isSettings: false,
        nbclick: 0
      };
    },
    created() {
      this.initCalendar();
      setInterval(function () {
        this.initCalendar();
      }.bind(this), 100);
    },
    methods: {
      initCalendar() {
        calendarServices.getDisplayedCalendars(this.nbclick, this.spaceId).then(response => {
          if (response) {
            this.displayedCalendars = this.parseArray(response.displayedCalendars);
            this.eventsDisplayedList = this.parseArray(response.eventsDisplayedList);
            this.calendarDisplayedMap = this.parseMap(response.calendarDisplayedMap);
            this.date_act = response.date_act;
            if (this.nbclick === 0) {
              this.dateLabel = this.$t("today.label") + ": ";
            } else if (this.nbclick === 1) {
              this.dateLabel = this.$t("tomorrow.label") + ": ";
            } else if (this.nbclick === -1) {
              this.dateLabel = this.$t("yesterday.label") + ": ";
            } else {
              this.dateLabel = '';
            }
          }
        });
      },
      eventSavedCalendar() {
        this.isSettings = false;
        this.initCalendar();
      },
      getEventCssClass(event) {
        if (new Date() > Date.parse(event.toDateTime)) {
          return 'pastEvent';
        }
        else {
          return 'eventItems';
        }
      },
      getTaskCssClass(task) {
        if ('completed' === task.status) {
          return "taskCompleted";
        } else if (('needs-action' === task.status) && (new Date() < Date.parse(task.to))) {
          return "taskNotCompleted";
        } else if (('needs-action' === task.status) && (new Date() > Date.parse(task.to))) {
          return "taskLateNotCompleted"  ;
        }
        return "";
      },
      getEventDuration(event) {
        return (new Date(event.to) - new Date(event.from));
      },
      parseArray(object) {
        var parsed = [];
        for (let el of object) {
          el = JSON.parse(el);
          parsed.push(el);
        }
        return parsed;
      },
      parseMap(map) {
        var parsed = {};
        Object.keys(map).forEach(function(key) {
          parsed[key] = JSON.parse(map[key]);
        });
        return parsed;
      },
      renderSettings() {
        this.isSettings = true;
        this.spaceId = `${exoConstants.SPACE_ID}`;
        calendarServices.getSettings().then(response => {
          if (response) {
            this.allDisplayedCals = this.parseArray(response.allDisplayedCals);
            this.nonDisplayedCals = this.parseArray(response.nonDisplayedCals);
          }
        });
      }
    }
  };
</script>