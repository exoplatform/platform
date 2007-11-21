eXo.require('eXo.widget.UIExoWidget');

UICalendarWidget.prototype = eXo.widget.UIExoWidget;
UICalendarWidget.prototype.constructor = UICalendarWidget;

function UICalendarWidget() {
	this.init("UICalendarWidget", "calendar");
	this.months = ['January','February','March','April','May','June','July','August','September','October','November','December'] ;
	this.currentDate = new Date();
}

UICalendarWidget.prototype.renderCalendar = function(appId) {
  var dayOfMonth = 1 ;
  var validDay = 0 ;
  var startDayOfWeek = this.getDayOfWeek(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, dayOfMonth) ;
  var daysInMonth = this.getDaysInMonth(this.currentDate.getFullYear(), this.currentDate.getMonth()) ;
  var clazz = null;
  var today = new Date();

	var table = '<div class="UICalendar">' ;
	table += 		'	<table class="MonthYearBox">' ;
	table += 		'	  <tr>' ;
	table += 		'			<td class="MonthButton"><a class="PreviousMonth" href="javascript:eXo.widget.web.calendar.UICalendarWidget.changeMonth(-1,\'' + appId + '\');"></a></td>' ;
	table += 		'			<td class="YearButton"><a class="PreviousYear" href="javascript:eXo.widget.web.calendar.UICalendarWidget.changeYear(-1,\'' + appId + '\');"></a></td>' ;
	table += 		'			<td onclick="eXo.widget.web.calendar.UICalendarWidget.goToday(\'' + appId + '\');"><font color="#f89302">' + this.months[this.currentDate.getMonth()] + '</font> - ' + this.currentDate.getFullYear() + '</td>' ;
	table += 		'			<td class="YearButton"><a class="NextYear" href="javascript:eXo.widget.web.calendar.UICalendarWidget.changeYear(1,\'' + appId + '\');"></a></td>' ;
	table += 		'			<td class="MonthButton"><a class="NextMonth" href="javascript:eXo.widget.web.calendar.UICalendarWidget.changeMonth(1,\'' + appId + '\');"></a></td>' ;
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
      	if ((today.getDate() == dayOfMonth) && (today.getMonth() == this.currentDate.getMonth()) && (today.getFullYear() == this.currentDate.getFullYear())) {
          clazz = 'Current';
        } else if (dayOfWeek == 0 || dayOfWeek == 6) {
          clazz = 'Weekend';
        } else {
          clazz = 'Weekday';
        }

        table = table + "<td><a class='"+clazz+"'>"+dayOfMonth+"</a></td>" ;
        dayOfMonth++ ;
      } else {
        table = table + "<td class='empty'><div>&nbsp;</div></td>" ;
      }
    }
    table += "</tr>" ;
  }		
	table += 		'		</table>' ;
	table += 		'	</div>' ;	
	table += 		'</div>' ;

	return table ;
}

UICalendarWidget.prototype.goToday = function(appId) {
	var today = new Date();
	this.currentDate.setMonth(today.getMonth());
	this.currentDate.setFullYear(today.getFullYear());
	var clndr = document.getElementById(appId) ;
	clndr.lastChild.innerHTML = this.renderCalendar(appId);
}

UICalendarWidget.prototype.changeMonth = function(change, appId) {
	this.currentDate.setMonth(this.currentDate.getMonth() + change) ;
  var clndr = document.getElementById(appId) ;
	clndr.lastChild.innerHTML = this.renderCalendar(appId) ;
}

UICalendarWidget.prototype.changeYear = function(change, appId) {
  this.currentDate.setFullYear(this.currentDate.getFullYear() + change) ;
  this.currentDay = 0 ;
  var clndr = document.getElementById(appId) ;
  clndr.lastChild.innerHTML = this.renderCalendar(appId) ;
}

UICalendarWidget.prototype.getDayOfWeek = function(year, month, day) {
  var date = new Date(year, month - 1, day) ;
  return date.getDay() ;
}

UICalendarWidget.prototype.getDaysInMonth = function(year, month) {
	return [31, ((!(year % 4 ) && ( (year % 100 ) || !( year % 400 ) ))? 29:28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
}

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.calendar == null) eXo.widget.web.calendar = {};
eXo.widget.web.calendar.UICalendarWidget = new UICalendarWidget();


