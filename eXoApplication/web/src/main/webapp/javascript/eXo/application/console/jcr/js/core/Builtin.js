/**
 * @author Nguyen Ba Uoc
 * 
 * This file contain all builtin commands.
 */

// Clear command
function Clear() {
  this.commandName = 'clear' ;  
} ;

Clear.prototype = new eXo.application.console.Command() ;

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

function Env() {
  this.commandName = 'env' ;
} ;

Env.prototype = new eXo.application.console.Command() ;

Env.prototype.help = function() {
  return 'The syntax of this command is:\
          <br> env [ set | remove | display ]\
          <br>\
          <br>Sub command description:\
          <br>\
          <br> set: setting value for variable or create a variable if it is not exists.\
          <br>     set name=value\
          <br>\
          <br> remove: remove a variable.\
          <br>     remove name\
          <br>\
          <br> display: display value of a variable.\
          <br>     display [name]\
          <br> if name is not provided, display all posible varibles.' ;
} ;

/**
 * 
 * @param {String} args
 * @param {Element} screen
 */
Env.prototype.execute = function(args, screen) {
  var envManager = eXo.application.console.EnvManager ;
  var envVars = envManager.getEnvVariables() ;
  // Detect sub command
  if (!args || args.trim() == 'display' || args.trim() == '') {
    // Display all posible environment variables
    if (envVars.length > 0) {
      var envStr = this.formatEnvList(envVars) ;
      return {retCode: 0, resultContent: envStr} ;
    } else {
      return {retCode: 0, resultContent: 'No environment variables at this time.'} ;
    }
  }
  var firstSpacePos = args.indexOf(' ') ;
  var subCmd = args.substring(0, firstSpacePos) ;
  var paramStr = args.substring(firstSpacePos, args.length).trim() ;
  
  if (subCmd == 'set') {
    var equalSignPos = paramStr.indexOf('=') ;
    if (equalSignPos != -1) {
      var name = paramStr.substring(0, equalSignPos) ;
      var value = paramStr.substring(equalSignPos + 1, paramStr.length) ;
      if (value) {
        value = value.trim() ;
      }
      envManager.setVariable(name, value) ;
      return {retCode:0, resultContent: (name + '=' + value)} ;
    } else {
      return {retCode:-1, msg: (paramStr + ' missing =')} ;
    }
  } else if (subCmd == 'remove') {
    var value = false ;
    for (var i=0; i<envVars.length; i++) {
      var v = envVars[i] ;
      if (v[0] == paramStr) {
        value = v[1] ;
        v[1] = false ;
        envManager.updateEnvVariables(envVars, screen) ;
        break ;
      }
    }
    if (value) {
      return {retCode:0, resultContent: ('remove ' + paramStr + '=' + value)} ;
    } else {
      return {retCode:-1, msg: ('Environment variable ' + paramStr + ' not defined')} ;
    }
  } else if (subCmd == 'display') {
    var envLstTmp = [] ;
    for (var i=0; i<envVars.length; i++) {
      var v = envVars[i] ;
      if (v[0].indexOf(paramStr) == 0) {
        envLstTmp[envLstTmp.length] = v ;
      }
    }
    if (envLstTmp.length > 0) {
      return {retCode:0, resultContent: (this.formatEnvList(envLstTmp))} ;
    } else {
      return {retCode:-1, msg: (paramStr + ' not defined')} ;
    }
  }
  return {retCode:-1, msg: 'Sub command ' + subCmd + ' is not implement'} ;  
} ;

/**
 * 
 * @param {Array} envLst
 * 
 * @return {String}
 */
Env.prototype.formatEnvList = function(envLst) {
  var envStr = '' ;
  for (var i=0; i<envLst.length; i++) {
    var v = envLst[i] ;
    envStr += '<br>' + v[0] + '=' + v[1] ;
  }
  return envStr ;
} ;

eXo.application.console.Env = new Env() ;
eXo.application.console.CommandManager.addCommand(eXo.application.console.Env) ;