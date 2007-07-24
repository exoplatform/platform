/**
 * @author uoc.nb
 * 
 * A Keyboard's listener API. 
 * 
 */
function DefaultKeyboardListener() {
}

DefaultKeyboardListener.prototype = {
  init : function() {}
  ,
  
  onFinish : function() {}
  ,
  
  write : function() {}
  ,
  
  // Printable keys
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onAlphabet : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onDigit : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onPunctuation : function(keynum, keychar) { return true ;}
  ,
  
  // Control keys
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onBackspace : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onDelete : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onEnter : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onTab : function(keynum, keychar) { return true ;}
  ,
  
  // Navigate keys
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onLeftArrow : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onRightArrow : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onUpArrow : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onDownArrow : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onHome : function(keynum, keychar) { return true ;}
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onEnd : function(keynum, keychar) { return true ;}  
} ;


eXo.core.DefaultKeyboardListener = DefaultKeyboardListener ;