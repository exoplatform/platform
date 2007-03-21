function UIPortalComponent() {
  alert("Portal Component");
};

UIPortalComponent.prototype.init = function() {
  alert("dkjsfhsdfhgjsdjsdfsdf");
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

UIPortalComponent.prototype.maximizeWindow = function() {
   
};

eXo.portal.UIPortalComponent = new UIPortalComponent();
