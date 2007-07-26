/**
 * @author Nguyen Ba Uoc
 */

function CommandManager() {
  this.commands = [] ;
  this.commandNode = false ;
  this.commandTypeNode = false ;
  this.eXoConsoleResult = false ;
  this.screenNode = false ;
  this.uiConsoleApplication = false ;
  this.ready = true ;
  this.envManager = eXo.application.console.EnvManager ;
} ;

CommandManager.prototype.init = function(node) {
  this.commandNode = node ;
  this.initCommon() ;
  this.envManager.init(this.screenNode) ;
  if (!this.envManager.getVariable('CMD_PREFIX')) {
    this.envManager.setVariable('CMD_PREFIX', 'xhtml') ;
  }
} ;

CommandManager.prototype.onFinish = function() {
  this.commandNode = false ;
  this.commandTypeNode = false ;
  this.eXoConsoleResult = false ;
  this.screenNode = false ;
  this.uiConsoleApplication = false ;
} ;

CommandManager.prototype.initCommon = function() {
  this.initUIConsoleApplication() ;
  if(!this.ready) {
    return ;
  }
  var nodeLst = this.uiConsoleApplication.getElementsByTagName('DIV') ;
  for(var node in nodeLst) {
    if (nodeLst[node].className == 'ConsoleQuickHelp') {
      this.screenNode = nodeLst[node] ;
      continue ;
    }
    if (nodeLst[node].className == 'CommandType') {
      this.commandTypeNode = nodeLst[node] ;
      continue ;
    }
    if (nodeLst[node].className == 'eXoConsoleResult') {
      this.eXoConsoleResult = nodeLst[node] ;
      continue ;
    }
  }
  if (!this.screenNode || !this.commandTypeNode || !this.eXoConsoleResult) {
    this.ready = false ;
  }
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

CommandManager.prototype.register = function() {
  this.registerJSModule('eXo.application.console.jcr.js.core.Builtin') ;
  this.registerJSModule('eXo.application.console.jcr.js.core.ShowNode') ;
  this.registerJSModule('eXo.application.console.jcr.js.core.Upload') ;
} ;

/**
 * 
 * @param {String} command
 * 
 * @return {String}
 */
CommandManager.prototype.getFullCmd = function(command) {
  var envCmdPrefix = this.envManager.getVariable('CMD_PREFIX') ;
  if (command.indexOf(':') != -1) {
    return command ;
  } else {
    return envCmdPrefix + ':' + command ;
  }
} ;

/**
 * 
 * 
 * @param {String} command
 * @param {Array} commandList
 */
CommandManager.prototype.commandMatch = function(command, commandList) {
  var command = this.getFullCmd(command) ;
  for (var cmd in this.commands) {
    if(!this.commands[cmd].commandName) {
      continue ;
    }
    var cmdObj = this.commands[cmd] ;
    var fullCmd = cmdObj.getFullCmd() ;
    if (command == fullCmd) {
      return true ;
    }
    if(fullCmd.indexOf(command) == 0) {
//      commandList[commandList.length] = cmdObj.commandName ;
      commandList[commandList.length] = fullCmd ;
    }
  }
  return false ;
}

/**
 * 
 * @param {String} str
 */
CommandManager.prototype.formatHelp = function(str) {
  while(str.indexOf(' ') != -1) {
    str = str.replace(' ', '&nbsp;') ;
  }
  return str ;
}

CommandManager.prototype.showQuickHelp = function(text) {
  eXo.application.console.UIConsoleApplication.showMaskWorkspace() ;
  if (!this.ready) {
    return ;
  }
  text = this.formatHelp(text) ;
  this.screenNode.innerHTML = '<div>' + text + '</div>' ;
} ;

CommandManager.prototype.consoleWrite = function(txt) {
  var node = document.createElement('DIV') ;
  node.innerHTML = txt ;
  this.eXoConsoleResult.appendChild(node) ;
  node.scrollIntoView(true) ;
} ;

/**
 * This method should:
 * 1. parse the command
 * 2. If the first argument is not completed, look up all the possible match commands and show 
 *    them in the help screen
 * 3. If the first argument is completed, look up  the command object that match the first 
 *    first argument and call the method help of that command object. If no comand is found,
 *    show "No command is found"
 * @param {String} command
 */
CommandManager.prototype.help = function(command) {
  command = command.trim() ;
  var commandState = false ;
  var commandLookup = [] ;
  commandState = this.commandMatch(command, commandLookup) ;

  var helpTxt = 'No command is found' ;
  
  // Command completed
  if (commandState) {
    command = command.replace(' ', '') ;
    helpTxt = this.commands[command].help() ;
  } else if(commandLookup.length > 0){
    helpTxt = '<strong>' + commandLookup.join(' ') + '</strong>' ;
  }
  this.showQuickHelp(helpTxt) ;
} ;

/**
 * 
 * @param {String} commandLine
 */
CommandManager.prototype.execute = function(commandLine) {
  if (commandLine == '') {
    this.consoleWrite('&nbsp;') ;
    return ;
  }
  commandLine = commandLine.trim() ;
  var commandName = false ;
  var parameters = false ;
  // 1. Get command name
  var firstSpacePos = commandLine.indexOf(' ') ;
  if (firstSpacePos == -1) {
    commandName = commandLine ;
  } else {
    commandName = commandLine.substring(0, firstSpacePos).trim() ;
    parameters = commandLine.substring(firstSpacePos, commandLine.length).trim() ;
  }
  
  // 2. Check command exist
  if(!this.commands[commandName]) {
    this.consoleWrite(commandLine + ': command not found') ;
    return ;
  }
  var result = this.commands[commandName].execute(parameters, this.eXoConsoleResult) ;
  if (result.retCode != 0) {
    this.consoleWrite('Error: ' + result.msg) ;
    this.consoleWrite('Exit code ' + result.retCode) ;
    return ;
  } else if(result.resultContent && result.resultContent != '') {
    this.consoleWrite(result.resultContent) ;
    return ;
  }
} ;

/**
 * 
 * @param {Command} command
 */
CommandManager.prototype.addCommand = function(command) {
  if(this.commands[command.commandName] && this.commands[command.commandName].help) {
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
 * eXo.application.console.CommandManager.addCommand(...) to register a command with the console
 * 
 * @param {String} jsFile
 * @param {String} jsLocation
 */
CommandManager.prototype.registerJSModule = function(jsFile) {
  eXo.require(jsFile, '/eXoAppWeb/javascript/') ;
} ;

if(!eXo.application.console) {
  eXo.application.console = {} ;
} 
eXo.application.console.CommandManager = new CommandManager() ;
eXo.application.console.CommandManager.register() ;