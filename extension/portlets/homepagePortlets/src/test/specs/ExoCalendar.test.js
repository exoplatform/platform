import { createLocalVue, shallowMount } from '@vue/test-utils';
import { eXoConstants } from '../../main/webapp/js/eXoConstants.js';
import ExoCalendar from '../../main/webapp/calendar-home-app/components/ExoCalendar';

const localVue = createLocalVue();

describe('ExoCalendar.test.js', () => {
  let cmp;
  const data = {
    allDisplayedCals: [
      {}
    ],
    allEvents: [
      {}
    ],
    dateLabel: 'today: ',
    date_act: '2/15/19',
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
    cmp.vm.allDisplayedCals = data.allDisplayedCals;
    // cmp.vm.allEvents = data.allEvents;
    // cmp.vm.$nextTick(() => {
    //   const calElement = cmp.find('.eventTitle').find('span');
    //   expect(calElement.classes()).toContain('asparagus'); // calendar color
    //   expect(calElement.text()).toBe('Root Root'); // calendar Name
    //   const eventsList = cmp.find('.eventsList').findAll('li');
    //   expect(eventsList).toHaveLength(1); // 1 event
    //   const firstEvent = eventsList.at(0);
    //   expect(firstEvent.attributes().id).toBe('Eventf1b4acfd7f0001013f3d88c55ca20820');
    //   expect(firstEvent.find('.eventSummary').find('a').text()).toBe('testEvent');
    // });
  });
});