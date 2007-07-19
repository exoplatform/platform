/**
 * @author Nguyen Ba Uoc
 * 
 * This file contain all builtin commands.
 */

// Clear command
function Clear() {
  this.commandName = 'clear' ;
}

Clear.prototype.help = function() {
  return 'Clear console content' ;
} ;

Clear.prototype.execute = function(args, screen) {
  screen.innerHTML = '' ;
  return {retCode:0} ;
} ;

eXo.application.console.Clear = new Clear() ;
eXo.application.console.CommandManager.addCommand(eXo.application.console.Clear) ;
//====================================================
