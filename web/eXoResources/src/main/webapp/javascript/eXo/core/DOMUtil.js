function  DOMUtil() {
	this.hideElementList = new eXo.core.Array() ;
} ;

DOMUtil.prototype.getChildrenByTagName = function(element, tagName) {
  var list = new Array() ;
  var children = element.childNodes ;
  for(var k = 0; k < children.length; k++) {
    var child = children[k] ;
    var nodeName = child.nodeName ;
    if(nodeName != null) nodeName = nodeName.toLowerCase() ;
    if(nodeName == tagName) {
      list.push(child) ;
    }
  }
  return list ;
} ;

DOMUtil.prototype.findChildrenByClass = function(root, elementName, cssClass) {
  if(elementName != null)  elementName = elementName.toUpperCase() ;
  var list = new Array();
  var elements = root.childNodes ;
  for(var k = 0; k < elements.length; k++) {
    if(elementName == elements[k].nodeName) {
      if(elements[k].className.indexOf(" ") >= 0) {
        var classes = elements[k].className.split(" ");
        for(var j = 0; j < classes.length; j++) {
          if(classes[j] == cssClass) list.push(elements[k]);
        }
      } else if(elements[k].className == cssClass) {
        list.push(elements[k]);
      }
    }
  }
  return list;
} ;

DOMUtil.prototype.findChildrenByAttribute = function(root,  elementName, attrName, attrValue) {
  if(elementName != null)  elementName = elementName.toUpperCase() ;
  var list = new Array();
  var elements = root.childNodes ;
  for(var k = 0; k < elements.length; k++) {
    if(elementName == elements[k].nodeName) {
      var retValue = elements[k].getAttribute(attrName);
      if(retValue == attrValue) list.push(elements[k]);
    }
  }
  return list;
} ;

DOMUtil.prototype.findFirstChildByClass = function(root,  elementName, cssClass) {
  if(elementName != null)  elementName = elementName.toUpperCase() ;
  var elements = root.childNodes ;
  for(var k = 0; k < elements.length; k++) {
    if(elementName == elements[k].nodeName) {
      if(elements[k].className.indexOf(" ") >= 0) {
        var classes = elements[k].className.split(" ");
        for(var j = 0; j < classes.length; j++) {
          if(classes[j] == cssClass) return elements[k] ;
        }
      } else if(elements[k].className == cssClass) {
        return elements[k] ;
      }
    }
  }
  return null;
} ;

DOMUtil.prototype.findAncestorByClass = function(element, clazz) {
  var parent = element.parentNode ;
  while(parent != null) {
    if(parent.className == null) {
    } else if(parent.className.indexOf(" ") >= 0) {
      var classes = parent.className.split(" ");
      for(var j = 0;j < classes.length; j++) {
        if(classes[j] == clazz)  return parent ;
      }
    } else if(parent.className == clazz)  {
      return parent ;
    }
    parent = parent.parentNode ;
  }
  return null ;
} ;

DOMUtil.prototype.findAncestorsByClass = function(element, clazz) {
	var result = new Array();
  var parent = element.parentNode ;
  while(parent != null) {
    if(parent.className == null) {
    } else  if(parent.className.indexOf(" ") >= 0) {
      var classes = parent.className.split(" ");
      for(var j = 0;j < classes.length; j++) {
        if(classes[j] == clazz)  result.push(parent) ;
      }
    } else if(parent.className == clazz)  {
      result.push(parent) ;
    }
    parent =  parent.parentNode ;
  }
  return result ;
} ;

DOMUtil.prototype.findAncestorById = function(element,  id) {
  var parent = element.parentNode ;
  while(parent != null) {
    if(parent.id == id)  return parent ;
    parent = parent.parentNode ;
  }
  return null ;
} ;

DOMUtil.prototype.findAncestorByTagName = function(element, tagName) {
  var parent = element.parentNode ;
  while(parent != null) {
    if(parent.nodeName && parent.nodeName.toLowerCase() == tagName)  return parent ;
    parent = parent.parentNode ;
  }
  return null ;
} ;

DOMUtil.prototype.findDescendantsByClass = function(root, elementName, clazz) {
  var listElements = new Array() ;
  var elements = root.getElementsByTagName(elementName) ;
  this.findDescendantsByTag(root, elementName, elements);
  for(var k = 0; k < elements.length; k++) { 
  	if(elements[k].className == clazz) {
      listElements.push(elements[k]) ;
      continue;
    } 	
    if(elements[k].className.indexOf(" ") >= 0) {
      var classes = elements[k].className.split(" ") ;
      for(var j = 0; j < classes.length; j++) {
        if(classes[j] == clazz) listElements.push(elements[k]) ;
      }
    }
  }
  return listElements;
} ;

DOMUtil.prototype.findFirstDescendantByClass = function(root, elementName, clazz) {		
  var elements = root.getElementsByTagName(elementName);		
  for(var k = 0; k < elements.length; k++) {  	  	
  	if(elements[k].className == clazz) return elements[k] ;  
  	if(elements[k].className.indexOf(" ") >= 0){
  		var classes = elements[k].className.split(" ");	
    	for(var j = 0;j < classes.length; j++) {
      	if(classes[j] == clazz) return elements[k] ;      
    	} 	 
  	}
  }
  return null;
} ;

DOMUtil.prototype.findDescendantById = function(root, id) {
  var elements =  root.getElementsByTagName('*') ;
  for(var i = 0; i < elements.length; i++) {
    if(elements[i].id == id) {
      return elements[i] ;
    }
  }
  return null ;
} ;

DOMUtil.prototype.findDescendantsByTagName = function(root, tagName) {
  var list = new Array() ;
  this.findDescendantsByTag(root, tagName, list) ;
  return list ;
} ;

DOMUtil.prototype.findDescendantsByTag = function(root, tagName, list) {
  var  children = root.childNodes ;
  for(var k = 0; k < children.length; k++) {
    var child = children[k] ;
    if(tagName == null) {
      list[list.length] = child ;
    } else if(child.nodeName == null) {
    	continue;
    }else {    
      if(tagName == child.nodeName.toLowerCase())  list[list.length] = child ;
    }
    this.findDescendantsByTag(child, tagName, list) ;
  }
} ;

DOMUtil.prototype.hasDescendant= function(root, obj) {
  var elements =  root.getElementsByTagName("*") ;
  for(var i = 0; i < elements.length; i++) {
    if(elements[i] == obj) return true ;
  }
  return false ;
} ;

DOMUtil.prototype.hasDescendantClass = function(root, clazz) {
  var elements =  root.getElementsByTagName("*") ;
  for(var i = 0; i < elements.length; i++) {
    if(elements[i].className == clazz) return true ;
  }
  return false ;
} ;

DOMUtil.prototype.findNextElementByTagName = function(element, tagName) {
	var nextElement = element.nextSibling ;
	while(nextElement != null) {
		var nodeName = nextElement.nodeName ;
    if(nodeName != null) nodeName = nodeName.toLowerCase() ;
		if(nodeName == tagName) return nextElement ;
			
		nextElement = nextElement.nextSibling ;
	}
	
	return null ;
} ;

DOMUtil.prototype.createElementNode = function(innerHTML, tagName) {
	var temporaryContainer = document.createElement(tagName);
	temporaryContainer.innerHTML = innerHTML ;
	var applicationNode = this.getChildrenByTagName(temporaryContainer, "div")[0];
	
	return applicationNode ;
} ;

DOMUtil.prototype.generateId = function(objectId) {
	var dateTime = new Date();
	var time = dateTime.getTime();
	return (objectId + "-" + time);
} ;

DOMUtil.prototype.swapPosition = function(e1, e2) {
  if(e1.parentNode != e2.parentNode) alert("Report  bug to the admin, cannot swap element position"); 
  var tmpSwap = document.createElement("div");
  
} ;

DOMUtil.prototype.getStyle = function(element, style, intValue) {
	var result = null;
	if (element.style[style]) {
		result = element.style[style];
	}
	else if (element.currentStyle) {
		result = element.currentStyle[style];
	}
	else if (document.defaultView && document.defaultView.getComputedStyle) {
		style = style.replace(/([A-Z])/g, "-$1");
		style = style.toLowerCase();
		
		var s = document.defaultView.getComputedStyle(element, "");
		result = s && s.getPropertyValue(style);
	}
	if (intValue && result) {
		// if intValue is specified to true, returns only the numeric value of the property
		var intRes = Number(result.match(/\d+/)); // \d is any number (0-9), + is 1 or more times
		if (!isNaN(intRes)) result = intRes; // if intRes is numeric, put it in result
	}
	return result;
};

DOMUtil.prototype.hideElements = function() {
	document.onclick = function() {
		if(eXo.core.DOMUtil.hideElementList.length > 0) {
			for (var i = 0; i < eXo.core.DOMUtil.hideElementList.length; i++) {
				eXo.core.DOMUtil.hideElementList[i].style.display = "none";
			}
			eXo.core.DOMUtil.hideElementList.clear();
		}
	}
} ;

DOMUtil.prototype.listHideElements = function(object) {
	if(!eXo.core.DOMUtil.hideElementList.contains(object)) {
		eXo.core.DOMUtil.hideElementList.push(object) ;
	}
} ;

DOMUtil.prototype.removeTemporaryElement = function(element) {
	var parentElement = element.parentNode ;
	parentElement.removeChild(element);
};

/****************************************************************************/
eXo.core.DOMUtil = new DOMUtil() ;
