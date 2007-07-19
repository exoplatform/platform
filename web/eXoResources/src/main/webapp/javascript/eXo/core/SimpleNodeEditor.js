/**
 * @author Nguyen Ba Uoc
 * 
 * A Keyboard's listener implement. 
 * 
 */
function SimpleNodeEditor() {
  this.cursor = '<span style="border: solid 1px red; width: 2px; height: 100%;" cursor="1">&nbsp;</span>' ;
}

SimpleNodeEditor.prototype = new eXo.core.DefaultKeyboardListener() ;

SimpleNodeEditor.prototype.init = function(node, beforeCursor, afterCursor) {
  if(!this.isSameNode(node)) {
    this.onFinish() ;
  }
  this.currentNode = node ;
  this.beforeCursor = beforeCursor ;
  this.afterCursor = afterCursor ;
  eXo.application.console.CommandManager.init(node) ;
}

SimpleNodeEditor.prototype.isSameNode = function(node) {
  if(this.currentNode === node) {
    return true ;
  }
  return false ;
}

SimpleNodeEditor.prototype.onFinish = function() {
  this.currentNode = null ;
  this.beforeCursor = null ;
  this.afterCursor = null ;
  this.removeCursor() ;
}

SimpleNodeEditor.prototype.removeCursor = function() {
  this.write(this.beforeCursor, '', this.afterCursor) ;
}

SimpleNodeEditor.prototype.defaultWrite = function() {
  this.write(this.beforeCursor, this.cursor, this.afterCursor) ;
}

SimpleNodeEditor.prototype.write = function(beforeCursor, cursor, afterCursor) {
  if(this.currentNode) {
    this.currentNode.innerHTML = beforeCursor + cursor + afterCursor ;
    // Detect empty node
    if (this.currentNode.innerHTML == '') {
      this.currentNode.innerHTML = '&nbsp;' ;
    }
    this.beforeCursor = beforeCursor ;
    this.afterCursor = afterCursor ;
  }
}

SimpleNodeEditor.prototype.getTextCommand = function() {
  var command = this.beforeCursor + this.afterCursor ;
  command = eXo.core.HTMLUtil.entitiesDecode(command) ;
  return command ;
}

// Overwrite DefaultKeyboardListener's methods.

// Printable keys
SimpleNodeEditor.prototype.onDefault = function(keynum, keychar) {
  eXo.application.console.UIConsoleApplication.hideMaskWorkspace() ;
  keychar = eXo.core.HTMLUtil.entitiesEncode(keychar) ;
//  window.alert('[' + keychar + ']') ;
  this.beforeCursor += keychar ;
  this.defaultWrite() ;
  return false ;
}

SimpleNodeEditor.prototype.onAlphabet = SimpleNodeEditor.prototype.onDefault ;

SimpleNodeEditor.prototype.onDigit = SimpleNodeEditor.prototype.onDefault ;

SimpleNodeEditor.prototype.onPunctuation = SimpleNodeEditor.prototype.onDefault ;

// Control keys
SimpleNodeEditor.prototype.onBackspace = function(keynum, keychar) {
  eXo.application.console.UIConsoleApplication.hideMaskWorkspace() ;
  this.beforeCursor = eXo.core.HTMLUtil.entitiesDecode(this.beforeCursor) ;
  this.beforeCursor = this.beforeCursor.substr(0, (this.beforeCursor.length - 1)) ;
  this.beforeCursor = eXo.core.HTMLUtil.entitiesEncode(this.beforeCursor) ;
  this.defaultWrite() ;  
  return false ;
}

SimpleNodeEditor.prototype.onDelete = function(keynum, keychar) {
  eXo.application.console.UIConsoleApplication.hideMaskWorkspace() ;
  this.afterCursor = this.afterCursor.substr(1, (this.afterCursor.length - 1)) ;
  this.defaultWrite() ;
  return false ;
}

SimpleNodeEditor.prototype.onEnter = function(keynum, keychar) {
  eXo.application.console.UIConsoleApplication.hideMaskWorkspace() ;
  eXo.application.console.CommandManager.execute(this.getTextCommand()) ;
  this.write('', this.cursor, '') ;
  return false ;
}

SimpleNodeEditor.prototype.onTab = function(keynum, keychar) {
  eXo.application.console.CommandManager.help(this.getTextCommand()) ;
  return false ;
}

// Navigate keys
SimpleNodeEditor.prototype.onHome = function(keynum, keychar) {
  eXo.application.console.UIConsoleApplication.hideMaskWorkspace() ;
  if(this.beforeCursor.length == '') {
    return false ;
  }
  this.afterCursor = this.beforeCursor + this.afterCursor ;
  this.beforeCursor = '' ;
  this.defaultWrite() ;
  return false ;
}

SimpleNodeEditor.prototype.onEnd = function(keynum, keychar) {
  eXo.application.console.UIConsoleApplication.hideMaskWorkspace() ;
  if(this.afterCursor.length == '') {
    return false ;
  }
  this.beforeCursor = this.beforeCursor + this.afterCursor ;
  this.afterCursor = '' ;
  this.defaultWrite() ;
  return false ;
}

SimpleNodeEditor.prototype.onLeftArrow = function(keynum, keychar) {
  eXo.application.console.UIConsoleApplication.hideMaskWorkspace() ;
  if(this.beforeCursor.length == '') {
    return false ;
  }
  if(this.beforeCursor.length == 1) {
    this.afterCursor = this.beforeCursor + this.afterCursor ;
    this.beforeCursor = '' ;
  } else {
    this.afterCursor = this.beforeCursor.substr(
                                                 (this.beforeCursor.length - 2), 
                                                 (this.beforeCursor.length - 1)
                                               ) + this.afterCursor ;
    this.beforeCursor = this.beforeCursor.substr(0, (this.beforeCursor.length - 2)) ;
  }
  this.defaultWrite() ;
  return false ;
}

SimpleNodeEditor.prototype.onRightArrow = function(keynum, keychar) {
  eXo.application.console.UIConsoleApplication.hideMaskWorkspace() ;
  if(this.afterCursor.length == '') {
    return false ;
  }
  this.beforeCursor += this.afterCursor.substr(0, 1) ;
  this.afterCursor = this.afterCursor.substr(1, (this.afterCursor.length - 1)) ;
  this.defaultWrite() ;
  return false ;
}

eXo.core.SimpleNodeEditor = new SimpleNodeEditor() ;