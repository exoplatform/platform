import { createLocalVue, shallowMount } from '@vue/test-utils';
import { eXoConstants } from '../../main/webapp/js/eXoConstants.js';
import ExoCalendar from '../../main/webapp/calendar-app/components/ExoCalendar';

const localVue = createLocalVue();

describe('ExoCalendar.test.js', () => {
    let cmp;
    const data = {
        displayedCalendars: [
            {"owner":"root","viewPermission":"","icsURL":"http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId/ics","color":"asparagus","name":"Root Root","timeZone":"Europe/Brussels","href":"http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId","id":"root-defaultCalendarId","type":"PERSONAL","editPermission":"","class":"class org.exoplatform.calendar.ws.bean.CalendarResource"}
        ],
        eventsDisplayedList: [
            {"calendar":"http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId","attachments":[],"reminder":["org.exoplatform.calendar.service.Reminder@5a680ed3"],"subject":"testEvent","privacy":"private","availability":"busy","priority":"none","repeat":"org.exoplatform.calendar.ws.bean.RepeatResource@3a602958","from":"3 AM","to":"3:30 AM","categories":["http://localhost:8080/portal/rest/v1/calendar/categories/defaultEventCategoryIdAll"],"href":"http://localhost:8080/portal/rest/v1/calendar/events/Eventf1b4acfd7f0001013f3d88c55ca20820","id":"Eventf1b4acfd7f0001013f3d88c55ca20820","class":"class org.exoplatform.calendar.ws.bean.EventResource","categoryId":"defaultEventCategoryIdAll","participants":["root"]}
        ],
        calendarDisplayedMap: {
            "root-defaultCalendarId": {"owner":"root","viewPermission":"","icsURL":"http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId/ics","color":"asparagus","name":"Root Root","timeZone":"Europe/Brussels","href":"http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId","id":"root-defaultCalendarId","type":"PERSONAL","editPermission":"","class":"class org.exoplatform.calendar.ws.bean.CalendarResource"}
        },
        dateLabel: "today: ",
        date_act: "2/15/19",
        spaceId: '',
        isSettings: false,
        nbclick: 0
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
        cmp.vm.eventsDisplayedList = data.eventsDisplayedList;
        cmp.vm.calendarDisplayedMap = data.calendarDisplayedMap;
        cmp.vm.$nextTick(() => {
            const calElement = cmp.find('.eventTitle').find('span');
            expect(calElement.classes()).toContain('asparagus'); // calendar color
            expect(calElement.text()).toBe('Root Root'); // calendar Name
            const eventsList = cmp.find('.eventsList').findAll('li');
            expect(eventsList).toHaveLength(1); // 1 event
            const firstEvent = eventsList.at(0);
            expect(firstEvent.attributes().id).toBe('Eventf1b4acfd7f0001013f3d88c55ca20820');
            expect(firstEvent.find('.eventSummary').find('a').text()).toBe('testEvent');
        });
    });
});