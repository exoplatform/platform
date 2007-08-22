function TableResizable() {

} ;
TableResizable.prototype.init = function(evt, markerobj) {
	_e = (window.event) ? window.event : evt ;
	this.posX = _e.clientX ;
	var marker = (typeof(markerobj) == "string")? document.getElementById(markerobj):markerobj ;
	this.beforeCol = eXo.core.DOMUtil.findAncestorByTagName(marker, "th") ;
	this.afterCol = eXo.core.DOMUtil.findNextElementByTagName(this.beforeCol, "th") ;
	var beforePaddingLeft = parseInt(eXo.core.DOMUtil.getStyle(this.beforeCol, "paddingLeft")) ;
	var afterPaddingLeft = parseInt(eXo.core.DOMUtil.getStyle(this.afterCol, "paddingLeft")) ;
	this.beforeCol.style.width = (this.beforeCol.offsetWidth - beforePaddingLeft - marker.offsetWidth) + "px" ;
	this.afterCol.style.width = (this.afterCol.offsetWidth - afterPaddingLeft - marker.offsetWidth) + "px" ;
	this.beforeColX = this.beforeCol.offsetWidth - beforePaddingLeft - marker.offsetWidth;
	this.afterColX = this.afterCol.offsetWidth - afterPaddingLeft - marker.offsetWidth;
	document.onmousemove = eXo.core.TableResizable.adjustWidth ;	
	document.onmouseup = eXo.core.TableResizable.clear ;
} ;

TableResizable.prototype.adjustWidth = function(evt) {
	_e = (window.event) ? window.event : evt ;
	var TableResizable = eXo.core.TableResizable ;
	var delta = _e.clientX - TableResizable.posX;
	var beforeWidth = TableResizable.beforeColX + delta ;
	var afterWidth = TableResizable.afterColX - delta ;
	if (beforeWidth <= 0  || afterWidth <= 0) return ;
	TableResizable.beforeCol.style.width = beforeWidth + "px" ;
	TableResizable.afterCol.style.width = afterWidth + "px" ;
} ;

TableResizable.prototype.clear = function() {
	document.onmousemove = null ;
} ;

eXo.core.TableResizable = new TableResizable() ;