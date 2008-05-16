/**
 * @param {String} layoutRootId Id of the element that is the parent of all
 *     gadgets.
 */
gadgets.eXoLayoutManager = function(layoutRootId) {
  gadgets.LayoutManager.call(this);
  this.layoutRootId_ = layoutRootId;
};

gadgets.eXoLayoutManager.inherits(gadgets.LayoutManager);

gadgets.eXoLayoutManager.prototype.getGadgetChrome =
    function(gadget) {
  var layoutRoot = document.getElementById(this.layoutRootId_);
  if (layoutRoot) {
    var chrome = document.createElement('div');
    chrome.className = 'gadgets-gadget-chrome';
    chrome.style.style = 'left' ;
    layoutRoot.appendChild(chrome);
    return chrome;
  } else {
    return null;
  }
};