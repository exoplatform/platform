<template>
  <div class="calendarPortlet">
    <div class="calendarPortletData uiBox">
      <h6 v-show="!isSettings" class="title clearfix">
        <a v-exo-tooltip.bottom.body="$t('prev.day')" href="#" class="actionIcon prevDate pull-left" @click="incDecDate(-1)">
          <i class="uiIconMiniArrowLeft uiIconLightGray"></i>
        </a>
        <a v-exo-tooltip.bottom.body="$t('next.day')" href="#" class="actionIcon nextDate pull-right" @click="incDecDate(1)">
          <i class="uiIconMiniArrowRight uiIconLightGray"></i>
        </a>
        <div class="currentDateContainer">
          <center><a href="#">{{ dateLabel }}</a></center>
        </div>
      </h6>
      <a v-exo-tooltip.bottom.body="$t('settings.label')" v-show="spaceGroup === '' && !isSettings" class="settingsLink actionIcon pull-right" @click="renderSettings()"><i class="uiIconSetting uiIconLightGray"></i> </a>
      <div v-if="isSettings && spaceGroup === ''" id="manage" class="tab-pane fade in active">
        <exo-home-calendar-settings displayed-calendars="allDisplayedCals" non-displayed-calendars="nonDisplayedCals" @savedCalendar="eventSavedCalendar"></exo-home-calendar-settings>
      </div>
      <!-- events -->
      <div v-show="displayedCalendars.length > 0 && displayedEvents.length > 0 && !isSettings" id="CalendarContainer" class="events uiContentBox">
        <div class="eventTitle">
          <span v-for="cal in displayedCalendars" :key="cal" :class="['calendarName ' + cal.color]" :id="cal.id" :title="cal.name">{{ cal.name }}</span>
        </div>
        <ul class="eventsList">
          <li v-for="event in displayedEvents" :key="event" :class="getEventCssClass(event)" :id="event.id">
            <div :class="findObjectById(allDisplayedCals, event.calendarId).color">
              <div class="clearfix itemColor" >
                <div class="pull-left eventSummary">
                  <a :href="getEventLink(event.id)" v-html="event.subject"></a>
                </div>
                <div class="pull-right time">
                  <div v-if="getEventDuration(event) === ONE_DAY_MS">
                    <span>{{ $t('all.day.label') }}</span>
                  </div>
                  <div v-else-if="getEventDuration(event) > ONE_DAY_MS">
                    <span>{{ moment(evt.from).toDate().toLocaleDateString(exoConstants.LANG) }}</span> -
                    <span>{{ moment(evt.to).toDate().toLocaleDateString(exoConstants.LANG) }}</span>
                  </div>
                  <div v-else>
                    <span> {{ getEventFromTimeLabel(event) }} - {{ getEventToTimeLabel(event) }} </span>
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import * as calendarServices from '../calendarServices';
import { exoConstants } from '../../js/eXoConstants.js';
import moment from 'moment';

export default {
  data() {
    return {
      allDisplayedCals: [],
      displayedCalendars: [],
      nonDisplayedCals: [],
      allEvents: [],
      displayedEvents: [],
      date_act: moment().startOf('day'),
      dateLabel: `${this.$t('today.label')}: ${new Date().toLocaleDateString(exoConstants.LANG)}`,
      spaceGroup: `${exoConstants.SPACE_GROUP}`,
      isSettings: false,
      nbclick: 0,
      ONE_DAY_MS: 86399999,
      SECOND_INDEX: 2,
      THIRD_INDEX: 3,
      FOURTH_INDEX: 4,
      FIFTH_INDEX: 5
    };
  },
  created() {
    this.initCalendar();
    const delay = 100;
    setInterval(function () {
      this.initCalendar();
    }.bind(this), delay);
  },
  methods: {
    initCalendar() {
      let start = this.date_act.startOf('day');
      start = start.format(moment.HTML5_FMT.DATETIME_LOCAL_MS);
      const end = this.date_act.endOf('day').format(moment.HTML5_FMT.DATETIME_LOCAL_MS);
      calendarServices.getEvents(start, end).then(response => {
        this.allEvents = response.data;
      });
      calendarServices.getDisplayedCalendars().then(response => {
        if (response) {
          this.allDisplayedCals = this.parseArray(response.allDisplayedCals);
          this.nonDisplayedCals = this.parseArray(response.nonDisplayedCals);
          if (this.spaceGroup !== '') {
            this.filterSpaceEvents();
          } else {
            this.filterEvents();
          }
        }
      });
    },
    incDecDate(days) {
      this.date_act.startOf('day').add(days, 'day');
      const diffDays = this.date_act.diff(moment().startOf('day'), 'days');
      if (diffDays === 0) {
        this.dateLabel = `${this.$t('today.label')}: ${this.date_act.toDate().toLocaleDateString(exoConstants.LANG)}`;
      } else if (diffDays === 1) {
        this.dateLabel = `${this.$t('tomorrow.label')}: ${this.date_act.toDate().toLocaleDateString(exoConstants.LANG)}`;
      } else if (diffDays === -1) {
        this.dateLabel = `${this.$t('yesterday.label')}: ${this.date_act.toDate().toLocaleDateString(exoConstants.LANG)}`;
      } else {
        this.dateLabel = this.date_act.toDate().toLocaleDateString(exoConstants.LANG);
      }
      this.initCalendar();
    },
    getEventLink(eventId) {
      return `${exoConstants.PORTAL}/${exoConstants.PORTAL_NAME}/calendar/details/${eventId}`;
    },
    eventSavedCalendar() {
      this.isSettings = false;
      this.initCalendar();
    },
    getEventCssClass(event) {
      if (new Date() > Date.parse(event.to)) {
        return 'pastEvent';
      }
      else {
        return 'eventItems';
      }
    },
    filterEvents() {
      this.displayedCalendars = [];
      this.displayedEvents = [];
      for (const evt of this.allEvents) {
        const notDisplayedFound = this.findObjectById(this.nonDisplayedCals, evt.calendarId);
        if (!notDisplayedFound) {
          const displayedFound = this.findObjectById(this.allDisplayedCals, evt.calendarId);
          if (displayedFound) {
            if (!this.findObjectById(this.displayedCalendars, displayedFound.id)) {
              this.displayedCalendars.push(displayedFound);
              this.displayedEvents.push(evt);
            }
          }
        }
      }
    },
    filterSpaceEvents() {
      this.displayedCalendars = [];
      this.displayedEvents = [];
      const calendarSpaceId = `${this.spaceGroup}_space_calendar`;
      for (const evt of this.allEvents) {
        if (evt.calendarId === calendarSpaceId) {
          this.displayedEvents.push(evt);
          const displayedFound = this.findObjectById(this.allDisplayedCals, evt.calendarId);
          if (displayedFound) {
            if (!this.findObjectById(this.displayedCalendars, displayedFound.id)) {
              this.displayedCalendars.push(displayedFound);
            }
          }
        }
      }
    },
    getEventFromTimeLabel(evt) {
      const start = moment(evt.from).toDate().toLocaleTimeString(exoConstants.LANG, {hour: '2-digit', minute:'2-digit'});
      if(exoConstants.LANG === 'en') {
        if (start.indexOf('00') === this.SECOND_INDEX) {
          return start.substring(0, 1) + start.substring(this.FOURTH_INDEX);
        }
        if (start.indexOf('00') === this.THIRD_INDEX) {
          return start.substring(0, this.SECOND_INDEX) + start.substring(this.FIFTH_INDEX);
        }
      }
      return start;
    },
    getEventToTimeLabel(evt) {
      const end = moment(evt.to).toDate().toLocaleTimeString(exoConstants.LANG, {hour: '2-digit', minute:'2-digit'});
      if(exoConstants.LANG === 'en') {
        if (end.indexOf('00') === this.SECOND_INDEX) {
          return end.substring(0, 1) + end.substring(this.FOURTH_INDEX);
        }
        if (end.indexOf('00') === this.THIRD_INDEX) {
          return end.substring(0, this.SECOND_INDEX) + end.substring(this.FIFTH_INDEX);
        }
      }
      return end;
    },
    getEventDuration(event) {
      return new Date(event.to) - new Date(event.from);
    },
    parseArray(object) {
      const parsed = [];
      for (let el of object) {
        el = JSON.parse(el);
        parsed.push(el);
      }
      return parsed;
    },
    findObjectById(array, value) {
      const val = Array.isArray(value) ? value[0] : value;
      for (const obj of array) {
        if (obj.id === val) {
          return obj;
        }
      }
    },
    renderSettings() {
      this.isSettings = true;
      this.spaceGroup = `${exoConstants.SPACE_GROUP}`;
    }
  }
};
</script>