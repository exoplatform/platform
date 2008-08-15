function UINotification(){
	this.timerlen = 5;
	this.slideAniLen = 1000;
	this.timerID = new Array();
	this.startTime = new Array();
	this.object = new Array();
	this.endHeight = new Array();
	this.moving = new Array();
	this.endSlideUpCallback = new Array();
	this.dir = new Array();	
	this.importantNoti = new Array();
	this.flagNoti = new Array();
	this.totalCurrentMessage = 0;
	this.numberMessageRecepted = 0;
	this.numImptNoti = 0;
	if (eXo.core.Topic != null) {
		eXo.core.Topic.subscribe("/eXo/portal/notification", function(event){
			eXo.webui.UINotification.addMessage(event.message);
		})
	}
}

UINotification.prototype.slideDown = function(objectName){
  if(this.moving[objectName]) return;        
  if(document.getElementById(objectName).style.display != "none") return; 
  this.moving[objectName] = true;
  this.dir[objectName] = "down";
  this.startSlide(objectName);      
}

UINotification.prototype.slideDownUp = function(objectName, endSlideUpCallback){
	this.slideDown(objectName);
	this.endSlideUpCallback[objectName] = endSlideUpCallback;
	if(this.flagNoti[objectName]) setTimeout("eXo.webui.UINotification.slideUp('" + objectName + "')", 3000);	
}

UINotification.prototype.closeNotification = function() {
	for(var i = 0; i < this.importantNoti.length; i ++) {
		this.flagNoti[this.importantNoti[i]] = true;
		setTimeout("eXo.webui.UINotification.slideUp('" + this.importantNoti[i] + "')", 100);
	}
}

UINotification.prototype.slideUp = function(objectName){
  if(this.moving[objectName]) return;        
  if(document.getElementById(objectName).style.display == "none") return;   
  this.moving[objectName] = true;
  this.dir[objectName] = "up";
  this.startSlide(objectName);
}

UINotification.prototype.startSlide = function(objectName){
  this.object[objectName] = document.getElementById(objectName);
  this.endHeight[objectName] = parseInt(this.object[objectName].style.height);
  this.startTime[objectName] = (new Date()).getTime();        
  if(this.dir[objectName] == "down"){
          this.object[objectName].style.height = "1px";
  }
  this.object[objectName].style.display = "block";
  this.timerID[objectName] = setInterval('eXo.webui.UINotification.slideTick(\'' + objectName + '\');',this.timerlen);
}

UINotification.prototype.slideTick = function(objectName){
  var elapsed = (new Date()).getTime() - this.startTime[objectName];		
  if (elapsed > this.slideAniLen)
    this.endSlide(objectName);
  else {
	var before = "before:" + this.object[objectName].id + "-" + this.object[objectName].style.height + "-";
    var d =Math.round(elapsed / this.slideAniLen * this.endHeight[objectName]);
    if(this.dir[objectName] == "up")
            d = this.endHeight[objectName] - d;
    this.object[objectName].style.height = d + "px";
  }
  return;
}

UINotification.prototype.destroyUINotification = function(){	
	var UINotification = document.getElementById("UINotification");		
	document.getElementsByTagName("body")[0].removeChild(UINotification);	
}

UINotification.prototype.endSlide = function(objectName){
  clearInterval(this.timerID[objectName]);
  if(this.dir[objectName] == "up") {
  	this.object[objectName].style.display = "none";
		if(this.endSlideUpCallback[objectName]) {
			this.endSlideUpCallback[objectName](objectName);
			this.totalCurrentMessage --;				
			if(this.totalCurrentMessage == 0) {	
			  this.destroyUINotification();
			  return;		
			}
		}
	}
  this.object[objectName].style.height = this.endHeight[objectName] + "px";
  delete(this.moving[objectName]);
  delete(this.timerID[objectName]);
  delete(this.startTime[objectName]);
  delete(this.endHeight[objectName]);
  delete(this.object[objectName]);
  delete(this.dir[objectName]);		
  delete(this.flagNoti[objectName]);		
  return;
}

UINotification.prototype.deleteBox = function(objectName) {
	var el = document.getElementById(objectName);
	el.parentNode.removeChild(el);
}

UINotification.prototype.createFrameForMessages = function() {
	var htmlString = "";		
	htmlString += 	"<div class=\"UIPopupNotification\">";
	htmlString += 		"<div class=\"TLPopupNotification\">";
	htmlString += 			"<div class=\"TRPopupNotification\">";
	htmlString += 				"<div class=\"TCPopupNotification\" ><span></span></div>";
	htmlString += 			"</div>";
	htmlString += 		"</div>";
	htmlString += 		"<div class=\"MLPopupNotification\">";
	htmlString += 			"<div class=\"MRPopupNotification\">";
	htmlString += 				"<div class=\"MCPopupNotification\">";
	htmlString += 					"<div class=\"TitleNotification\">";
	htmlString += 						"<a class=\"ItemTitle\" href=\"#\">Notification</a>";
	htmlString += 						"<a class=\"Close\" href=\"#\" onclick=\"eXo.webui.UINotification.closeNotification();\"><span></span></a>";
	htmlString += 					"</div>";
	htmlString += 					"<div id=\"UINotificationContent\">";
	htmlString += 					"</div>";			
	htmlString += 				"</div>";
	htmlString += 			"</div>";
	htmlString += 		"</div>";
	htmlString += 		"<div class=\"BLPopupNotification\">";
	htmlString += 			"<div class=\"BRPopupNotification\">";
	htmlString += 				"<div class=\"BCPopupNotification\"><span></span></div>";
	htmlString += 			"</div>";
	htmlString += 		"</div>";
	htmlString += 	"</div>";
	return htmlString;
}

UINotification.prototype.addMessage = function(messageContent, flag) {
	var currMessageBoxId = "UIMessageBox_" + this.numberMessageRecepted++;
	var UIMessageContent = document.createElement('div');
	this.totalCurrentMessage++;	
	
	this.flagNoti[currMessageBoxId] = flag;
	if(!flag) {
		this.importantNoti[this.numImptNoti] = currMessageBoxId;
		this.numImptNoti++;
	}
	UIMessageContent.id = currMessageBoxId;
	UIMessageContent.style.height = "75px";
	UIMessageContent.style.display = "none";
	UIMessageContent.className = "Item";
	UIMessageContent.innerHTML = "<div id='UIMessageContent'>" + messageContent + "</div>";	
	var UINotification = document.getElementById("UINotification");
	if (UINotification == null) {
		document.body.appendChild(document.createElement('div')).id = "UINotification";
		UINotification = document.getElementById("UINotification");	
		UINotification.className = 'UINotification';
		UINotification.innerHTML=this.createFrameForMessages();
	} 
	var msPanel = document.getElementById("UINotificationContent");	
	msPanel.appendChild(UIMessageContent);	
	eXo.webui.UINotification.slideDownUp(currMessageBoxId, this.deleteBox);
}

eXo.webui.UINotification = new UINotification();