export const exoConstants = {
  ENV: eXo.env.portal || '',
  PORTAL: eXo.env.portal.context || '',
  BASE_URL: eXo.env.server.portalBaseURL,
  PORTAL_NAME: eXo.env.portal.portalName || '',
  PORTAL_REST: eXo.env.portal.rest,
  SOCIAL_USER_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users`,
  HOST_NAME: window.location.host,
  SPACE_ID: eXo.env.portal.spaceId,
  LANG: typeof eXo !== 'undefined' ? eXo.env.portal.language : 'en'
};