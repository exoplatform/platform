import { createLocalVue, shallowMount } from '@vue/test-utils';
import { eXoConstants } from '../../main/webapp/js/eXoConstants.js';
import ExoCalendarSettings from '../../main/webapp/calendar-home-app/components/ExoCalendarSettings';

const localVue = createLocalVue();

describe('ExoCalendarSettings.test.js', () => {
  let cmp;
  const data = {
    displayedCalendars: [
      {'owner':'root','viewPermission':'','icsURL':'http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId/ics','color':'asparagus','name':'Root Root','timeZone':'Europe/Brussels','href':'http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId','id':'root-defaultCalendarId','type':'PERSONAL','editPermission':'','class':'class org.exoplatform.calendar.ws.bean.CalendarResource'}
    ],
    nonDisplayedCalendars: [
      {'viewPermission':'*.*;','icsURL':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar1064933f7f00010155d364ce68e65f3b/ics','color':'asparagus','name':'Users','timeZone':'Europe/Brussels','groups':['/platform/users'],'href':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar1064933f7f00010155d364ce68e65f3b','id':'calendar1064933f7f00010155d364ce68e65f3b','type':'GROUP','editPermission':'/platform/users/:*.*;','class':'class org.exoplatform.calendar.ws.bean.CalendarResource'},
      {'viewPermission':'*.*;','icsURL':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar106495807f0001014ae369389b7485ca/ics','color':'asparagus','name':'Employees','timeZone':'Europe/Brussels','groups':['/organization/employees'],'href':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar106495807f0001014ae369389b7485ca','id':'calendar106495807f0001014ae369389b7485ca','type':'GROUP','editPermission':'/organization/employees/:*.*;','class':'class org.exoplatform.calendar.ws.bean.CalendarResource'}
    ]
  };
  beforeEach(() => {
    cmp = shallowMount(ExoCalendarSettings, {
      localVue,
      mocks: {
        $t: () => {},
        $constants : eXoConstants
      }
    });

  });

  it('should display 1 calendar in displayed list when 1 displayedCalendars in data', () => {
    cmp.vm.displayedCalendars = data.displayedCalendars;
    cmp.vm.$nextTick(() => {
      const calsList = cmp.find('.DisplayedCalendarContainer').findAll('.calendarName');
      expect(calsList).toHaveLength(1); // 1 calendar
      const firstCal = calsList.at(0);
      expect(firstCal.attributes().id).toBe('root-defaultCalendarId'); // calendar id
      expect(firstCal.classes()).toContain('asparagus'); // calendar color
      expect(firstCal.find('span').text()).toBe('Root Root'); // calendar name
    });
  });

  it('should display 2 calendars in non displayed list when 2 nonDisplayedCalendars in data', () => {
    cmp.vm.nonDisplayedCalendars = data.nonDisplayedCalendars;
    cmp.vm.$nextTick(() => {
      const calsList = cmp.find('#nonDisplayedCalendarContainer').findAll('li');
      expect(calsList).toHaveLength(2); // 2 calendars
      const firstCal = calsList.at(0);
      expect(firstCal.attributes().id).toBe('calendar1064933f7f00010155d364ce68e65f3b'); // calendar id
      expect(firstCal.find('.colorBox').classes()).toContain('asparagus'); // calendar color
      expect(firstCal.find('.calName').text()).toBe('Users'); // calendar name
      const secondCal = calsList.at(1);
      expect(secondCal.attributes().id).toBe('calendar106495807f0001014ae369389b7485ca'); // calendar id
      expect(secondCal.find('.colorBox').classes()).toContain('asparagus'); // calendar color
      expect(secondCal.find('.calName').text()).toBe('Employees'); // calendar name
    });
  });
});