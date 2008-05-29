function UIAvailablePortletControl() {};

UIAvailablePortletControl.prototype.checkAll = function (){
	var root = document.getElementById('UIFormTableIterator');
	var checkboxlist = eXo.core.DOMUtil.findDescendantsByClass(root,'input','checkbox');
	var len = checkboxlist.length;
	for(i=0;i<len;i++){
		checkboxlist[i].checked=true;
	}
};	

UIAvailablePortletControl.prototype.removeCheck = function (){
	var root = document.getElementById('UIFormTableIterator');
	var checkboxlist = eXo.core.DOMUtil.findDescendantsByClass(root,'input','checkbox');
	var len = checkboxlist.length;
	for(i=0;i<len;i++){
		checkboxlist[i].checked=false;
	}
};

eXo.webui.UIAvailablePortletControl = new UIAvailablePortletControl() ;
