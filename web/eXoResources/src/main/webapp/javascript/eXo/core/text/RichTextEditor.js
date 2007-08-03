/**
 * @author Nguyen Ba Uoc
 */

function RichTextEditor() {
}

RichTextEditor.prototype = new eXo.core.DefaultKeyboardListener() ;

RichTextEditor.prototype.onAlphabet = RichTextEditor.prototype.onDefault ;
RichTextEditor.prototype.onDigit = RichTextEditor.prototype.onDefault ;

RichTextEditor.prototype.onNavGetBegin = function(keynum, keychar) {
  var node = this.currentNode ;
  var previousEditableNode = false ;
  while((node = node.previousSibling) &&
        node.className != 'UIWindow') {
    if (eXo.core.CoreEditor.isEditableNode(node)) {
      previousEditableNode = node ;
      break ;
    }
  }
  if (previousEditableNode) {
    eXo.core.CoreEditor.init(previousEditableNode) ;
    this.beforeCursor = this.getEditContent() ;
    this.afterCursor = '' ;
    this.defaultWrite() ;
  }
} ;

RichTextEditor.prototype.onNavGetEnd = function(keynum, keychar) {
  var node = this.currentNode ;
  var nextEditableNode = false ;
  while((node = node.nextSibling)) {
    if (eXo.core.CoreEditor.isEditableNode(node)) {
      nextEditableNode = node ;
      break ;
    }
  }
  if (nextEditableNode) {
    eXo.core.CoreEditor.init(nextEditableNode) ;
    this.beforeCursor = '' ; 
    this.afterCursor = this.getEditContent() ;
    this.defaultWrite() ;
  }
} ;

if (!eXo.core.text) eXo.core.text = {} ;
eXo.core.text.RichTextEditor = new RichTextEditor() ;