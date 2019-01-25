import { createLocalVue, shallowMount } from '@vue/test-utils';
import { eXoConstants } from '../../main/webapp/js/eXoConstants.js';
import ExoWhoIsOnline from "../../main/webapp/who-is-online-app/components/ExoWhoIsOnline";

const localVue = createLocalVue();

// mock exo-tooltip directive
localVue.directive('exo-tooltip', function() {});

describe('ExoWhoIsOnline.test.js', () => {
  let cmp;
  const data = {
    users: [
      {
        'id': '1',
        'profileUrl': 'toto',
        'avatar': 'titi'
      },
      {
        'id': '2',
          'profileUrl': 'toto',
          'avatar': 'titi'
      }
    ],
  };
  beforeEach(() => {
    cmp = shallowMount(ExoWhoIsOnline, {
      localVue,
      mocks: {
        $t: () => {},
        $constants : eXoConstants
      }
    });

  });

  it('should display 2 users in list when 2 users in data', () => {
    cmp.vm.users = data.users;
    cmp.vm.$nextTick(() => {
      const usersList = cmp.findAll('#onlineList');
      expect(usersList).toHaveLength(2); // 2 rows
    });
  });
});