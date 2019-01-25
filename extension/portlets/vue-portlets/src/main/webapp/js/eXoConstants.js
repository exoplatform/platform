export const exoConstants = {
    ENV: eXo.env.portal || '',
    PORTAL: eXo.env.portal.context || '',
    PORTAL_NAME: eXo.env.portal.portalName || '',
    PORTAL_REST: eXo.env.portal.rest,
    SOCIAL_USER_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users/`
};