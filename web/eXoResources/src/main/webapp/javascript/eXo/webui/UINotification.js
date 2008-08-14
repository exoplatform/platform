function UINotification(){
	this.timerlen = 5;
	this.slideAniLen = 1000;

	this.timerID = new Array();
	this.startTime = new Array();
	this.obj = new Array();
	this.endHeight = new Array();
	this.moving = new Array();
	this.endSlideUpCallback = new Array();
	this.dir = new Array();
	this.total = 0;
	this.msgId = 0;
	
	if (eXo.core.Topic != null) {
		eXo.core.Topic.subscribe("/eXo/portal/notification", function(event){
			eXo.webui.UINotification.addMessage(event.message);
		})
	}
}

UINotification.prototype.slidedown = function(objname){
        if(this.moving[objname])
                return;

        if(document.getElementById(objname).style.display != "none")
                return; // cannot slide down something that is already visible

        this.moving[objname] = true;
        this.dir[objname] = "down";
        this.startslide(objname);
      
}

UINotification.prototype.slidedownup = function(objname, endSlideUpCallback){
	this.slidedown(objname);
	this.endSlideUpCallback[objname] = endSlideUpCallback;
	setTimeout("eXo.webui.UINotification.slideup('" + objname + "')", 3000);
	
}

UINotification.prototype.slideup = function(objname){
        if(this.moving[objname])
                return;
        if(document.getElementById(objname).style.display == "none")
                return; // cannot slide up something that is already hidden
        this.moving[objname] = true;
        this.dir[objname] = "up";
        this.startslide(objname);
}

UINotification.prototype.startslide = function(objname){
        this.obj[objname] = document.getElementById(objname);

        this.endHeight[objname] = parseInt(this.obj[objname].style.height);

        this.startTime[objname] = (new Date()).getTime();

        if(this.dir[objname] == "down"){
                this.obj[objname].style.height = "1px";
        }
        this.obj[objname].style.display = "block";
        this.timerID[objname] = setInterval('eXo.webui.UINotification.slidetick(\'' + objname + '\');',this.timerlen);
}

UINotification.prototype.slidetick = function(objname){
        var elapsed = (new Date()).getTime() - this.startTime[objname];
		
        if (elapsed > this.slideAniLen)
                this.endSlide(objname);
        else {
				var before = "before:" + this.obj[objname].id + "-" + this.obj[objname].style.height + "-";
                var d =Math.round(elapsed / this.slideAniLen * this.endHeight[objname]);
                if(this.dir[objname] == "up")
                        d = this.endHeight[objname] - d;
                this.obj[objname].style.height = d + "px";
        }

        return;
}

UINotification.prototype.endSlide = function(objname){
        clearInterval(this.timerID[objname]);
        if(this.dir[objname] == "up") {
        	this.obj[objname].style.display = "none";
			if(this.endSlideUpCallback[objname]) {
				this.endSlideUpCallback[objname](objname);
				  this.total --;
				  if(this.total == 0) {
						var msgsEl = document.getElementById("msgs").innerHTML = "";
						return;
				  }
		//		  this.totalMessages();
			}
		}

        this.obj[objname].style.height = this.endHeight[objname] + "px";

        delete(this.moving[objname]);
        delete(this.timerID[objname]);
        delete(this.startTime[objname]);
        delete(this.endHeight[objname]);
        delete(this.obj[objname]);
        delete(this.dir[objname]);
				
        return;
}
/**
 * this function used for count total of messages.
 */
UINotification.prototype.totalMessages = function() {
		var totalHTML = document.getElementById('totalMessage');
		totalHTML.innerHTML="" + this.total;
	}

UINotification.prototype.deleteBox = function(objname) {
	var el = document.getElementById(objname);
	el.parentNode.removeChild(el);

}

UINotification.prototype.createFrameForMessages = function() {
	var htmlString = "";
				htmlString += "<div class=\"UINotification\">";				
				htmlString += 	"<div class=\"UIPopupNotification\">";
				htmlString += 		"<div class=\"TLPopupNotification\">";
				htmlString += 			"<div class=\"TRPopupNotification\">";
				htmlString += 				"<div class=\"TCPopupNotification\"><span></span></div>";
				htmlString += 			"</div>";
				htmlString += 		"</div>";
				htmlString += 		"<div class=\"MLPopupNotification\">";
				htmlString += 			"<div class=\"MRPopupNotification\">";
				htmlString += 				"<div class=\"MCPopupNotification\">";
				htmlString += 					"<div class=\"TitleNotification\">";
				htmlString += 						"<a class=\"ItemTitle\" href=\"#\">Notification</a>";
				htmlString += 						"<a class=\"Close\" href=\"#\"><span></span></a>";
				htmlString += 					"</div>";
				htmlString += 					"<div id=\"msPanel\">";
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
				htmlString += "</div>";
				return htmlString;
}

UINotification.prototype.addMessage = function(msg) {
	var currBoxId = "messageBox_" + this.msgId++;
	var msgEl = document.createElement('div');
	this.total++;	
	msgEl.id = currBoxId;
//	msgEl.style.width= "200px";
	msgEl.style.height = "75px";
	msgEl.style.display = "none";
	msgEl.className = "Item";
	msgEl.innerHTML = "<div id='messageContent'>" + msg + "</div>";	
	var msgsEl = document.getElementById("msgs");

	if (msgsEl == null) {
		document.body.appendChild(document.createElement('div')).id = "msgs";
		msgsEl = document.getElementById("msgs");
	//	alert(this.createFrameForMessages());
		msgsEl.innerHTML=this.createFrameForMessages();
	} else if(msgsEl.innerHTML =="") {
		msgsEl.innerHTML=this.createFrameForMessages();
	}
	
//	this.totalMessages();
	var msPanel = document.getElementById("msPanel");
	
	msPanel.appendChild(msgEl);
	
	eXo.webui.UINotification.slidedownup(currBoxId, this.deleteBox);
}

eXo.webui.UINotification = new UINotification();