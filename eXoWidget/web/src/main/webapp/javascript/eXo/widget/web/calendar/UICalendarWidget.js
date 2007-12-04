eXo.require('eXo.widget.UIExoWidget');

UICalendarWidget.prototype = eXo.widget.UIExoWidget;
UICalendarWidget.prototype.constructor = UICalendarWidget;

function UICalendarWidget() {
	this.init("UICalendarWidget", "calendar");
	this.months = ['January','February','March','April','May','June','July','August','September','October','November','December'] ;
	this.currentDate = new Date();
	this.currentDates = new Array();
}

UICalendarWidget.prototype.show = function(appId) {
	return this.renderCalendar(appId);
}

UICalendarWidget.prototype.renderCalendar = function(appId) {
  var dayOfMonth = 1 ;
  var validDay = 0 ;
  
  if(this.currentDates[appId] == null) {
  	this.currentDates[appId] = new Date();
  }
  
  var cDate = this.currentDates[appId];
  var startDayOfWeek = this.getDayOfWeek(cDate.getFullYear(), cDate.getMonth() + 1, dayOfMonth) ;
  var daysInMonth = this.getDaysInMonth(cDate.getFullYear(), cDate.getMonth()) ;
  var clazz = null;
  var today = new Date(); 

	var table = '<div class="UICalendarWidget">' ;
	
	table += 		'	<div class="TLCalendar">' ;
	table += 		'		<div class="TRCalendar">' ;
	table += 		'			<div class="TMCalendar">' ;
	table += 		'				<div class="BGTopCalendar"><span></span></div>' ;
	table += 		'			</div>' ;
	table += 		'		</div>' ;
	table += 		'	</div>' ;	
	
	table += 		'	<div class="MLCalendar">' ;
	table += 		'		<div class="MRCalendar">' ;
	table += 		'			<div class="BGCalendar">' ;
	table += 		'				<div class="Container">' ;
	table += 		'					<table class="MonthYearBox">' ;
	table += 		'		  			<tr>' ;
	table += 		'							<td class="MonthButton"><a class="PreviousMonth" href="javascript:eXo.widget.web.calendar.UICalendarWidget.changeMonth(-1,\'' + appId + '\');"></a></td>' ;
	table += 		'		  				<td>' ;
	table += 		'								<div class="YearButton"><a class="PreviousYear" href="javascript:eXo.widget.web.calendar.UICalendarWidget.changeYear(-1,\'' + appId + '\');"></a></div>' ;
	table += 		'								<div class="Time" onclick="eXo.widget.web.calendar.UICalendarWidget.goToday(\'' + appId + '\');"><font color="#f89302">' + this.months[cDate.getMonth()] + '</font> - ' + cDate.getFullYear() +'</div>' ;
	table += 		'								<div class="YearButton"><a class="NextYear" href="javascript:eXo.widget.web.calendar.UICalendarWidget.changeYear(1,\'' + appId + '\');"></a></div>' ;
	table += 		'							</td>' ;
	table += 		'							<td class="MonthButton"><a class="NextMonth" href="javascript:eXo.widget.web.calendar.UICalendarWidget.changeMonth(1,\'' + appId + '\');"></a></td>' ;
	table += 		'						</tr>' ;
	table += 		'					</table>' ;
	table += 		'					<div style="margin: 5px 0px;padding: 0px 5px;">' ;
	table += 		'						<table class="DayTable">' ;
	table += 		'							<tr>' ;
	table += 		'								<td class="SunDay">S</font></td><td>M</td><td>T</td><td>W</td><td>T</td><td>F</td><td>S</td>' ;
	table += 		'							</tr>' ;
	table += 		'						</table>' ;
	table += 		'					</div>' ;
	table += 		'					<div class="CalendarGrid">' ;
	table += 		'						<table border="1" bordercolor=white>' ;	
	
  for (var week=0; week < 6; week++) {
    table += "<tr>";
    for (var dayOfWeek=0; dayOfWeek < 7; dayOfWeek++) {
      if (week == 0 && startDayOfWeek == dayOfWeek) {
        validDay = 1;
      } else if (validDay == 1 && dayOfMonth > daysInMonth) {
        validDay = 0;
      }

      if (validDay) {
      	if ((today.getDate() == dayOfMonth) && (today.getMonth() == cDate.getMonth()) && (today.getFullYear() == cDate.getFullYear())) {
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
	table += 		'						</table>' ;
	table += 		'					</div>' ;	
	table += 		'				</div>' ;	
	table += 		'			</div>' ;	
	table += 		'		</div>' ;	
	table += 		'	</div>' ;	
	
	table += 		'	<div class="BLCalendar">' ;
	table += 		'		<div class="BRCalendar">' ;
	table += 		'			<div class="BMCalendar">' ;
	table += 		'				<div class="BGBottomCalendar"><span></span></div>' ;
	table += 		'			</div>' ;
	table += 		'		</div>' ;
	table += 		'	</div>' ;
	
	table += 		'</div>' ;
	
	this.currentDates[appId] = cDate;
	
	return table ;
}

UICalendarWidget.prototype.goToday = function(appId) {
		this.currentDates[appId] = new Date();
	var clndr = document.getElementById(appId) ;
	clndr.lastChild.innerHTML = this.renderCalendar(appId);
}

UICalendarWidget.prototype.changeMonth = function(change, appId) {	
	this.currentDates[appId].setMonth(this.currentDates[appId].getMonth() + change) ;
  var clndr = document.getElementById(appId) ;
	clndr.lastChild.innerHTML = this.renderCalendar(appId) ;
}

UICalendarWidget.prototype.changeYear = function(change, appId) {
  this.currentDates[appId].setFullYear(this.currentDates[appId].getFullYear() + change) ;
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


