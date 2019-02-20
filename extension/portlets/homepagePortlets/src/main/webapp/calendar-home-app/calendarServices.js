import { exoConstants } from '../js/eXoConstants.js';

export function getEvents(startDate, endDate) {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/v1/calendar/events?startTime=${startDate}&endTime=${endDate}`, {credentials: 'include'}).then(resp => resp.json());
}

export function getDisplayedCalendars(spaceId) {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/portlet/homePage/calendar/settings?spaceId=${spaceId}`, {credentials: 'include'}).then(resp => resp.json());
}

export function updateSettings(ids) {
  return fetch(`${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/portlet/homePage/calendar/settings`, {
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'include',
    method: 'POST',
    body: ids
  });
}

