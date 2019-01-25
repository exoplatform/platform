import { exoConstants } from '../js/eXoConstants.js';

export function getOnlineUsers(userName, spaceName){
  return fetch(`${exoConstants.PORTAL_NAME}/${exoConstants.PORTAL_REST}/whoIsOnline/${userName}/${spaceName}`, {credentials: 'include'}).then(resp => resp.json());
}

