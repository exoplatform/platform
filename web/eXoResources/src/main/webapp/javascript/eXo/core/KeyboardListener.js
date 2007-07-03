/**
 * @author uoc.nb
 */

function KeyboardListener() {
  this.onAlphabet          = 0 ;
  this.onDigit             = 1 ;
  this.onPunctuation       = 2 ;
  this.onBackspace         = 3 ;
  this.onDelete            = 4 ;
  this.onEnter             = 5 ;
  this.onDefault           = 6 ;
  this.onNavigate          = 7 ;
  
  this.clearRegisteredHandler() ;
  this.clearDefaultRegisteredHandler() ;
  
  document.onkeyup = function(e) {    
    return eXo.core.KeyboardListener.onKeyPress(e) ;
  } ;
}


KeyboardListener.prototype.register = function(eventCode, handler) {
  this.keyHandler[eventCode] = handler ;
}

KeyboardListener.prototype.clearRegisteredHandler = function() {
  this.keyHandler =  [null, null, null] ;
}

KeyboardListener.prototype.registerDefault = function(eventCode, handler) {
  this.defaultKeyHandler[eventCode] = handler ;
}

KeyboardListener.prototype.clearDefaultRegisteredHandler = function() {
  this.defaultKeyHandler = [null, null, null] ;
}

KeyboardListener.prototype.onKeyPress = function(event) {
  var keynum ;
  if(window.event) { /* IE */
    keynum = window.event.keyCode;    
  } else if(event.which) { /* Netscape/Firefox/Opera */
    keynum = event.which ;
  }
    
  if(!keynum) {
    keynum = event.keyCode ;
  }
  
  var eventCode = this.onDefault ;  
  if(keynum >= 65 && keynum <= 90  || keynum >= 97 && keynum <= 122) {
    eventCode = this.onAlphabet ;
  } else if(keynum >= 48 && keynum <= 57) {
    eventCode = this.onDigit ;
  } else if(keynum == 13) {
    eventCode = this.onEnter ;
  } else if(keynum == 8) {
    eventCode = this.onBackspace ;
  } else if(keynum == 46) {
    eventCode = this.onDelete ;
  } else if(keynum >= 37 && keynum <= 40) {
    eventCode = this.onNavigate ;
  }

  var keychar = String.fromCharCode(keynum) ;

  var handler = this.keyHandler[eventCode] ;
  if(handler != null) return handler(keynum, keychar) ;
  handler = this.defaultKeyHandler[eventCode] ;
  if(handler != null) return handler(keynum, keychar) ;
 

  return false ;
}

eXo.core.KeyboardListener = new KeyboardListener() ;