function UIPortalComponent() {
  
};

UIPortalComponent.prototype.init = function() {

};

UIPortalComponent.prototype.showHidePortletControl = function(objClicked) {
  if(objClicked.className == "ExpandButton") {
    objClicked.className = "CollapseButton";
  } else {
    objClicked.className = "ExpandButton";
  }

  var objShowHide = document.getElementById("ShowHide");

  if(objShowHide.className == "OnHide") {
    objShowHide.className = "OnShow";
  } else {
    objShowHide.className = "OnHide";
  }
};

UIPortalComponent.prototype.createUILogin = function() {
  alert("yahoo");
};

eXo.portal.UIPortalComponent = new UIPortalComponent();
