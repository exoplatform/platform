UICalendar = function(calendarId) {
	if(!document.getElementById(calendarId)) {
		var clndr = document.createElement("DIV") ;
		clndr.id = calendarId ;
		clndr.className = 'UICalendar' ;
		clndr.innerHTML = "<iframe id='UICalendarControlIFrame' src='javascript:false;' frameBorder='1' scrolling='no'></iframe><div style='position: absolute'></div>"
		document.body.appendChild(clndr) ;
	}
	this.calendarId = calendarId;
  this.dateField = null;
  
  this.currentDate = null;
  this.selectedDate = null;
  
  this.months = ['January','February','March','April','May','June','July','August','September','October','November','December'];
}

UICalendar.prototype.show = function(field) {
	document.onmousedown = new Function('eXo.webui.UICalendar.hide()') ;
  this.dateField = field;
	var re = /^(\d{1,2}\/\d{1,2}\/\d{1,4})\s*(\s+\d{1,2}:\d{1,2}:\d{1,2})?$/i;
  this.selectedDate = new Date();
	if(re.test(field.value)) {
	  var dateParts = this.dateField.value.split(" ");
	  var arr = dateParts[0].split("/") ;
	  this.selectedDate.setDate(parseInt(arr[1],10)) ;
	  this.selectedDate.setMonth(parseInt(arr[0],10) - 1) ;
	  this.selectedDate.setFullYear(parseInt(arr[2],10)) ;
	  if(dateParts.length > 1 && dateParts[dateParts.length - 1] != "") {
	  	arr = dateParts[dateParts.length - 1].split(":") ;
	  	this.selectedDate.setHours(arr[0], 10)
	  	this.selectedDate.setMinutes(arr[1], 10)
	  	this.selectedDate.setSeconds(arr[2], 10)
	  }
	}
	this.currentDate = new Date(this.selectedDate.valueOf()) ;

  var clndr = document.getElementById(this.calendarId);
  clndr.lastChild.innerHTML = this.renderCalendar();

  var x = eXo.core.Browser.findPosX(this.dateField);
  var y = eXo.core.Browser.findPosY(this.dateField) + this.dateField.offsetHeight;

  with(clndr.style) {
  	display = 'block';
	  left = x + "px";
	  top = y + "px";
  }
  if (document.all) {
  	with(document.getElementById('UICalendarControlIFrame').style) {
	    display = 'block';
	    width = clndr.lastChild.offsetWidth + "px";
	    height = clndr.lastChild.offsetHeight + "px";
  	}
  }
}

UICalendar.prototype.hide = function() {
  if(this.dateField) {
    document.getElementById(this.calendarId).style.display = 'none';
    document.getElementById('UICalendarControlIFrame').style.display = 'none';
    this.dateField = null;
  }
 	document.onmousedown = null;
}

UICalendar.prototype.renderCalendar = function() {
  var dayOfMonth = 1;
  var validDay = 0;
  var startDayOfWeek = this.getDayOfWeek(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, dayOfMonth);
  var daysInMonth = this.getDaysInMonth(this.currentDate.getFullYear(), this.currentDate.getMonth());
  var clazz = null;

  var table = "<table cellspacing='0' cellpadding='0' border='0' onmousedown='event.cancelBubble = true'>";
  table = table + "<tr class='header'><th colspan='7' style='padding: 3px;'><span style='background-color: white'>&nbsp;<input size='2' maxlength='2' value='" + this.currentDate.getHours() + "'> : <input size='2' maxlength='2' value='" + this.currentDate.getMinutes() + "'> : <input size='2' maxlength='2' value='" + this.currentDate.getSeconds() + "'>&nbsp;</span></th></tr>";
  table = table + "<tr class='header'>";
  table = table + "  <td colspan='2' class='previous'><a href='javascript:eXo.webui.UICalendar.changeMonth(-1);'>&lt;</a> <a href='javascript:eXo.webui.UICalendar.changeYear(-1);'>&laquo;</a></td>";
  table = table + "  <td colspan='3' class='title'>" + this.months[this.currentDate.getMonth()] + "<br>" + this.currentDate.getFullYear() + "</td>";
  table = table + "  <td colspan='2' class='next'><a href='javascript:eXo.webui.UICalendar.changeYear(1);'>&raquo;</a> <a href='javascript:eXo.webui.UICalendar.changeMonth(1);'>&gt;</a></td>";
  table = table + "</tr>";
  table = table + "<tr><th>S</th><th>M</th><th>T</th><th>W</th><th>T</th><th>F</th><th>S</th></tr>";

  for(var week=0; week < 6; week++) {
    table = table + "<tr>";
    for(var dayOfWeek=0; dayOfWeek < 7; dayOfWeek++) {
      if(week == 0 && startDayOfWeek == dayOfWeek) {
        validDay = 1;
      } else if (validDay == 1 && dayOfMonth > daysInMonth) {
        validDay = 0;
      }

      if(validDay) {
        if (dayOfMonth == this.selectedDate.getDate() && this.currentDate.getFullYear() == this.selectedDate.getFullYear() && this.currentDate.getMonth() == this.selectedDate.getMonth()) {
          clazz = 'current';
        } else if (dayOfWeek == 0 || dayOfWeek == 6) {
          clazz = 'weekend';
        } else {
          clazz = 'weekday';
        }

        table = table + "<td><a class='"+clazz+"' href=\"javascript:eXo.webui.UICalendar.setDate("+this.currentDate.getFullYear()+","+(this.currentDate.getMonth() + 1)+","+dayOfMonth+")\">"+dayOfMonth+"</a></td>";
        dayOfMonth++;
      } else {
        table = table + "<td class='empty'>&nbsp;</td>";
      }
    }
    table = table + "</tr>";
  }
//  table = table + "<tr class='header'><th colspan='7' style='padding: 3px;'><a href='javascript:eXo.webui.UICalendar.clearDate();'>Clear</a> | <a href='javascript:eXo.webui.UICalendar.hide();'>Close</a></td></tr>";
  table = table + "<tr class='header'><th colspan='7' style='padding: 3px;'><a href='javascript:eXo.webui.UICalendar.clearDate();'>Clear</a></th></tr>";
  table = table + "</table>";

  return table;
}

UICalendar.prototype.changeMonth = function(change) {
	this.currentDate.setMonth(this.currentDate.getMonth() + change)
//  alert(this.currentDate.getDate() + " : " + this.currentDate.getMonth() + " : " + this.currentDate.getYear() + "\n" + this.selectedDate.getDate() + " : " + this.selectedDate.getMonth() + " : " + this.selectedDate.getYear())

  var clndr = document.getElementById(this.calendarId);
  clndr.lastChild.innerHTML = this.renderCalendar();
}

UICalendar.prototype.changeYear = function(change) {
  this.currentDate.setFullYear(this.currentDate.getFullYear() + change)
  this.currentDay = 0;
  var clndr = document.getElementById(this.calendarId);
  clndr.lastChild.innerHTML = this.renderCalendar();
}

UICalendar.prototype.setDate = function(year, month, day) {
  if (this.dateField) {
    if (month < 10) {month = "0" + month;}
    if (day < 10) {day = "0" + day;}
    var dateString = month+"/"+day+"/"+year + " " + this.currentDate.getHours() + ":" + this.currentDate.getMinutes() + ":" + this.currentDate.getSeconds();
    this.dateField.value = dateString;
    this.hide();
  }
  return;
}

UICalendar.prototype.clearDate = function() {
  this.dateField.value = '';
  this.hide();
}

UICalendar.prototype.getDayOfWeek = function(year, month, day) {
  var date = new Date(year,month-1,day)
  return date.getDay();
}

UICalendar.prototype.getDaysInMonth = function(year, month) {
	return [31,((!(year % 4 ) && ( (year % 100 ) || !( year % 400 ) ))?29:28),31,30,31,30,31,31,30,31,30,31][month-1];
}

eXo.webui.UICalendar = new UICalendar('UICalendarControl') ;