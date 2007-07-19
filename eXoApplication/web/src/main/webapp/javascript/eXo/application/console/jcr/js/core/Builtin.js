// Clear command
function Clear() {
  this.commandName = 'clear' ;
}

Clear.prototype.help = function() {
  return 'Clear console content' ;
} ;

Clear.prototype.execute = function(args, screen) {
  screen.innerHTML = '' ;
} ;

// ShowNode command
function ShowNode() {
}

eXo.application.console.Clear = new Clear() ;
//====================================================
