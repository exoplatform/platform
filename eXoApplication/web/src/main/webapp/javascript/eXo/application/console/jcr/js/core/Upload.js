/**
 * @author Nguyen Ba Uoc
 */
function Upload() {
  this.commandName = 'upload' ;
} ;

Upload.prototype = new eXo.application.console.Command() ;

Upload.prototype.help = function() {
  return 'Upload some data to server' ;
} ;

Upload.prototype.execute = function(args, screen) {
  var uploadData = '' ;
  var dataNode = false ;
  
  dataNode = eXo.application.console.CommandManager.uiConsoleApplication ;
  
  uploadData = dataNode.innerHTML ;
  
  var httpReq = eXo.core.Browser.create ;
  
  return {retCode: 0, resultContent: 'Implementing... back soon!'} ;
} ;

eXo.application.console.Upload = new Upload() ;
eXo.application.console.CommandManager.addCommand(eXo.application.console.Upload) ;