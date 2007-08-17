function DOMUtil() {
	this.hideElementList = new Array() ;
} ;

DOMUtil.prototype.hasClass = function(elemt, className) {
	var reg = new RegExp('(^|\\s+)' + className + '(\\s+|$)') ;
	return reg.test(elemt['className']) ;
} ;

DOMUtil.prototype.addClass = function(elemt, className) {
  if (this.hasClass(elemt, className)) return ;
	elemt['className'] = [elemt['className'], className].join(' ');
} ;

DOMUtil.prototype.replaceClass = function(elemt, oldClazz, newClazz) {} ;

DOMUtil.prototype.getChildrenByTagName = function(element, tagName) {
	var ln = 0 ;
	var list = [] ;
	if (element && element.childNodes) ln = element.childNodes.length ;
	for (var k = 0; k < ln; k++) {
		if (element.childNodes[k].nodeName == tagName.toUpperCase()) list.push(element.childNodes[k]) ;
	}
	return list ;
} ;

DOMUtil.prototype.findChildrenByClass = function(root, elementName, cssClass) {
  if (elementName) elementName = elementName.toUpperCase() ;
  var elements = root.childNodes ;
  var ln = elements.length ;
	var list = [] ;
  for (var k = 0; k < ln; k++) {
    if (elementName == elements[k].nodeName && this.hasClass(elements[k], cssClass)) {
    	list.push(elements[k]);
    }
  }
  return list ;
} ;

DOMUtil.prototype.findChildrenByAttribute = function(root,  elementName, attrName, attrValue) {
  if (elementName) elementName = elementName.toUpperCase() ;
  var elements = root.childNodes ;
  var ln = elements.length ;
  var list = [] ;
  for(var k = 0; k < ln; k++) {
    if (elementName == elements[k].nodeName) {
      var retValue = elements[k].getAttribute(attrName) ;
      if (retValue == attrValue) list.push(elements[k]) ;
    }
  }
  return list ;
} ;

DOMUtil.prototype.findFirstChildByClass = function(root,  elementName, cssClass) {
  if(elementName != null)  elementName = elementName.toUpperCase() ;
  var elements = root.childNodes ;
  for(var k = 0; k < elements.length; k++) {
    if(elementName == elements[k].nodeName && this.hasClass(elements[k], cssClass)) {
    	return elements[k] ;
    }
  }
  return null;
} ;

DOMUtil.prototype.findAncestorByClass = function(element, clazz) {
  if(element == null) return null ;
  var parent = element.parentNode ;
  while (parent != null) {
  	if (this.hasClass(parent, clazz)) return parent ;
    parent = parent.parentNode ;
  }
  return null ;
} ;

DOMUtil.prototype.findAncestorsByClass = function(element, clazz) {
	var list = [] ;
  var parent = element.parentNode ;
  while (parent != null) {
  	if (this.hasClass(parent, clazz)) list.push(parent) ;
    parent =  parent.parentNode ;
  }
  return list ;
} ;

DOMUtil.prototype.findAncestorById = function(element,  id) {
  var parent = element.parentNode ;
  while (parent != null) {
    if (parent.id == id) return parent ;
    parent = parent.parentNode ;
  }
  return null ;
} ;

DOMUtil.prototype.findAncestorByTagName = function(element, tagName) {
  var parent = element.parentNode ;
  while(parent != null) {
    if(parent.nodeName && parent.nodeName.toLowerCase() == tagName) return parent ;
    parent = parent.parentNode ;
  }
  return null ;
} ;

DOMUtil.prototype.findDescendantsByTag = function(root, tagName, list) {
  var children = root.childNodes ;
  var ln = children.length ;
  var child = null;
  for (var k = 0; k < ln; k++) {
    child = children[k] ;
    if (tagName == null) {
      list[list.length] = child ;
    } else if (child.nodeName == null) {
    	continue ;
    } else {    
      if (tagName == child.nodeName.toLowerCase()) list[list.length] = child ;
    }
    this.findDescendantsByTag(child, tagName, list) ;
  }
} ;

DOMUtil.prototype.findDescendantsByTagName = function(root, tagName) {
  var list = [] ;
  this.findDescendantsByTag(root, tagName, list) ;
  return list ;
} ;

DOMUtil.prototype.findDescendantsByClass = function(root, elementName, clazz) {
  var elements = root.getElementsByTagName(elementName) ;
  var ln = elements.length ;
  var list = [] ;
  this.findDescendantsByTag(root, elementName, elements) ;
  for (var k = 0; k < ln; k++) { 
  	if (this.hasClass(elements[k], clazz)) list.push(elements[k]);
  }
  return list ;
} ;

DOMUtil.prototype.findFirstDescendantByClass = function(root, elementName, clazz) {
  var elements = root.getElementsByTagName(elementName);
  var ln = elements.length ;	
  for(var k = 0; k < ln; k++) {  	  	
  	if(this.hasClass(elements[k], clazz)) return elements[k] ;  
  }
  return null;
} ;

DOMUtil.prototype.findDescendantById = function(root, id) {
  var elements = root.getElementsByTagName('*') ;
  var ln = elements.length ;
  for (var i = 0; i < ln; i++) {
    if (elements[i].id == id) {
      return elements[i] ;
    }
  }
  return null ;
} ;

DOMUtil.prototype.hasDescendant= function(root, obj) {
  var elements =  root.getElementsByTagName("*") ;
  var ln = elements.length ;
  for (var k = 0; k < ln; k++) {
    if (elements[k] == obj) return true ;
  }
  return false ;
} ;

DOMUtil.prototype.hasDescendantClass = function(root, clazz) {
  var elements =  root.getElementsByTagName("*") ;
  var ln = elements.length ;
  for (var k = 0; k < ln; k++) {
    if (this.hasClass(elements[k], clazz)) return true ;
  }
  return false ;
} ;

DOMUtil.prototype.findNextElementByTagName = function(element, tagName) {
	var nextElement = element.nextSibling ;
	while (nextElement != null) {
		var nodeName = nextElement.nodeName ;
    if (nodeName != null) nodeName = nodeName.toLowerCase() ;
		if (nodeName == tagName) return nextElement ;
		nextElement = nextElement.nextSibling ;
	}
	return null ;
} ;

DOMUtil.prototype.findPreviousElementByTagName = function(element, tagName) {
	var previousElement = element.previousSibling ;
	while (previousElement != null) {
		var nodeName = previousElement.nodeName ;
    if (nodeName != null) nodeName = nodeName.toLowerCase() ;
		if (nodeName == tagName) return previousElement ;
		previousElement = previousElement.previousSibling ;
	}
	return null ;
} ;

DOMUtil.prototype.createElementNode = function(innerHTML, tagName) {
	var temporaryContainer = document.createElement(tagName) ;
	temporaryContainer.innerHTML = innerHTML ;
	var applicationNode = this.getChildrenByTagName(temporaryContainer, "div")[0] ;
	return applicationNode ;
} ;

DOMUtil.prototype.generateId = function(objectId) {
	var dateTime = new Date() ;
	var time = dateTime.getTime() ;
	return (objectId + "-" + time) ;
} ;

DOMUtil.prototype.swapPosition = function(e1, e2) {
  if (e1.parentNode != e2.parentNode) alert("Report  bug to the admin, cannot swap element position") ; 
  var tmpSwap = document.createElement("div") ;
} ;

DOMUtil.prototype.getStyle = function(element, style, intValue) {
	var result = null ;
	if (element.style[style]) {
		result = element.style[style] ;
	}	else if (element.currentStyle) {
		result = element.currentStyle[style] ;
	}	else if (document.defaultView && document.defaultView.getComputedStyle) {
		style = style.replace(/([A-Z])/g, "-$1") ;
		style = style.toLowerCase() ;
		var s = document.defaultView.getComputedStyle(element, "") ;
		result = s && s.getPropertyValue(style) ;
	}
	if (intValue && result) {
		var intRes = Number(result.match(/\d+/)) ;
		if(!isNaN(intRes)) result = intRes ;
	}
	return result ;
} ;

/* TODO: review this function: document.onclick */
/*
 * user for method eXo.webui.UIPopupSelectCategory.show();
 * reference file : UIPopupSelectCategory.js
 */
DOMUtil.prototype.hideElements = function() {
	document.onclick = function() {
		var ln = eXo.core.DOMUtil.hideElementList.length;
		if (ln > 0) {
			for (var i = 0; i < ln; i++) {
				eXo.core.DOMUtil.hideElementList[i].style.display = "none" ;
			}
			eXo.core.DOMUtil.hideElementList.clear() ;
		}
	}
} ;

DOMUtil.prototype.listHideElements = function(object) {
	if (!eXo.core.DOMUtil.hideElementList.contains(object)) {
		eXo.core.DOMUtil.hideElementList.push(object) ;
	}
} ;

DOMUtil.prototype.removeTemporaryElement = function(element) {
	var parentElement = element.parentNode ;
	parentElement.removeChild(element) ;
} ;

/****************************************************************************/
eXo.core.DOMUtil = new DOMUtil() ;
