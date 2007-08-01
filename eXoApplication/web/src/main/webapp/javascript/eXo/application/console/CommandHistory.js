/**
 * @author Nguyen Ba Uoc
 */
function CommandHistory() {
  this.SEPARATE = '}{' ;
  this.rootNode = false ;
  this.init() ;
}

CommandHistory.prototype.init = function(node) {
  this.rootNode = node ;
  if (this.rootNode && this.rootNode.getAttribute('cmdHistory')) {
    this.commands = this.rootNode.getAttribute('cmdHistory').split(this.SEPARATE) ;
  } else {
    this.commands = [] ;
  }
  this.currentIndex = this.commands.length - 1 ;
} ;

CommandHistory.prototype.finish = function() {
  if (this.rootNode) {
    this.rootNode.setAttribute('cmdHistory', this.commands.join(this.SEPARATE)) ;
  }
  this.rootNode = false ;
  this.commands = false ;
  this.currentIndex = false ;
} ;

CommandHistory.prototype.getPrevious = function() {
  if (this.commands.length <= 0) return false ; 
  this.currentIndex -- ;
  if (this.currentIndex < 0) {
    this.currentIndex = this.commands.length - 1 ;
  }
  return this.commands[this.currentIndex] ;
} ;

CommandHistory.prototype.getNext = function() {
  if (this.commands.length <= 0) return false ;
  this.currentIndex ++ ;
  if (this.currentIndex >= this.commands.length) {
    this.currentIndex = 0 ;
  }
  return this.commands[this.currentIndex] ;
} ;

/**
 * 
 * @param {String} cmd
 */
CommandHistory.prototype.insert = function(command) {
  if (command && command != '') {
    if (this.commands.contains(command)) {
      this.commands.remove(command) ;
    }
    this.commands[this.commands.length] = command ;
    this.currentIndex = this.commands.length - 1 ;
  }
} ;

CommandHistory.prototype.remove = function(index) {
  this.commands.splice(index, 1) ;
} ;

CommandHistory.prototype.clear = function() {
  this.commands.clear() ;
} ;

eXo.application.console.CommandHistory = new CommandHistory() ;