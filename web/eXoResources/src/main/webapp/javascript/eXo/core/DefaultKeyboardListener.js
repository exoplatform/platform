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
/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onAlphabet = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onDigit = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onPunctuation = function(keynum, keychar) { return true ;}

// Control keys
/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onBackspace = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onDelete = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onEnter = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onTab = function(keynum, keychar) { return true ;}

// Navigate keys
/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onLeftArrow = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onRightArrow = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onUpArrow = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onDownArrow = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onHome = function(keynum, keychar) { return true ;}

/**
 * 
 * @param {Number} keynum
 * @param {Char} keychar
 */
DefaultKeyboardListener.prototype.onEnd = function(keynum, keychar) { return true ;}

eXo.core.DefaultKeyboardListener = DefaultKeyboardListener.prototype.constructor ;