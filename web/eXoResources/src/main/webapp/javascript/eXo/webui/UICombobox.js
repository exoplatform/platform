function UICombobox() {
}

UICombobox.prototype.init = function(textbox) {
	if(typeof(textbox) == "string") textbox = document.getElementById(textbox) ;
	textbox = eXo.core.DOMUtil.findNextElementByTagName(textbox,"input") ;
	var UICombobox = eXo.webui.UICombobox ;
	var onfocus = textbox.getAttribute("onfocus") ;
	var onclick = textbox.getAttribute("onclick") ;
	if(!onfocus) textbox.onfocus = UICombobox.show ;
	if(!onclick) textbox.onclick = UICombobox.show ;
} ;

UICombobox.prototype.show = function(evt) {
	var uiCombo = eXo.webui.UICombobox;
	uiCombo.items = eXo.core.DOMUtil.findDescendantsByTagName(this.parentNode,"a");
	if(uiCombo.list) uiCombo.list.style.display = "none";
	uiCombo.list = eXo.core.DOMUtil.findFirstDescendantByClass(this.parentNode,"div","UIComboboxContainer");
	uiCombo.list.parentNode.style.position = "absolute";
	uiCombo.fixForIE6(this);
	uiCombo.list.style.display = "block";	
	uiCombo.list.style.top = this.offsetHeight + "px";
	uiCombo.list.style.width = this.offsetWidth + "px";
	uiCombo.setSelectedItem(this);
	uiCombo.list.onmousedown = eXo.core.EventManager.cancelEvent;
	document.onmousedown = uiCombo.hide;
} ;

UICombobox.prototype.getSelectedItem = function(textbox){
	var val = textbox.value;
	var data = eval(textbox.getAttribute("options"));
	var len = data.length;
	for(var i = 0; i<len; i++) {
		if(val == data[i]) return i;
	}
	return false;
};

UICombobox.prototype.setSelectedItem = function(textbox){
	if(this.lastSelectedItem) eXo.core.DOMUtil.replaceClass(this.lastSelectedItem,"UIComboboxSelectedItem","");
	var selectedIndex = parseInt(this.getSelectedItem(textbox));
	if(selectedIndex >=0 ) {
		eXo.core.DOMUtil.addClass(this.items[selectedIndex],"UIComboboxSelectedItem");
		this.lastSelectedItem = this.items[selectedIndex];
		var y = eXo.core.Browser.findPosYInContainer(this.lastSelectedItem,this.list);
		this.list.firstChild.scrollTop = y ;
		var hidden = eXo.core.DOMUtil.findPreviousElementByTagName(textbox,"input");
		hidden.value = this.items[selectedIndex].getAttribute("value");
		
	}
};

UICombobox.prototype.fixForIE6 = function(obj) {
  if(!eXo.core.Browser.isIE6()) return ;
  if(eXo.core.DOMUtil.getChildrenByTagName(this.list,"iframe").length > 0) return ;
	var iframe = document.createElement("iframe") ;
  iframe.frameBorder = 0 ;
	iframe.style.width = obj.offsetWidth+ "px" ;
  this.list.appendChild(iframe) ;
} ;

UICombobox.prototype.cancelBubbe = function(evt) {
	var _e = window.event || evt ;
	_e.cancelBubble = true ;
} ;

UICombobox.prototype.complete = function(obj,evt) {
	if(evt.keyCode == 16 ) {
		this.setSelectedItem(obj);
		return;
	}
	if(evt.keyCode == 13 ) {
		this.setSelectedItem(obj);
		this.hide();
		return;
	}
	var sVal = obj.value.toLowerCase();
	if(evt.keyCode == 8 )	sVal = sVal.substring( 0, sVal.length - 1 )
	if( sVal.length < 1 ) return ;
	var data = eval(obj.getAttribute("options").trim());
	var len = data.length;
	var tmp = null;
	for( var i = 0; i < data.length; i++ )	{
		tmp = data[i].trim();
		var idx = tmp.toLowerCase().indexOf( sVal, 0);
		if( idx == 0 && tmp.length > sVal.length )	{
			obj.value = data[i];
			if( obj.createTextRange )	{
				hRange = obj.createTextRange();
				hRange.findText( data[i].substr( sVal.length ) );
				hRange.select();
			}
			else	{
				obj.setSelectionRange( sVal.length, tmp.length );
			}
			break;
		}
	}
	this.setSelectedItem(obj);
} ;

UICombobox.prototype.hide = function() {
	eXo.webui.UICombobox.list.style.display = "none" ;
	document.onmousedown = null ;
} ;

UICombobox.prototype.getValue = function(obj) {
	var UICombobox = eXo.webui.UICombobox ;
	var val = obj.getAttribute("value") ;
	var hiddenField = eXo.core.DOMUtil.findNextElementByTagName(UICombobox.list.parentNode,"input") ;
	hiddenField.value = val ;
	var text = eXo.core.DOMUtil.findNextElementByTagName(hiddenField,"input") ;
	text.value = eXo.core.DOMUtil.findFirstDescendantByClass(obj,"div","UIComboboxLabel").innerHTML ;
	UICombobox.list.style.display = "none" ;
} ;

eXo.webui.UICombobox = new UICombobox() ;

function EventManager(){
	
}

EventManager.prototype.cancelBubble = function(evt) {
  if(eXo.core.Browser.browserType == 'ie')
    window.event.cancelBubble = true ;
  else 
    evt.stopPropagation() ;		  
};

EventManager.prototype.cancelEvent = function(evt) {
	eXo.core.EventManager.cancelBubble(evt) ;
  if(eXo.core.Browser.browserType == 'ie')
    window.event.returnValue = true ;
  else
    evt.preventDefault() ;
};

eXo.core.EventManager = new EventManager() ;