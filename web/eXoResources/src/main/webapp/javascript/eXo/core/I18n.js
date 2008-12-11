function I18n() {
  this.init();
}

I18n.prototype.init = function() {
  var html = document.getElementsByTagName('html')[0];
  var lang = html.getAttribute('xml:lang') || html.getAttribute('lang') || "en";
  var dir = html.getAttribute('dir') || "lt";
  this.lang = lang;
  this.dir = dir;
  this.orientation = "rtl" == dir ? "rt" : "lt";
  this.lt = this.orientation == "lt";
}

I18n.prototype.getLanguage = function() {
  return this.lang;
}

I18n.prototype.getOrientation = function() {
  return this.orientation;
}

I18n.prototype.getDir = function() {
  return !this.lt;
}

I18n.prototype.isLT = function() {
  return this.lt;
}

I18n.prototype.isRT = function() {
  return !this.lt;
}

eXo.core.I18n = new I18n();
