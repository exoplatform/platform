/**
 * @author Nguyen Ba Uoc
 */

function CoreEditor() {
  this.autoDetectFire = false ;
  this.preSplitNodes = false ;
  this.HTMLUtil = eXo.core.HTMLUtil ;
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
    if (this.isProcessMultiSelect(node)) this.initMultiSelect() ;
    return ;
  }
  if (!this.autoDetectHandler(node)) {
    throw (new Error('Missing keyboard handler!')) ;
  }
  return this.initSingleSelect(node) ;  
} ;

CoreEditor.prototype.initSingleSelect = function(node) {
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
  eXo.core.Keyboard.clearListeners() ;
  eXo.core.Keyboard.register(this.handler) ;
  document.onclick = eXo.core.CoreEditor.onFinish ;
  return false ;
} ;

/**
 * This method should use for split selected DOM nodes
 * 
 * @param {Element} node
 */
CoreEditor.prototype.initMultiSelect = function(node) {
  window.alert('get here') ;
  if(window.getSelection) { // Netscape/Firefox/Opera
    var selObj = window.getSelection() ;
    var anchorNode = selObj.anchorNode ;
    var anchorOffset = selObj.anchorOffset ;
    var focusNode = selObj.focusNode ;
    var focusOffset = selObj.focusOffset ;
    if (focusNode === anchorNode) {
      var nodeValue = this.HTMLUtil.entitiesDecode(node.innerHTML) ;
      this.preSplitNodes[0] = this.createEditableNode(nodeValue.substr(0, anchorOffset)) ;
      this.preSplitNodes[1] = this.createEditableNode(nodeValue.substr(anchorOffset, focusOffset)) ;
      this.preSplitNodes[2] = this.createEditableNode(nodeValue.substr(focusOffset, nodeValue.length - 1)) ;
    } else {
      // We will break out selected node content from anchor node, then all next node until we get node who before focus node
      // and we need a final node contain remain value of focus node.
      var firstNodeContent = this.HTMLUtil.entitiesDecode(anchorNode.innerHTML) ;
      this.preSplitNodes[0] = this.createEditableNode(firstNodeContent.substr(0, anchorNode)) ;
      
      // get second selected content in mutilple node.
      var secondNodeContent = firstNodeContent.substr(anchorOffset, firstNodeContent.length - 1) ;
      while ((iNode = anchorNode.nextSibling) && iNode !== focusNode) {
        
      }
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


// Create editable node wrapper for text node.
CoreEditor.prototype.createEditableNode = function(nodeContent, tagName) {
  if (!tagName) tagName = 'SPAN' ;
  var editableNode = document.createElement(tagName) ;
  editableNode.setAttribute('editable', '1') ;
  editableNode.innerHTML = nodeContent ;
  return editableNode ;
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

/**
 * 
 * @param {Element} node
 */
CoreEditor.prototype.isProcessMultiSelect = function(node) {
  if (!node) return ;
  if (node.getAttribute && node.getAttribute('multiselect') == '1') {
    return true ;
  }
  return false ;
} ;

CoreEditor.prototype.onFinish = function(event) {
  eXo.core.Keyboard.clearListeners() ;
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