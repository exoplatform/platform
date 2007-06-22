/**
 * @author uoc.nb
 */

eXo.require('eXo.core.KeyboardListener');

function Editor() {
  this.LEFT_KEY    = 37 ;
  this.RIGHT_KEY   = 39 ;
  this.containerIdentify = 'editcontainer' ;
  this.editableIdentify = 'editable' ;
  this.activeEditClass = 'ActiveEdit';
  this.keyboardListener = eXo.core.KeyboardListener ;
  this.beforeCursor = null ;
  this.cursor = '<span style="border: solid 1px red; width: 2px; height: 100%;" cursor="1">&nbsp;</span>' ;
  this.afterCursor = null ;
  this.currentNode = null ;  
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
  this.onFinish() ;
  var text = node.innerHTML ;
  if(clickPosition > 0) {
    this.beforeCursor = text.substring(0, clickPosition) ;
    this.afterCursor = text.substring(clickPosition, text.length) ;
  } else if(clickPosition == 0) {
    this.beforeCursor = '' ;
    this.afterCursor = text ;
  }
  node.innerHTML = this.beforeCursor + this.cursor + this.afterCursor ;
  this.currentNode = node ;  
  this.highLightEditContainer() ;
  this.registerkeyboardListenerHandler(node) ;  
}

Editor.prototype.highLightEditContainer = function() {
  var parent = this.getEditContainer() ;
  if(parent) {
    parent.className = this.activeEditClass + ' ' + parent.className ;
  }
}

Editor.prototype.deHighLightEditContainer = function() {  
  var parent = this.getEditContainer() ;
  if(parent) {
    parent.className = parent.className.replace(this.activeEditClass + ' ', '') ;
  }
}

Editor.prototype.getEditContainer = function() {
  if(!this.currentNode) {
    return false ;
  }
  var parent = this.currentNode ;
  while(parent.nodeName != 'BODY' && parent.getAttribute('editcontainer') != 1) {
    parent = parent.parentNode ;
  }
  if(parent.getAttribute(this.containerIdentify) == 1) {
    return parent ;
  }
  return false ;
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

Editor.prototype.onFinish = function() {
  if(this.currentNode == null) {
    return ;
  }
  this.currentNode.innerHTML = this.beforeCursor +  this.afterCursor ;
  this.deHighLightEditContainer() ;
  this.currentNode = null ;
  this.keyboardListener.clearRegisteredHandler() ;
}

Editor.prototype.clearSelection = function() {  
  if (window.getSelection) { /* Netscape/Firefox/Opera */    
    window.getSelection().removeAllRanges() ;
  } else if(document.selection && document.selection.createRange) { // IE Only
    document.selection.clear() ;
  }
  this.removeCursor() ;
}

Editor.prototype.removeCursor = function() {
  if(this.currentNode) {
    this.currentNode.innerHTML = this.beforeCursor +  this.afterCursor ;
  }
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

Editor.prototype.registerkeyboardListenerHandler = function() {
  this.keyboardListener.clearRegisteredHandler() ;
  this.keyboardListener.register(this.keyboardListener.onBackspace, this.onBackspaceKey) ;
  this.keyboardListener.register(this.keyboardListener.onDelete, this.onDeleteKey) ;
  this.keyboardListener.register(this.keyboardListener.onEnter, this.onEnterKey) ;
  this.keyboardListener.register(this.keyboardListener.onDefault, this.onDefaultKey) ;
  this.keyboardListener.register(this.keyboardListener.onAlphabet, this.onDefaultKey) ;
  this.keyboardListener.register(this.keyboardListener.onDigit, this.onDefaultKey) ;
  this.keyboardListener.register(this.keyboardListener.onNavigate, this.onNavigateKey) ;
}

Editor.prototype.onDefaultKey = function(keynum) {
  var keychar = String.fromCharCode(keynum) ;
  var editor = eXo.core.Editor ;
  editor.beforeCursor += keychar ;
  editor.currentNode.innerHTML = editor.beforeCursor + editor.cursor + editor.afterCursor ;
  return true ;
}

Editor.prototype.onBackspaceKey = function() {
  var editor = eXo.core.Editor ;
  editor.beforeCursor = editor.beforeCursor.substr(0, (editor.beforeCursor.length - 1)) ;
  editor.currentNode.innerHTML = editor.beforeCursor + editor.cursor + editor.afterCursor ;
  return false ;
}

Editor.prototype.onDeleteKey = function() {
  var editor = eXo.core.Editor ;
  editor.afterCursor = editor.afterCursor.substr(1, (editor.afterCursor.length - 1)) ;
  editor.currentNode.innerHTML = editor.beforeCursor + editor.cursor + editor.afterCursor ;
  return false ;
}

Editor.prototype.onEnterKey = function() {
  eXo.core.Editor.clearSelection();
  return true ;
}

Editor.prototype.onLeftKey = function(direction) {
  var editor = eXo.core.Editor ;
  switch(direction) {
    case this.LEFT_KEY: 
      // Move cursor to left
      editor.beforeCursor = editor.beforeCursor.substr(0, (editor.beforeCursor.length - 2)) ;
      editor.afterCursor = editor.beforeCursor.substr((editor.beforeCursor.length - 2), (editor.beforeCursor.length - 1)) + 
                            editor.afterCursor ;
      editor.currentNode.innerHTML = editor.beforeCursor + editor.cursor + editor.afterCursor ;
      break ;
    case this.RIGHT_KEY:
      // Move cursor to right
      editor.beforeCursor += editor.afterCursor.substr(0, 1) ;
      editor.afterCursor = editor.afterCursor.substr(1, (editor.beforeCursor.length - 1)) ;
      editor.currentNode.innerHTML = editor.beforeCursor + editor.cursor + editor.afterCursor ;
      break ;
    default:
      return true ;
  }
  return false ;
}

eXo.core.Editor = new Editor() ;