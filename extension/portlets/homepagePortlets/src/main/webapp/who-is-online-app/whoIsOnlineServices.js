import { exoConstants } from '../js/eXoConstants.js';

export function getOnlineUsers(spaceName){
  if (spaceName && spaceName != '') {
    return fetch(`${exoConstants.SOCIAL_USER_API}/online?spaceName=${spaceName}`, {credentials: 'include'}).then(resp => resp.json());
  }
  return fetch(`${exoConstants.SOCIAL_USER_API}/online`, {credentials: 'include'}).then(resp => resp.json());
}

