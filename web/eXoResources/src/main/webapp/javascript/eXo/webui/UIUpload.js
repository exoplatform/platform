function UIUpload() {
  this.listUpload = new Array();
};

//TODO: Try to use the javascript template here
UIUpload.prototype.createUploadEntry = function(uploadId) {
  var iframe = document.getElementById(uploadId+'uploadFrame');   
  var idoc = iframe.contentWindow.document ;
  var uploadAction = eXo.env.server.context + "/upload?uploadId=" + uploadId+"&action=upload" ;
  idoc.open();
  idoc.write("<head>");
  idoc.write("<script type='text/javascript'>var eXo = parent.eXo</script>");
  idoc.write("<style type='text/css'>");
  idoc.write("  .UploadButton{");
  idoc.write("    background: url('/eXoResources/skin/webui/component/UIUpload/DefaultSkin/Background/UpArrow16x16.gif') no-repeat left; ");
  idoc.write("    float:right; font-size: 16px; cursor:pointer; color: blue; padding-left: 20px;");
  idoc.write("  }");
  idoc.write("</style>");
  idoc.write("</head>");
  idoc.write("<body style='margin: 0px; border: 0px;'>");
  idoc.write("  <form id='"+uploadId+"' class='UIUploadForm' style='margin: 0px; padding: 0px;' action='"+uploadAction+"' enctype='multipart/form-data' method='post'>");
  idoc.write("    <input type='file' name='file' id='file' value='' style='float:left;'/>");
  idoc.write("    <div class='UploadButton' onclick='eXo.webui.UIUpload.upload(this, "+uploadId+")'>upload</div>");
  idoc.write("    <div style='clear:both;'><span></span></div>");
  idoc.write("  </form>");
  idoc.write("</body>");
  idoc.body.style.margin = "0px" ;
  idoc.body.style.padding = "0px" ;
  idoc.close();  
};

UIUpload.prototype.refeshProgress = function(elementId) {
  var list =  eXo.webui.UIUpload.listUpload;
  if(list.length < 1) return;
  var url =  eXo.env.server.context + "/upload?action=progress";  
  for(var i = 0; i < list.length; i++){
    url = url + "&uploadId=" + list[i];
  }
  var request =  eXo.core.Browser.createHttpRequest();
  request.open('GET', url, false);
  request.setRequestHeader("Cache-Control", "max-age=86400");
  request.send(null);
  
  if(list.length > 0) {
    setTimeout("eXo.webui.UIUpload.refeshProgress('" + elementId + "');", 1000); 
  }
    
  var response;
   try{
    eval("response = "+request.responseText);
  }catch(err){
    return;  
  }
  
  for(id in response.percent) {
    var element = document.getElementById(id+"ProgressIframe");
    var value  =   response.percent[id];
    var container = parent.document.getElementById(elementId);
    var progressBarMiddle = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarMiddle") ;
    var blueProgressBar = eXo.core.DOMUtil.findFirstChildByClass(progressBarMiddle, "div", "BlueProgressBar") ;
    var progressBarLabel = eXo.core.DOMUtil.findFirstChildByClass(blueProgressBar, "div", "ProgressBarLabel") ;
    blueProgressBar.style.width = value + "%" ;
    progressBarLabel.innerHTML = value + "%" ;
  }

  if(value == 100){
    eXo.webui.UIUpload.listUpload.remove(id);
    element.innerHTML =  "<span></span>";
      
    var selectFileFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "SelectFileFrame") ;
    selectFileFrame.style.display = "block" ;
    var progressBarFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarFrame") ;
    progressBarFrame.style.display = "none" ;
    var fileNameLabel = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "FileNameLabel") ;
    
    var tmp = element.parentNode;
    var temp = tmp.parentNode;
    var child = eXo.core.DOMUtil.getChildrenByTagName(temp,"label");
    child[0].style.visibility =  "hidden" ;
    return;
  }
  if (element){
    element.innerHTML = "Uploaded "+ value + "% " +
                        "<span onclick='parent.eXo.webui.UIUpload.abortUpload("+id+")'>Abort</span>";
  }
};

UIUpload.prototype.abortUpload = function(id) {
  eXo.webui.UIUpload.listUpload.remove(id);
  var url = eXo.env.server.context + "/upload?uploadId=" +id+"&action=abort" ;
  var request =  eXo.core.Browser.createHttpRequest();
  request.open('GET', url, false);
  request.setRequestHeader("Cache-Control", "max-age=86400");
  request.send(null);
  
  var container = parent.document.getElementById(id);
  var uploadIframe =  eXo.core.DOMUtil.findDescendantById(container, id+"UploadIframe");
  uploadIframe.style.display = "block";
  eXo.webui.UIUpload.createUploadEntry(id);
  var progressIframe = eXo.core.DOMUtil.findDescendantById(container, id+"ProgressIframe");
  progressIframe.style.display = "none";

  var tmp = progressIframe.parentNode;
  var temp = tmp.parentNode;
  var child = eXo.core.DOMUtil.getChildrenByTagName(temp,"label");
  child[0].style.visibility =  "visible" ;
  var progressBarFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarFrame") ;
  progressBarFrame.style.display = "none" ;
  var selectFileFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "SelectFileFrame") ;
  selectFileFrame.style.display = "none" ;
   
  var  input = parent.document.getElementById('input' + id);
  input.value = "false";
}

UIUpload.prototype.deleteUpload = function(id) {
  var url = eXo.env.server.context + "/upload?uploadId=" +id+"&action=delete" ;
  var request =  eXo.core.Browser.createHttpRequest();
  request.open('GET', url, false);
  request.setRequestHeader("Cache-Control", "max-age=86400");
  request.send(null);
  
  var container = parent.document.getElementById(id);
  var uploadIframe =  eXo.core.DOMUtil.findDescendantById(container, id+"UploadIframe");
  uploadIframe.style.display = "block";
  eXo.webui.UIUpload.createUploadEntry(id);
  var progressIframe = eXo.core.DOMUtil.findDescendantById(container, id+"ProgressIframe");
  progressIframe.style.display = "none";

  var tmp = progressIframe.parentNode;
  var temp = tmp.parentNode;
  var child = eXo.core.DOMUtil.getChildrenByTagName(temp,"label");
  child[0].style.visibility =  "visible" ;
  var progressBarFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarFrame") ;
  progressBarFrame.style.display = "none" ;
  var selectFileFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "SelectFileFrame") ;
  selectFileFrame.style.display = "none" ;
   
  var  input = parent.document.getElementById('input' + id);
  input.value = "false";
} ;


UIUpload.prototype.upload = function(clickEle, id) {  
  var container = parent.document.getElementById(id);  
  var uploadFrame = parent.document.getElementById(id+"uploadFrame");
  var form = uploadFrame.contentWindow.document.getElementById(id);

  var file  = eXo.core.DOMUtil.findDescendantById(form, "file");
  if(file.value == null || file.value == '') return;  
  var infoUploaded = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "FileNameLabel") ;
  infoUploaded.innerHTML = "Uploaded "+file.value;
  
  var progressBarFrame = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarFrame") ;
  progressBarFrame.style.display = "block" ;  
  var progressBarMiddle = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ProgressBarMiddle") ;
  var blueProgressBar = eXo.core.DOMUtil.findFirstChildByClass(progressBarMiddle, "div", "BlueProgressBar") ;
  var progressBarLabel = eXo.core.DOMUtil.findFirstChildByClass(blueProgressBar, "div", "ProgressBarLabel") ;
  blueProgressBar.style.width = "0%" ;
  progressBarLabel.innerHTML = "0%" ;
  
  var  input = parent.document.getElementById('input' + id);
  input.value = "true";
  
  var uploadIframe =  eXo.core.DOMUtil.findDescendantById(container, id+"UploadIframe");
  uploadIframe.style.display = "none";
  var progressIframe = eXo.core.DOMUtil.findDescendantById(container, id+"ProgressIframe");
  progressIframe.style.display = "none";

  var tmp = progressIframe.parentNode;
  var temp = tmp.parentNode;
  var child = eXo.core.DOMUtil.getChildrenByTagName(temp,"label");// Label contains string "Upload File" in front of File Chooseer.
  child[0].style.visibility = "hidden" ;
  
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
