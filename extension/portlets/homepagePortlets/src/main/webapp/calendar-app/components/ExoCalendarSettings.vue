<template>
  <div class="settingsCalContainer" >
    <div class="uiBox settingsContainerData" >
      <h6 class="title  center">HELLO FROM SETTINGS</h6>
      <div class= "uiContentBox" >
      </div>
      <div class= "uiContentBox " >
        <div class="settingValidationButton"><button class=" btn" @click="saveSettings">OK</button></div>
      </div>
    </div>
  </div>
</template>

<script>

  export default {
    name: "CalendarSettings",
    data() {
      return {
        displayedCalendars: []
      };
    },
    methods: {
      saveSettings() {
        var calendars = this.displayedCalendars;
        this.$emit('savedCalendar', {calendars});
      }
    }
  };
</script>