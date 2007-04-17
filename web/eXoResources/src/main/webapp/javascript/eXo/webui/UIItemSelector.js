function UIItemSelector() {
  this.backupClass;
};

UIItemSelector.prototype.onOver = function(clickedElement, mouseOver) {
	eXo.webui.UIItemSelector.beforeActionHappen(clickedElement);
  
  if(mouseOver) {
    this.backupClass = clickedElement.className;
    clickedElement.className = "OverItem Item";
    this.onChangeItemDetail(clickedElement, true);
  } else {
    clickedElement.className = this.backupClass;
    this.onChangeItemDetail(clickedElement, false);
  }
};

UIItemSelector.prototype.onClick = function(clickedElement) {
	
  var itemListContainer = clickedElement.parentNode;
	var allItems =  eXo.core.DOMUtil.findDescendantsByClass(itemListContainer, "div", "Item");
	eXo.webui.UIItemSelector.beforeActionHappen(clickedElement);
  
  for(var i = 0; i < allItems.length; i++) {
    if(allItems[i] != clickedElement) {
      allItems[i].className = "Item";
		  this.onChangeItemDetail(clickedElement, true);
    } else {
      allItems[i].className = "SelectedItem Item";
	    this.backupClass = " SelectedItem Item";
  		this.onChangeItemDetail(clickedElement, false);
    }
  }
};

UIItemSelector.prototype.onClick = function(clickedElement, form, component, option) {
  var itemListContainer = clickedElement.parentNode;
	var allItems =  eXo.core.DOMUtil.findDescendantsByClass(itemListContainer, "div", "Item");
	eXo.webui.UIItemSelector.beforeActionHappen(clickedElement);
  
  for(var i = 0; i < allItems.length; i++) {
    if(allItems[i] != clickedElement) {
      allItems[i].className = "Item";
		  this.onChangeItemDetail(clickedElement, true);
    } else {
      allItems[i].className = "SelectedItem Item";
	    this.backupClass = " SelectedItem Item";
  		this.onChangeItemDetail(clickedElement, false);
    }
  }
  if (eXo.webui.UIItemSelector.SelectedItem == null)	
	  eXo.webui.UIItemSelector.SelectedItem = new Object();
  eXo.webui.UIItemSelector.SelectedItem.component = component;
  eXo.webui.UIItemSelector.SelectedItem.option = option;  
};

UIItemSelector.prototype.onClickOption = function(clickedElement, form, component, option) {
	var itemDetails = eXo.core.DOMUtil.getChildrenByTagName(clickedElement.parentNode, "div");
	for (var i = 0; i < itemDetails.length; i++)
		itemDetails[i].className = "NormalItem";
	clickedElement.className = "SelectedItem";
	if (eXo.webui.UIItemSelector.SelectedItem == null)
	  eXo.webui.UIItemSelector.SelectedItem = new Object();
  eXo.webui.UIItemSelector.SelectedItem.component = component;
  eXo.webui.UIItemSelector.SelectedItem.option = option;  
	
};

/*TODO: Review This Function (Ha's comment)*/
UIItemSelector.prototype.beforeActionHappen = function(selectedItem) {
	this.uiItemSelector = eXo.core.DOMUtil.findAncestorByClass(selectedItem, "UIItemSelector");
  this.itemList = eXo.core.DOMUtil.findAncestorByClass(selectedItem, "ItemList");
  this.itemListContainer = eXo.core.DOMUtil.findAncestorByClass(selectedItem, "ItemListContainer") ;
  this.itemListAray = eXo.core.DOMUtil.findDescendantsByClass(this.itemListContainer.parentNode, "div", "ItemList");
  
  if(this.itemListAray.length > 1) {
	  this.itemDetailLists = eXo.core.DOMUtil.findDescendantsByClass(this.itemListContainer.parentNode, "div", "ItemDetailList");
		this.itemDetailList = null;
	  for(var i = 0; i < this.itemListAray.length; i++) {
	  	if(this.itemListAray[i].style.display == "none") {
	  		this.itemDetailLists[i].style.display = "none" ;
	  	} else {
		  	this.itemDetailList = this.itemDetailLists[i];
		  	this.itemDetailList.style.display = "block";
	  	}
	  }
	} else {
	  this.itemDetailList = eXo.core.DOMUtil.findFirstDescendantByClass(this.itemListContainer.parentNode, "div", "ItemDetailList");
	}
  //this.itemDetails = eXo.core.DOMUtil.findChildrenByClass(this.itemDetailList, "div", "ItemDetail");
	var itemDetailContainer = eXo.core.DOMUtil.findChildrenByClass(this.itemDetailList, "div", "ItemDetailContainer")[0];
  this.itemDetails = eXo.core.DOMUtil.findChildrenByClass(itemDetailContainer, "div", "ItemDetail");
  var firstItemDescendant = eXo.core.DOMUtil.findFirstDescendantByClass(this.itemList, "div", "Item");
  var firstItemParent = firstItemDescendant.parentNode;
  this.allItems = eXo.core.DOMUtil.findChildrenByClass(firstItemParent, "div", "Item");
};

UIItemSelector.prototype.onChangeItemDetail = function(itemSelected, mouseOver) {
	
  if(mouseOver) {
    for(var i = 0; i < this.allItems.length; i++) {
      if(this.allItems[i] == itemSelected) {
        this.itemDetails[i].style.display = "block";      	
      } else {
        this.itemDetails[i].style.display = "none";
      }
    }
  } else {
    for(var i = 0; i < this.allItems.length; i++) {
      if(this.allItems[i].className == "SelectedItem Item") {
        this.itemDetails[i].style.display = "block";
      } else {
        this.itemDetails[i].style.display = "none";
      }
    }
  }
  
};

UIItemSelector.prototype.showPopupCategory = function(selectedNode) {
	var DOMUtil = eXo.core.DOMUtil ;
	var itemListContainer = DOMUtil.findAncestorByClass(selectedNode, "ItemListContainer") ;
	var uiPopupCategory = DOMUtil.findFirstDescendantByClass(itemListContainer, "div", "UIPopupCategory") ;
	
	itemListContainer.style.position = "relative" ;
	
	if(uiPopupCategory.style.display == "none") {
		uiPopupCategory.style.position = "absolute" ;
		uiPopupCategory.style.top = "23px" ;
		uiPopupCategory.style.left = "0px" ;
		uiPopupCategory.style.display = "block" ;
		uiPopupCategory.style.width = "100%" ;
	} else {
		uiPopupCategory.style.display = "none" ;
	}		
};

UIItemSelector.prototype.selectCategory = function(selectedNode) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiPopupCategory = DOMUtil.findAncestorByClass(selectedNode, "UIPopupCategory") ;
	var itemListContainer = DOMUtil.findAncestorByClass(selectedNode, "OverflowContainer") ;
	var selectedNodeIndex = eXo.webui.UIItemSelector.findIndex(selectedNode) ;
	
	var itemLists = DOMUtil.findDescendantsByClass(itemListContainer, "div", "ItemList") ;
	var itemDetailLists = DOMUtil.findDescendantsByClass(itemListContainer, "div", "ItemDetailList");
	
	for(var i = 0; i < itemLists.length; i++) {
		if(i != selectedNodeIndex){
			itemLists[i].style.display = "none" ;
			itemDetailLists[i].style.display = "none" ;
		}	else{
			itemDetailLists[i].style.display = "block" ;
			itemLists[i].style.display = "block" ;
		}
	}
	
	uiPopupCategory.style.display = "none" ;

};

UIItemSelector.prototype.findIndex = function(object) {
	var parentNode = object.parentNode ;
	var objectElements = eXo.core.DOMUtil.findChildrenByClass(parentNode, "div", object.className) ;
	for(var i = 0; i < objectElements.length; i++) {
		if(objectElements[i] == object) return i ;
	}
};

eXo.webui.UIItemSelector = new UIItemSelector() ;