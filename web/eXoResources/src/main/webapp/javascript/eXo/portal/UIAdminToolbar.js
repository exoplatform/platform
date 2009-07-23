function UIAdminToolbar() {} ;
UIAdminToolbar.prototype.onLoad = function(baseId) {
  var uiNavPortlet = document.getElementById(baseId) ;
  var mainContainer = eXo.core.DOMUtil.findFirstDescendantByClass(uiNavPortlet, "div", "TabsContainer");
  if(mainContainer) eXo.portal.UIPortalNavigation.init(uiNavPortlet, mainContainer, 0, 0);
} ;

eXo.portal.UIAdminToolbar = new UIAdminToolbar() ;