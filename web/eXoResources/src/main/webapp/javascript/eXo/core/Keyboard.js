/**
 * @author uoc.nb
 */
function Keyboard() {
  this.listeners = [] ;
  document.onkeydown = function(e) {
    return eXo.core.Keyboard.onKeyDown(e) ;
  }
  document.onkeypress = function(e) {    
    return eXo.core.Keyboard.onKeyPress(e) ;
  } ;
}

Keyboard.prototype.register = function(listener) {
  this.listeners[this.listeners.length] = listener ;
}

Keyboard.prototype.clearListeners = function() {
  this.listeners = [] ;
}

Keyboard.prototype.getKeynum = function(event) {
  var keynum = false ;
  if(window.event) { /* IE */
    keynum = window.event.keyCode;
    event = window.event ;
  } else if(event.which) { /* Netscape/Firefox/Opera */
    keynum = event.which ;
  }
  if(keynum == 0) {
    keynum = event.keyCode ;
  }
  return keynum ;
}

Keyboard.prototype.onKeyDown = function(event) {
  var keynum = this.getKeynum(event) ; 
  var keychar = String.fromCharCode(keynum) ;
  
  if(keynum == 13) {
    keychar = '' ;
  }
  var eventHandler = false ;
 
  if(keynum == 13) {
    eventHandler = 'onEnter' ;
  } else if(keynum == 9) {
    eventHandler = 'onTab' ;
  } else if(keynum == 8) {
    eventHandler = 'onBackspace' ;
  } else if(keynum == 46) {
    eventHandler = 'onDelete' ;
  } else if(keynum == 37){
    eventHandler = 'onLeftArrow' ;
  } else if(keynum == 39){
    eventHandler = 'onRightArrow' ;
  } else if(keynum == 38){
    eventHandler = 'onUpArrow' ;
  } else if(keynum == 40){
    eventHandler = 'onDownArrow' ;
  } else if(keynum == 36){
    eventHandler = 'onHome' ;
  } else if(keynum == 35){
    eventHandler = 'onEnd' ;
  }
  
  return this.listenerCallback(eventHandler, event, keynum, keychar) ;
}

Keyboard.prototype.onKeyPress = function(event) {
  var keynum = this.getKeynum(event) ; 
  var keychar = String.fromCharCode(keynum) ;
  var eventHandler = false ;
 
  if((keynum >= 65 && keynum <= 90) || (keynum >= 97 && keynum <= 122)) {
    eventHandler = 'onAlphabet' ;
  } else if(keynum >= 48 && keynum <= 57) {
    eventHandler = 'onDigit' ;
  } else if((keynum >= 32 && keynum <= 47) || (keynum >= 58 && keynum <= 64) || 
            (keynum >= 91 && keynum <= 96) || (keynum >= 123 && keynum <= 65532)) {
    eventHandler = 'onPunctuation' ;
  }
  
  return this.listenerCallback(eventHandler, event, keynum, keychar) ;
}

Keyboard.prototype.listenerCallback = function(eventHandler, event, keynum, keychar) {
  var retVal = true ; 
  if(!eventHandler || eventHandler == '') {
    return retVal ;
  }
  for(var i=0; i<this.listeners.length; i++) {
    retVal &= eval('this.listeners[' + i + '].' + eventHandler + '(' + keynum + ', "' + keychar + '")') ;
  } 
  
  if(!retVal) {
    eXo.core.Editor.cancelEvent(event) ;
    return false ;
  }
  
  // Release event if nobody want to capture
  return true ;
}

eXo.core.Keyboard = new Keyboard() ;