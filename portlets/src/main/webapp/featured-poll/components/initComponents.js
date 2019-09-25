import PollAppComponent from './PollAppComponent.vue';
import PollSelectionComponent from './PollSelectionComponent.vue';
import PollDetailComponent from './PollDetailComponent.vue';

const components = {
  'exo-poll-app': PollAppComponent,
  'exo-poll-selection': PollSelectionComponent,
  'exo-poll-detail': PollDetailComponent
};

for(const key in components) {
  Vue.component(key, components[key]);
}
