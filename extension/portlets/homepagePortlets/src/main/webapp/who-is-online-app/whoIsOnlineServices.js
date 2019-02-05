import { exoConstants } from '../js/eXoConstants.js';

export function getOnlineUsers(spaceId){
  if (spaceId && spaceId !== '') {
    return fetch(`${exoConstants.SOCIAL_USER_API}?status=online&spaceId=${spaceId}`, {credentials: 'include'}).then(resp => resp.json());
  }
  return fetch(`${exoConstants.SOCIAL_USER_API}?status=online`, {credentials: 'include'}).then(resp => resp.json());
}

