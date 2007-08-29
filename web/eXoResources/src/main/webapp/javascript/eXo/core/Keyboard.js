/**
 * @author uoc.nb
 */
function Keyboard() {
  this.listeners = [] ;
  this.controlKeyCodes = [8, 9, 13, 27, 35, 36, 37, 38, 39, 40, 46] ;
}

Keyboard.prototype.init = function() {
  if (eXo.core.Browser.browserType == 'ie') {
    document.onkeydown = function(e) {
      return eXo.core.Keyboard.onKeyDown(e) ;
    }
  }
  document.onkeypress = function(e) {
    return eXo.core.Keyboard.onKeyPress(e) ;
  } ;
} ;

Keyboard.prototype.finish = function() {
  if (eXo.core.Browser.browserType == 'ie') {
    document.onkeydown = null ;
  }
  document.onkeypress = null ;
  this.listeners = [] ;
} ;

Keyboard.prototype.register = function(listener) {
  this.listeners[this.listeners.length] = listener ;
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
  var keychar = '' ;
  
  var eventHandler = false ;
 
  if(keynum == 13) {
    eventHandler = 'onEnter' ;
  } else if(keynum == 9) {
    eventHandler = 'onTab' ;
  } else if(keynum == 8) {
    eventHandler = 'onBackspace' ;
  } else if(keynum == 27) {
    eventHandler = 'onEscapse' ;
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
  var keychar = '' ;
  var eventHandler = false ;

  if (eXo.core.Browser.browserType == 'ie' && this.controlKeyCodes.contains(keynum)) {
    return false ;
  }
  
  if(keynum == 13) {
    eventHandler = 'onEnter' ;
  } else if(keynum == 9) {
    eventHandler = 'onTab' ;
  } else if(keynum == 8) {
    eventHandler = 'onBackspace' ;
  } else if(keynum == 27) {
    eventHandler = 'onEscapse' ;
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
  
  if (!eventHandler) keychar = String.fromCharCode(keynum) ;
  if((keynum >= 65 && keynum <= 90) || (keynum >= 97 && keynum <= 122)) {
    eventHandler = 'onAlphabet' ;
  } else if(keynum >= 48 && keynum <= 57) {
    eventHandler = 'onDigit' ;
  } else if(((keynum >= 32 && keynum <= 34) || (keynum >= 41 && keynum <= 47) || 
            (keynum >= 58 && keynum <= 64) || (keynum >= 91 && keynum <= 96) || 
            (keynum >= 123 && keynum <= 65532)) && !this.controlKeyCodes.contains(keynum)) {
    eventHandler = 'onPunctuation' ;
  }
  
  return this.listenerCallback(eventHandler, event, keynum, keychar) ;
}

/**
 * 
 * @param {String} eventHandler
 * @param {Object} event
 * @param {Number} keynum
 * @param {Char} keychar
 */
Keyboard.prototype.listenerCallback = function(eventHandler, event, keynum, keychar) {
  var retVal = true ; 
  if(!eventHandler || eventHandler == '') {
    return retVal ;
  }
  
  // Fix special character
  if (keychar == '"') {
    keychar = '\\"' ;
  } else if (keychar == '\\') {
    keychar = '\\\\' ;
  }
  for(var i=0; i<this.listeners.length; i++) {
    retVal &= eval('this.listeners[' + i + '].' + eventHandler + '(' + keynum + ', "' + keychar + '") ;') ;
  }
  
  if(!retVal) {
    eXo.core.Keyboard.cancelEvent(event) ;
    return false ;
  }
  
  // Release event if nobody want to capture
  return true ;
} ;

/**
 * 
 * @param {Event} event
 */
Keyboard.prototype.cancelEvent = function(event) {
  if(eXo.core.Browser.browserType == 'ie') { // Cancel bubble for ie
    window.event.cancelBubble = true ;
    window.event.returnValue = true ;
    return ;
  } else { // Cancel event for Firefox, Opera, Safari
    event.stopPropagation() ;
    event.preventDefault() ;
  }
} ;

eXo.core.Keyboard = new Keyboard() ;
