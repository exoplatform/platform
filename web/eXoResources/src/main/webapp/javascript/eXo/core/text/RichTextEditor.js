/**
 * @author Nguyen Ba Uoc
 */

function RichTextEditor() {
}

RichTextEditor.prototype = new eXo.core.DefaultKeyboardListener() ;

RichTextEditor.prototype.onNavGetStart = function(keynum, keychar) {
  var node = this.currentNode ;
  var previousEditableNode = false ;
  while((node = node.previousSibling) && node.className != 'UIWindow') {
    if (!eXo.core.CoreEditor.isEditableNode(node)) {
      previousEditableNode = node ;
      break ;
    }
  }
  if (previousEditableNode) {
    eXo.core.CoreEditor.init(previousEditableNode) ;
    this.afterCursor = this.getEditContent() ;
    this.beforeCursor = '' ;
    this.defaultWrite() ;
  }
} ;

if (!eXo.core.text) eXo.core.text = {} ;
eXo.core.text.RichTextEditor = new RichTextEditor() ;