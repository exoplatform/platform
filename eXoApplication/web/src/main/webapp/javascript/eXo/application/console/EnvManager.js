/**
 * @author Nguyen Ba Uoc
 */

function EnvManager() {
  this.rootNode = false ;
} ;

/**
 * 
 * @param {Element} node
 */
EnvManager.prototype.init = function(node) {
  this.rootNode = node ;
} ;

/**
 * 
 * @param {String} key
 * @param {String} value
 */
EnvManager.prototype.setVariable = function(key, value) {
  // Convert to String type
  key += '' ;
  if (value !== false) {
    value += '' ;
  }
  var envVars = this.getEnvVariables() ;
  var found = false ;
  for (var i=0; i<envVars.length; i++) {
    var v = envVars[i] ;
    if (v[0] == key) {
      found = true ;
      v[1] = value ;
      break ;
    }
  }
  if (!found) {
    var v = [key, value] ;
    envVars[envVars.length] = v ;
  }
  this.updateEnvVariables(envVars) ;
  return found ;
} ;

/**
 * 
 * @param {String} key
 * 
 * @return {String}
 */
EnvManager.prototype.getVariable = function(key) {
  var envVars = this.getEnvVariables() ;
  var value = false ;
  for (var i=0; i<envVars.length; i++) {
    var v = envVars[i] ;
    if (v[0] == key) {
      value = v[1] ;
      break ;
    }
  }
  return value ;
} ;

/**
 * 
 * @return {Array}
 */
EnvManager.prototype.getEnvVariables = function() {
  if (!this.rootNode) {
    return false ;
  }
  var envStr = this.rootNode.getAttribute('env') + '' ;
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
 */
EnvManager.prototype.updateEnvVariables = function(envVars) {
  if (!this.rootNode) {
    return false ;
  }
  var envStr = '' ;
  for (var i=0; i<envVars.length; i++) {
    var v = envVars[i] ;
    if (v && v[1] && v[1] != '') {
      if (v[0] == 'CMD_PREFIX') {
        eXo.application.console.CommandManager.updateCmdNameSpace(v[1]) ;
      }
      envStr += v[0] + '=' + v[1] + ':' ;
    }
  }
  this.rootNode.setAttribute('env', envStr) ;
} ;

if (!eXo.application.console) {
  eXo.application.console = {} ;
}
eXo.application.console.EnvManager = new EnvManager() ;
