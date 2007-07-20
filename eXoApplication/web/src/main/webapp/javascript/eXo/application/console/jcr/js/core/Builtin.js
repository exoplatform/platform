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

function Env() {
  this.commandName = 'env' ;
} ;

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
  if (!args || args == '') {
    return {retCode: -1, msg: 'Missing sub command'} ;
  }
  var envVars = this.getEnvVariables(screen) ;
  // Detect sub command
  var firstSpacePos = args.indexOf(' ') ;
  if (firstSpacePos == -1) {
    if (args != 'display') {
      return {retCode: -1, msg: 'Missing sub command arguments'} ;
    } else {
      // Display all posible environment variables
      if (envVars.length > 0) {
        var envStr = this.formatEnvList(envVars) ;
        return {retCode: 0, resultContent: envStr} ;
      } else {
        return {retCode: 0, resultContent: 'No environment variables at this time.'} ;
      }
    }
  }
//  return {retCode: 0, resultContent: args} ;
  var subCmd = args.substring(0, firstSpacePos) ;
  var paramStr = args.substring(firstSpacePos, args.length).trim() ;
  
  if (subCmd == 'set') {
    var equalSignPos = paramStr.indexOf('=') ;
    var name = paramStr.substring(0, equalSignPos) ;
    var value = paramStr.substring(equalSignPos + 1, paramStr.length) ;
    if (value) {
      value = value.trim() ;
    }
    var found = false ;
    for (var i=0; i<envVars.length; i++) {
      var v = envVars[i] ;
      if (v[0] == name) {
        if (!value || value == '') {
          envVars.remove(v) ;
        } else {
          v[1] = value ;
        }
        found = true ;
        break ;
      }
    }
    if (!found) {
      envVars[envVars.length] = [name, value] ;      
    }
    this.updateEnvVariables(envVars, screen) ;
    return {retCode:0, resultContent: (name + '=' + value)} ;
  } else if (subCmd == 'remove') {
    var value = false ;
    for (var i=0; i<envVars.length; i++) {
      var v = envVars[i] ;
      if (v[0] == paramStr) {
        value = v[1] ;
        envVars.remove(v) ;
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
    return {retCode:0, resultContent: ('Not implement')} ;
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
  return envStr
} ;

/**
 * 
 * @param {Element} node
 * 
 * @return {Array}
 */
Env.prototype.getEnvVariables = function(node) {
  var envStr = node.getAttribute('env') + '' ;
  var envVars = [] ;
  if (envStr.trim() != '') {
    var envLst = envStr.split(':') ;
    for (var i=0; i<envLst.length; i++) {
      var tmpArr = envLst[i].split('=') ;
      if (tmpArr.length < 2) {
        continue ;
      }
      envVars[envVars.length] = tmpArr ;
    }
  }
  return envVars ;
} ;

/**
 * 
 * @param {Array} envVars
 * @param {Element} node
 */
Env.prototype.updateEnvVariables = function(envVars, node) {
  var envStr = '' ;
  for (var i=0; i<envVars.length; i++) {
    var v = envVars[i] ;
    if (v) {
      envStr += v[0] + '=' + v[1] + ':' ;
    }
  }
  node.setAttribute('env', envStr) ;
} ;

eXo.application.console.Env = new Env() ;
eXo.application.console.CommandManager.addCommand(eXo.application.console.Env) ;