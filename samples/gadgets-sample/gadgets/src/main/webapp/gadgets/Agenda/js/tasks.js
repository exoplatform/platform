function DateTimeFormater(){
};
DateTimeFormater.prototype.masks = {
	"default":      "ddd mmm dd yyyy HH:MM:ss",
	shortDate:      "m/d/yy",
	mediumDate:     "mmm d, yyyy",
	longDate:       "mmmm d, yyyy",
	fullDate:       "dddd, mmmm d, yyyy",
	shortTime:      "h:MM TT",
	mediumTime:     "h:MM:ss TT",
	longTime:       "h:MM:ss TT Z",
	isoDate:        "yyyy-mm-dd",
	isoTime:        "HH:MM:ss",
	isoDateTime:    "yyyy-mm-dd'T'HH:MM:ss",
	isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
};
DateTimeFormater.prototype.token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g;
DateTimeFormater.prototype.timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g;
DateTimeFormater.prototype.timezoneClip = /[^-+\dA-Z]/g;
DateTimeFormater.prototype.pad = function(val, len) {
	val = String(val);
	len = len || 2;
	while (val.length < len) val = "0" + val;
	return val;
};

DateTimeFormater.prototype.i18n = {
	dayNames: [
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
		"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
	],
	monthNames: [
		"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
		"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
	]
};

DateTimeFormater.prototype.format = function (date, mask, utc) {
	var dF = DateTimeFormater;

	// You can't provide utc if you skip other args (use the "UTC:" mask prefix)
	if (arguments.length == 1 && (typeof date == "string" || date instanceof String) && !/\d/.test(date)) {
		mask = date;
		date = undefined;
	}

	// Passing date through Date applies Date.parse, if necessary
	date = date ? new Date(date) : new Date();
	if (isNaN(date)) throw new SyntaxError("invalid date");

	mask = String(dF.masks[mask] || mask || dF.masks["default"]);

	// Allow setting the utc argument via the mask
	if (mask.slice(0, 4) == "UTC:") {
		mask = mask.slice(4);
		utc = true;
	}

	var	_ = utc ? "getUTC" : "get",
		d = date[_ + "Date"](),
		D = date[_ + "Day"](),
		m = date[_ + "Month"](),
		y = date[_ + "FullYear"](),
		H = date[_ + "Hours"](),
		M = date[_ + "Minutes"](),
		s = date[_ + "Seconds"](),
		L = date[_ + "Milliseconds"](),
		o = utc ? 0 : date.getTimezoneOffset(),
		flags = {
			d:    d,
			dd:   dF.pad(d),
			ddd:  dF.i18n.dayNames[D],
			dddd: dF.i18n.dayNames[D + 7],
			m:    m + 1,
			mm:   dF.pad(m + 1),
			mmm:  dF.i18n.monthNames[m],
			mmmm: dF.i18n.monthNames[m + 12],
			yy:   String(y).slice(2),
			yyyy: y,
			h:    H % 12 || 12,
			hh:   dF.pad(H % 12 || 12),
			H:    H,
			HH:   dF.pad(H),
			M:    M,
			MM:   dF.pad(M),
			s:    s,
			ss:   dF.pad(s),
			l:    dF.pad(L, 3),
			L:    dF.pad(L > 99 ? Math.round(L / 10) : L),
			t:    H < 12 ? "a"  : "p",
			tt:   H < 12 ? "am" : "pm",
			T:    H < 12 ? "A"  : "P",
			TT:   H < 12 ? "AM" : "PM",
			Z:    utc ? "UTC" : (String(date).match(dF.timezone) || [""]).pop().replace(dF.timezoneClip, ""),
			o:    (o > 0 ? "-" : "+") + dF.pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
			S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
		};

	return mask.replace(dF.token, function ($0) {
		return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
	});
};

DateTimeFormater = new DateTimeFormater();

function DOMUtil(){	
};

DOMUtil.prototype.findNextElementByTagName = function(element, tagName) {
	var nextElement = element.nextSibling ;
	if(!nextElement) return null;
	var nodeName = nextElement.nodeName.toLowerCase();
	if(nodeName != tagName) return null;
	return nextElement ;
} ;

DOMUtil = new DOMUtil();
function eXoEventGadget(){
};

eXoEventGadget.prototype.getPrefs = function(){
	var setting = (new gadgets.Prefs()).getString("setting");
	if(setting =="") {
	        setting = ["/calendar","/portal/rest/cs/calendar/events/personal","10","Default","0"];
	        }
	else {
		setting = setting.split(";");
	}
	var calendars = "";
	var calendarId = "";
	if ((setting[3] == "Default") && (defaultCalendar != "")) {
	        var myDefaultCalendar = defaultCalendar.split(";");
	        calendars = myDefaultCalendar[0];
	        calendarId = myDefaultCalendar[1];
	}
	else {
	        calendars = setting[3];
	        calendarId = setting[4];
	}
	this.prefs = {
		"url"  : setting[0],
		"subscribeurl"  : setting[1],
		"limit": setting[2],
		//"timeformat" : setting[3],
		"calendars"  : calendars,
		"calendarId" : calendarId		
	}
	return this.prefs;
}

//TODO: Need a new solution for creating url replace for using parent 
eXoEventGadget.prototype.setLink = function(){
	var url   = eXoEventGadget.prefs.url;
	baseUrl = "http://" +  top.location.host + parent.eXo.env.portal.context + "/intranet"; //+ parent.eXo.env.portal.portalName;
	a = document.getElementById("ShowAll");
	url = (url)?baseUrl + url: baseUrl + "/calendar";
	a.href = url;
	eXoEventGadget.adjustHeight();
	//a.href = "http://localhost:8080/portal/intranet/calendar";
}

eXoEventGadget.prototype.createRequestUrl = function(){
	var prefs = eXoEventGadget.getPrefs();
	var limit = (prefs.limit && (parseInt(prefs.limit) > 0))? prefs.limit:0;
	var subscribeurl = (prefs.subscribeurl)?prefs.subscribeurl: "/portal/rest/cs/calendar/events/personal" ;
	var today = new Date();
	var fiveDaysAfter = (new Date()).setDate(today.getDate()+7);
	subscribeurl += "/Task/" + prefs.calendarId + "/" + today.getTime() + "/" + fiveDaysAfter + "/" + limit;
	//var subscribeurl = (prefs.subscribeurl)?prefs.subscribeurl: "/portal/rest/cs/calendar/getissues" ;
	//subscribeurl +=  "/" + DateTimeFormater.format((new Date()),"yyyymmdd") + "/Task/" + limit ;
	//subscribeurl += "?rnd=" + (new Date()).getTime();
	return subscribeurl;
}

eXoEventGadget.prototype.createRequestUrlEvent = function(){
	var prefs = eXoEventGadget.getPrefs();
	var limit = (prefs.limit && (parseInt(prefs.limit) > 0))? prefs.limit:0;
	var subscribeurl = (prefs.subscribeurl)?prefs.subscribeurl: "/portal/rest/cs/calendar/events/personal";
	var today = new Date();
	var fiveDaysAfter = (new Date()).setDate(today.getDate()+7);
	subscribeurl += "/Event/" + prefs.calendarId + "/" + today.getTime() + "/" + fiveDaysAfter + "/" + limit;
	//var subscribeurl = (prefs.subscribeurl)?prefs.subscribeurl: "/portal/rest/private/cs/calendar/getissues" ;
	//subscribeurl +=  "/" + DateTimeFormater.format((new Date()),"yyyymmdd") + "/Event/" + limit ;
	//subscribeurl += "?rnd=" + (new Date()).getTime();
	return subscribeurl;
}

eXoEventGadget.prototype.getData = function(){					 
	var url = eXoEventGadget.createRequestUrl();					
	eXoEventGadget.ajaxAsyncGetRequest(url,eXoEventGadget.render);
	var urlEvent = eXoEventGadget.createRequestUrlEvent();					
	eXoEventGadget.ajaxAsyncGetRequestEvent(urlEvent,eXoEventGadget.renderEvent);
	if(typeof(requestInterval) == "undefined") requestInterval = setInterval(eXoEventGadget.getData,100000);
}

eXoEventGadget.prototype.getFullTime = function(dateObj) {
	var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        var month = monthNames[dateObj.getMonth()];
        var day = dateObj.getDate();
        //var year = dateObj.getFullYear();
        var hourNum = dateObj.getHours();
        var hour = (hourNum > 9) ? ("" + hourNum):("0" + hourNum);
        var minuteNum = dateObj.getMinutes();
        var minute = (minuteNum > 9) ? ("" + minuteNum):("0" + minuteNum);
        var fullDate = month + " " + day + " " + hour + ":" + minute;
        return fullDate;
}
				
eXoEventGadget.prototype.render =  function(data){
	var userTimezoneOffset = data.userTimezoneOffset;
	data = data.info;

	if(!data || data.length == 0){
		eXoEventGadget.notify();
		return;
	}
	var numberTask = 0;
	for (i=0;i<data.length;i++){
	        var item1 = data[i];
	        if (item1.eventState.indexOf("completed") == -1) numberTask++;
	}
	
        $("#numTask").html(" (" + numberTask + ")");
	var cont = document.getElementById("taskDiv");
	var prefs = eXoEventGadget.getPrefs();
	var gadgetPref = new gadgets.Prefs();
	//var timemask = "h:MM TT";
	var html = '';
	var len = (prefs.limit && (parseInt(prefs.limit) > 0) &&  (parseInt(prefs.limit) < data.length))? prefs.limit:data.length;
	//if(prefs.timeformat == "24h") timemask = "HH:MM";
	for(var i = 0 ; i < len; i++){	
		var status = "";
		var disable = "";
	        var item = data[i];
		var className = "TaskItem";
		if(item.eventState.indexOf("completed") != -1) {
			status = "checked";
			className += " TaskDone";
			//disable = "disabled";
		}
		var time = 0;
		if (userTimezoneOffset != null) time = parseInt(item.fromDateTime.time) + parseInt(userTimezoneOffset) + (new Date()).getTimezoneOffset()*60*1000;
		else time = parseInt(item.fromDateTime.time);
		var fullDate = eXoEventGadget.getFullTime(new Date(time));
		//time = DateTimeFormater.format(new Date(time),timemask);
		html += '<div class="CheckBox ' + className + '">';
		html += '<input type="checkbox" ' + status + ' name="checkbox" onclick="eXoEventGadget.doTask(this);" value="'+ item.id + '"></input>';
		html += '<label onclick="eXoEventGadget.showDetail(this);">' + fullDate +  '<span>'+ item.summary +'</span></label>';
		html += '</div>';
		if(item.description) html += '<div class="TaskDetail">' + item.description + '</div>';
	}		
  	cont.innerHTML = html;
	eXoEventGadget.setLink();
	eXoEventGadget.adjustHeight();
}

eXoEventGadget.prototype.renderEvent =  function(data){
	var userTimezoneOffset = data.userTimezoneOffset;
	data = data.info;
	
	if(!data || data.length == 0){
		eXoEventGadget.notifyEvent();
		return;
	}
	//var msg = gadgets.Prefs().getMsg("title");
	var numberEvent = data.length;
        $("#numEvent").html(" (" + numberEvent + ")");
  	var cont = document.getElementById("eventDiv");	
	var prefs = eXoEventGadget.getPrefs();
	var gadgetPref = new gadgets.Prefs();
	//var timemask = "h:MM TT";
  	var html = '';
	var len = (prefs.limit && (parseInt(prefs.limit) > 0) &&  (parseInt(prefs.limit) < data.length))? prefs.limit:data.length;
	//if(prefs.timeformat == "24h") timemask = "HH:MM";
  	for(var i = 0 ; i < len; i++){	
    	        var item = data[i];
		var time = 0;
		if (userTimezoneOffset != null) time = parseInt(item.fromDateTime.time) + parseInt(userTimezoneOffset) + (new Date()).getTimezoneOffset()*60*1000;
		else time = parseInt(item.fromDateTime.time);
		var fullDate = eXoEventGadget.getFullTime(new Date(time));
		//time = DateTimeFormater.format(new Date(time),timemask);
		html += '<a href="javascript:void(0);" class="IconLink" onclick="eXoEventGadget.showDetailEvent(this);">' + fullDate + '<span>'+ item.summary +'</span></a>';
		if(item.description) html += '<div class="EventDetail">' + item.description + '</div>';
  	}
  	html += '';
  	cont.innerHTML = html;
	eXoEventGadget.setLink();
	eXoEventGadget.adjustHeight();
}

eXoEventGadget.prototype.showDetail = function(obj){
	var detail = DOMUtil.findNextElementByTagName(obj.parentNode,"div");
	if(!detail) return;
	var condition = this.lastShowItem && (this.lastShowItem != detail) && (this.lastShowItem.style.display == "block"); 
	if(condition) this.lastShowItem.style.display = "none";
	if(detail.style.display == "block") detail.style.display = "none";
	else detail.style.display = "block";
	this.lastShowItem = detail;
	eXoEventGadget.adjustHeight();
}

eXoEventGadget.prototype.showDetailEvent = function(obj){
	var detail = DOMUtil.findNextElementByTagName(obj,"div");
	if(!detail) return;
	var condition = this.lastShowItem && (this.lastShowItem != detail) && (this.lastShowItem.style.display == "block"); 
	if(condition) {
	this.lastShowItem.style.display = "none";
	this.lastShowLink.style.background = "url('/exo-gadget-resources/skin/exo-gadget/images/IconLink.gif') no-repeat left 4px";
	}
	if(detail.style.display == "block") {
	        detail.style.display = "none";
	        obj.style.background = "url('/exo-gadget-resources/skin/exo-gadget/images/IconLink.gif') no-repeat left 4px";
	}
	else {
	        detail.style.display = "block";
	        obj.style.background = "url('/exo-gadget-resources/skin/exo-gadget/images/DownIconLink.gif') no-repeat left 4px";
	}
	this.lastShowItem = detail;
	this.lastShowLink = obj;
	eXoEventGadget.adjustHeight();
}

eXoEventGadget.prototype.onLoadHander = function(){
	eXoEventGadget.initiate();
	eXoEventGadget.getPrefs();
	eXoEventGadget.getCalendars();
	eXoEventGadget.trigger();
	eXoEventGadget.adjustHeight();
}

eXoEventGadget.prototype.initiate = function(){
	var url = "http://" +  top.location.host + parent.eXo.env.portal.context + "/rest/cs/calendar/getcalendars";
	eXoEventGadget.ajaxAsyncGetRequest(url,eXoEventGadget.getDefaultCalendar);
}

eXoEventGadget.prototype.getDefaultCalendar = function(data){
	defaultCalendar = data.calendars[0].name + ";" + data.calendars[0].id;
}

eXoEventGadget.prototype.ajaxAsyncGetRequest = function(url, callback) {
	
	var request =  parent.eXo.core.Browser.createHttpRequest() ;
	request.open('GET', url, true) ;
	request.setRequestHeader("Cache-Control", "max-age=86400") ;
	request.send(null) ;
	if(!callback) return;
	request.onreadystatechange = function(){
		if (request.readyState == 4) {
			if (request.status == 200) {
				var data = gadgets.json.parse(request.responseText);
				callback(data);
			}
			//IE treats a 204 success response status as 1223. This is very annoying
			if (request.status == 404  || request.status == 204  || request.status == 1223) {
				eXoEventGadget.notify();
	  	}
		}
	}					
}

eXoEventGadget.prototype.ajaxAsyncGetRequestEvent = function(url, callback) {
	
  var request =  parent.eXo.core.Browser.createHttpRequest() ;
  request.open('GET', url, true) ;
  request.setRequestHeader("Cache-Control", "max-age=86400") ;
  request.send(null) ;
	request.onreadystatechange = function(){
		if (request.readyState == 4) {
			if (request.status == 200) {
				var data = gadgets.json.parse(request.responseText);
				callback(data);
			}
			//IE treats a 204 success response status as 1223. This is very annoying
			if (request.status == 404  || request.status == 204  || request.status == 1223) {
				eXoEventGadget.notifyEvent();
	  	}
		}
	}					
}

eXoEventGadget.prototype.doTask = function(obj){
	var taskitem = obj.parentNode;
	var statusid;
	if (!obj.checked) statusid = 1;
	else statusid = 3;
	var url = eXoEventGadget.createRequestUrl();
	url = url.replace(/calendar.*$/ig,"calendar/updatestatus/"+obj.value + "?statusid=" + statusid);
	eXoEventGadget.ajaxAsyncGetRequest(url);
	eXoEventGadget.swapClass(taskitem);
	eXoEventGadget.updateTaskNum(statusid);	        

}

eXoEventGadget.prototype.updateTaskNum = function(statusid) {
	var str = $("#numTask").html();
	var numberTask = str.substring(2,3);
	if (statusid != 3) numberTask = parseInt(numberTask) + 1;
	else numberTask = parseInt(numberTask) - 1;
        $("#numTask").html(" (" + numberTask + ")");					
}

eXoEventGadget.prototype.swapClass = function(obj){
	var className = obj.className;
	if (className.indexOf(" TaskDone") != -1) className = className.replace(" TaskDone","");
	else className += " TaskDone";
	obj.className = className;
}

eXoEventGadget.prototype.notify = function(){
	var msg = gadgets.Prefs().getMsg("notask");
	//var msg2 = gadgets.Prefs().getMsg("titleTask");
	document.getElementById("taskDiv").innerHTML = '<div class="light_message" style="margin-left: 5px">' + msg + '</div>';
        $("#numTask").html(" (0)");
	eXoEventGadget.setLink();
}

eXoEventGadget.prototype.notifyEvent = function(){
	var msg = gadgets.Prefs().getMsg("noevent");
	//var msg2 = gadgets.Prefs().getMsg("title");
	document.getElementById("eventDiv").innerHTML = '<div class="light_message" style="margin-left: 5px">' + msg + '</div>';
        $("#numEvent").html(" (0)");
	eXoEventGadget.setLink();
}


eXoEventGadget.prototype.getCalendars = function(){
	var url = eXoEventGadget.createRequestUrl();
	url = url.replace(/calendar.*$/ig,"calendar/getcalendars/");
	eXoEventGadget.ajaxAsyncGetRequest(url,eXoEventGadget.write2Setting);
	if(typeof(requestInterval) == "undefined") requestInterval = setInterval(eXoEventGadget.getCalendars,100000);
}

eXoEventGadget.prototype.write2Setting = function(data){
	var frmSetting = document.getElementById("Setting");
	var html = "";
	var calendarName = "";
	for(var i=0,len = data.calendars.length; i < len;i++){
	        if(data.calendars[i].name.indexOf("default") != -1) calendarName = gadgets.Prefs().getMsg("default");
	        else calendarName = data.calendars[i].name;
		html += '<option value="' + data.calendars[i].id + '">' + calendarName + '</option>';
	}
	frmSetting["calendars"].innerHTML = html;
	eXoEventGadget.getData();
}

/*eXoEventGadget.prototype.convertCalendar = function(data){
	var arr = new Array();
	var len = data.length;
	for(var i = 0; i < len; i++){
		arr.push({"name":data[i].name,"id":data[i].calendarId});
	}
	return arr;
}*/

eXoEventGadget.prototype.showHideSetting = function(isShow){
	var frmSetting = document.getElementById("Setting");
	var display = "";
	if(isShow) {
		eXoEventGadget.loadSetting();
		display = "block";
	}	else display = "none";
	frmSetting.style.display = display;
	eXoEventGadget.adjustHeight();
}

eXoEventGadget.prototype.saveSetting = function(){
	var prefs = new gadgets.Prefs();
	var frmSetting = document.getElementById("Setting");
	var setting = eXoEventGadget.createSetting(frmSetting);
	prefs.set("setting",setting);
	frmSetting.style.display = "none";
	eXoEventGadget.getData();
	eXoEventGadget.adjustHeight();
	//return false;
}

eXoEventGadget.prototype.createSetting = function(frmSetting){
	var setting = "";
	setting += "/calendar;";
	setting += "/portal/rest/cs/calendar/events/personal;";
	setting += frmSetting["limit"].value + ";";
	//setting += frmSetting["timeformat"].options[frmSetting["timeformat"].selectedIndex].text + ";";
	setting += frmSetting["calendars"].options[frmSetting["calendars"].selectedIndex].text + ";";
	setting += frmSetting["calendars"].options[frmSetting["calendars"].selectedIndex].value;
	return setting;
}

eXoEventGadget.prototype.loadSetting = function(){
	var frmSetting = document.getElementById("Setting");
	//frmSetting["url"].value = eXoEventGadget.prefs.url;
	//frmSetting["subscribeurl"].value = eXoEventGadget.prefs.subscribeurl;
	frmSetting["limit"].value = eXoEventGadget.prefs.limit;
	//eXoEventGadget.selectedValue(frmSetting["timeformat"],eXoEventGadget.prefs.timeformat);
	eXoEventGadget.selectedValue(frmSetting["calendars"],eXoEventGadget.prefs.calendars);
}

eXoEventGadget.prototype.selectedValue = function(selectbox,value){
	for(var i = 0, len = selectbox.options.length; i < len; i++){
		if(value == selectbox.options[i].text) selectbox.selectedIndex = i;
	}
}

eXoEventGadget.prototype.trigger = function(){
  this.moreButton = document.getElementById("ShowAll");
  this.settingButton = document.getElementById("SettingButton");
  this.hiddenTimeout = null;
  this.moreButton.onmouseover = this.moveOver;
  this.moreButton.onmouseout = this.moveOut;
  this.settingButton.onmouseover = this.moveOver;
  this.settingButton.onmouseout = this.moveOut;
}
eXoEventGadget.prototype.moveOver = function(){
  if(eXoEventGadget.hiddenTimeout) window.clearTimeout(eXoEventGadget.hiddenTimeout);
}
eXoEventGadget.prototype.moveOut = function(){
  eXoEventGadget.hiddenTimeout = window.setTimeout(function(){
  },200);
}

eXoEventGadget.prototype.adjustHeight = function(){
	setTimeout(function(){
	gadgets.window.adjustHeight($("#agenda-gadget").get(0).offsetHeight);		
	},500);
}

eXoEventGadget =  new eXoEventGadget();

gadgets.util.registerOnLoadHandler(eXoEventGadget.onLoadHander);
