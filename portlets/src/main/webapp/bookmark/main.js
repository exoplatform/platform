import './components/initComponents';
import {exoConstants} from '../js/eXoConstants';

// getting language of the PLF
const lang = typeof eXo !== 'undefined' ? eXo.env.portal.language : 'en';

// should expose the locale ressources as REST API
const url = `${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/i18n/bundle/locale.portlet.bookmark.BookmarkPortlet-${lang}.json`;

// getting locale ressources
export function init() {
  exoi18n.loadLanguageAsync(lang, url).then(i18n => {
    // init Vue app when locale ressources are ready
    new Vue({
      el: '#bookmark-app',
      template: '<exo-bookmark-app></exo-bookmark-app>',
      i18n
    });
  });
}
