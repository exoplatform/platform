function renderSuggestFriends() {
  var strHtml = "";
  var mutualFriends_;
  $.getJSON('/rest/private/friendSuggestion/getSuggestedFriends', function(data) {
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
      //if (key > 2) display = "none";
      strHTML +=  '<div class="Friend ClearFix" style="border-bottom: none; margin-top: 0px; display: ' + display + ';">';
      if(avatar) {
        strHTML +=    '<div class="avatar"><img src="'+ avatar +'" width="60" height="60" alt="avatar" title="avatar" /></div>';
      } else {
        strHTML +=    '<div class="avatar"><img src="/social-resources/skin/ShareImages/activity/AvatarPeople.gif" width="60" height="60" alt="avatar" title="avatar" /></div>';
      }
      strHTML += '<a href="#" class="close" style="float:right">x</a>';
      strHTML   +=     '<div class="info">';
      strHTML   +=       '<div class="name">' + name + '</div>';   
      if (mutualFriends.length > 1)  text = "mutual friends";
      strHTML   +=       '<div class="mutual" mutualfriends="'+ mutualFriends +'">'+ mutualFriends.length  +'<small><a href="#">' + text + '</a></small></div>';        
      strHTML   +=     '</div>';            
      strHTML    +=    '<div style="clear:both;"><span></span></div>';
      strHTML    +=    '<div class="addfriend" friend="'+ id +'"><a href="#" class="AddasFriend" friend="'+ id +'">Invite</a></div>';
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
        $("#mutual-friend").css("left", left+10);
    });
      
    $(".AddasFriend").click(function(event){
      event.stopPropagation();
      requestAddFriend($(this).attr("friend"));
      $(this).parents("div.Friend").fadeOut("slow");
    });
    
    $(".close").click(function(event){
      event.stopPropagation();
      $(this).parents("div.Friend").fadeOut("slow");
    });
  
      $(document).click(function(){
        $("#friends").html("");
        $("#mutual-friend").hide();    
      });  
  });
}

function renderMutualFriends(mFriends) {
  if (mFriends.length ==1) {
    $(".Boder").css("width", 208)
  } else if (mFriends.length > 1) {
    $(".Boder").css("width", 416)  
  }
  $.each(mFriends, function(key){
      mutualFriend = mFriends[key];
      mutualFriends(mutualFriend);
  });
}

function mutualFriends(ids) {
  var htm = "";
  $.getJSON('/rest/private/friendSuggestion/getProfile/' + ids, function(data) {
    var id = data["id"];
    var fullname = data["fullName"];
    var avatar = data["avatarPath"];
    htm += "<div class='ContainerFR ClearFix' style='margin-top:5px; width: 208px; float:left'>";
    if(avatar) {
        htm +=    '<div class="avatar"><img src="'+ avatar +'" width="60" height="60" alt="avatar" title="avatar" /></div>';
      } else {
        htm +=    '<div class="avatar"><img src="/social-resources/skin/ShareImages/activity/AvatarPeople.gif" width="60" height="60" alt="avatar" title="avatar" /></div>';
      }
    htm += "<div class='info' style='width: 137px;'>";
    htm += "<div class='name'>" + fullname + "</div>";
    htm += "<small>" + id + "</small>";
    htm += "</div></div>";
    $("#friends").append(htm);
  });
}

function requestAddFriend(id) {
  $.getJSON('/rest/private/addFriend/add/' + id, function(data) {});
  alert("Your invitation was sent to " + id + " !");
}