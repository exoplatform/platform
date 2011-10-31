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
function eXoLastpostGadget(){	
} ;

eXoLastpostGadget.prototype.getPrefs = function(){
	var prefs = new gadgets.Prefs();
	var url = prefs.getString("url") || "/portal/rest/ks/forum/getmessage";
	var baseurl = prefs.getString("baseurl") || "/forum";
	var limit = prefs.getString("limit") || 5;	
	this.prefs = {
		"url"  : url,
		"baseurl"  : baseurl,
		"limit": limit
	}
	return this.prefs;
}

//TODO: Need a new solution for creating url replace for using parent 
eXoLastpostGadget.prototype.setLink = function(){
	var host = "http://" +  top.location.host + parent.eXo.env.portal.context + "/" + parent.eXo.env.portal.accessMode + "/" + parent.eXo.env.portal.portalName;
	var baseurl   = eXoLastpostGadget.prefs.baseurl;
	var url = (baseurl)?host + baseurl: host + "/forum";
	var a = document.getElementById("ShowAll");
	a.href = url;
}
eXoLastpostGadget.prototype.createRequestUrl = function(){
	var prefs = eXoLastpostGadget.prefs || eXoLastpostGadget.getPrefs();
	var url = prefs.url + "/" + prefs.limit;
	return url;
}
eXoLastpostGadget.prototype.getData = function(){					 
	var url = eXoLastpostGadget.createRequestUrl();					
	eXoLastpostGadget.ajaxAsyncGetRequest(url,eXoLastpostGadget.render);
	if(typeof(requestInterval) == "undefined") requestInterval = setInterval(eXoLastpostGadget.getData,300000);
}				
eXoLastpostGadget.prototype.render =  function(data){
	data = data.data;
	if(!data || data.length == 0){
		eXoLastpostGadget.notify();
		return;
	}
	var cont = document.getElementById("ItemContainer");
	var prefs = eXoLastpostGadget.prefs || eXoLastpostGadget.getPrefs();
	var gadgetPref = new gadgets.Prefs();
	
	var html = '';
	var len = (prefs.limit && (parseInt(prefs.limit) > 0) &&  (parseInt(prefs.limit) < data.length))? prefs.limit:data.length;
	
	for(var i = 0 ; i < len; i++){	
	  var item = data[i];
		var time = time = DateTimeFormater.format(new Date(item.createdDate.time));
		html += '<div class="PostItem">';
		html += '<a href="' + item.link + '" target="_blank">'+ eXoLastpostGadget.truncateMessage(item.message,100) +'</a>';
		html += '</div>';
		html += '<div class="DateTime">' + time + '</div>';
	}		
  cont.innerHTML = html;
	eXoLastpostGadget.setLink();
	gadgets.window.adjustHeight();
}

eXoLastpostGadget.prototype.cleanString = function(str){
	str = str.replace(/(<([^>]+)>)/ig,"");
	var start = str.lastIndexOf("QUOTE]");
	if(start < 0) return str;	
	var end = str.length;
	if(start == (end - 6)){ 
		start = 0;
		end = str.indexOf("[QUOTE");
		return str.substring(start,end);
	}
	str = str.substring(start + 6,end);
	return str;
}

eXoLastpostGadget.prototype.truncateMessage = function(msg,limit){
	msg = eXoLastpostGadget.cleanString(msg);
	if(msg.length < limit) return msg;
	limit++;
	var tmpMsg = msg.substring(0,limit);
	var lastSpaceChar = tmpMsg.lastIndexOf(" ");
	if(lastSpaceChar < limit) {
		tmpMsg = tmpMsg.substring(0,lastSpaceChar);
	}
	tmpMsg += "...";
	return tmpMsg;
}

eXoLastpostGadget.prototype.onLoadHander = function(){
	eXoLastpostGadget.getPrefs();
	eXoLastpostGadget.getData();
}
eXoLastpostGadget.prototype.ajaxAsyncGetRequest = function(url, callback) {
	/* User when get this gadget by gedget service.
	var params = {};  
	params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.SIGNED;
	params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.GET;
	params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
  	gadgets.io.makeRequest(url, callback, params);
  	return;
	 */
	var request =  parent.eXo.core.Browser.createHttpRequest() ;
	request.open('GET', url, true) ;
	request.setRequestHeader("Cache-Control", "max-age=86400") ;
	request.send(null) ;
	if(!callback) return;
	request.onreadystatechange = function(){
		if(request.readyState == 4 && (request.status == 200 || request.status == 204)){
			callback(gadgets.json.parse(request.responseText));
		}
	}					
}

eXoLastpostGadget.prototype.notify = function(){
	var msg = gadgets.Prefs().getMsg("noevent");
	document.getElementById("ItemContainer").innerHTML = '<div class="Warning">' + msg + '</div>';
	eXoLastpostGadget.setLink();
}

eXoLastpostGadget =  new eXoLastpostGadget();

gadgets.util.registerOnLoadHandler(eXoLastpostGadget.onLoadHander);		
