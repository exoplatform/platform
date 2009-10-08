/**
 * @author Nguyen Ba Uoc
 * 
 * A Keyboard's listener implement. 
 * 
 */
function ConsoleInputManager() {
  this.cmdManager = eXo.application.console.CommandManager ;
}

ConsoleInputManager.prototype = new eXo.core.DefaultKeyboardListener() ;

ConsoleInputManager.prototype.init = function(node, beforeCursor, afterCursor) {
  if(!this.isSameNode(node)) {
    this.onFinish() ;
  }
  var cmdTmp = beforeCursor + afterCursor ;
  if (cmdTmp == '...') {
    beforeCursor = '' ;
    afterCursor = '' ;
  }
  this.currentNode = node ;
  this.beforeCursor = beforeCursor ;
  this.afterCursor = afterCursor ;
  this.cmdManager.init(node) ;
} ;

ConsoleInputManager.prototype.onFinish = function() {
  var content = this.beforeCursor + this.afterCursor + '' ;
  if (content.trim() == '') {
    this.beforeCursor = '...' ;
  }
  this.removeCursor() ;
  this.currentNode = null ;
  this.beforeCursor = null ;
  this.afterCursor = null ;
} ;

ConsoleInputManager.prototype.onBeforePreKeyProcess = function() {
  eXo.application.console.CommandManager.hideQuickHelp() ;
} ;

// Overwrite DefaultKeyboardListener's methods.

// Printable keys

ConsoleInputManager.prototype.onAlphabet = ConsoleInputManager.prototype.onDefault ;

ConsoleInputManager.prototype.onDigit = ConsoleInputManager.prototype.onDefault ;

// Control keys
ConsoleInputManager.prototype.onEnter = function(keynum, keychar) {
  this.cmdManager.hideQuickHelp() ;
  this.cmdManager.execute(this.getEditContent()) ;
  eXo.application.console.CommandHistory.insert(this.getEditContent()) ;
  this.write('', this.cursor, '') ;
  return false ;
} ;

ConsoleInputManager.prototype.onTab = function(keynum, keychar) {
  this.cmdManager.help(this.getEditContent()) ;
  return false ;
} ;

ConsoleInputManager.prototype.onEscapse = function(keynum, keychar) {
  this.cmdManager.hideQuickHelp() ;
  this.beforeCursor = '' ;
  this.afterCursor = '' ;
  this.defaultWrite() ;
  return false ;
} ;

// Navigate keys
ConsoleInputManager.prototype.onUpArrow = function(keynum, keychar) {
  var cmd = this.cmdManager.cmdHistory.getPrevious() ;
  if (cmd) {
    this.beforeCursor = cmd ;
    this.afterCursor = '' ;
    this.defaultWrite() ;
  }
  return false ;
} ;

ConsoleInputManager.prototype.onDownArrow = function(keynum, keychar) {
  var cmd = this.cmdManager.cmdHistory.getNext() ;
  if (cmd) {
    this.beforeCursor = cmd ;
    this.afterCursor = '' ;
    this.defaultWrite() ;
  }
  return false ;
} ;

eXo.application.console.ConsoleInputManager = new ConsoleInputManager() ;