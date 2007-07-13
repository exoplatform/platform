function Console() {
  this.commands = new Array();
  this.commands['clear'] = new Clear() ;
  this.commands['shownode'] = new ShowNode() ;
  this.consoleUINode = false ;
}

Console.prototype.init = function(node) {
  this.consoleUINode = node ;
} ;

/**
 * TODO: rename the method to help
 * This method should:
 * 1. parse the command
 * 2. If the first argument is completed, look up  the command object that match the first 
 *    first argument and call the method help of that command object. If no comand is found,
 *    show "No command is found"
 * 3. If the first argument is not completed, look up all the possible match commands and show 
 *    them in the help screen
 * @param {Object} command
 */
Console.prototype.genericComplete = function(command) {
  var commandState = false ;
  for (var cmd in this.commands) {
    if (command == (cmd + ' ')) {
      commandState = true ;
      break ;
    }
  }
  if (commandState) {
    
  }
} ;

Console.prototype.addCommand = function(command) {
  if(this.commands[command.commandName] != null) {
    alert('Command ' + command.commandName + ' is already registered') ; 
    return ;
  }
  this.commands[command.commandName] = command ;
} ;

/**
 * This method should:
 * 1. Load the javascript file
 * 2. evaluate the javascript 
 * 3. In the javascript, the developer should call the method 
 * eXo.app.console.Application.addCommand(...) to register a command with the console
 * 
 * @param {Object} jsFile
 * @param {Object} jsLocation
 */
Console.prototype.registerJSModule = function(jsFile, jsLocation) {
  eXo.require(js)
} ;

if(eXo.app.console == null) eXo.app.console = {} ; 
eXo.app.console.Application = new Console() ;
eXo.app.console.register() ;