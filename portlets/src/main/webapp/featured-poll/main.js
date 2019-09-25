import './components/initComponents';
import {exoConstants} from '../js/eXoConstants';
import pollService from './pollService';

// getting language of the PLF
const lang = typeof eXo !== 'undefined' ? eXo.env.portal.language : 'en';

// should expose the locale ressources as REST API
const url = `${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/i18n/bundle/locale.portlet.poll.FeaturedPoll-${lang}.json`;

// get overrided components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('FeaturedPoll');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}

// getting locale ressources
export function init(pollId, saveURL) {
  exoi18n.loadLanguageAsync(lang, url).then(i18n => {
    pollService.setChosenPollURL(saveURL);
    // init Vue app when locale ressources are ready
    new Vue({
      el: '#featured-poll-app',
      data: {
        pollId: pollId
      },
      template: '<exo-poll-app :poll="pollId"></exo-poll-app>',
      i18n
    });
  });
}
