// ------------------------
// eXoUserPrefStore

/**
 * eXo user preference store.
 * @constructor
 */
gadgets.eXoUserPrefStore = function() {
  gadgets.UserPrefStore.call(this);
};

gadgets.eXoUserPrefStore.inherits(gadgets.UserPrefStore);

gadgets.eXoUserPrefStore.prototype.USER_PREFS_PREFIX =
    'gadgetUserPrefs-';

gadgets.eXoUserPrefStore.prototype.getPrefs = function(gadget) {
  var userPrefs = {};
  var cookieName = this.USER_PREFS_PREFIX + gadget.id;
  var cookie = goog.net.cookies.get(cookieName);
  if (cookie) {
    var pairs = cookie.split('&');
    for (var i = 0; i < pairs.length; i++) {
      var nameValue = pairs[i].split('=');
      var name = decodeURIComponent(nameValue[0]);
      var value = decodeURIComponent(nameValue[1]);
      userPrefs[name] = value;
    }
  }
  return userPrefs;
};

gadgets.eXoUserPrefStore.prototype.savePrefs = function(gadget) {
  var pairs = [];
  for (var name in gadget.getUserPrefs()) {
    var value = gadget.getUserPref(name);
    var pair = encodeURIComponent(name) + '=' + encodeURIComponent(value);
    pairs.push(pair);
  }

  var cookieName = this.USER_PREFS_PREFIX + gadget.id;
  var cookieValue = pairs.join('&');
  goog.net.cookies.set(cookieName, cookieValue);
};