/**
 * Some utility functions to use the DOM
 */
function DOMUtil() {
	this.hideElementList = new Array() ;
} ;
/**
 * Returns true if elemt has the css class className
 * Uses a regular expression to search more quickly
 */
DOMUtil.prototype.hasClass = function(elemt, className) {
	var reg = new RegExp('(^|\\s+)' + className + '(\\s+|$)') ;
	return reg.test(elemt['className']) ;
} ;
/**
 * Adds the css class className to elemt, unless it already has it
 */
DOMUtil.prototype.addClass = function(elemt, className) {
	if (this.hasClass(elemt, className)) return ;
	elemt['className'] = [elemt['className'], className].join(' ') ;
} ;

DOMUtil.prototype.removeClass = function(elemt, className) {
 var reg = new RegExp('(^|\\s+)' + className) ;
 elemt['className'] = elemt['className'].replace(reg, '') ;
} ;
/**
 * Replaces oldClazz by newClazz in elemt
 */
DOMUtil.prototype.replaceClass = function(elemt, oldClazz, newClazz) {
	var reg = new RegExp('(^|\\s+)' + oldClazz) ;
	elemt['className'] = elemt['className'].replace(reg, newClazz) ; 
} ;

/**
 * Gets the children of element that are tagName elements
 * Returns an empty array if no element is found
 */
DOMUtil.prototype.getChildrenByTagName = function(element, tagName) {
	var ln = 0 ;
	var list = [] ;
	if (element && element.childNodes) ln = element.childNodes.length ;
	for (var k = 0; k < ln; k++) {
		if (element.childNodes[k].nodeName == tagName.toUpperCase()) list.push(element.childNodes[k]) ;
	}
	return list ;
} ;
/**
 * Gets the children of root, of type elementName and with css class cssClass
 * Returns an empty array if no element is found
 */
DOMUtil.prototype.findChildrenByClass = function(root, elementName, cssClass) {
  if (elementName) elementName = elementName.toUpperCase() ;
  var elements = root.childNodes ;
  var ln = elements.length ;
		var list = [] ;
  for (var k = 0; k < ln; k++) {
    if (elementName == elements[k].nodeName && this.hasClass(elements[k], cssClass)) {
    	list.push(elements[k]) ;
    }
  }
  return list ;
} ;
/**
 * Gets the children of root, of type elementName and with the attribute attrName that has the value attrValue
 * Returns an empty array if no element is found
 */
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
/**
 * Gets the first child of root, of type elementName and with the css class cssClass
 * Returns null if no element is found
 */
DOMUtil.prototype.findFirstChildByClass = function(root,  elementName, cssClass) {
  if(elementName != null) elementName = elementName.toUpperCase() ;
  var elements = root.childNodes ;
  for(var k = 0; k < elements.length; k++) {
    if(elementName == elements[k].nodeName && this.hasClass(elements[k], cssClass)) {
    	return elements[k] ;
    }
  }
  return null;
} ;
/**
 * Returns the first ancestor node of element that has the css class clazz
 * Returns null if no element is found
 */
DOMUtil.prototype.findAncestorByClass = function(element, clazz) {
  if(element == null) return null ;
  var parent = element.parentNode ;
  while (parent != null) {
  	if (this.hasClass(parent, clazz)) return parent ;
    parent = parent.parentNode ;
  }
  return null ;
} ;
/**
 * Gets the ancestors of element with the css class clazz
 * Returns an empty array if no element is found
 */
DOMUtil.prototype.findAncestorsByClass = function(element, clazz) {
		var list = [] ;
	 var parent = element.parentNode ;
	 while (parent != null) {
	 	if (this.hasClass(parent, clazz)) list.push(parent) ;
	   parent =  parent.parentNode ;
	 }
	 return list ;
} ;
/**
 * Gets the ancestor of element identified by id
 * Returns null if no element is found
 */
DOMUtil.prototype.findAncestorById = function(element,  id) {
  var parent = element.parentNode ;
  while (parent != null) {
    if (parent.getAttributeNode && parent.getAttributeNode("id")
     && parent.getAttributeNode("id").value == id) return parent ;
    parent = parent.parentNode ;
  }
  return null ;
} ;
/**
 * Gets the first ancestor of element of type tagName
 * Returns null if no element is found
 */
DOMUtil.prototype.findAncestorByTagName = function(element, tagName) {
  var parent = element.parentNode ;
  while(parent != null) {
    if(parent.nodeName && parent.nodeName.toLowerCase() == tagName) return parent ;
    parent = parent.parentNode ;
  }
  return null ;
} ;
/**
 * Gets descendants of root, of type tagName, in the list list
 * Returns the same list if no element is found
 */
DOMUtil.prototype.findDescendantsByTag = function(root, tagName, list) {
  var children = root.childNodes ;
  var ln = children.length ;
  var child = null ;
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
/**
 * Gets descendants of root, of type tagName
 * Returns an empty array if no element is found
 */
DOMUtil.prototype.findDescendantsByTagName = function(root, tagName) {
  var list = [] ;
  this.findDescendantsByTag(root, tagName, list) ;
  return list ;
} ;
/**
 * Gets the descendants of root, of type elementName and with css class clazz
 * Returns an empty array if no element is found
 */
DOMUtil.prototype.findDescendantsByClass = function(root, elementName, clazz) {
  var elements = root.getElementsByTagName(elementName) ;
  var ln = elements.length ;
  var list = [] ;
  this.findDescendantsByTag(root, elementName, elements) ;
  for (var k = 0; k < ln; k++) { 
  	if (this.hasClass(elements[k], clazz)) list.push(elements[k]) ;
  }
  return list ;
} ;
/**
 * Gets the first descendant of root, of type elementName, and with css class clazz
 * Returns null if no element is found
 */
/*
* This methods returns the first DOM element which has the clazz type 
*/
DOMUtil.prototype.findFirstDescendantByClass = function(root, elementName, clazz) {
		if (!root) return ;
  var elements = root.getElementsByTagName(elementName) ;
  var ln = elements.length ;	
  for(var k = 0; k < ln; k++) {  	  	
  	if(this.hasClass(elements[k], clazz)) return elements[k] ;  
  }
  return null;
} ;
/**
 * Gets the descendant of root identified by id
 * Returns null if no element is found
 */
DOMUtil.prototype.findDescendantById = function(root, id) {
  var elements = root.getElementsByTagName('*') ;
  var ln = elements.length ;
  for (var i = 0; i < ln; i++) {
  	if (elements[i].getAttributeNode && elements[i].getAttributeNode("id")
  	 && elements[i].getAttributeNode("id").value == id) return elements[i] ;
  }
  return null ;
} ;
/**
 * Returns true if root has obj as a descendant
 */
DOMUtil.prototype.hasDescendant= function(root, obj) {
  var elements = root.getElementsByTagName("*") ;
  var ln = elements.length ;
  for (var k = 0; k < ln; k++) {
    if (elements[k] == obj) return true ;
  }
  return false ;
} ;
/**
 * Returns true if root has a descendant with css class clazz
 */
DOMUtil.prototype.hasDescendantClass = function(root, clazz) {
  var elements = root.getElementsByTagName("*") ;
  var ln = elements.length ;
  for (var k = 0; k < ln; k++) {
    if (this.hasClass(elements[k], clazz)) return true ;
  }
  return false ;
} ;
/**
 * Finds the first next sibling element of type tagName
 */
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
/**
 * Finds the first previous sibling element of type tagName
 */
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

DOMUtil.prototype.moveElemt = function(srcElemt, destElemt) {
	if(typeof(srcElemt) == "string") srcElemt = document.getElementById(srcElemt) ;
	if(typeof(destElemt) == "string") destElemt = document.getElementById(destElemt) ;
	if(srcElemt && destElemt) destElemt.appendChild(srcElemt) ;
}

/**
 * Creates an element tagName with innerHTML content
 * Returns the first node div in this element
 */
DOMUtil.prototype.createElementNode = function(innerHTML, tagName) {
	var temporaryContainer = document.createElement(tagName) ;
	temporaryContainer.innerHTML = innerHTML ;
	var applicationNode = this.getChildrenByTagName(temporaryContainer, "div")[0] ;
	return applicationNode ;
} ;
/**
 * Generates an id based on the current time and random number
 */
DOMUtil.prototype.generateId = function(objectId) {
	return (objectId + "-" + new Date().getTime() + Math.random().toString().substring(2)) ;
} ;
/**
 * Gets the style of an element, in several steps
 *  . the style as defined in the css
 * or if it doesn't exist
 *  . the computed style
 * if intValue is true, a numeric value will be returned as a number, as a string otherwise
 */
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
 /**
  * Hides the elements in the hideElementList array
  * This function is called when a click appear on the page,
  * and that all opened popup menu should be hidden
  */
DOMUtil.prototype.hideElements = function() {
	document.onclick = eXo.core.DOMUtil.cleanUpHiddenElements;
};

DOMUtil.prototype.cleanUpHiddenElements = function() {
	var ln = eXo.core.DOMUtil.hideElementList.length ;
	if (ln > 0) {
		for (var i = 0; i < ln; i++) {
			eXo.core.DOMUtil.hideElementList[i].style.display = "none" ;
		}
		eXo.core.DOMUtil.hideElementList.clear() ;
	}
};

/**
 * Adds an element to the hideElementList array
 * Should only contain elements from a popup menu
 */
DOMUtil.prototype.listHideElements = function(object) {
	if (!eXo.core.DOMUtil.hideElementList.contains(object)) {
		eXo.core.DOMUtil.hideElementList.push(object) ;
	}
} ;
/**
 * Removes element node from the DOM tree
 */
DOMUtil.prototype.removeElement = function(elemt) {
	if(typeof(elemt) == "string") elemt = document.getElementById(elemt) ;
	if(!elemt) return ;
	var parentElement = elemt.parentNode ;
	parentElement.removeChild(elemt) ;
} ;

/**
 * Copyright (c) 2008, Yahoo! Inc. All rights reserved.
   Code licensed under the BSD License:
   http://developer.yahoo.net/yui/license.txt
   version: 2.5.2
 * Returns a array of HTMLElements that pass the test applied by supplied boolean method.
 * For optimized performance, include a tag and/or root node when possible.
 * @method getElementsBy
 * @param {Function} method - A boolean method for testing elements which receives the element as its only argument.
 * @param {String} tag (optional) The tag name of the elements being collected
 * @param {String | HTMLElement} root (optional) The HTMLElement or an ID to use as the starting point 
 * @param {Function} apply (optional) A function to apply to each element when found 
 * @return {Array} Array of HTMLElements
 */
DOMUtil.prototype.getElementsBy = function(method, tag, root, apply) {
  tag = tag || '*';
  root = (root) ? this.get(root) : null || document;

  if (!root) {
    return [];
  }

  var nodes = [],
    elements = root.getElementsByTagName(tag);

  for (var i = 0, len = elements.length; i < len; ++i) {
    if ( method(elements[i]) ) {
      nodes[nodes.length] = elements[i];
      if (apply) {
        apply(elements[i]);
      }
    }
  }

  return nodes;
}

     /**
	  * Copyright (c) 2008, Yahoo! Inc. All rights reserved.
	   Code licensed under the BSD License:
	   http://developer.yahoo.net/yui/license.txt
	   version: 2.5.2
      * Returns an HTMLElement reference.
      * @method get
      * @param {String | HTMLElement |Array} el Accepts a string to use as an ID for getting a DOM reference, an actual DOM reference, or an Array of IDs and/or HTMLElements.
      * @return {HTMLElement | Array} A DOM reference to an HTML element or an array of HTMLElements.
      */
DOMUtil.prototype.get = function(el) {
  if (el && (el.nodeType || el.item)) { // Node, or NodeList
    return el;
  }

  if ((typeof el === 'string') || !el) { // id or null
    return document.getElementById(el);
  }
         
  if (el.length !== undefined) { // array-like
    var c = [];
    for (var i = 0, len = el.length; i < len; ++i) {
      c[c.length] = eXo.core.DOMUtil.get(el[i]);
    }
             
    return c;
  }

  return el; // some other object, just pass it back
 }

/****************************************************************************/
eXo.core.DOMUtil = new DOMUtil() ;
