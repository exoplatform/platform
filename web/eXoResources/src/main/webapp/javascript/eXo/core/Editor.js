/**
 * @author uoc.nb
 */

/*
eXo.require('eXo.core.DefaultListener');
eXo.require('eXo.core.SimpleNodeEditor');
eXo.require('eXo.core.Keyboard');
*/

function Editor() {
  this.containerIdentify = 'editcontainer' ;
  this.editableIdentify = 'editable' ;
  this.activeEditClass = 'ActiveEdit';
}

Editor.prototype.registerEditors = function(node4Reg) {
  if (!node4Reg || node4Reg.nodeType != 1) {
    throw (new Error('Error when register...')) ;
  }
  var nodeList = node4Reg.getElementsByTagName('DIV') ;
  for(var i=0; i<nodeList.length; i++) {
    var node = nodeList.item(i) ;
    if (node.nodeType == 1 && node.getAttribute(this.containerIdentify) == 1) {
      this.registerSubEditor(node) ;
    }
  }
}

Editor.prototype.registerSubEditor = function(node) {
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
    if(child.getAttribute(this.editableIdentify) == 1) {
      child.onclick = function() {
        eXo.core.Editor.init(this);
      } ;
    }
  }
}

// Create editable node wrapper for text node.
Editor.prototype.createEditableNode = function(nodeContent) {
  var editableNode = document.createElement('SPAN') ;
  editableNode.setAttribute(this.editableIdentify, '1') ;
  editableNode.innerHTML = nodeContent ;
  return editableNode ;
}

Editor.prototype.init = function(node) {
  if(node == null) return ;
  if(this.isMultipleSelection()) {
    return ;
  }
  var clickPosition =  this.getClickPosition(node) ;
  this.clearSelection() ;
  var text = node.innerHTML ;
  var beforeCursor = '' ;
  var afterCursor = '' ;
  if(clickPosition > 0) {
    beforeCursor = text.substring(0, clickPosition) ;
    afterCursor = text.substring(clickPosition, text.length) ;
  } else if(clickPosition == 0) {
    beforeCursor = '' ;
    afterCursor = text ;
  }
  eXo.core.SimpleNodeEditor.init(node, beforeCursor, afterCursor) ;
  eXo.core.SimpleNodeEditor.defaultWrite() ;
  eXo.core.Keyboard.clearListeners() ;
  eXo.core.Keyboard.register(eXo.core.SimpleNodeEditor) ;
}

// ----- Will remove ----
Editor.prototype.highLightEditContainer = function() {
  var parent = this.getEditContainer() ;
  if(parent) {
    parent.className = this.activeEditClass + ' ' + parent.className ;
  }
}

Editor.prototype.isMultipleSelection = function() {
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
}

Editor.prototype.clearSelection = function() {  
  if (window.getSelection) { /* Netscape/Firefox/Opera */    
    window.getSelection().removeAllRanges() ;
  } else if(document.selection && document.selection.createRange) { // IE Only
    document.selection.clear() ;
  }
  eXo.core.SimpleNodeEditor.removeCursor() ;
}

Editor.prototype.getClickPosition = function(node) {
  if(window.getSelection) { /* Netscape/Firefox/Opera */
    var selObj = window.getSelection() ;
    var clickPos = anchorOffset = selObj.anchorOffset ;
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
}

eXo.core.Editor = new Editor() ;