import { createLocalVue, shallowMount } from '@vue/test-utils';
import { eXoConstants } from '../../main/webapp/js/eXoConstants.js';
import ExoCalendar from '../../main/webapp/calendar-home-app/components/ExoCalendar';

const localVue = createLocalVue();

describe('ExoCalendar.test.js', () => {
  let cmp;
  const data = {
    displayedCalendars: [
      {'owner':'root','viewPermission':'','icsURL':'http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId/ics','color':'asparagus','name':'Root Root','timeZone':'Europe/Brussels','href':'http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId','id':'root-defaultCalendarId','type':'PERSONAL','editPermission':'','class':'class org.exoplatform.calendar.ws.bean.CalendarResource'},
      {'viewPermission':'*.*;','icsURL':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar106493027f0001010bec30b1aff4a93e/ics','color':'asparagus','name':'Administration','timeZone':'Europe/Brussels','groups':['/platform/administrators'],'href':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar106493027f0001010bec30b1aff4a93e','id':'calendar106493027f0001010bec30b1aff4a93e','type':'GROUP','editPermission':'/platform/administrators/:*.*;','class':'class org.exoplatform.calendar.ws.bean.CalendarResource'},
      {'viewPermission':'*.*;','icsURL':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar106493b87f000101633055f4ac73a8e3/ics','color':'asparagus','name':'Content Management','timeZone':'Europe/Brussels','groups':['/platform/web-contributors'],'href':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar106493b87f000101633055f4ac73a8e3','id':'calendar106493b87f000101633055f4ac73a8e3','type':'GROUP','editPermission':'/platform/web-contributors/:*.*;','class':'class org.exoplatform.calendar.ws.bean.CalendarResource'},
      {'viewPermission':'*.*;','icsURL':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar106495087f00010160b56d0a18ea102e/ics','color':'asparagus','name':'Executive Board','timeZone':'Europe/Brussels','groups':['/organization/management/executive-board'],'href':'http://localhost:8080/portal/rest/v1/calendar/calendars/calendar106495087f00010160b56d0a18ea102e','id':'calendar106495087f00010160b56d0a18ea102e','type':'GROUP','editPermission':'/organization/management/executive-board/:*.*;','class':'class org.exoplatform.calendar.ws.bean.CalendarResource'},
      {'owner':'/spaces/testspace','viewPermission':'','icsURL':'http://localhost:8080/portal/rest/v1/calendar/calendars/testspace_space_calendar/ics','color':'asparagus','name':'testSpace','timeZone':'Europe/Paris','groups':['/spaces/testspace'],'href':'http://localhost:8080/portal/rest/v1/calendar/calendars/testspace_space_calendar','id':'testspace_space_calendar','type':'GROUP','editPermission':'/spaces/testspace/:*.*;','class':'class org.exoplatform.calendar.ws.bean.CalendarResource'}
    ],
    displayedEvents: [
      {
        'to': '2019-02-28T01:00:00.000Z',
        'subject': 'testEvent',
        'attachments': [],
        'categories': [
          'http://localhost:8080/portal/rest/v1/calendar/categories/defaultEventCategoryIdAll'
        ],
        'categoryId': 'defaultEventCategoryIdAll',
        'calendarId': 'root-defaultCalendarId',
        'availability': 'busy',
        'participants': [
          'root'
        ],
        'uploadResources': null,
        'repeat': {
          'exclude': null,
          'repeatOn': null,
          'repeateBy': '',
          'every': 0,
          'enabled': false,
          'end': {
            'value': null,
            'type': 'neverEnd'
          },
          'type': 'norepeat'
        },
        'recurrenceId': null,
        'reminder': [
          {
            'summary': null,
            'eventId': 'Event2ea2e3ca7f0001017a518ecea57c0762',
            'reminderOwner': null,
            'reminderType': 'email',
            'emailAddress': 'root@gatein.com',
            'fromDateTime': {
              'date': 28,
              'time': 1551313800000,
              'year': 119,
              'month': 1,
              'day': 4,
              'hours': 1,
              'minutes': 30,
              'seconds': 0,
              'timezoneOffset': -60
            },
            'repeatInterval': 0,
            'alarmBefore': 5,
            'repeat': false,
            'description': '',
            'id': 'Reminder2ea2e3ca7f0001015b9ab33a4f5cb487'
          }
        ],
        'privacy': 'private',
        'originalEvent': null,
        'isOccur': false,
        'from': '2019-02-28T00:30:00.000Z',
        'calendar': 'http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId',
        'description': null,
        'location': null,
        'priority': 'none',
        'href': 'http://localhost:8080/portal/rest/v1/calendar/events/Event2ea2e3ca7f0001017a518ecea57c0762',
        'id': 'Event2ea2e3ca7f0001017a518ecea57c0762'
      }
    ],
  };
  beforeEach(() => {
    cmp = shallowMount(ExoCalendar, {
      localVue,
      mocks: {
        $t: () => {},
        $constants : eXoConstants
      }
    });

  });

  it('should display 1 event in list when 1 eventsDisplayedList in data', () => {
    cmp.vm.displayedCalendars = data.displayedCalendars;
    cmp.vm.displayedEvents = data.displayedEvents;
    cmp.vm.$nextTick(() => {
      const calElement = cmp.find('.eventTitle').find('span');
      expect(calElement.classes()).toContain('asparagus'); // calendar color
      expect(calElement.text()).toBe('Root Root'); // calendar Name
      const eventsList = cmp.find('.eventsList').findAll('li');
      expect(eventsList).toHaveLength(1); // 1 event
      const firstEvent = eventsList.at(0);
      expect(firstEvent.attributes().id).toBe('Event2ea2e3ca7f0001017a518ecea57c0762');
      expect(firstEvent.find('.eventSummary').find('a').text()).toBe('testEvent');
    });
  });
});