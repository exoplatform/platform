import { exoConstants } from '../js/eXoConstants.js';

export function getDisplayedCalendars() {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/portlet/calendar/init?lang=${exoConstants.LANG}`, {credentials: 'include'}).then(resp => resp.json());
}

export function getFromToLabels(eventId) {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/portlet/calendar/fromToLabels/${eventId}?lang=${exoConstants.LANG}`, {credentials: 'include'}).then(resp => resp.text());
}

