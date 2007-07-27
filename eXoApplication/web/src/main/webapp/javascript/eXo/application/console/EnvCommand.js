/**
 * @author Nguyen Ba Uoc
 * 
 */
function Env() {
  this.commandName = 'env' ;
  this.prefix = '' ;
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
Env.prototype.execute = function(args, consoleScreen) {
  var envManager = eXo.application.console.EnvManager ;
  var envVars = envManager.getEnvVariables() ;
  // Detect sub command
  if (!args || args.trim() == 'display' || args.trim() == '') {
    // Display all posible environment variables
    if (envVars.length > 0) {
      var envStr = this.formatEnvList(envVars) ;
      consoleScreen.write(envStr) ;
      return 0 ;
    } else {
      consoleScreen.write('No environment variables at this time.') ;
      return 0 ;
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
      consoleScreen.write(name + '=' + value) ;
      return 0 ;
    } else {
      consoleScreen.write(paramStr + ' missing =') ;
      return -1 ;
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
      consoleScreen.write('remove ' + paramStr + '=' + value) ;
      return 0 ;
    } else {
      consoleScreen.write('Environment variable ' + paramStr + ' not defined') ;
      return -1 ;
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
      consoleScreen.write(this.formatEnvList(envLstTmp)) ;
      return 0 ;
    } else {
      consoleScreen.write(paramStr + ' not defined') ;
      return -1 ;
    }
  }
  consoleScreen.write('Sub command ' + subCmd + ' is not implement') ;
  return -1 ;  
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