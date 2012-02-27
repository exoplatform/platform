/**
 * Locale.js
 * patch file for change app title
 * (can't use messageBundle for app title + description #bug?)
 */

var eXo = eXo || {};
eXo.social = eXo.social || {};

/**
 * Local class define
 */
eXo.social.Locale = function() {

}


/**
 * gets current lang
 * @static
 */
eXo.social.Locale.getLang = function() {
  return new gadgets.Prefs().getLang();
}

/**
 * gets current country
 * @static
 */
eXo.social.Locale.getCountry = function() {
  return new gadgets.Prefs().getCountry();
}

/**
 * alternative for prefs.getMsg(key)
 * uses to getMsg with provided key and substitute args
 * <p>
 * eg: <msg key="test">Test for {0}, {1}</msg>
 * If args does not match num of {\d}, warning and try to replace by corresponding index.
 * {0} should be replaced by args[0], etc.,
 * If args not provided, functions as prefs.getMsg(key)
 * @param	key String
 * @param	opt_args Array
 * @static
 */
eXo.social.Locale.getMsg = function(key, opt_args) {
  if (!key) {
    debug.warn('key is null!');
    return;
  }
  var prefs = new gadgets.Prefs();
  var msg = prefs.getMsg(key);
  if (msg === '') {
    debug.warn('Can not find resource bundle with key = ' + key);
    return msg;
  }
  if (!opt_args) return msg;

  //checks if number of {\d} in msg matches opt_args.length
  var regex = /{\d+}/g;
  var matches = msg.match(regex);
  if (matches == null) {
    debug.warn('no argument found to be substituted with: ' + opt_args);
    return msg;
  }
  if (matches.length !== opt_args.length) {
    debug.warn("required " + matches.length + " args, provided: " + opt_args.length);
  }
  //substitutes by index: {0} in msg should be replaced by opt_args[0] and so on
  for (var i = 0, l = matches.length; i < l; i++) {
    var index = matches[i].match(/\d+/g)[0];
    //TODO should improve performance
    var strToReplace = opt_args[index];
    if (!strToReplace) {
      debug.warn('matches[' + i + ']: ' + matches[i] + ' but no opt_args[' + index + ']');
    } else {
      msg = msg.replace(matches[i], opt_args[index]);
    }
  }
  return msg;
}