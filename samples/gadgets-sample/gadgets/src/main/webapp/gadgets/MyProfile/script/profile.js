function loadProfile() {
   // Adding eXo Platform container information
    var opts = {};
    opts[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [
     opensocial.Person.Field.PROFILE_URL,
     opensocial.Person.Field.THUMBNAIL_URL,
     "portalName",
     "restContext",
     "host"];        
    var req = opensocial.newDataRequest();
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER, opts), 'viewer');
    req.send(onLoadProfile);
}

function onLoadProfile(data) {
  this.viewer = data.get('viewer').getData();
  
  var hostName = viewer.getField('hostName');
  var portalName = viewer.getField('portalName');
  var restContext = viewer.getField('restContextName');
  var address = window.top.location.href;        
  var baseContext = hostName + "/" + portalName + "/";
  var extensionContext = address.replace(baseContext, "");
  var extensionParts = extensionContext.split("/");
  //var context = baseContext + extensionParts[0] + "/" + extensionParts[1];
  var context = baseContext + extensionParts[0];
  var profileTempUrl = this.viewer.getField(opensocial.Person.Field.PROFILE_URL);
  var eXoUserID = profileTempUrl.substr(profileTempUrl.lastIndexOf('/') + 1);
  
  var profileUrl = context + '/profile/' + eXoUserID;
  var profileName = viewer.getDisplayName();
  var profileThumb = this.viewer.getField(opensocial.Person.Field.THUMBNAIL_URL);
  
  
  var prefs = new gadgets.Prefs();
  var linkmodifylabel = prefs.getMsg("modifylink");
  
  var html = new Array();
  html.push('<div class="ProfilePicture">',
            '<img class="GadCont ProfilePicture" src="' + profileThumb + '" alt="Thumb"/></div>',
            '<div class="GadCont ProfileInfo">', profileName, "<br/>",
            '<a target="_parent" href="' + profileUrl + '">' + linkmodifylabel + '</a>');
  
  document.getElementById('Profil').innerHTML = html.join('');
}

function init() {
  loadProfile();
}
