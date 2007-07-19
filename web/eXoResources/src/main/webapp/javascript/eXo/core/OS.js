/**
 * @author Nguyen Ba Uoc
 */

function OS() {
  this.isWin = false ;
  this.isMac = false ;
  this.init() ;
}

OS.prototype.init = function() {
  var detect = navigator.platform.toLowerCase() ;
  if (detect.indexOf('win') != -1) {
    this.isWin = true ;
  } else if (detect.indexOf('mac') != -1) {
    this.isMac = true ;
  }
}

eXo.core.OS = new OS() ;