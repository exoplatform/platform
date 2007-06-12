/*
 * Coder       : Dunghm
 * Description : Vertical Scroller
 * */
UIVerticalScroller = function () {
	this.index = 0 ;
	this.enableUpClass = "Icon ScrollUpButton" ;
	this.disableUpClass = "Icon DisableScrollUpButton" ;
	this.enableDownClass = "Icon ScrollDownButton" ;
	this.disableDownClass = "Icon DisableScrollDownButton" ;		
} ;

UIVerticalScroller.prototype.init = function() {	
	this.DOMUtil =  eXo.core.DOMUtil ;
	this.container = document.getElementById("UIWorkspaceContainer") ;
	if((this.container.style.display == "none") || this.container.style.display == "") return ;
	this.itemContainer = document.getElementById('UIWidgets') ;
	this.items = this.DOMUtil.findDescendantsByClass(this.itemContainer,"div",'UIWidget') ;
	this.scrollZone = this.DOMUtil.findFirstDescendantByClass(this.itemContainer,"div","ScrollZone") ;
	this.itemSize = this.items.length ;
		
	var height = 0 ;
	for(var i = this.index ; i < this.itemSize ; i++) {
		height += this.items[i].offsetHeight ;
		if (height > this.scrollZone.offsetHeight) {
			this.items[i].style.display = "none" ;
		}
		else this.items[i].style.display = "block" ;
	}
} ;

UIVerticalScroller.prototype.scrollDown = function(element, containerClass, itemClass) {
	var j = 0 ;
	for(var i = 0 ; i < this.itemSize ; i ++ ) {
		if(this.items[i].style.display != "none") {			
			j ++ ;
		}
	}	
	if ((this.index + j) >= this.itemSize) {
		element.className = this.disableDownClass ;//"Icon DisableScrollDownButton" ;
		return ;
	}
	var upButton = this.DOMUtil.findNextElementByTagName(element,'div') ;
	upButton.className = this.enableUpClass ; //"Icon ScrollUpButton" ;
	this.items[this.index].style.display = "none" ;
	this.items[this.index + j].style.display = "block" ;
	if (document.all) {
		try {
			this.items[this.index + j + 1].style.display = "block" ;				
		}
		catch (e){
			
		};
	}	
	this.index ++ ;
	height = 0;
	for(var i = 0 ; i < this.itemSize ; i ++ ) {
		if(this.items[i].style.display != "none") {
			height += this.items[i].offsetHeight ;
			if (height > this.scrollZone.offsetHeight) {					
					this.items[i].style.display = "none" ;
			}			
		}
	}	
} ;

UIVerticalScroller.prototype.scrollUp = function(element, containerClass, itemClass) {
	var j = 0 ;
	for(var i = 0 ; i < this.itemSize ; i ++ ) {
		if(this.items[i].style.display != "none") {
			j ++ ;
		}
	}
	if (this.index <= 0) {
		element.className = this.disableUpClass ;//"Icon DisableScrollUpButton" ;
		return ;
	}
	var downButton = this.DOMUtil.findPreviousElementByTagName(element,'div') ;
	this.items[this.index - 1].style.display = "block" ;
	downButton.className = this.enableDownClass ;//"Icon ScrollDownButton" ;
	this.items[j + this.index - 1].style.display = "none" ;
	this.index -- ;
	height = 0;
	for(var i = 0 ; i < this.itemSize ; i ++ ) {
		if(this.items[i].style.display != "none") {
			height += this.items[i].offsetHeight ;
			if (height > this.scrollZone.offsetHeight) this.items[i].style.display = "none" ;			
		}
	}	
} ;

eXo.webui.UIVerticalScroller = new UIVerticalScroller() ;