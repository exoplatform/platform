import ExoBookmarkAppComponent from './ExoBookmarkAppComponent.vue';
import ExoBookmarkComponent from './ExoBookmarkComponent.vue';
import ExoBookmarkFormComponent from './ExoBookmarkFormComponent.vue';

const components = {
  'exo-bookmark-app': ExoBookmarkAppComponent,
  'exo-bookmark': ExoBookmarkComponent,
  'exo-bookmark-form': ExoBookmarkFormComponent
};

for(const key in components) {
  Vue.component(key, components[key]);
}
