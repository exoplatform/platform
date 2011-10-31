/* 
 * Convert a number of milliseconds to the appropriate unit
 */
function formatDuration (milliseconds) {
  var SEC = 1000;
  var MIN = 60 * SEC;
  var HOUR = 60 * MIN;
  var DAY = 24 * HOUR;
  var WEEK = 7 * DAY;
  
  var remaining=milliseconds; 
  
  if (milliseconds < MIN) {
    return milliseconds + " ms";
  } else if (milliseconds < HOUR) {
    var minutes = Math.floor(remaining/MIN);
    remaining = remaining - minutes*MIN;
    var seconds = Math.floor(remaining/SEC);
    remaining = remaining - seconds*SEC;
    return minutes + " min " + seconds + " sec " + remaining + " ms";
  } else if (milliseconds < DAY) {
    var hours = Math.floor(remaining/HOUR);
    remaining = remaining - hours*HOUR;
    var minutes = Math.floor(remaining/MIN);
    remaining = remaining - minutes*MIN;
    var seconds = Math.floor(remaining/SEC);
    remaining = remaining - seconds*SEC;
    return hours + " h " + minutes + " min " + seconds + " sec " + remaining + " ms";
  } else {
    var days = Math.floor(remaining/DAY);
    remaining = remaining - days*DAY;
    var hours = Math.floor(remaining/HOUR);
    remaining = remaining - hours*HOUR;
    var minutes = Math.floor(remaining/MIN);
    remaining = remaining - minutes*MIN;
    var seconds = Math.floor(remaining/SEC);
    remaining = remaining - seconds*SEC;
    return days + " d " + hours + " h " + minutes + " min " + seconds + " sec " + remaining + " ms";
  }
}
