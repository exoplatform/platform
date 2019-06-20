export const brandingConstants = {
  ENV: eXo.env.portal || '',
  PORTAL: eXo.env.portal.context || '',
  PORTAL_NAME: eXo.env.portal.portalName || '',
  PORTAL_REST: eXo.env.portal.rest,
  COMPANY_BRANDING_API: `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/platform/branding/`,
  HOMEICON : '/eXoSkin/skin/images/themes/default/platform/skin/ToolbarContainer/HomeIcon.png',
  LOADER : '/eXoSkin/skin/images/themes/default/platform/portlets/branding/ajax-loader.gif',
  PERIOD : 10000 
};