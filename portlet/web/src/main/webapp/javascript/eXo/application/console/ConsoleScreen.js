/**
 * @author Nguyen Ba Uoc 
 */

function ConsoleScreen() {
  this.screenNode = false ;
}

ConsoleScreen.prototype.init = function(node) {
  this.screenNode = node ;
} ;

ConsoleScreen.prototype.finish = function() {
  this.screenNode = false ;
} ;

/**
 * 
 * @param {String} txt
 */
ConsoleScreen.prototype.write = function(txt) {
  var node = document.createElement('DIV') ;
  node.innerHTML = txt ;
  this.screenNode.appendChild(node) ;
  node.scrollIntoView(false) ;
} ;

ConsoleScreen.prototype.clear = function() {
  this.screenNode.innerHTML = '' ;
} ;

if (!eXo.application.console) eXo.application.console = {} ;
eXo.application.console.ConsoleScreen = new ConsoleScreen() ;