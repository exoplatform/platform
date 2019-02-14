import { exoConstants } from '../js/eXoConstants.js';

export function getDisplayedCalendars(nbClick, spaceId) {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/portlet/calendar/init?lang=${exoConstants.LANG}&nbclick=${nbClick}&spaceId=${spaceId}`, {credentials: 'include'}).then(resp => resp.json());
}

export function getSettings() {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/portlet/calendar/settings`, {credentials: 'include'}).then(resp => resp.json());
}

export function updateSettings(ids) {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/portlet/calendar/settings`, {
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'include',
    method: 'POST',
    body: ids
  });
}

export function search(key) {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/portlet/calendar/search?key=${key}`, {credentials: 'include'}).then(resp => resp.json());
}

