UICalendar = function(calendarId) {
	this.calendarId = calendarId ;
  this.dateField = null ;
  this.currentDate = null ;
  this.selectedDate = null ;
  this.months = ['January','February','March','April','May','June','July','August','September','October','November','December'] ;
}

UICalendar.prototype.init = function(field, isDisplayTime) {
	this.isDisplayTime = isDisplayTime ;
	if (this.dateField) {
		this.dateField.parentNode.style.position = '' ;
	}
	this.dateField = field ;
	if (!document.getElementById(this.calendarId)) this.create() ;
//	field.parentNode.style.position = 'relative' ;
  field.parentNode.insertBefore(document.getElementById(this.calendarId), field) ;
  this.show() ;
}

UICalendar.prototype.create = function() {
	var clndr = document.createElement("div") ;
	clndr.id = this.calendarId ;
	clndr.style.position = "absolute" ;
	if (document.all) {
		clndr.innerHTML = "<div class='UICalendarComponent'><iframe id='" + this.calendarId + "IFrame' src='javascript:false;' frameBorder='0' scrolling='no'></iframe><div style='position: absolute'></div></div>" ;
	} else {
		clndr.innerHTML = "<div class='UICalendarComponent'><div style='position: absolute; width: 100%;'></div></div>" ;
	}
	document.body.appendChild(clndr) ;
}

UICalendar.prototype.show = function() {
	document.onmousedown = new Function('eXo.webui.UICalendar.hide()') ;
	var re = /^(\d{1,2}\/\d{1,2}\/\d{1,4})\s*(\s+\d{1,2}:\d{1,2}:\d{1,2})?$/i ;
  this.selectedDate = new Date() ;
	if (re.test(this.dateField.value)) {
	  var dateParts = this.dateField.value.split(" ") ;
	  var arr = dateParts[0].split("/") ;
	  this.selectedDate.setDate(parseInt(arr[1],10)) ;
	  this.selectedDate.setMonth(parseInt(arr[0],10) - 1) ;
	  this.selectedDate.setFullYear(parseInt(arr[2],10)) ;
	  if (dateParts.length > 1 && dateParts[dateParts.length - 1] != "") {
	  	arr = dateParts[dateParts.length - 1].split(":") ;
	  	this.selectedDate.setHours(arr[0], 10) ;
	  	this.selectedDate.setMinutes(arr[1], 10) ;
	  	this.selectedDate.setSeconds(arr[2], 10) ;
	  }
	}
	this.currentDate = new Date(this.selectedDate.valueOf()) ;

  var clndr = document.getElementById(this.calendarId) ;
  clndr.firstChild.lastChild.innerHTML = this.renderCalendar() ;
  var x = 0 ;
  var y = this.dateField.offsetHeight ;
  with (clndr.firstChild.style) {
  	display = 'block' ;
	  left = x + "px" ;
	  top = y + "px" ;
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
										
	var table =	'<div class="UICalendar" onmousedown="event.cancelBubble = true">' ;
	table += 		'	<table class="MonthYearBox">' ;
	table += 		'	  <tr>' ;
	table += 		'			<td class="MonthButton"><a class="PreviousMonth" href="javascript:eXo.webui.UICalendar.changeMonth(-1);"></a></td>' ;
	table += 		'			<td class="YearButton"><a class="PreviousYear" href="javascript:eXo.webui.UICalendar.changeYear(-1);"></a></td>' ;
	table += 		'			<td><font color="#f89302">' + this.months[this.currentDate.getMonth()] + '</font> - ' + this.currentDate.getFullYear() + '</td>' ;
	table += 		'			<td class="YearButton"><a class="NextYear" href="javascript:eXo.webui.UICalendar.changeYear(1);"></a></td>' ;
	table += 		'			<td class="MonthButton"><a class="NextMonth" href="javascript:eXo.webui.UICalendar.changeMonth(1);"></a></td>' ;
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
	table += 		'		<table>' ;
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
	table += 		'	<div class="CalendarTimeBox">' ;
	table += 		'		<div class="CalendarTimeBoxR">' ;
	table += 		'			<div class="CalendarTimeBoxM"><span><input size="2" maxlength="2" value="' + this.currentDate.getHours() + '">:<input size="2" maxlength="2" value="' + this.currentDate.getMinutes() + '">:<input size="2" maxlength="2" value="' + this.currentDate.getSeconds() + '"></span></div>' ;
	table += 		'		</div>' ;
	table += 		'	</div>' ;
	table += 		'</div>' ;
	return table ;
}

UICalendar.prototype.changeMonth = function(change) {
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
    var dateString = month + "/" + day + "/" + year ;
    if(this.isDisplayTime) dateString += " " + this.currentDate.getHours() + ":" + this.currentDate.getMinutes() + ":" + this.currentDate.getSeconds() ;
    this.dateField.value = dateString ;
    this.hide() ;
  }
  return ;
}

UICalendar.prototype.clearDate = function() {
  this.dateField.value = '' ;
  this.hide() ;
}

UICalendar.prototype.getDayOfWeek = function(year, month, day) {
  var date = new Date(year, month-1, day) ;
  return date.getDay() ;
}

UICalendar.prototype.getDaysInMonth = function(year, month) {
	return [31, ((!(year % 4 ) && ( (year % 100 ) || !( year % 400 ) ))? 29:28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month-1];
}

eXo.webui.UICalendar = new UICalendar('UICalendarControl') ;