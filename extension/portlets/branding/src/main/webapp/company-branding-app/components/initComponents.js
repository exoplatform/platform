import ExoModal from './modal/ExoModal.vue';
import ExoCompanyBranding from './ExoCompanyBranding.vue';


const components = {
  'exo-company-branding': ExoCompanyBranding,
  'exo-modal' : ExoModal
};

for(const key in components) {
  Vue.component(key, components[key]);
}