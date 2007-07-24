/**
 * @author Nguyen Ba Uoc
 */
function Upload() {
  this.commandName = 'upload' ;
} ;

Upload.prototype = new eXo.application.console.Command() ;

Upload.prototype.help = function() {
  return 'Usage: upload nodeid\
     <br/>  Upload data in nodeid to server' ;
} ;

Upload.prototype.execute = function(args, screen) {
  if (!args  || args.trim() == '') {
    return {retCode: -1, msg: 'Missing node id'} ;
  }
  var nodeId = args.trim() ;
  var url = eXo.env.server.context + "/command?" ;
  url += 'type=org.exoplatform.web.command.handler.ConsoleUploadHandler&action=upload&uploadId=' + nodeId ;
  var result = ajaxAsyncGetRequest(url, false) ;
  return {retCode: 0, resultContent: 'upload node with id=' + nodeId + '<br>' + result} ;
} ;

eXo.application.console.Upload = new Upload() ;
eXo.application.console.CommandManager.addCommand(eXo.application.console.Upload) ;