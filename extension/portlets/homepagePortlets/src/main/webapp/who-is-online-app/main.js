import './components/initComponents.js';
import { exoConstants } from '../js/eXoConstants.js';

// getting language of the PLF
const lang = `${exoConstants.LANG}`;

// should expose the locale ressources as REST API
const url = `${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/i18n/bundle/locale.portlet.whoisonline.whoisonline-${lang}.json`;

// get overrided components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('WhoIsOnLinPortlet');
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
      el: '#whoIsOnline',
      template: '<exo-who-is-online></exo-who-is-online>',
      i18n
    });
  });
}