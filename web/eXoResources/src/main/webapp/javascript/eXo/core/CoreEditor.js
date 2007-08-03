/**
 * @author Nguyen Ba Uoc
 */

function CoreEditor() {
  this.autoDetectFire = false ;
} ;

/**
 * 
 * @param {Element} node
 */
CoreEditor.prototype.isContainerNode = function(node) {
  if (node.getAttribute && 
      node.getAttribute('editcontainer') == 1) {
    return true ;
  }
  return false ;
} ;

/**
 * 
 * @param {Element} node
 */
CoreEditor.prototype.isEditableNode = function(node) {
  if (node.getAttribute && 
      node.getAttribute('editable') == 1) {
    return true ;
  }
  return false ;
} ;

CoreEditor.prototype.registerCoreEditors = function(node4Reg) {
  if (!node4Reg || node4Reg.nodeType != 1) {
    throw (new Error('Error when register...')) ;
  }
  var nodeList = node4Reg.getElementsByTagName('DIV') ;
  for(var i=0; i<nodeList.length; i++) {
    var node = nodeList.item(i) ;
    if (node.nodeType == 1 && this.isContainerNode(node) &&
        node.getAttribute('handler')) {
      this.registerSubCoreEditor(node) ;
      node.onclick = this.autoDetectSubCoreEditor ;
      
    }
  }
} ;

CoreEditor.prototype.registerSubCoreEditor = function(node) {
  var childNodes = node.childNodes ;
  for(var i=0; i<childNodes.length; i++) {
    var child = childNodes[i] ;
    // Autodetect: replace textnode by span node with editable attribute.
    if(child.nodeType == 3 && child.nodeValue != '') {
      child = this.createEditableNode(child.nodeValue) ;
      node.replaceChild(child, childNodes[i]) ;
    }
    if (child.nodeType != 1) {
      continue ;
    }
    if(this.isEditableNode(child) == 1) {
      child.onclick = function(event) {
        eXo.core.Keyboard.cancelEvent(event) ;
        return eXo.core.CoreEditor.init(this);
      } ;
    }
  }
} ;

/**
 * 
 * @param {Event} event
 */
CoreEditor.prototype.autoDetectSubCoreEditor = function(event) {
  var childNodes = this.childNodes ;
  for(var i=0; i<childNodes.length; i++) {
    var child = childNodes[i] ;
    if(eXo.core.CoreEditor.isEditableNode(child) && child.onclick) {
      eXo.core.CoreEditor.autoDetectFire = true ;
      eXo.core.Keyboard.cancelEvent(event) ;
      return eXo.core.CoreEditor.init(child);
    }
  }
} ;

// Create editable node wrapper for text node.
CoreEditor.prototype.createEditableNode = function(nodeContent) {
  var editableNode = document.createElement('SPAN') ;
  editableNode.setAttribute(this.editableIdentify, '1') ;
  editableNode.innerHTML = nodeContent ;
  return editableNode ;
} ;

CoreEditor.prototype.init = function(node) {
  if(node == null) return ;
  if(this.isMultipleSelection()) {
    return ;
  }
  if (!this.autoDetectHandler(node)) {
    throw (new Error('Missing keyboard handler!')) ;
  }
  var clickPosition =  this.getClickPosition(node) ;
  this.clearSelection() ;
  var text = eXo.core.HTMLUtil.entitiesDecode(node.innerHTML) ;
  var beforeCursor = '' ;
  var afterCursor = '' ;
  if(clickPosition > 0) {
    beforeCursor = text.substring(0, clickPosition) ;
    afterCursor = text.substring(clickPosition, text.length) ;
  } else if(clickPosition == 0) {
    beforeCursor = '' ;
    afterCursor = text ;
  }
  beforeCursor = eXo.core.HTMLUtil.entitiesEncode(beforeCursor) ;
  afterCursor = eXo.core.HTMLUtil.entitiesEncode(afterCursor) ;
  this.handler.init(node, beforeCursor, afterCursor) ;
  this.handler.defaultWrite() ;
  eXo.core.Keyboard.clearListeners() ;
  eXo.core.Keyboard.register(this.handler) ;
  document.onclick = eXo.core.CoreEditor.onFinish ;
  return false ;
} ;

/**
 * @param {Element} node
 * 
 * @return {DefaultKeyboardListener}
 */
CoreEditor.prototype.autoDetectHandler = function(node) {
  var handler = false ;
  for (var nodeIter = node;; nodeIter = nodeIter.parentNode) {
    if (nodeIter.nodeType == 1) {
      if (nodeIter.className == 'UIConsoleApplication') break ;
      if (this.isContainerNode(nodeIter)) {
        handler = nodeIter.getAttribute('handler') ;
        break ;
      }
    }
  }
  try {
    this.handler = eval(handler) ;
    if (!this.handler) return false ;
    return true ;
  }
  catch (e) {
    return false ;
  }
} ;

CoreEditor.prototype.onFinish = function(event) {
  eXo.core.Keyboard.clearListeners() ;
  if (eXo.core.CoreEditor.handler) eXo.core.CoreEditor.handler.onFinish() ;
} ;

CoreEditor.prototype.isMultipleSelection = function() {
  if(window.getSelection) { /* Netscape/Firefox/Opera */
    if((window.getSelection() + '').length > 0) {
      return true ;
    } else {
      return false ;
    }
  }
  else if(document.selection && document.selection.createRange) { // IE Only
    if(document.selection.createRange().text.length > 0) {
      return true ;
    } else {
      return false ;
    }
  }
} ;

CoreEditor.prototype.clearSelection = function() {  
  if (window.getSelection) { /* Netscape/Firefox/Opera */    
    window.getSelection().removeAllRanges() ;
  } else if(document.selection && document.selection.createRange) { // IE Only
    document.selection.clear() ;
  }
  this.handler.removeCursor() ;
} ;

CoreEditor.prototype.getClickPosition = function(node) {
  if (this.autoDetectFire) {
    this.autoDetectFire = !this.autoDetectFire ;
    return node.innerHTML.length ;
  }
  if(window.getSelection) { /* Netscape/Firefox/Opera */
    var selObj = window.getSelection() ;
    var clickPos = selObj.anchorOffset ;
    if(selObj.anchorNode && selObj.anchorNode.nodeType == 3) {
      var tmpTextNode = selObj.anchorNode.previousSibling ;
      while(tmpTextNode) {
        if(tmpTextNode.nodeType == 3) {
          clickPos += tmpTextNode.nodeValue.length ;
        }
        tmpTextNode = tmpTextNode.previousSibling ;
      } 
    }
    return clickPos ;
  }
  else if(document.selection && document.selection.createRange) { // IE Only
    var sel = document.selection.createRange();
    var clone = sel.duplicate();
    sel.collapse(true);
    clone.moveToElementText(node);
    clone.setEndPoint('EndToEnd', sel);
    return clone.text.length;
  }
} ;

eXo.core.CoreEditor = new CoreEditor() ;