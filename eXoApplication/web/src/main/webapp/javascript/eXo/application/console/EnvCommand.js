/**
 * @author Nguyen Ba Uoc
 * 
 */
function Env() {
  this.commandName = 'env' ;
  this.prefix = '' ;
} ;

Env.prototype = eXo.application.console.Command.createInstance() ;

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
  this.parametersParser(args) ;
  var envManager = eXo.application.console.EnvManager ;
  var envVars = envManager.getEnvVariables() ;
  if (!args || args == '') {
    if (envVars.length > 0) {
      var envStr = this.formatEnvList(envVars) ;
      consoleScreen.write(envStr) ;
      return 0 ;
    } else {
      consoleScreen.write('No environment variables at this time.') ;
      return 0 ;
    }
  }
  
  var name = false ;
  var value = false ;
  
  if (this.params) {
    for (var param in this.params) {
      if (typeof this.params[param] == 'function') {
        continue ;
      }
      name = param ;
      value = this.params[param] ;
      break ;
    }
  }
  
  if (!name && this.params) {
    name = this.params[this.params.length - 1] ;
  }
  
  if (this.subCmds[0] == 'set') {
    if (name) {
      envManager.setVariable(name, value) ;
      if (value) {
        consoleScreen.write(name + '=' + value) ;
      } else {
        consoleScreen.write('set ' + name + ' to null') ;
      }
      return 0 ;
    } else {
      consoleScreen.write('missing name of variable.') ;
      return -1 ;
    }
  } else if (this.subCmds[0] == 'remove') {
    if (!name) {
      consoleScreen.write('Missing name of variable.') ;
      return -1 ;
    }
    if (value = envManager.getVariable(name)) {
      consoleScreen.write('remove ' + name + '=' + value) ;
      envManager.setVariable(name, false) ;
      return 0 ;
    } else {
      consoleScreen.write('Environment variable ' + name + ' not defined') ;
      return -1 ;
    }
  } else if (this.subCmds[0] == 'display') {
    var envLstTmp = [] ;
    for (var i=0; i<envVars.length; i++) {
      var v = envVars[i] ;
      if (v[0].indexOf(name) == 0) {
        envLstTmp[envLstTmp.length] = v ;
      }
    }
    if (envLstTmp.length > 0) {
      consoleScreen.write(this.formatEnvList(envLstTmp)) ;
      return 0 ;
    } else {
      consoleScreen.write(name + ' not defined') ;
      return -1 ;
    }
  }
  consoleScreen.write('Sub command ' + this.subCmd + ' is not implement') ;
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