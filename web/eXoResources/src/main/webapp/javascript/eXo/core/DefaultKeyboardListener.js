/**
 * @author uoc.nb
 * 
 * A Keyboard's listener API. 
 * 
 */
function DefaultKeyboardListener() {
}

DefaultKeyboardListener.prototype.init = function() {}

DefaultKeyboardListener.prototype.onFinish = function() {}

DefaultKeyboardListener.prototype.write = function() {}

// Printable keys
DefaultKeyboardListener.prototype.onAlphabet = function(keynum, keychar) { return true ;}
DefaultKeyboardListener.prototype.onDigit = function(keynum, keychar) { return true ;}

DefaultKeyboardListener.prototype.onPunctuation = function(keynum, keychar) { return true ;}

// Control keys
DefaultKeyboardListener.prototype.onBackspace = function(keynum, keychar) { return true ;}

DefaultKeyboardListener.prototype.onDelete = function(keynum, keychar) { return true ;}

DefaultKeyboardListener.prototype.onEnter = function(keynum, keychar) { return true ;}

// Navigate keys
DefaultKeyboardListener.prototype.onLeftArrow = function(keynum, keychar) { return true ;}

DefaultKeyboardListener.prototype.onRightArrow = function(keynum, keychar) { return true ;}

DefaultKeyboardListener.prototype.onUpArrow = function(keynum, keychar) { return true ;}

DefaultKeyboardListener.prototype.onDownArrow = function(keynum, keychar) { return true ;}

DefaultKeyboardListener.prototype.onHome = function(keynum, keychar) { return true ;}

DefaultKeyboardListener.prototype.onEnd = function(keynum, keychar) { return true ;}

eXo.core.DefaultKeyboardListener = DefaultKeyboardListener.prototype.constructor ;