import { exoConstants } from '../js/eXoConstants.js';

export function getDisplayedCalendars() {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/portlet/calendar/init?lang=${exoConstants.LANG}`, {credentials: 'include'}).then(resp => resp.json());
}

