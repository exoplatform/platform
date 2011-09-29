function renderSuggestFriends() {
  var strHtml = "";
  var mutualFriends_;
  $.getJSON('/rest/friendSuggestion/getSuggestedFriends', function(data) {
    var listSuggestFriends = data.data;
    var suggestFriend = null;
    var strHTML = "";
    $.each(listSuggestFriends, function(key){
      suggestFriend = listSuggestFriends[key];
      var friend = listSuggestFriends[key];
      var id = friend["id"];
      var name = friend["fullName"];
      var avatar = friend["avatarPath"];
      var mutualFriends = friend["mutualFriends"];
      mutualFriends_ = mutualFriends;
      
      var text = "mutual friend";
      var display = "block";
      if(!avatar) avatar = "/social-resources/skin/ShareImages/activity/AvatarPeople.gif";
      strHTML +=  '<div class="ClearFix" style="border-bottom: none; margin-top: 5px; display: ' + display + ';">';
      strHTML +=  '  <div class="avatar"><img src="'+ avatar +'" width="32" height="32" alt="avatar" title="' + name + '" /></div>';
      strHTML   +=     '<div class="info">';
      strHTML +=  '    <div>';
      strHTML +=  '      <span class="name ThemeColor">' + name + '</span>';   
      strHTML +=  '      <span class="mutual customLink" style="color: #757575;" mutualfriends="'+ mutualFriends +'">' + mutualFriends.length  + ' ' + text + (mutualFriends.length > 1?'s':'') + '</span>';        
      strHTML +=  '    </div>';
      strHTML +=  '    <div class="addfriend" friend="'+ id +'"> <small class="AddasFriend customLink ThemeColor" friend="'+ id +'">Send invitation</small> </div>';
      strHTML   +=     '</div>';
      strHTML   +=   '</div>';
    });

    $("#container").append(strHTML);
    
    $(".mutual").click(function(event){
      $("#friends").html("");
        event.stopPropagation();
        renderMutualFriends($(this).attr("mutualfriends").split(","));
        $("#mutual-friend").show();
      var top =  $(this).offset().top;
      var left =  $(this).offset().left;
        $("#mutual-friend").css("top", top + 16);
        $("#mutual-friend").css("left", left);
    });

    $(".AddasFriend").click(function(event){
      event.stopPropagation();
      requestAddFriend($(this).attr("friend"));
      $(this).parents("div.Friend").fadeOut("slow");
    });

    $(document).click(function(){
      $("#friends").html("");
      $("#mutual-friend").hide();    
    });
  });
}

function renderMutualFriends(mFriends) {
  if (mFriends.length < 3) {
    $(".Boder").css("width", 128);
    $("div.TopMenu").css("width", 150);
      } else {
    $(".Boder").css("width", 208);
    $("div.TopMenu").css("width", 230); 
      }
  $.each(mFriends, function(key){
      mutualFriend = mFriends[key];
      mutualFriends(mutualFriend);
  });
    }
    
function mutualFriends(ids) {
  $.getJSON('/rest/friendSuggestion/getProfile/' + ids, function(data) {
      var htm = "";
    var id = data["id"];
    var fullname = data["fullName"];
    var avatar = data["avatarPath"];
    if(!avatar) avatar = "/social-resources/skin/ShareImages/activity/AvatarPeople.gif";
    htm += "<div class='ContainerFR ClearFix' style='margin-top:5px; width: 42px; float:left; padding-right: 10px;'>";
    htm += "  <center>";
    htm += "    <div><img src='"+ avatar +"' width='30' height='30' alt='avatar' title='" + fullname + "'/></div>";
    htm += "    <div style='width: 100%; overflow: hidden;'>";
    htm += "      <small style='font-size: 9px;'>" + id + "</small>";
    htm += "    </div>";
    htm += "  </center>";
    htm += "</div>";
      $("#friends").append(htm);
    });
}

function requestAddFriend(id) {
  $.getJSON('/rest/friendSuggestion/add/' + id, function(data) {});
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
