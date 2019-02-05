import { createLocalVue, shallowMount } from '@vue/test-utils';
import { eXoConstants } from '../../main/webapp/js/eXoConstants.js';
import ExoWhoIsOnline from '../../main/webapp/who-is-online-app/components/ExoWhoIsOnline';

const localVue = createLocalVue();

describe('ExoWhoIsOnline.test.js', () => {
  let cmp;
  const data = {
    users: [
      {
        'id': '1',
        'href': 'toto1',
        'avatar': 'titi1'
      },
      {
        'id': '2',
        'href': 'toto2',
        'avatar': 'titi2'
      }
    ],
  };
  beforeEach(() => {
    cmp = shallowMount(ExoWhoIsOnline, {
      localVue,
      stubs: {
      },
      mocks: {
        $t: () => {},
        $constants : eXoConstants
      }
    });

  });

  it('should display 2 users in list when 2 users in data', () => {
    cmp.vm.users = data.users;
    cmp.vm.$nextTick(() => {
      const usersList = cmp.findAll('li');
      expect(usersList).toHaveLength(2); // 2 rows
    });
  });
});