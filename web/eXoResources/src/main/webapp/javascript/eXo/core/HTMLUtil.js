/**
 * @author Nguyen Ba Uoc
 */
// 4test
if (eXo.require) eXo.require('eXo.core.html.HTMLEntities');

function HTMLUtil() {
  this.entities = eXo.core.html.HTMLEntities ;
}

/**
 * 
 * @param {String} str
 * 
 * @return {String}
 * 
 */
HTMLUtil.prototype.entitiesEncode = function(str) {
  if (!str || str == '') {
    return str ;
  }
  for(var n in this.entities) {
    var entityChar = String.fromCharCode(this.entities[n]) ;
    if(entityChar == '&') {
      entityChar = '\\' + entityChar ;
    }
    while(str.indexOf(entityChar) != -1) {
      str = str.replace(entityChar, '&' + n + ';') ;
    }
  }
  return str ;
}

/**
 * 
 * @param {String} str
 * 
 * @return {String}
 * 
 */
HTMLUtil.prototype.entitiesDecode = function(str) {
  if (!str || str == '') {
    return str ;
  }
  for(var n in this.entities) {
    var entityChar = String.fromCharCode(this.entities[n]) ;
    var htmlEntity = '&' + n + ';' ;
    while(str.indexOf(htmlEntity) != -1) {
      str = str.replace(htmlEntity, entityChar) ;
    }
  }
  return str ;
}

eXo.core.HTMLUtil = new HTMLUtil() ;