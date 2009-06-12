function UIUpload() {
  this.listUpload = new Array();
  this.isAutoUpload = false;
  //this.listLimitMB = new Array();
};

UIUpload.prototype.initUploadEntry = function(uploadId, isAutoUpload) {
	var url = eXo.env.server.context + "/command?" ;
	url += "type=org.exoplatform.web.command.handler.UploadHandler&action=progress&uploadId="+uploadId ;
	var responseText = ajaxAsyncGetRequest(url, false);
	
	var response;
   try{
    eval("response = "+responseText);
  }catch(err){
    return;  
  }
  UIUpload.isAutoUpload = isAutoUpload;
	if(response.upload[uploadId] == undefined || response.upload[uploadId].percent == undefined) {
		//eXo.webui.UIUpload.listLimitMB.push();
		this.createUploadEntry(uploadId, isAutoUpload);
	} else if(response.upload[uploadId].percent == 100)  {
		this.showUploaded(uploadId, decodeURIComponent(response.upload[uploadId].fileName));
	} 
};


UIUpload.prototype.createUploadEntry = function(uploadId, isAutoUpload) {
  var iframe = document.getElementById(uploadId+'uploadFrame');
  var idoc = iframe.contentWindow.document ;
  var uploadAction = eXo.env.server.context + "/command?" ;
  uploadAction += "type=org.exoplatform.web.command.handler.UploadHandler";
  uploadAction += "&uploadId=" + uploadId+"&action=upload" ;
  idoc.open();
	idoc.write("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>");
  idoc.write("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='" +eXo.core.I18n.lang+ "' dir='" +eXo.core.I18n.dir+ "'>");
  idoc.write("<head>");
  idoc.write("<style type='text/css'>");
  idoc.write(".UploadButton {width: 20px; height: 20px; cursor: pointer; vertical-align: bottom;");
  idoc.write(" background: url('/eXoResources/skin/DefaultSkin/webui/component/UIUpload/background/UpArrow16x16.gif') no-repeat left; }");
  idoc.write("</style>");
  idoc.write("<script type='text/javascript'>var eXo = parent.eXo</script>");
  idoc.write("</head>");
  idoc.write("<body style='margin: 0px; border: 0px;'>");
  idoc.write("  <form id='"+uploadId+"' class='UIUploadForm' style='margin: 0px; padding: 0px' action='"+uploadAction+"' enctype='multipart/form-data' method='post'>");
  if(isAutoUpload){
  	idoc.write("    <input type='file' name='file' id='file' value='' onchange='eXo.webui.UIUpload.upload(this, "+uploadId+")' onkeypress='return false;' />");
  }else{
		idoc.write("    <input type='file' name='file' id='file' value='' onkeypress='return false;' />");
	  idoc.write("    <img class='UploadButton' onclick='eXo.webui.UIUpload.upload(this, "+uploadId+")' alt='' src='/eXoResources/skin/sharedImages/Blank.gif'/>");  	
  }
  idoc.write("  </form>");
  idoc.write("</body>");
  idoc.write("</html>");
  idoc.close();
};


UIUpload.prototype.refeshProgress = function(elementId) {
  var list =  eXo.webui.UIUpload.listUpload;
  if(list.length < 1) return;
  var url = eXo.env.server.context + "/command?" ;
	url += "type=org.exoplatform.web.command.handler.UploadHandler&action=progress" ;
//  var url =  eXo.env.server.context + "/upload?action=progress";  
  for(var i = 0; i < list.length; i++){
    url = url + "&uploadId=" + list[i];
  }

  var responseText = ajaxAsyncGetRequest(url, false);
  if(list.length > 0) {
    setTimeout("eXo.webui.UIUpload.refeshProgress('" + elementId + "');", 1000); 
  }
    
  var response;
  try {
    eval("response = "+responseText);
  }catch(err) {
    return;  
  }
  
  for(id in response.upload) {
    var container = parent.document.getElementById(elementId);
  	if (response.upload[id].status == "failed") {
  		this.abortUpload(id);
  		var message = eXo.core.DOMUtil.findFirstChildByClass(container, "div", "LimitMessage").innerHTML ;
  		alert(message.replace("{0}", response.upload[id].size)) ;
//  		alert(response.upload[id].message);
  		continue;
  	}
    var element = document.getElementById(id+"ProgressIframe");
    var percent  =   response.upload[id].percent;
    var progressBarMiddle = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarMiddle") ;
    var blueProgressBar = eXo.core.DOMUtil.findFirstChildByClass(progressBarMiddle, "div", "BlueProgressBar") ;
    var progressBarLabel = eXo.core.DOMUtil.findFirstChildByClass(blueProgressBar, "div", "ProgressBarLabel") ;
    blueProgressBar.style.width = percent + "%" ;
    progressBarLabel.innerHTML = percent + "%" ;
    
    if(percent == 100) this.showUploaded(id, "");
  }
  
  if(eXo.webui.UIUpload.listUpload.length < 1) return;

  if (element){
    element.innerHTML = "Uploaded "+ percent + "% " +
                        "<span onclick='parent.eXo.webui.UIUpload.abortUpload("+id+")'>Abort</span>";
  }
};

UIUpload.prototype.showUploaded = function(id, fileName) {
	eXo.webui.UIUpload.listUpload.remove(id);
	var container = parent.document.getElementById(id);
  var element = document.getElementById(id+"ProgressIframe");
  element.innerHTML =  "<span></span>";
  
  var uploadIframe = eXo.core.DOMUtil.findDescendantById(container, id+"UploadIframe");
  uploadIframe.style.display = "none";
  var progressIframe = eXo.core.DOMUtil.findDescendantById(container, id+"ProgressIframe");
  progressIframe.style.display = "none";
    
  var selectFileFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "SelectFileFrame") ;
  selectFileFrame.style.display = "block" ;
  var fileNameLabel = eXo.core.DOMUtil.findFirstDescendantByClass(selectFileFrame, "div", "FileNameLabel") ;
  if(fileName != null) fileNameLabel.innerHTML += " " + fileName;
  var progressBarFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarFrame") ;
  progressBarFrame.style.display = "none" ;
  var tmp = element.parentNode;
  var temp = tmp.parentNode;
  //TODO: dang.tung - always return true even we reload browser
  var  input = parent.document.getElementById('input' + id);
	input.value = "true" ;  
};

UIUpload.prototype.abortUpload = function(id) {
  eXo.webui.UIUpload.listUpload.remove(id);
  var url = eXo.env.server.context + "/command?" ;
	url += "type=org.exoplatform.web.command.handler.UploadHandler&uploadId=" +id+"&action=abort" ;
//  var url = eXo.env.server.context + "/upload?uploadId=" +id+"&action=abort" ;
  var request =  eXo.core.Browser.createHttpRequest();
  request.open('GET', url, false);
  request.setRequestHeader("Cache-Control", "max-age=86400");
  request.send(null);
  
  var container = parent.document.getElementById(id);
  var uploadIframe =  eXo.core.DOMUtil.findDescendantById(container, id+"UploadIframe");
  uploadIframe.style.display = "block";
  eXo.webui.UIUpload.createUploadEntry(id, UIUpload.isAutoUpload);
  var progressIframe = eXo.core.DOMUtil.findDescendantById(container, id+"ProgressIframe");
  progressIframe.style.display = "none";

  var tmp = progressIframe.parentNode;
  var temp = tmp.parentNode;
//  var child = eXo.core.DOMUtil.getChildrenByTagName(temp,"label");
//  child[0].style.visibility =  "visible" ;
  var progressBarFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarFrame") ;
  progressBarFrame.style.display = "none" ;
  var selectFileFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "SelectFileFrame") ;
  selectFileFrame.style.display = "none" ;
   
  var  input = parent.document.getElementById('input' + id);
  input.value = "false";
};

UIUpload.prototype.deleteUpload = function(id) {
	var url = eXo.env.server.context + "/command?";
	url += "type=org.exoplatform.web.command.handler.UploadHandler&uploadId=" +id+"&action=delete" ;
//  var url = eXo.env.server.context + "/upload?uploadId=" +id+"&action=delete" ;
  var request =  eXo.core.Browser.createHttpRequest();
  request.open('GET', url, false);
  request.setRequestHeader("Cache-Control", "max-age=86400");
  request.send(null);
  var DOMUtil = eXo.core.DOMUtil;
  var container = parent.document.getElementById(id);
  var uploadIframe =  DOMUtil.findDescendantById(container, id+"UploadIframe");
  uploadIframe.style.display = "block";
  eXo.webui.UIUpload.createUploadEntry(id, UIUpload.isAutoUpload);
  var progressIframe = DOMUtil.findDescendantById(container, id+"ProgressIframe");
  progressIframe.style.display = "none";

  var tmp = progressIframe.parentNode;
  var temp = tmp.parentNode;
  var progressBarFrame = DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarFrame") ;
  progressBarFrame.style.display = "none" ;
  var selectFileFrame = DOMUtil.findFirstDescendantByClass(container, "div", "SelectFileFrame") ;
  selectFileFrame.style.display = "none" ;
   
  var  input = parent.document.getElementById('input' + id);
  input.value = "false";
} ;


UIUpload.prototype.upload = function(clickEle, id) {
	var DOMUtil = eXo.core.DOMUtil;  
  var container = parent.document.getElementById(id);  
  var uploadFrame = parent.document.getElementById(id+"uploadFrame");
  var form = uploadFrame.contentWindow.document.getElementById(id);

  var file  = DOMUtil.findDescendantById(form, "file");
  if(file.value == null || file.value == '') return;  
  var infoUploaded = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "FileNameLabel") ;
  var temp = file.value;

  if (temp.indexOf('/') != -1) {
    temp = temp.substr((temp.lastIndexOf('/') + 1), temp.length - 1) ;
  }
  
  if (temp.indexOf('\\') != -1) {
    temp = temp.substr((temp.lastIndexOf('\\') + 1), temp.length - 1) ;
  }
  
  infoUploaded.innerHTML = temp ;

  var progressBarFrame = DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarFrame") ;
  progressBarFrame.style.display = "block" ;  
  var progressBarMiddle = DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarMiddle") ;
  var blueProgressBar = DOMUtil.findFirstChildByClass(progressBarMiddle, "div", "BlueProgressBar") ;
  var progressBarLabel = DOMUtil.findFirstChildByClass(blueProgressBar, "div", "ProgressBarLabel") ;
  blueProgressBar.style.width = "0%" ;
  progressBarLabel.innerHTML = "0%" ;
  
  var  input = parent.document.getElementById('input' + id);
  input.value = "true";
  
  var uploadIframe = DOMUtil.findDescendantById(container, id+"UploadIframe");
  uploadIframe.style.display = "none";
  var progressIframe = DOMUtil.findDescendantById(container, id+"ProgressIframe");
  progressIframe.style.display = "none";

  var tmp = progressIframe.parentNode;
  var temp = tmp.parentNode;
  
  form.submit() ;
  
  var list = eXo.webui.UIUpload.listUpload;
  if(list.length == 0) {
    eXo.webui.UIUpload.listUpload.push(form.id);
    setTimeout("eXo.webui.UIUpload.refeshProgress('" + id + "');", 1000);
  } else {
    eXo.webui.UIUpload.listUpload.push(form.id);  
  }
} ;

eXo.webui.UIUpload = new UIUpload();
