/**
 * @author uoc.nb
 * 
 * A Keyboard's listener API. 
 * 
 */
function KeyboardListenerAPI() {
}

KeyboardListenerAPI.prototype = {
  init : function(node, beforeCursor, afterCursor) {}
  ,
  
  onFinish : function() {}
  ,
  
  removeCursor : function() {}
  ,
  
  defaultWrite : function() {}
  ,
  
  /**
   * 
   * @param {String} beforeCursor
   * @param {String} cursor
   * @param {String} afterCursor
   */
  write: function(beforeCursor, cursor, afterCursor){}
  ,
  
  isSameNode : function(node) {}
  ,
  
  /**
   * @return {String}
   */
  getEditContent : function() {}
  ,
  
  preKeyProcess : function() {}
  ,
  
  // Printable keys
  onDefault : function(keynum, keychar) { return true ;}
  ,
  
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
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onEscapse : function(keynum, keychar) { return true ;}
  ,
  
  // Navigate keys
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onLeftArrow : function(keynum, keychar) { return true ; }
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onRightArrow : function(keynum, keychar) { return true ; }
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
  onHome : function(keynum, keychar) { return true ; }
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onEnd : function(keynum, keychar) { return true ; }  
} ;

eXo.core.KeyboardListenerAPI = KeyboardListenerAPI ;