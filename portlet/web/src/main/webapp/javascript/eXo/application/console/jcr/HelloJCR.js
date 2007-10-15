/**
 * @author Nguyen Ba Uoc
 */
function HelloJCR() {
  this.commandName = 'HelloJCR' ;
  this.prefix = 'jcr' ;
} ;

HelloJCR.prototype = eXo.application.console.Command.createInstance() ;

HelloJCR.prototype.help = function() {
  return 'Usage: HelloJCR nodeid\
     <br/>  HelloJCR data in nodeid to server' ;
} ;

HelloJCR.prototype.execute = function(args, consoleScreen) {
  var handler = 'org.exoplatform.web.command.handler.HelloJCRHandler' ;
  var params = [] ;
  params['action'] = 'testonly' ;
  params['data'] = 'this is some data will send to server' ;
  var result = this.callServer(handler, params) ;
  consoleScreen.write(result) ;
  return 0 ;
} ;

eXo.application.console.HelloJCR = new HelloJCR() ;
eXo.application.console.CommandManager.addCommand(eXo.application.console.HelloJCR) ;