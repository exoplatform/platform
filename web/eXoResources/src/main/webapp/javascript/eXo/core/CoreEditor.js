/**
 * @author Nguyen Ba Uoc
 */

function CoreEditor() {
  this.autoDetectFire = false ;
  this.HTMLUtil = eXo.core.HTMLUtil ;
} ;

/**
 * 
 * @param {Element} node
 */
CoreEditor.prototype.isContainerNode = function(node) {
  if (!node) return ;
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
  if (!node) return ;
  if (node.getAttribute && 
      node.getAttribute('editable') == '1') {
    return true ;
  }
  return false ;
} ;

CoreEditor.prototype.registerCoreEditors = function(node4Reg) {
  if (node4Reg && !node4Reg.nodeName) {
    node4Reg = document.getElementById(node4Reg) ;
  }
  
  if (!node4Reg || node4Reg.nodeType != 1) {
    node4Reg = document.body ;
//    throw (new Error('Error when register...')) ;
  }
  var nodeList = node4Reg.getElementsByTagName('*') ;
  for(var i=0; i<nodeList.length; i++) {
    var node = nodeList.item(i) ;
    if (node.nodeType == 1 && this.isContainerNode(node) &&
        node.getAttribute('handler')) {
      this.registerSubCoreEditor(node) ;
      node.onclick = this.autoDetectSubCoreEditor ;
    }
  }
} ;

/**
 * 
 * @param {Element} node
 */
CoreEditor.prototype.registerSubCoreEditor = function(node) {
  var childNodes = node.getElementsByTagName('*') ;
  for(var i=0; i<childNodes.length; i++) {
    var child = childNodes[i] ;
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

CoreEditor.prototype.init = function(node) {
  if(node == null) return ;
  if(this.isMultiSelection()) {
    return ;
  }
  if (!this.autoDetectHandler(node)) {
    throw (new Error('Missing keyboard handler!')) ;
  }
  var clickPosition =  this.getClickPosition(node) ;
  this.clearSelection() ;
  var text = this.HTMLUtil.entitiesDecode(node.innerHTML) ;
  var beforeCursor = '' ;
  var afterCursor = '' ;
  if(clickPosition > 0) {
    beforeCursor = text.substring(0, clickPosition) ;
    afterCursor = text.substring(clickPosition, text.length) ;
  } else if(clickPosition == 0) {
    beforeCursor = '' ;
    afterCursor = text ;
  }
  beforeCursor = this.HTMLUtil.entitiesEncode(beforeCursor) ;
  afterCursor = this.HTMLUtil.entitiesEncode(afterCursor) ;
  this.handler.init(node, beforeCursor, afterCursor) ;
  this.handler.defaultWrite() ;
  eXo.core.Keyboard.finish() ;
  eXo.core.Keyboard.init() ;
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
  if (!node) return ;
  var handler = false ;
  for (var nodeIter = node;; nodeIter = nodeIter.parentNode) {
    if (nodeIter.nodeType == 1) {
      if (nodeIter.className == 'UIWindow') break ;
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

/**
 * 
 * @param {Element} node
 */
CoreEditor.prototype.isProcessMultiSelect = function(node) {
  if (!node) return ;
  while((node = node.parentNode) && 
        node.className != 'UIWindow') {
    if (!this.isContainerNode(node)) break ;      
  }
  if (node.getAttribute && node.getAttribute('multiselect') == '1') {
    return true ;
  }
  return false ;
} ;

CoreEditor.prototype.onFinish = function(event) {
  eXo.core.Keyboard.finish() ;
  if (eXo.core.CoreEditor.handler) {
    var containerNode = eXo.core.CoreEditor.handler.currentNode ;
    while (containerNode && 
          (containerNode = containerNode.parentNode) && 
           containerNode.className && containerNode.className != 'UIWindow') {
      if (eXo.core.CoreEditor.isContainerNode(containerNode)) {
        
      }
    }
    eXo.core.CoreEditor.handler.onFinish() ;
  }
} ;

CoreEditor.prototype.isMultiSelection = function() {
  if(window.getSelection) { // Netscape/Firefox/Opera
    if(window.getSelection().toString().length > 0) {
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
  if (window.getSelection) { // Netscape/Firefox/Opera 
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
  if(window.getSelection) { // Netscape/Firefox/Opera
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