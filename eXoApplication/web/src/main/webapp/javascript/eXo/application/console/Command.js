/**
 * @author Nguyen Ba Uoc
 */

function Command() {
  this.commandName = 'noname' ;
  this.prefix      = 'xhtml' ;
} ;

Command.prototype = {
  /**
   * @return {String} the full command with prefix
   */
  getFullCmd : function() {
    return ((this.prefix != '' ? this.prefix + ':' : '') + this.commandName) ;
  } 
  ,
  
  /**
   * 
   * @param {String} cmdQuery
   * 
   * @return {Array}
   */
  parametersParser : function(cmdQuery) {
    if (!cmdQuery || cmdQuery.trim() == '') {
      return ;
    }
    cmdQuery = cmdQuery.trim() ;
    var subCmdPattern = /^[^-\s]*/ ;
    if (subCmdPattern.test(cmdQuery)) {
      this.subCmd = subCmdPattern.exec(cmdQuery)[0].trim() ;
      cmdQuery = cmdQuery.replace(this.subCmd, '').trim() ;
    }
    var spliter = /(^-{1,2}[^-]*)|(^.*=.*\s?)/ ;
    var params = [] ;
    var param = false ;
    while((param = spliter.exec(cmdQuery))) {
      var tmp = param[0].trim().split('=') ;
      tmp[1] = tmp[1] ? tmp[1] : false ;
      params[tmp[0]] = tmp[1] ;
      cmdQuery = cmdQuery.replace(param[0], '').trim() ;
    }
    this.params = params ;
  }
  ,
  
  /**
   * @return {String} command help for tab complete
   */
  help : function() {
    return 'Not implement' ;
  }
  ,
  
  /**
   * 
   * @param {String} args
   * @param {ConsoleScreen} consoleScreenObj
   * 
   * @return {Number} return the return code. 
   * This value will be automatic set to RET_CODE environment variable by CommandManager object.
   */
  execute : function(args, consoleScreenObj) {
    return 0 ;
  },

  /**
   * 
   * @param {String} actionHandler full path of java service to call(full path include package must be used).
   * @param {Array} params Array include all parameters pass throught to Java  Object
   * 
   * @return {String} response text return from server
   */
  callServer : function(actionHandler, params) {
    var queryStr = '' ;
    for(var param in params) {
      if (typeof params[param] == 'function') {
        continue ;
      }
      queryStr += '&' + param + '=' + params[param] ;
    }
    var url = eXo.env.server.context + "/command?" ;
    url += 'type=' + actionHandler + queryStr ;
    return ajaxAsyncGetRequest(url, false) ;
  }
} ;

Command.prototype.createInstance = function() {
  return new Command() ;
} ;

if (!eXo.application.console)  eXo.application.console = {} ;
eXo.application.console.Command =  new Command() ;
