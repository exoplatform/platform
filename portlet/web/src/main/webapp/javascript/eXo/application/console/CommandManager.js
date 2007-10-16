/**
 * @author Nguyen Ba Uoc
 */

function CommandManager() {
  this.commands = [] ;
  this.commandNode = false ;
  this.commandTypeNode = false ;
  this.commandNameSpaceNode = false
  this.consoleResultNode = false ;
  this.screenNode = false ;
  this.uiMaskWindowConsoleNode = false ;
  this.uiConsoleApplicationNode = false ;
  this.ready = true ;
  this.envManager = eXo.application.console.EnvManager ;
  this.consoleScreen = eXo.application.console.ConsoleScreen ;
  this.cmdHistory = eXo.application.console.CommandHistory ;
} ;

CommandManager.prototype.init = function(node) {
  this.commandNode = node ;
  this.initCommon() ;
  this.envManager.init(this.screenNode) ;
  if (!this.envManager.getVariable('CMD_PREFIX')) {
    this.envManager.setVariable('CMD_PREFIX', 'xhtml') ;
  }
  this.consoleScreen.init(this.consoleResultNode) ;
  this.cmdHistory.init(this.screenNode) ;
} ;

CommandManager.prototype.onFinish = function() {
  this.commandNode = false ;
  this.commandTypeNode = false ;
  this.commandNameSpaceNode = false ;
  this.consoleResultNode = false ;
  this.screenNode = false ;
  this.uiMaskWindowConsoleNode = false ;
  this.uiConsoleApplicationNode = false ;
  this.cmdHistory.finish() ;
} ;

CommandManager.prototype.initCommon = function() {
  this.inituiConsoleApplicationNode() ;
  if(!this.ready) {
    return ;
  }
  var nodeLst = this.uiConsoleApplicationNode.getElementsByTagName('*') ;
  for(var node in nodeLst) {
    if (nodeLst[node].className == 'ConsoleQuickHelp') {
      this.screenNode = nodeLst[node] ;
      continue ;
    }
    if (nodeLst[node].className == 'UIMaskWindowConsole') {
      this.uiMaskWindowConsoleNode = nodeLst[node] ;
      continue ;
    }
    if (nodeLst[node].className == 'CommandType') {
      this.commandTypeNode = nodeLst[node] ;
      continue ;
    }
    if (nodeLst[node].className == 'CommandNameSpace') {
      this.commandNameSpaceNode = nodeLst[node] ;
      continue ;
    }
    if (nodeLst[node].className == 'ConsoleResult') {
      this.consoleResultNode = nodeLst[node] ;
      continue ;
    }
  }
  if (!this.screenNode || !this.commandTypeNode || !this.consoleResultNode) {
    this.ready = false ;
  }
} ;

CommandManager.prototype.inituiConsoleApplicationNode = function() {
  var uiConsoleApplicationNodeTmp = this.commandNode.parentNode ;
  while(uiConsoleApplicationNodeTmp && uiConsoleApplicationNodeTmp.className != 'UIConsoleApplication') {
    if (uiConsoleApplicationNodeTmp.tagName == 'BODY') {
      break ;
    }
    uiConsoleApplicationNodeTmp = uiConsoleApplicationNodeTmp.parentNode ;
  }
  if (uiConsoleApplicationNodeTmp.className == 'UIConsoleApplication') {
    this.uiConsoleApplicationNode = uiConsoleApplicationNodeTmp ;
    this.ready = true ;
    return ;
  } else {
    this.ready = false ;
  }
} ;

CommandManager.prototype.register = function() {
  this.registerJSModule('eXo.application.console.EnvCommand') ;
  this.registerJSModule('eXo.application.console.builtin.History') ;
  this.registerJSModule('eXo.application.console.jcr.HelloJCR') ;
  this.registerJSModule('eXo.application.console.xhtml.ShowNode') ;
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
  var fullInputCommand = this.getFullCmd(command) ;
  for (var cmd in this.commands) {
    if(!this.commands[cmd].commandName) {
      continue ;
    }
    var cmdObj = this.commands[cmd] ;
    var fullCmd = cmdObj.getFullCmd() ;
    if (fullInputCommand == fullCmd || command == fullCmd) {
      return true ;
    }
    if(fullCmd.indexOf(command) == 0 || fullCmd.indexOf(fullInputCommand) == 0) {
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

CommandManager.prototype.hideQuickHelp = function() {
  if (!this.ready) {
    return ;
  }
  this.uiMaskWindowConsoleNode.style.display = 'none' ;
} ;

CommandManager.prototype.showQuickHelp = function(text) {
  if (!this.ready) {
    return ;
  }
  text = this.formatHelp(text) ;
  this.screenNode.innerHTML = '<div>' + text + '</div>' ;
  this.uiMaskWindowConsoleNode.style.display = 'block' ;
} ;

CommandManager.prototype.updateCmdNameSpace = function(nameSpace) {
  if (this.commandNameSpaceNode) {
    this.commandNameSpaceNode.innerHTML = nameSpace + '$' ;
  }
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
  if (command != '') {
    commandState = this.commandMatch(command, commandLookup) ;
  } else {
    for (var cmd in this.commands) {
      if (this.commands[cmd].commandName) {
        commandLookup[commandLookup.length] = this.commands[cmd].getFullCmd() ;
      }
    }
  }

  var helpTxt = 'No command is found' ;
  
  // Command completed
  if (commandState) {
    if (this.commands[command]) {
      helpTxt = this.commands[command].help() ;
    } else {
      command = this.getFullCmd(command) ;
      helpTxt = this.commands[command].help() ;
    }
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
    this.consoleScreen.write('&nbsp;') ;
    return ;
  }
  commandLine = commandLine.trim() ;
  this.cmdHistory.insert(commandLine) ;
  var commandName = false ;
  var fullCommandName = false ;
  var parameters = false ;
  var retCode = 0 ;
  var firstSpacePos = commandLine.indexOf(' ') ;
  if (firstSpacePos == -1) {
    commandName = commandLine ;
    fullCommandName = this.getFullCmd(commandName) ;
  } else {
    commandName = commandLine.substring(0, firstSpacePos).trim() ;
    fullCommandName = this.getFullCmd(commandName) ;
    parameters = commandLine.substring(firstSpacePos, commandLine.length).trim() ;
  }
  if (commandLine == 'clear') {
    this.consoleScreen.clear() ;
    retCode = 0 ;
  } else if(!this.commands[commandName] && !this.commands[fullCommandName]) {
    this.consoleScreen.write(commandLine + ': command not found') ;
    return ;
  } else {
    if (this.commands[commandName]) {
      retCode = this.commands[commandName].execute(parameters, this.consoleScreen) ;
    } else if (this.commands[fullCommandName]) {
      retCode = this.commands[fullCommandName].execute(parameters, this.consoleScreen) ;
    }
  }
  this.envManager.setVariable('RET_CODE', retCode) ;
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
  this.commands[command.getFullCmd()] = command ;
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
  eXo.require(jsFile, '/web/javascript/') ;
} ;

if(!eXo.application.console) {
  eXo.application.console = {} ;
} 
eXo.application.console.CommandManager = new CommandManager() ;
eXo.application.console.CommandManager.register() ;
