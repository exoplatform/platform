<template>
  <div class="settingsCalContainer" >
    <div class="uiBox settingsContainerData" >
      <h6 class="title  center">{{ $t('settings.label') }}</h6>
      <div class= "uiContentBox" >
        <p>{{ $t('displayed.calendar.label') }}</p>
        <div class="DisplayedCalendarContainer">
          <span v-for="(cal, index) in displayedCalendars" v-show="displayedCalendars.length > 0" :key="cal" :class="['calendarName ' + cal.color]" :id="cal.id">
            <span v-exo-tooltip.bottom.body="cal.name">{{ cal.name }}</span>
            <i v-exo-tooltip.bottom.body="$t('calendar.remove')" class="uiIconDel" @click="removeCalendar(cal, index)"></i>
          </span>
        </div>
      </div>
      <div class= "uiContentBox " >
        <p>{{ $t('display.additional.calendar.label') }}</p>
        <input v-model="searchKey" :placeholder="$t('search.calendar.label')" type="text" class="PLFcalendarSearchKey" @input="search()"/>
        <ul id="nonDisplayedCalendarContainer" class="calendarItems NonDisplayedCalendar">
          <li v-for="(nonDispCal, index) in nonDisplayedCalendars" v-show="nonDisplayedCalendars.length > 0 && searchKey === ''" :key="nonDispCal" :id="nonDispCal.id" class="calendarItem clearfix" @mouseover="isShowAdd = index" @mouseout="isShowAdd = null">
            <a v-exo-tooltip.bottom.body="$t('calendar.add')" v-show="isShowAdd === index" href="javascript:void(0);" class="addButton pull-right" @click="addDisplayCalendar(nonDispCal, index, false)">
              <i class="uiIconSimplePlusMini uiIconLightGray"></i>
            </a>
            <a :class="[nonDispCal.color + ' colorBox pull-left']" href="javascript:void(0);"></a>
            <a v-exo-tooltip.bottom.body="nonDispCal.name" href="javascript:void(0);" class="calName">{{ nonDispCal.name }}</a>
          </li>
          <li v-for="(result, index) in searchResults" v-show="searchKey !== ''" :key="result" :id="result.id" class="calendarItem clearfix" @mouseover="isShowResultAdd = index" @mouseout="isShowResultAdd = null">
            <a v-exo-tooltip.bottom.body="$t('calendar.add')" v-show="isShowResultAdd === index" href="javascript:void(0);" class="addButton pull-right" @click="addDisplayCalendar(result, index, true)">
              <i class="uiIconSimplePlusMini uiIconLightGray"></i>
            </a>
            <a :class="[result.color + ' colorBox pull-left']" href="javascript:void(0);"></a>
            <a v-exo-tooltip.bottom.body="result.name" href="javascript:void(0);" class="calName">{{ result.name }}</a>
          </li>
        </ul>
        <div class="settingValidationButton"><button class=" btn" @click="saveSettings">OK</button></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as calendarServices from '../calendarServices';

export default {
  name: 'CalendarSettings',
  props: {
    displayedCalendars: {
      type: Object,
      required: true
    },
    nonDisplayedCalendars: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      isShowAdd: null,
      isShowResultAdd: null,
      searchKey: '',
      searchResults: []
    };
  },
  methods: {
    saveSettings() {
      calendarServices.updateSettings(this.nonDisplayedCalendars.map(cal => cal.id).join()).then(this.$emit('savedCalendar'));
    },
    addDisplayCalendar(item, i, isSearch) {
      if (isSearch) {
        this.displayedCalendars.push(item);
        this.searchResults.splice(i, 1);
        const index = this.nonDisplayedCalendars.indexOf(item);
        if (index !== -1) {
          this.nonDisplayedCalendars.splice(index, 1);
        }
      } else {
        this.displayedCalendars.push(item);
        this.nonDisplayedCalendars.splice(i, 1);
      }
    },
    removeCalendar(item, i) {
      $('.tooltip.fade.bottom.in').remove();
      this.nonDisplayedCalendars.push(item);
      this.displayedCalendars.splice(i, 1);
    },
    search() {
      this.searchResults = [];
      if (this.searchKey !== '') {
        for (const cal of this.nonDisplayedCalendars) {
          if (cal.name.toLowerCase().includes(this.searchKey.toLowerCase())) {
            this.searchResults.push(cal);
          }
        }
      }
    }
  }
};
</script>