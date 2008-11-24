function UIUserSelector() {
} ;

UIUserSelector.prototype.init = function(cont) {
	if(typeof(cont) == "string") cont = document.getElementById(cont) ;
	var checkboxes = eXo.core.DOMUtil.findDescendantsByClass(cont, "input", "checkbox") ;
	if(checkboxes.length <=0) return ;
	checkboxes[0].onclick = this.checkAll ;
	var len = checkboxes.length ;
	for(var i = 1 ; i < len ; i ++) {
		checkboxes[i].onclick = this.check ;
	}
} ;

UIUserSelector.prototype.checkAll = function() {
	eXo.webui.UIUserSelector.checkAllItem(this);
} ;

UIUserSelector.prototype.getItems = function(obj) {
	var table = eXo.core.DOMUtil.findAncestorByTagName(obj, "table");
	var checkboxes = eXo.core.DOMUtil.findDescendantsByClass(table, "input","checkbox");
	return checkboxes ;
} ;

UIUserSelector.prototype.check = function() {
	eXo.webui.UIUserSelector.checkItem(this);
} ;

UIUserSelector.prototype.checkAllItem = function(obj){
	var checked = obj.checked ;
	var items = eXo.webui.UIUserSelector.getItems(obj) ;
	var len = items.length ;
	for(var i = 1 ; i < len ; i ++) {
		items[i].checked = checked ;
	}	
} ;

UIUserSelector.prototype.checkItem = function(obj){
	var checkboxes = eXo.webui.UIUserSelector.getItems(obj);
	var len = checkboxes.length;
	var state = true;
	if (!obj.checked) {
		checkboxes[0].checked = false;
	}
	else {
		for (var i = 1; i < len; i++) {
			state = state && checkboxes[i].checked;
		}
		checkboxes[0].checked = state;
	}
} ;

UIUserSelector.prototype.getKeynum = function(event) {
  var keynum = false ;
  if(window.event) { /* IE */
    keynum = window.event.keyCode;
    event = window.event ;
  } else if(event.which) { /* Netscape/Firefox/Opera */
    keynum = event.which ;
  }
  if(keynum == 0) {
    keynum = event.keyCode ;
  }
  return keynum ;
} ;

UIUserSelector.prototype.captureInput = function(input, action) {
  if(typeof(input) == "string") input = document.getElementById(input) ;
	input.form.onsubmit = eXo.webui.UIUserSelector.cancelSubmit ;
  input.onkeypress= eXo.webui.UIUserSelector.onEnter ;
} ;

UIUserSelector.prototype.onEnter = function(evt) {
  var _e = evt || window.event ;
  _e.cancelBubble = true ;
  var keynum = eXo.webui.UIUserSelector.getKeynum(_e) ;
  if (keynum == 13) {
    var action = eXo.core.DOMUtil.findNextElementByTagName(this, "a");
		if(!action) action = eXo.core.DOMUtil.findPreviousElementByTagName(this, "a")  ;
    action = String(action.href).replace("javascript:","").replace("%20","") ;
    eval(action) ;
  }
} ;

UIUserSelector.prototype.cancelSubmit = function() {
  return false ;
} ;

eXo.webui.UIUserSelector = new UIUserSelector() ;