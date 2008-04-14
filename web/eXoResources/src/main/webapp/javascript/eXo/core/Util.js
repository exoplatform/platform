/**
 *   Array convenience method to clear membership.
 *   @param object element
 *   @returns void
 */
Array.prototype.clear = function () {
  this.length = 0 ;
} ;

/**
 *   Array convenience method to remove element.
 *
 *   @param object element
 *   @returns boolean
 */
Array.prototype.remove = function (element) {
  var result = false ;
  var array = [] ;
  for (var i = 0; i < this.length; i++) {
    if (this[i] == element) {
      result = true ;
    } else {
      array.push(this[i]) ;
    }
  }
  this.clear() ;
  for (var i = 0; i < array.length; i++) {
    this.push(array[i]) ;
  }
  array = null ;
  return result ;
} ;

/**
 *   Array convenience method to check for membership.
 *
 *   @param object element
 *   @returns boolean
 */
Array.prototype.contains = function (element) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == element) {
      return true ;
    }
  }
  return false ;
} ;

Array.prototype.insertAt = function (what, iIndex) {
  if (iIndex < this.length) {
    var aAfter = this.splice(iIndex, 100000, what) ;
    for (var i = 0; i < aAfter.length; i++) {
      this.push(aAfter[i]) ;
    }
  } else {
    this.push(what) ;
  }
} ;

Array.prototype.pushAll = function (array) {
	if (array != null) {
		for (var i = 0; i < array.length; i++) {
			this.push(array[i]) ;
		}
	}
} ;

Array.prototype.each = function (iterator, context) {
	iterator = iterator.bind(context);
  	for (var i = 0; i < this.length; i++) {
		iterator(this[i]) ;
	}
};

/*************************************************************************/
function  HashMap() { 
	 this.properties = new Object() ;
	 this.length =  0 ;
} ;

HashMap.prototype.copyProperties = function(names, object) {
  for (var i = 0; i < names.length; i++) {
    var name = names[i] ;
    this.put(name, object[name]) ;
  }
} ;


HashMap.prototype.setProperties = function(object, clear) {
  for(var name in this.properties) {
    object[name] = this.properties[name] ; 
  }
} ;

HashMap.prototype.get = function (name) {
  return  this.properties[name] ;
} ;

HashMap.prototype.remove = function (name) {
  var value = this.properties[name] ;
  if (value != null)  { 
    this.properties[name] = null ;
    this.length-- ;
    return value ;
  } else {
    return null ;
  }
} ;

HashMap.prototype.put = function (name, value) {
  if (this.properties[name] == null) {
    this.length++ ;
  }
  this.properties[name] =  value ;
} ;

HashMap.prototype.size = function () { return this.length ; } ;

HashMap.prototype.clear = function() {
 this.properties = new Object() ;
 this.length =  0 ;
} ;

/*************************************************************************/
eXo.core.HashMap = HashMap.prototype.constructor ;
/*************************************************************************/

function ExoDateTime() {
};

ExoDateTime.prototype.getTime = function() {
  var dateTime = new Date() ;
  var hour = dateTime.getHours() ;
  var minute = dateTime.getMinutes() ;
  var second = dateTime.getSeconds() ;
  
  if (hour <= 9) hour = "0" + hour ;
  if (minute <= 9) minute = "0" + minute ;

  var AM_PM = "" ;
  if (hour < 12) {
    AM_PM = "AM" ;
  } else if (hour == 12) {
    AM_PM = "PM" ;
  } else {
    AM_PM = "PM" ;
    hour = hour - 12 ;
  }
  
  var time = "" ;
  time += hour + ":" + minute + " " + AM_PM ;
  var digitalClock = document.getElementById("DigitalClock") ;
  digitalClock.innerHTML = time ;
  setTimeout("eXo.core.ExoDateTime.getTime()", 60000) ;
} ;

ExoDateTime.prototype.getDate = function() {
  var dateTime = new Date() ;
  var date = dateTime.getDate() ;
  var month = dateTime.getMonth() ;
  var year = dateTime.getFullYear() ;
  
  if(date <= 9) date = "0" + date ;
  
  switch(month) {
    case 0:
      month = "Jan" ;
      break ;
    case 1:
      month = "Feb" ;
      break ;
    case 2:
      month = "Mar" ;
      break ;
    case 3:
      month = "Apl" ;
      break ;
    case 4:
      month = "May" ;
      break ;
    case 5:
      month = "Jun" ;
      break ;
    case 6:
      month = "Jul" ;
      break ;
    case 7:
      month = "Aug" ;
      break ;
    case 8:
      month = "Sep" ;
      break ;
    case 9:
      month = "Oct" ;
      break ;
    case 10:
      month = "Nov" ;
      break ;
    case 11:
      month = "Dec" ;
      break ;
    default:
      month = month ;
      break ;
  }
  
  var today = date + " " + month + " " + year ;
  var dateElement = eXo.core.DOMUtil.findFirstDescendantByClass(document.body, "div", "Date") ;
  dateElement.innerHTML = today ;
};

eXo.core.ExoDateTime = new ExoDateTime() ;
/*************************************************************************/

/**
 * @author Nguyen Ba Uoc
 * 
 * String util
 */

String.prototype.trim = function () {
  var tmp = this.replace(/^\s*/, '');
  return tmp.replace(/\s*$/, '');
}


/**
 * @author jeremi joslin
 * 
 * Function util
 */
Function.prototype.bind = function(object) {
  var method = this;
  return function() {
    method.apply(object, arguments);
  }
}

Function.prototype.inherits = function(parentCtor) {
  function tempCtor() {};
  tempCtor.prototype = parentCtor.prototype;
  this.superClass_ = parentCtor.prototype;
  this.prototype = new tempCtor();
  this.prototype.constructor = this;
};