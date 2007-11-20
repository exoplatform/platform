/**
 * Created by The eXo Platform SARL
 * @author : dang.tung
 *          tungcnw@gmail.com
 */
function UIFormInputThemeSelector() {} ;

UIFormInputThemeSelector.prototype.showThemeSelected = function(obj,param) {
	var DOMUtil = eXo.core.DOMUtil ;
	var itemListContainer = DOMUtil.findAncestorByClass(obj,"ItemListContainer") ;
	var itemDetailList = DOMUtil.findNextElementByTagName(itemListContainer,'div') ;
	var detailList = DOMUtil.findFirstDescendantByClass(itemDetailList,'div','UIThemeSelector') ;
	var nameTheme = DOMUtil.findNextElementByTagName(detailList,'div') ;
	var nameStyle = DOMUtil.findFirstDescendantByClass(obj,'div','NameStyle') ;
	nameTheme.innerHTML = nameStyle.innerHTML ;
	detailList.className = "UIThemeSelector " + param ;
	
	// get hide input
	var itemList = obj.parentNode ;
	var hidenInput = DOMUtil.findPreviousElementByTagName(itemList,'input') ;
	hidenInput.value = param ;
} ;

UIFormInputThemeSelector.prototype.setDefaultTheme = function(obj, param) {
	var DOMUtil = eXo.core.DOMUtil ;
	var itemDetailList = DOMUtil.findAncestorByClass(obj,"ItemDetailList") ;
	var detailList = DOMUtil.findFirstDescendantByClass(itemDetailList,'div','UIThemeSelector') ;
	detailList.className = "UIThemeSelector " + param ;
	
	var nameTheme = DOMUtil.findNextElementByTagName(detailList,'div') ;
	nameTheme.innerHTML = "Default Theme"
	
	// get hide input
	var itemListContainer = DOMUtil.findPreviousElementByTagName(itemDetailList,'div') ;
	var itemThemeSelector = DOMUtil.findFirstDescendantByClass(itemListContainer,'div','ItemList') ;
	var hidenInput = DOMUtil.findPreviousElementByTagName(itemThemeSelector,'input') ;
	hidenInput.value = param ;
} ;

eXo.webui.UIFormInputThemeSelector = new UIFormInputThemeSelector() ;