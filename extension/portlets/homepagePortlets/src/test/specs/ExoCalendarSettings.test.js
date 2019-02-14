import { createLocalVue, shallowMount } from '@vue/test-utils';
import { eXoConstants } from '../../main/webapp/js/eXoConstants.js';
import ExoCalendarSettings from '../../main/webapp/calendar-app/components/ExoCalendarSettings';

const localVue = createLocalVue();

describe('ExoCalendarSettings.test.js', () => {
    let cmp;
    const data = {
        displayed: [
            {"owner":"root","viewPermission":"","icsURL":"http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId/ics","color":"asparagus","name":"Root Root","timeZone":"Europe/Brussels","href":"http://localhost:8080/portal/rest/v1/calendar/calendars/root-defaultCalendarId","id":"root-defaultCalendarId","type":"PERSONAL","editPermission":"","class":"class org.exoplatform.calendar.ws.bean.CalendarResource"}
        ],
        nondisplayed: [
            {"viewPermission":"*.*;","icsURL":"http://localhost:8080/portal/rest/v1/calendar/calendars/calendarb8fcb1277f0001010b7fb33aa179709b/ics","color":"asparagus","name":"Users","timeZone":"Europe/Brussels","groups":["/platform/users"],"href":"http://localhost:8080/portal/rest/v1/calendar/calendars/calendarb8fcb1277f0001010b7fb33aa179709b","id":"calendarb8fcb1277f0001010b7fb33aa179709b","type":"GROUP","editPermission":"/platform/users/:*.*;","class":"class org.exoplatform.calendar.ws.bean.CalendarResource"},
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

    it('should display 1 calendar in displayed list when 1 allDisplayedCals in data', () => {
        cmp.vm.displayed = data.displayed;
        cmp.vm.nondisplayed = data.nondisplayed;
        cmp.vm.$nextTick(() => {
            const calsList = cmp.find('.DisplayedCalendarContainer').findAll('.calendarName');
            expect(calsList).toHaveLength(1); // 1 calendar
            const firstCal = calsList.at(0);
            expect(firstCal.attributes().id).toBe('root-defaultCalendarId'); // calendar id
            expect(firstCal.classes()).toContain('asparagus'); // calendar color
            expect(firstCal.find('span').text()).toBe('Root Root'); // calendar name
        });
    });

    it('should display 1 calendar in non displayed list when 1 nonDisplayedCals in data', () => {
        cmp.vm.displayed = data.displayed;
        cmp.vm.nondisplayed = data.nondisplayed;
        cmp.vm.$nextTick(() => {
            const calsList = cmp.find('.nonDisplayedCalendarContainer').findAll('li');
            expect(calsList).toHaveLength(1); // 1 calendar
            const firstCal = calsList.at(0);
            expect(firstCal.attributes().id).toBe('calendarb8fcb1277f0001010b7fb33aa179709b'); // calendar id
            expect(firstCal.find('.colorBox').classes()).toContain('asparagus'); // calendar color
            expect(firstCal.find('.calName').text()).toBe('Users'); // calendar name
        });
    });
});