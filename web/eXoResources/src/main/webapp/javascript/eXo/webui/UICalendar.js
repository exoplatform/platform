UICalendar = function(calendarId) {
	this.calendarId = calendarId ;
  this.dateField = null ;
  this.datePattern = null;
  this.value = null;
  this.currentDate = null ; 	// Datetime value base of selectedDate for displaying calendar below
  														// if selectedDate is invalid, currentDate deals with system time;
  this.selectedDate = null ; //Datetime value of input date&time field
  this.months ;
}

UICalendar.prototype.init = function(field, isDisplayTime, datePattern, value, monthNames) {
	this.isDisplayTime = isDisplayTime ;
	
	if (this.dateField) {
		this.dateField.parentNode.style.position = '' ;
	}
	this.dateField = field ;
	this.datePattern = datePattern;
	this.value = value;
	
	this.months = new Array();
	this.months = monthNames.split(',');
	this.months.pop();
	
	if (!document.getElementById(this.calendarId)) this.create();
  this.show() ;

	// fix bug for IE 6
  var cld = document.getElementById(this.calendarId);
  if(eXo.core.Browser.isIE6())  {
    var blockClnd = document.getElementById('BlockCaledar') ;
    var iframe = document.getElementById(this.calendarId + 'IFrame') ;
    iframe.style.height = blockClnd.offsetHeight + "px";
  }
  field.parentNode.insertBefore(cld, field) ;
}

UICalendar.prototype.create = function() {
	var clndr = document.createElement("div") ;
	clndr.id = this.calendarId ;
	clndr.style.position = "absolute";
  if (eXo.core.Browser.isIE6()) {
		clndr.innerHTML = "<div class='UICalendarComponent'><iframe id='" + this.calendarId + "IFrame' frameBorder='0' style='position:absolute;height:100%;' scrolling='no'></iframe><div style='position:absolute;'></div></div>" ;
	} else {
		clndr.innerHTML = "<div class='UICalendarComponent'><div style='position: absolute; width: 100%;'></div></div>" ;
	}
	document.body.appendChild(clndr) ;
}

UICalendar.prototype.show = function() {
	document.onmousedown = new Function('eXo.webui.UICalendar.hide()') ;
	
	var re = /^(\d{1,2}\/\d{1,2}\/\d{1,4})\s*(\s+\d{1,2}:\d{1,2}:\d{1,2})?$/i ;
  this.selectedDate = new Date() ;


//	if (re.test(this.dateField.value)) {
//	  var dateParts = this.dateField.value.split(" ") ;
//	  var arr = dateParts[0].split("/") ;
//	  this.selectedDate.setMonth(parseInt(arr[0],10) - 1) ;
//	  this.selectedDate.setDate(parseInt(arr[1],10)) ;
//	  this.selectedDate.setFullYear(parseInt(arr[2],10)) ;
//	  if (dateParts.length > 1 && dateParts[dateParts.length - 1] != "") {
//	  	arr = dateParts[dateParts.length - 1].split(":") ;
//	  	this.selectedDate.setHours(arr[0], 10) ;
//	  	this.selectedDate.setMinutes(arr[1], 10) ;
//	  	this.selectedDate.setSeconds(arr[2], 10) ;
//	  }
//	}
	if (this.dateField.value != '') {
		// TODO: tamnd - set selected date to calendar
		var dateFieldValue = this.dateField.value;
		var dateIndex = this.datePattern.indexOf("dd");
		this.selectedDate.setDate(parseInt(dateFieldValue.substring(dateIndex,dateIndex+2),10)) ; 
		
		var monthIndex = this.datePattern.indexOf("MM");
		this.selectedDate.setMonth(parseInt(dateFieldValue.substring(monthIndex,monthIndex+2) - 1,10)) ;
		
		var yearIndex = this.datePattern.indexOf("yyyy");
		this.selectedDate.setFullYear(parseInt(dateFieldValue.substring(yearIndex,yearIndex+4),10)) ;
		
		var hourIndex = this.datePattern.indexOf("hh");
		this.selectedDate.setHours(parseInt(dateFieldValue.substring(hourIndex,hourIndex+2),10)) ;
		
		var minuteIndex = this.datePattern.indexOf("mm");
		this.selectedDate.setMinutes(parseInt(dateFieldValue.substring(minuteIndex,minuteIndex+2),10)) ;
		
		var secondIndex = this.datePattern.indexOf("ss");
		this.selectedDate.setSeconds(parseInt(dateFieldValue.substring(secondIndex,secondIndex+2),10)) ;
	}
	this.currentDate = new Date(this.selectedDate.valueOf()) ;
  var clndr = document.getElementById(this.calendarId) ;
  clndr.firstChild.lastChild.innerHTML = this.renderCalendar() ;
//  var x = 0 ;
  var y = this.dateField.offsetHeight ;
  var beforeShow = eXo.core.Browser.getBrowserHeight();
  with (clndr.firstChild.style) {
  	display = 'block' ;
//	  left = x + "px" ;
	  top = y + "px" ;
	  if(eXo.core.I18n.isLT()) left = "0px";
	  else right = "0px";
  }
  var posCal = eXo.core.Browser.findPosY(this.dateField) - y;
  var heightCal = document.getElementById('BlockCaledar');
  var afterShow = posCal+heightCal.offsetHeight;
  if(afterShow > beforeShow)	 {
    clndr.firstChild.style.top = -heightCal.offsetHeight + 'px';
  }
	
	var drag = document.getElementById("BlockCaledar");
	var component =  eXo.core.DOMUtil.findAncestorByClass(drag, "UICalendarComponent");
	var calendar = eXo.core.DOMUtil.findFirstChildByClass(drag, "div", "UICalendar");
	var innerWidth = drag.offsetWidth;
	drag.onmousedown = function(evt) {
		var event = evt || window.event;
		event.cancelBubble = true;
		drag.style.position = "absolute";
		if(eXo.core.Browser.isIE7()) drag.style.height = calendar.offsetHeight + "px";
		drag.style.width = innerWidth + "px";
		eXo.core.DragDrop.init(null, drag, component, event);
 	}
	
	//
	var primary = eXo.core.DOMUtil.findAncestorById(this.dateField, "UIECMSearch");
	if (primary && eXo.core.Browser.isFF()) {
			calendar = clndr.firstChild;
			calendar.style.top = "0px";
			calendar.style.left = this.dateField.offsetLeft - this.dateField.offsetWidth - 32 + "px";
	}
}

UICalendar.prototype.hide = function() {
  if (this.dateField) {
    document.getElementById(this.calendarId).firstChild.style.display = 'none' ;
//		this.dateField.parentNode.style.position = '' ;
    this.dateField = null ;
  }
 	document.onmousedown = null ;
}

/* TODO: Move HTML code to a javascript template file (.jstmpl) */
UICalendar.prototype.renderCalendar = function() {
  var dayOfMonth = 1 ;
  var validDay = 0 ;
  var startDayOfWeek = this.getDayOfWeek(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, dayOfMonth) ;
  var daysInMonth = this.getDaysInMonth(this.currentDate.getFullYear(), this.currentDate.getMonth()) ;
  var clazz = null;
	var table = '<div id="BlockCaledar" class="BlockCalendar">' ;
	table += 		'<div class="UICalendar" onmousedown="event.cancelBubble = true">' ;
	table += 		'	<table class="MonthYearBox">' ;
	table += 		'	  <tr>' ;
	table += 		'			<td class="MonthButton"><a class="PreviousMonth" href="javascript:eXo.webui.UICalendar.changeMonth(-1);" title="Previous Month"></a></td>' ;
	table += 		'			<td class="YearButton"><a class="PreviousYear" href="javascript:eXo.webui.UICalendar.changeYear(-1);" title="Previous Year"></a></td>' ;
	table += 		'			<td><font color="#f89302">' + this.months[this.currentDate.getMonth()] + '</font> - ' + this.currentDate.getFullYear() + '</td>' ;
	table += 		'			<td class="YearButton"><a class="NextYear" href="javascript:eXo.webui.UICalendar.changeYear(1);" title="Next Year"></a></td>' ;
	table += 		'			<td class="MonthButton"><a class="NextMonth" href="javascript:eXo.webui.UICalendar.changeMonth(1);" title="Next Month"></a></td>' ;
	table += 		'		</tr>' ;
	table += 		'	</table>' ;
	table += 		'	<div style="margin-top: 6px;padding: 0px 5px;">' ;
	table += 		'		<table>' ;
	table += 		'			<tr>' ;
	table += 		'				<td><font color="red">S</font></td><td>M</td><td>T</td><td>W</td><td>T</td><td>F</td><td>S</td>' ;
	table += 		'			</tr>' ;
	table += 		'		</table>' ;
	table += 		'	</div>' ;
	table += 		'	<div class="CalendarGrid">' ;
	table += 		'	<table>' ;
  for (var week=0; week < 6; week++) {
    table += "<tr>";
    for (var dayOfWeek=0; dayOfWeek < 7; dayOfWeek++) {
      if (week == 0 && startDayOfWeek == dayOfWeek) {
        validDay = 1;
      } else if (validDay == 1 && dayOfMonth > daysInMonth) {
        validDay = 0;
      }
      if (validDay) {
        if (dayOfMonth == this.selectedDate.getDate() && this.currentDate.getFullYear() == this.selectedDate.getFullYear() && this.currentDate.getMonth() == this.selectedDate.getMonth()) {
          clazz = 'Current';
        } else if (dayOfWeek == 0 || dayOfWeek == 6) {
          clazz = 'Weekend';
        } else {
          clazz = 'Weekday';
        }

        table = table + "<td><a class='"+clazz+"' href=\"javascript:eXo.webui.UICalendar.setDate("+this.currentDate.getFullYear()+","+(this.currentDate.getMonth() + 1)+","+dayOfMonth+")\">"+dayOfMonth+"</a></td>" ;
        dayOfMonth++ ;
      } else {
        table = table + "<td class='empty'><div>&nbsp;</div></td>" ;
      }
    }
    table += "</tr>" ;
  }		
	table += 		'		</table>' ;
	table += 		'	</div>' ;
	if (this.isDisplayTime) {
		table += 		'	<div class="CalendarTimeBox">' ;
		table += 		'		<div class="CalendarTimeBoxR">' ;
		table += 		'			<div class="CalendarTimeBoxM"><span><input class="InputTime" size="2" maxlength="2" value="' +
								((this.currentDate.getHours())>9 ? this.currentDate.getHours() : "0"+this.currentDate.getHours()) + 
								'" onkeyup="eXo.webui.UICalendar.setHour(this)" >:<input size="2" class="InputTime" maxlength="2" value="' + 
								((this.currentDate.getMinutes())>9 ? this.currentDate.getMinutes() : "0"+this.currentDate.getMinutes()) + 
								'" onkeyup = "eXo.webui.UICalendar.setMinus(this)">:<input size="2" class="InputTime" maxlength="2" value="' + 
								((this.currentDate.getSeconds())>9 ? this.currentDate.getSeconds() : "0"+this.currentDate.getSeconds()) + 
								'" onkeyup = "eXo.webui.UICalendar.setSeconds(this)"></span></div>' ;
		table += 		'		</div>' ;
		table += 		'	</div>' ;
	}
	table += 		'</div>' ;
	table += 		'</div>' ;
	return table ;
}

UICalendar.prototype.changeMonth = function(change) {
	this.currentDate.setDate(1);
	this.currentDate.setMonth(this.currentDate.getMonth() + change) ;
  var clndr = document.getElementById(this.calendarId) ;
  clndr.firstChild.lastChild.innerHTML = this.renderCalendar() ;
}

UICalendar.prototype.changeYear = function(change) {
  this.currentDate.setFullYear(this.currentDate.getFullYear() + change) ;
  this.currentDay = 0 ;
  var clndr = document.getElementById(this.calendarId) ;
  clndr.firstChild.lastChild.innerHTML = this.renderCalendar() ;
}

UICalendar.prototype.setDate = function(year, month, day) {	
  if (this.dateField) {
    if (month < 10) month = "0" + month ;
    if (day < 10) day = "0" + day ;
    var dateString = this.datePattern ;
    dateString = dateString.replace("dd",day);
    dateString = dateString.replace("MM",month);
    dateString = dateString.replace("yyyy",year);

    this.currentHours = this.currentDate.getHours() ;
    this.currentMinutes = this.currentDate.getMinutes() ;
    this.currentSeconds = this.currentDate.getSeconds() ;
    if(this.isDisplayTime) {
    	if(typeof(this.currentHours) != "string") hour = this.currentHours.toString() ;
			if(typeof(this.currentMinutes) != "string") minute = this.currentMinutes.toString() ;
			if(typeof(this.currentSeconds) != "year") second = this.currentSeconds.toString() ;
			
			while(hour.length < 2) { hour = "0" + hour ; }
			while(minute.length < 2) { minute = "0" + minute ; }
			while(second.length < 2) { second = "0" + second ; }
	
    	dateString = dateString.replace("hh",hour);
    	dateString = dateString.replace("mm",minute);
    	dateString = dateString.replace("ss",second);
    	//dateString += " " + this.makeTimeString(this.currentHours, this.currentMinutes, this.currentSeconds );
    }
    this.dateField.value = dateString ;
    this.hide() ;
  }
  return ;
}

UICalendar.prototype.setSeconds = function(object) {
		if(this.dateField) {
			var seconds = object.value;
			if (seconds >= 60) {
				object.value = seconds.substring(0,1);
				return;
			}
//			this.currentHours = this.currentDate.getHours() ;
//    	this.currentMinutes = this.currentDate.getMinutes() ;
			if(seconds.length < 2) seconds = "0" + seconds;
			var timeString = this.makeTimeString(this.currentDate.getHours(), this.currentDate.getMinutes(), seconds);
			this.currentDate.setSeconds(seconds);
			if(!this.currentDay) this.currentDay = this.currentDate.getDate();
			if(!this.currentMonth) this.currentMonth = this.currentDate.getMonth() + 1;
			if(!this.currentYear) this.currentYear = this.currentDate.getFullYear();
			if(this.isDisplayTime) timeString = this.currentMonth + "/" + this.currentDay + "/" + this.currentYear + " " + timeString;
			this.dateField.value = timeString;
	}
	return;
}

UICalendar.prototype.setMinus = function(object) {
		if(this.dateField) {
			var minus = object.value;
			if(minus >= 60){
				object.value = minus.substring(0,1);
				return;
			}
//			this.currentHours = this.currentDate.getHours() ;
// 			this.currentSeconds = this.currentDate.getSeconds() ;
			if(minus.length < 2) minus = "0" + minus;
			this.currentDate.setMinutes(minus);
			var timeString = this.makeTimeString(this.currentDate.getHours(), minus, this.currentDate.getSeconds());
			if(!this.currentDay) this.currentDay = this.currentDate.getDate();
			if(!this.currentMonth) this.currentMonth = this.currentDate.getMonth() + 1;
			if(!this.currentYear) this.currentYear = this.currentDate.getFullYear();
			if(this.isDisplayTime) timeString = this.currentMonth + "/" + this.currentDay + "/" + this.currentYear + " " + timeString;
			this.dateField.value = timeString;
	}
	return;
}

UICalendar.prototype.setHour = function(object) {
		if(this.dateField) {
			var hour = object.value;
			if (hour >= 24){
				object.value = hour.substring(0,1);	
				return;
			}
//			this.currentMinutes = this.currentDate.getMinutes() ;
//    	this.currentSeconds = this.currentDate.getSeconds() ;
			if(hour.length < 2) hour = "0" + hour;
			this.currentDate.setHours(hour);
			var timeString = this.makeTimeString(hour, this.currentDate.getMinutes(), this.currentDate.getSeconds());
			if(!this.currentDay) this.currentDay = this.currentDate.getDate();
			if(!this.currentMonth) this.currentMonth = this.currentDate.getMonth() + 1;
			if(!this.currentYear) this.currentYear = this.currentDate.getFullYear();
			if(this.isDisplayTime) timeString = this.currentMonth + "/" + this.currentDay + "/" + this.currentYear + " " + timeString;
			this.dateField.value = timeString;
	}
	return;
}

UICalendar.prototype.makeTimeString = function(hour, minute, second, time) {
	if(typeof(hour) != "string") hour = hour.toString() ;
	if(typeof(minute) != "string") minute = minute.toString() ;
	if(typeof(second) != "year") second = second.toString() ;
	while(hour.length < 2) { hour = "0" + hour ; }
	while(minute.length < 2) { minute = "0" + minute ; }
	while(second.length < 2) { second = "0" + second ; }
	return hour + ":" + minute + ":" + second;
}

UICalendar.prototype.clearDate = function() {
  this.dateField.value = '' ;
  this.hide() ;
}

UICalendar.prototype.getDayOfWeek = function(year, month, day) {
  var date = new Date(year, month - 1, day) ;
  return date.getDay() ;
}

UICalendar.prototype.getDaysInMonth = function(year, month) {
	return [31, ((!(year % 4 ) && ( (year % 100 ) || !( year % 400 ) ))? 29:28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
}

//UICalendar.prototype.getChangedTime = function(input, type) {
//	var time = input.value ;
//	if (isNaN(time)) {
//		return ; 
//	}
//	if (type == 'h') this.currentHours = time ;
//	else if (type == 'm') this.currentMinutes = time ;
//	else if (type == 's') this.currentSeconds = time ;
//}

eXo.webui.UICalendar = new UICalendar('UICalendarControl') ;