eXo.require('eXo.webui.UIItemSelector');
/**
 * Manages a form component
 */
function UIForm() {
};

//TODO:Lambkin think Portal don't use this method.
/*
UIForm.prototype.onFixSize = function() {
	var DOMUtil = eXo.core.DOMUtil;
	var arrowIcon = DOMUtil.findFirstDescendantByClass(document, "div", "Button");
	if(arrowIcon != null) {
		arrowIcon.className = "IconHolder ArrowS1Down16x16Icon" ;
	}
  var uiFormTabPane = DOMUtil.findFirstDescendantByClass(document, "div", "UIFormTabPane");
  if(!uiFormTabPane) return ;
  var tabPaneContent = DOMUtil.findFirstDescendantByClass(uiFormTabPane, "div", "TabPaneContent");
  var uiQuickHelp = DOMUtil.findFirstChildByClass(tabPaneContent, "div", "UIQuickHelp");
  if(uiQuickHelp == null) return ;
  
  var workingAreaWithHelp = DOMUtil.findFirstChildByClass(tabPaneContent, "div", "WorkingAreaWithHelp");
  var scrollArea = DOMUtil.findFirstDescendantByClass(uiQuickHelp, "div", "ScrollArea");

  scrollArea.style.height = (workingAreaWithHelp.offsetHeight - 78 ) + "px";

  scrollArea.style.overflow = "auto";
};
*/

/*ie bug  you cannot have more than one button tag*/
/**
 * A function that submits the form identified by formId, with the specified action
 * If useAjax is true, calls the ajaxPost function from PortalHttpRequest, with the given callback function
 */
UIForm.prototype.submitForm = function(formId, action, useAjax, callback) {
	if (!callback) callback = null;
  var form = document.getElementById(formId) ;
  form.elements['formOp'].value = action ;
  if(useAjax) ajaxPost(form, callback) ;
  else  form.submit();
} ;

/*ie bug  you cannot have more than one button tag*/
/**
 * Submits a form by Ajax, with the given action and the given parameters
 * Calls ajaxPost of PortalHttpRequest
 */
UIForm.prototype.submitEvent = function(formId, action, params) {
  var form = document.getElementById(formId) ;
  form.elements['formOp'].value = action ; 
	form.action =  form.action +  params ;
  ajaxPost(form) ;
} ;

UIForm.prototype.selectBoxOnChange = function(formId, elemt) {
	var selectBox = eXo.core.DOMUtil.findAncestorByClass(elemt, "UISelectBoxOnChange");
	var contentContainer = eXo.core.DOMUtil.findFirstDescendantByClass(selectBox, "div", "SelectBoxContentContainer") ;
	var tabs = eXo.core.DOMUtil.findChildrenByClass(contentContainer, "div", "SelectBoxContent");
	for(var i=0; i < tabs.length; i++) {
		tabs[i].style.display = "none";
	}
	tabs[elemt.selectedIndex].style.display = "block";
} ;
/**
 * Sets the value (hiddenValue) of a hidden field (typeId) in the form (formId)
 */
UIForm.prototype.setHiddenValue = function(formId, typeId, hiddenValue) {
  var form = document.getElementById(formId) ;
  if(form == null){
	  maskWorkspace =	document.getElementById("UIMaskWorkspace");
	  form = eXo.core.DOMUtil.findDescendantById(maskWorkspace, formId);
  }
  form.elements[typeId].value = hiddenValue;  
} ;
/**
 * Returns a string that contains all the values of the elements of a form (formElement) in this format
 *  . fieldName=value
 * The result is a string like this : abc=def&ghi=jkl...
 * The values are encoded to be used in an URL
 * Only serializes the elements of type :
 *  . text, hidden, password, textarea
 *  . checkbox and radio if they are checked
 *  . select-one if one option is selected
 */
UIForm.prototype.serializeForm = function (formElement) {
	//TODO: TrongTT -> Solve the temporary problem about WYSIWYG Editor
	try{eXo.ecm.ExoEditor.saveHandler();} catch(err) {}
	
	var queryString = "";
  var element ;
  var elements = formElement.elements;
  
  this.addField = function(name, value) { 
	  if (queryString.length > 0) queryString += "&";
	  queryString += name + "=" + encodeURIComponent(value);
  };
  
  for(var i = 0; i < elements.length; i++) {
    element = elements[i];
    //if(element.disabled) continue;
    switch(element.type) {
      case "text":
      case "hidden":
      case "password":
      case "textarea" :  
        this.addField(element.name, element.value);  
        break; 
          
      case "checkbox":
      case "radio":
        if(element.checked) this.addField(element.name, element.value);  
        break;  
  
      case "select-one":
        if(element.selectedIndex > -1){
        	this.addField(element.name, element.options[element.selectedIndex].value);  
        }
        break;
    } // switch
   } // for   
   return queryString;
};

eXo.webui.UIForm = new UIForm();