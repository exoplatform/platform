import './components/initComponents.js';
import { brandingConstants } from './companyBrandingConstants.js';
import * as companyBrandingDirectives from './companyBrandingDirectives.js';

// getting language of the PLF 
const lang = typeof eXo !== 'undefined' ? eXo.env.portal.language : 'en';

// should expose the locale ressources as REST API 
const url = `${brandingConstants.PORTAL}/${brandingConstants.PORTAL_REST}/i18n/bundle/locale.portlet.platform.branding-${lang}.json`;

Vue.directive('exo-tooltip', companyBrandingDirectives.tooltip);

// get overrided components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('Branding');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}

// getting locale ressources
export function init() {
  exoi18n.loadLanguageAsync(lang, url).then(i18n => {
    // init Vue app when locale ressources are ready
    new Vue({
      el: '#branding',
      template: '<exo-company-branding></exo-company-branding>',
      i18n
    });
  });
}