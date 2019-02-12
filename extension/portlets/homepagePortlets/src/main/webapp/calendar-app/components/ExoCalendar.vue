<template>
  <div class="calendarPortlet ">
    <div class="calendarPortletData uiBox">
      <h6 class="title clearfix">
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
      <a v-exo-tooltip.bottom.body="$t('settings.label')" v-show="!isInSpace" class="settingsLink actionIcon pull-right" @click="isSettings = true"><i class="uiIconSetting uiIconLightGray"></i> </a>
      <div v-if="isSettings" id="manage" class="tab-pane fade in active">
        <exo-calendar-settings @savedCalendar="eventSavedCalendar"></exo-calendar-settings>
      </div>
      <!-- events -->
      <div id="CalendarContainer" class="events uiContentBox">
        <div class="eventTitle">
          <span v-for="cal in displayedCalendars" :key="cal" :class="['calendarName ' + cal.calendarColor]" :id="cal.id" :title="cal.name">{{ cal.name }}</span>
        </div>
        <ul class="eventsList ">
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
                  <!-- <% } else if (event.toDateTime.getTime() - event.fromDateTime.getTime() > 86399999) {
                  from = sdf1.format(event.fromDateTime);
                  String year=event.fromDateTime.format("yyyy");
                  String[] dateSplit = from.split("/");
                  if(dateSplit.length > 1) from=dateSplit[0]+"/"+dateSplit[1]+"/"+year;
                  to = sdf1.format(event.toDateTime);
                  year=event.toDateTime.format("yyyy");
                  dateSplit =to.split("/");
                  if(dateSplit.length > 1) to=dateSplit[0]+"/"+dateSplit[1]+"/"+year;
                  %> -->
                  <div v-else-if="getEventDuration(event) > 86399999">
                    <span>{{ event.from }}</span> -
                    <span>{{ event.to }}</span>
                  </div>
                  <!-- <% } else if (event.toDateTime.getTime() - event.fromDateTime.getTime() < 86399999) {
                  from = sdf2.format(event.fromDateTime);
                  to = sdf2.format(event.toDateTime);
                  if(locale.getLanguage().equals("en")){
                  if(from.indexOf("00")==2)   from=from.substring(0,1)+ from.substring(4);
                  if(from.indexOf("00")==3)   from=from.substring(0,2)+ from.substring(5);
                  if(to.indexOf("00")==2)   to=to.substring(0,1) + to.substring(4);
                  if(to.indexOf("00")==3)   to=to.substring(0,2) + to.substring(5);
                  }
                  %> -->
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
    </div>
  </div>
</template>

<script>
  import * as calendarServices from '../calendarServices';

  export default {
    data() {
      return {
        displayedCalendars: [],
        eventsDisplayedList: [],
        calendarDisplayedMap: [],
        dateLabel: this.$t("today.label") + ": ",
        date_act: '',
        isInSpace: false,
        isSettings: false,
        nbclick: 0
      };
    },
    created() {
      this.initCalendar();
    },
    methods: {
      initCalendar() {
        calendarServices.getDisplayedCalendars(this.nbclick).then(response => {
          if (response) {
            this.displayedCalendars = this.parseArray(response.displayedCalendars);
            this.eventsDisplayedList = this.parseArray(response.eventsDisplayedList);
            this.calendarDisplayedMap = this.parseMap(response.calendarDisplayedMap);
            this.date_act = response.date_act;
            if (nbclick === 0) {
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
      eventSavedCalendar(calendars) {
        console.log("Hello the child event is catched from parent " + calendars);
        this.isSettings = false;
      },
      getEventCssClass(event) {
        if (new Date() > Date.parse(event.toDateTime)) {
          return "pastEvent";
        }
        else {
          return "eventItems";
        }
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
      }
    }
  };
</script>