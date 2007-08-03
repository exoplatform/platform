/**
 * @author uoc.nb
 * 
 * A Keyboard's listener API. 
 * 
 */
function DefaultKeyboardListener() {
  this.cursor = '<span class="ConsoleCursor" cursor="1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>' ;
  this.htmlUtil = eXo.core.HTMLUtil ;
}

DefaultKeyboardListener.prototype = {
  init : function(node, beforeCursor, afterCursor) {
    if(!this.isSameNode(node)) {
      this.onFinish() ;
    }
    this.currentNode = node ;
    this.beforeCursor = beforeCursor ;
    this.afterCursor = afterCursor ;
  }
  ,
  
  onFinish : function() {
    this.removeCursor() ;
    this.currentNode = null ;
    this.beforeCursor = null ;
    this.afterCursor = null ;
  }
  ,
  
  removeCursor : function() {
    this.write(this.beforeCursor, '', this.afterCursor) ;
  }
  ,
  
  defaultWrite : function() {
    this.beforeCursor = this.htmlUtil.entitiesEncode(this.beforeCursor) ;
    this.afterCursor = this.htmlUtil.entitiesEncode(this.afterCursor) ;
    this.write(this.beforeCursor, this.cursor, this.afterCursor) ;
  }
  ,
  
  /**
   * 
   * @param {String} beforeCursor
   * @param {String} cursor
   * @param {String} afterCursor
   */
  write : function(beforeCursor, cursor, afterCursor) {
    if(this.currentNode) {
      this.currentNode.innerHTML = beforeCursor + cursor + afterCursor ;
      this.beforeCursor = beforeCursor ;
      this.afterCursor = afterCursor ;
    }
  }
  ,
  
  isSameNode : function(node) {
    if(this.currentNode === node) {
      return true ;
    }
    return false ;
  }
  ,
  
  /**
   * @return {String}
   */
  getEditContent : function() {
    var editContent = this.beforeCursor + this.afterCursor ;
    editContent = this.htmlUtil.entitiesDecode(editContent) ;
    return editContent ;
  }
  ,
  
  preKeyProcess : function() {
    if (this['onBeforePreKeyProcess']) {
      this.onBeforePreKeyProcess() ;
    }
    this.beforeCursor = this.htmlUtil.entitiesDecode(this.beforeCursor) ;
    this.afterCursor = this.htmlUtil.entitiesDecode(this.afterCursor) ;
  }
  ,
  
  // Printable keys
  onDefault : function(keynum, keychar) {
    this.preKeyProcess() ;
    this.beforeCursor += keychar ;
    this.defaultWrite() ;
    return false ;
  }
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
  onPunctuation : function(keynum, keychar) {
    this.preKeyProcess() ;
    keychar = this.htmlUtil.entitiesEncode(keychar) ;
    this.beforeCursor += keychar ;
    this.defaultWrite() ;
    return false ;
  }
  ,
  
  // Control keys
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onBackspace : function(keynum, keychar) {
    this.preKeyProcess() ;
    this.beforeCursor = this.beforeCursor.substr(0, (this.beforeCursor.length - 1)) ;
    this.defaultWrite() ;  
    return false ;
  }
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onDelete : function(keynum, keychar) {
    this.preKeyProcess() ;
    this.afterCursor = this.afterCursor.substr(1, (this.afterCursor.length - 1)) ;
    this.defaultWrite() ;
    return false ;
  }
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
  onLeftArrow : function(keynum, keychar) {
    this.preKeyProcess() ;
    if((this.beforeCursor.length + '')== 0) {
      if (this.onNavGetBegin) {
        this.onNavGetBegin(keynum, keychar) ;
      }
      return false ;
    }
    this.afterCursor = this.beforeCursor.charAt(this.beforeCursor.length - 1) + this.afterCursor ;
    this.beforeCursor = this.beforeCursor.substr(0, (this.beforeCursor.length - 1)) ;
    this.defaultWrite() ;
    return false ;
  }
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onRightArrow : function(keynum, keychar) {
    this.preKeyProcess() ;
    if((this.afterCursor + '').length == 0) {
      if (this.onNavGetEnd) {
        this.onNavGetEnd(keynum, keychar) ;
      }
      return false ;
    }
    this.beforeCursor += this.afterCursor.charAt(0) ;
    this.afterCursor = this.afterCursor.substr(1, (this.afterCursor.length - 1)) ;
    this.defaultWrite() ;
    return false ;
  }
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
  onHome : function(keynum, keychar) {
    if(this.beforeCursor.length == '') {
      return false ;
    }
    this.preKeyProcess() ;
    this.afterCursor = this.beforeCursor + this.afterCursor ;
    this.beforeCursor = '' ;
    this.defaultWrite() ;
    return false ;
  }
  ,
  
  /**
   * 
   * @param {Number} keynum
   * @param {Char} keychar
   */
  onEnd : function(keynum, keychar) {
    if(this.afterCursor.length == '') {
      return false ;
    }
    this.preKeyProcess() ;
    this.beforeCursor = this.beforeCursor + this.afterCursor ;
    this.afterCursor = '' ;
    this.defaultWrite() ;
    return false ;
  }  
} ;

eXo.core.DefaultKeyboardListener = DefaultKeyboardListener ;