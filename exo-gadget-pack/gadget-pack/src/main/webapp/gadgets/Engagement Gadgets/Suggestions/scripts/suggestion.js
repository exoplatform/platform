var peopleUrl = "#";
var profilePage = "#";
var ListSuggestion = {};
function render(data) {
  var viewer = data.get('viewer').getData();

  var hostName = viewer.getField('hostName');
  var portalName = viewer.getField('portalName');
  var restContext = viewer.getField('restContextName');
  var address = window.top.location.href;
  var baseContext = hostName + "/" + portalName + "/";
  var extensionContext = address.replace(baseContext, "");
  var extensionParts = extensionContext.split("/");
  var context = baseContext + extensionParts[0] + "/" + extensionParts[1];
  profilePage = context + '/profile/';
  peopleUrl = context + '/people/';

  var strHtml = "";
  var mutualFriends_;
  $.getJSON('/rest/private/suggestion/suggested-friends', function(data) {
    var listSuggestFriends = data.data;
    var suggestFriend = null;
    var strHTML = "";
    $.each(listSuggestFriends, function(key){
      suggestFriend = listSuggestFriends[key];
      var friend = listSuggestFriends[key];
      var id = friend["id"];
      var name = friend["fullName"];
      var avatar = friend["avatarUrl"];
      
      strHTML +=  '<div class="Friend ClearFix">';
      if(avatar) {
        strHTML +=    '<div class="avatar"><img src="'+ avatar +'" width="60" height="60" alt="avatar" title="avatar" /></div>';
      } else {
        strHTML +=    '<div class="avatar"><img src="/social-resources/skin/ShareImages/activity/AvatarPeople.gif" width="60" height="60" alt="avatar" title="avatar" /></div>';
      }
      strHTML   +=      '<a href="javascript:void(0);" class="close" style="float:right;">x</a>';
      strHTML   +=     '<div class="info">';
      strHTML   +=       '<div class="NameFriend" friendId="'+ id +'"><a href="javascript:void(0);">' + name + '</a></div>';
      strHTML   +=      '<div class="addfriend" friend="'+ id +'"><a href="javascript:void(0);" class="AddasFriend" friend="'+ id +'">Send invitation</a></div>';
      strHTML   +=     '</div>';
      strHTML   +=    '<div style="clear:both;"><span></span></div>';
      strHTML   +=   '</div>';

    });

    $("#container").append(strHTML);
    
    $(".NameFriend").click(function(){
      $("#friends").html("");
      $(".Loading").show();
      renderMutualFriends($(this).attr("friendId"));
      var top =  $(this).offset().top;
      var left =  $(this).offset().left;
      $("#mutual-friend").css("top", top - 7);
      $("#mutual-friend").css("left", left + 167);  
    });

    $(".AddasFriend").click(function(event){
      event.stopPropagation();
      requestAddFriend($(this).attr("friend"));
      $(this).parents("div.Friend").fadeOut("slow");
    });

    $("a.close").click(function(){
      $(this).parents("div.Friend").fadeOut("slow");
    });

    $(document).click(function(){
      $("#friends").html("");
      $(".MuatualContainer").hide();  
    });
      
  });
}

function renderMutualFriends(suggestedId) {
  $.getJSON('/rest/private/suggestion/get-mutual-friends/' + suggestedId, function(data) {
    var mutualFriends = data.data;
    if (mutualFriends.length == 1) {
      $("#friends").css("width", 208);
      $("#friends").css("height", 100);
    } else if (mutualFriends.length > 1) {
      $("#friends").css("width", 440);
      if(mutualFriends.length < 8) {
        var heightFriend = 65; // height of each friend = height of img + margin top
        var height = Math.round(mutualFriends.length/2) * heightFriend + 35;
        $("#friends").css("height", height);
      } else {
        $("#friends").css("height", 295);
      }
    }
    
    $("#friends").append('<div class="Title">' + mutualFriends.length + ' mutual friends<div>');
    $.each(mutualFriends, function(key){
      var mutualFriend = mutualFriends[key];
      var htm = "";
      var avatar = mutualFriend["avatarUrl"];
      htm += '<div class="ContainerFR ClearFix">';
      if(avatar) {
        htm +=    '<div class="avatar"><img src="'+ avatar +'" width="60" height="60" alt="avatar" title="avatar" /></div>';
      } else {
        htm +=    '<div class="avatar"><img src="/social-resources/skin/ShareImages/activity/AvatarPeople.gif" width="60" height="60" alt="avatar" title="avatar" /></div>';
      }
      htm += '<div class="info">';
      htm += '<div class="NameFriend"><a href="' + profilePage + mutualFriend["id"] + '">' + mutualFriend["fullName"] + '</a></div>';
      htm += '<small>' + mutualFriend["id"] + '</small>';
      htm += '</div></div>';
      $("#friends").append(htm);
    });
  }).success(function(){
      $(".Loading").hide();
      $(".MuatualContainer").show();
  });
}

function requestAddFriend(id) {
  $.getJSON('/rest/private/suggestion/send-invitation/' + id, function(data) {});
  alert("Your invitation was sent to " + id + " !");
}

function showAll() {
  parent.location = peopleUrl;
}

function init() {
  var opts = {};
  opts[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [
      opensocial.Person.Field.PROFILE_URL,
      "portalName",
      "restContext",
      "host"];
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER, opts), 'viewer');

  var viewerFriends = opensocial.newIdSpec({ "userId" : "VIEWER", "groupId" : "FRIENDS" });
  var opt_params = {};
  opt_params[opensocial.DataRequest.PeopleRequestFields.MAX] = 100;
  opt_params[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [opensocial.Person.Field.PROFILE_URL];

  req.add(req.newFetchPeopleRequest(viewerFriends, opt_params), 'viewerFriends');

  req.send(render);
}
