function CommandManager() {
  this.commands = [] ;
  this.commandNode = false ;
  this.commandTypeNode = false ;
  this.eXoConsoleResult = false ;
  this.screenNode = false ;
  this.uiConsoleApplication = false ;
  this.ready = true ;
}

CommandManager.prototype.init = function(node) {
  this.commandNode = node ;
  this.initCommon() ;
} ;

CommandManager.prototype.initCommon = function() {
  this.initUIConsoleApplication() ;
  this.initCommandType() ;
  this.initScreen() ;
} ;

CommandManager.prototype.initUIConsoleApplication = function() {
  var uiConsoleApplicationTmp = this.commandNode.parentNode ;
  while(uiConsoleApplicationTmp && uiConsoleApplicationTmp.className != 'UIConsoleApplication') {
    if (uiConsoleApplicationTmp.tagName == 'BODY') {
      break ;
    }
    uiConsoleApplicationTmp = uiConsoleApplicationTmp.parentNode ;
  }
  if (uiConsoleApplicationTmp.className == 'UIConsoleApplication') {
    this.uiConsoleApplication = uiConsoleApplicationTmp ;
    this.ready = true ;
    return ;
  } else {
    this.ready = false ;
  }
} ;

CommandManager.prototype.initCommandType = function() {
  if(!this.ready) {
    return ;
  }
  var nodeList = this.uiConsoleApplication.getElementsByTagName('DIV') ;
  for(var node in nodeList) {
    if (nodeList[node].className == 'CommandType') {
      this.commandTypeNode = node ;
      this.ready = true ;
      return ;
    }
  }
  this.ready = false ;
} ;

CommandManager.prototype.initScreen = function() {
  var nodeList = this.uiConsoleApplication.getElementsByTagName('DIV') ;
  for(var node in nodeList) {
    if (nodeList[node].className == 'ConsoleQuickHelp') {
      this.screenNode = node ;
      this.ready = true ;
      return true ;
    }
  }
  this.ready = false ;
} ;

CommandManager.prototype.register = function() {
  this.registerJSModule('eXo.application.console.jcr.js.core.Builtin', '') ;
  this.registerJSModule('eXo.application.console.jcr.js.core.ShowNode', '') ;
} ;

/**
 * This method should:
 * 1. parse the command
 * 2. If the first argument is not completed, look up all the possible match commands and show 
 *    them in the help screen
 * 3. If the first argument is completed, look up  the command object that match the first 
 *    first argument and call the method help of that command object. If no comand is found,
 *    show "No command is found"
 * @param {Object} command
 */
CommandManager.prototype.help = function(command) {
  eXo.application.console.UIConsoleApplication.showMaskWorkspace() ;
  if (!this.initScreen()) {
    window.alert('Detect screen node failed...') ;
    window.alert('ScreenNode: ' +  this.screenNode) ;
    return ;
  }
  var commandState = false ;
  var commandLookup = [] ;
  for (var cmd in this.commands) {
    if (command == (cmd + ' ')) {
      commandState = true ;
      break ;
    }
    if(cmd.indexOf(command) != -1) {
      commandLookup[commandLookup.length] = cmd ;
    }
  }

  var helpTxt = 'No command is found' ;
  
  // Command completed
  if (commandState) {
    helpTxt = this.commands[command].help() ;
  } else if(commandLookup.length > 0){
    helpTxt = commandLookup.join(' ') ;
  }
  this.screenNode.innerHTML = helpTxt ;
} ;

CommandManager.prototype.addCommand = function(command) {
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
 * eXo.application.console.Application.addCommand(...) to register a command with the console
 * 
 * @param {Object} jsFile
 * @param {Object} jsLocation
 */
CommandManager.prototype.registerJSModule = function(jsFile, jsLocation) {
  eXo.require(jsFile, '/eXoAppWeb/javascript/') ;
} ;

if(!eXo.application.console) {
  eXo.application.console = {} ;
} 
eXo.application.console.CommandManager = new CommandManager() ;
eXo.application.console.CommandManager.register() ;