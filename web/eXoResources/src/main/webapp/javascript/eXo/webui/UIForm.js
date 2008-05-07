/**
 * Manages a form component
 */
function UIForm() {
};

UIForm.prototype.getFormElemt = function(pattern) {
	if(pattern.indexOf("#") == -1) return document.getElementById(pattern) ;
	var strArr = pattern.split("#") ;
	var portlet = document.getElementById(strArr[0]) ;
	return eXo.core.DOMUtil.findDescendantById(portlet, strArr[1]) ;
}

/*ie bug  you cannot have more than one button tag*/
/**
 * A function that submits the form identified by formId, with the specified action
 * If useAjax is true, calls the ajaxPost function from PortalHttpRequest, with the given callback function
 */
UIForm.prototype.submitForm = function(formId, action, useAjax, callback) {
 if (!callback) callback = null;
 var form = this.getFormElemt(formId) ;
 //TODO need review try-cactch block for form doesn't use FCK
 try {
  if (FCKeditorAPI && typeof FCKeditorAPI == "object") {
 	  for ( var name in FCKeditorAPI.__Instances ) {
 	  	var oEditor ;
 	  	try {
 	  	  oEditor = FCKeditorAPI.__Instances[name] ;
	 	  	if (oEditor && oEditor.GetParentForm && oEditor.GetParentForm() == form ) {
	 	  		oEditor.UpdateLinkedField() ;
	 	  	}
 	  	} catch(e) {
 	  	  continue ;
 	  	}
  	}
  }
 } catch(e) {}

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
  var form = this.getFormElemt(formId) ;
	 try {
	  if (FCKeditorAPI && typeof FCKeditorAPI == "object") {
	 	  for ( var name in FCKeditorAPI.__Instances ) {
	 	  	var oEditor = FCKeditorAPI.__Instances[name] ;
	 	  	if ( oEditor.GetParentForm && oEditor.GetParentForm() == form ) {
	 	  		oEditor.UpdateLinkedField() ;
	 	  	}
	  	}
	  }
	 } catch(e) {}
  form.elements['formOp'].value = action ; 
  if(!form.originalAction) form.originalAction = form.action ; 
	form.action =  form.originalAction +  encodeURI(params) ;
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
/*
* This method goes through the form element passed as an argument and 
* generates a string output in a GET request way.
* It also encodes the the form parameter values
*/
UIForm.prototype.serializeForm = function (formElement) {
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
        this.addField(element.name, element.value.replace(/\r/gi, ""));  
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
      case "select-multiple":
        while (element.selectedIndex != -1) {
        	this.addField(element.name, element.options[element.selectedIndex].value);
        	element.options[element.selectedIndex].selected = false; 
        }
        break;
    } // switch
   } // for 
   return queryString;
};

eXo.webui.UIForm = new UIForm();