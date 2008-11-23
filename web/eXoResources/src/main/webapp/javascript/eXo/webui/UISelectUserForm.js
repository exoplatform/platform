function UISelectUserForm() {
} ;

UISelectUserForm.prototype.init = function(cont) {
	if(typeof(cont) == "string") cont = document.getElementById(cont) ;
	var checkboxes = eXo.core.DOMUtil.findDescendantsByClass(cont, "input", "checkbox") ;
	if(checkboxes.length <=0) return ;
	checkboxes[0].onclick = this.checkAll ;
	var len = checkboxes.length ;
	for(var i = 1 ; i < len ; i ++) {
		checkboxes[i].onclick = this.check ;
	}
} ;

UISelectUserForm.prototype.checkAll = function() {
	eXo.webui.UISelectUserForm.checkAllItem(this);
} ;

UISelectUserForm.prototype.getItems = function(obj) {
	var table = eXo.core.DOMUtil.findAncestorByTagName(obj, "table");
	var checkboxes = eXo.core.DOMUtil.findDescendantsByClass(table, "input","checkbox");
	return checkboxes ;
} ;

UISelectUserForm.prototype.check = function() {
	eXo.webui.UISelectUserForm.checkItem(this);
} ;

UISelectUserForm.prototype.checkAllItem = function(obj){
	var checked = obj.checked ;
	var items = eXo.webui.UISelectUserForm.getItems(obj) ;
	var len = items.length ;
	for(var i = 1 ; i < len ; i ++) {
		items[i].checked = checked ;
	}	
} ;

UISelectUserForm.prototype.checkItem = function(obj){
	var checkboxes = eXo.webui.UISelectUserForm.getItems(obj);
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
eXo.webui.UISelectUserForm = new UISelectUserForm() ;