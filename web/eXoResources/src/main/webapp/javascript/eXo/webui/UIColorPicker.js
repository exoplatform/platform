function UIColorPicker() {
}

UIColorPicker.prototype.show = function(obj){
  document.onmousedown = new Function("eXo.webui.UIColorPicker.hide()") ;
  this.tableColor = eXo.core.DOMUtil.findNextElementByTagName(obj, "div");
  this.title = eXo.core.DOMUtil.findFirstDescendantByClass(obj, "span", "DisplayValue");
  this.input = eXo.core.DOMUtil.findFirstDescendantByClass(obj.parentNode, "input", "UIColorPickerValue");
  this.showHide();
  this.getSelectedValue() ;
}
UIColorPicker.prototype.setColor = function(color) {
  if (eXo.core.DOMUtil.hasClass(this.title, color)) {
    this.hide() ;
    return ;
  }
  var className = "DisplayValue " + color ;
  this.title.className = className ;
  this.input.value = color ;
  this.hide() ;
} ;

UIColorPicker.prototype.clearSelectedValue = function() {
  var selectedValue = this.input.value ;
  var colorCell = eXo.core.DOMUtil.findDescendantsByTagName(this.tableColor, "a") ;
  var len = colorCell.length ;
  for(var i = 0 ; i < len ; i ++) {
    if(eXo.core.DOMUtil.hasClass(colorCell[i],"SelectedColorCell")) {
      colorCell[i].className = colorCell[i].className.replace("SelectedColorCell","") ;
      break ;
    }
  }
} ;

UIColorPicker.prototype.getSelectedValue = function() {
  var selectedValue = this.input.value ;
  var colorCell = eXo.core.DOMUtil.findDescendantsByTagName(this.tableColor, "a") ;
  var len = colorCell.length ;
  this.clearSelectedValue() ;
  for(var i = 0 ; i < len ; i ++) {
    if(eXo.core.DOMUtil.hasClass(colorCell[i],selectedValue)) {
      eXo.core.DOMUtil.addClass(colorCell[i],"SelectedColorCell") ;
      break ;
    }
  }
} ;

UIColorPicker.prototype.hide = function() {
  if(eXo.webui.UIColorPicker.tableColor) {
    eXo.webui.UIColorPicker.tableColor.style.display = "none" ;
    eXo.webui.UIColorPicker.tableColor = null ;
    eXo.webui.UIColorPicker.title = null ;
    eXo.webui.UIColorPicker.input = null ;
    document.onmousedown = null ;    
  }
} ;

UIColorPicker.prototype.showHide = function() {
  var obj = this.tableColor ;
  if(obj.style.display != "block") {
    obj.style.display = "block" ;
  } else {
    obj.style.display = "none" ;
  }
} ;

eXo.webui.UIColorPicker = new UIColorPicker() ;