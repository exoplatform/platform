/**
 * @author Nguyen Ba Uoc
 */

function History() {
  this.commandName = 'history' ;
  this.prefix = 'builtin' ;
}

History.prototype = eXo.application.console.Command.createInstance() ;

History.prototype.help = function() {
  return 'Manage command history\
          <br/> Posible sub command:\
          <br/>   clear  clear history' ;
} ;

History.prototype.execute = function(args, consoleScreen) {
  var cmdHistory = eXo.application.console.CommandManager.cmdHistory ;
  if (args && args != '') {
    args = args.trim() ;
    if (args == 'clear') {
      cmdHistory.clear() ;
      return 0 ;
    } else {
      consoleScreen.write(args + ' is not valid') ;
      return -1 ;
    }
  }
  consoleScreen.write(cmdHistory.commands.join('<br />')) ;
  return 0 ;
} ;

eXo.application.console.History = new History() ;
eXo.application.console.CommandManager.addCommand(eXo.application.console.History) ;