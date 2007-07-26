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
    return (this.prefix + ':' + this.commandName) ;
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
   * @return {Object} return an object with retCode, msg or resultContent properties
   *   depend state of execute command process.
   */
  execute : function(args, screen) {
    return {retCode:-1, msg: 'Not implement'} ;
  },

  callServer(actionHandler, params) {
    var url = eXo.env.server.context + "/command?" ;
    url += 'type=org.exoplatform.web.command.handler.ConsoleUploadHandler&action=upload&uploadId=' + nodeId ;
    return ajaxAsyncGetRequest(url, false) ;
  }
} ;

if (!eXo.application.console)  eXo.application.console = {} ;
eXo.application.console.Command =  Command ;
