function initIncoming() {

  $.getJSON("/rest/homepage/intranet/invitations/allInvitations", function(items){

    if (items.length > 0){
      $("#peopleInvite").show();
      $("#emptyInviteMessage").hide();
    }

    $.each(items, function(i, item){
      if(item.invitationType == "people"){
        var link = "<li id='"+item.relationId+"'>";
        link += "<div class='peopleInvitePicture' ><a href='#'><img src='"+item.avatar+"'></a></div>";
        link += "<div class='peopleInviteInfo'>";
        link += "<div class='peopleInviteName'><div class='name'><a href='"+item.profile+"' target='_parent'>"+item.senderName+"</a></div><div class='peopleInviteAction' style='visibility:hidden;'><button type='button' class='accept'>Accept</button> | <a class='deny' href='#' onclick='return false'><img src='/homepage-portlets/style/images/deny.png'></a></div></div>";

        if (item.position != undefined)
          link += "<div class='peopleInvitePosition'>"+item.position+"</div>"
        link += "</div></li>";

        $("#requests").append(link);

        $("#"+item.relationId).mouseover(function() { $("#"+item.relationId+" .name").addClass("actionInviteAppears");$("#"+item.relationId+" .peopleInviteAction").css('visibility','visible'); });

        $("#"+item.relationId).mouseout(function() { $("#"+item.relationId+" .name").removeClass("actionInviteAppears"); $("#"+item.relationId+" .peopleInviteAction").css('visibility','hidden'); });

        $("#"+item.relationId+" button.accept").live("click", function(){
          $.getJSON("/rest/homepage/intranet/people/contacts/confirm/"+item.relationId, null);

          if($("#requests").children().length == 1) {
            $("#peopleInvite").fadeOut(500, function () {
              $("#"+item.relationId).remove();
              $("#peopleInvite").hide();
            });
          }
          else {
            $("#"+item.relationId).fadeOut(500, function () {
              $("#"+item.relationId).remove();
              var count = parseInt($("#inviteCounter").html());
              $("#inviteCounter").html(count-1);
            });
          }
        });

        $("#"+item.relationId+" a.deny").live("click", function(){
          $.getJSON("/rest/homepage/intranet/people/contacts/deny/"+item.relationId, null);

          if($("#requests").children().length == 1) {
            $("#peopleInvite").fadeOut(500, function () {
              $("#"+item.relationId).remove();
              $("#peopleInvite").hide();
            });
          }
          else {
            $("#"+item.relationId).fadeOut(500, function () {
              $("#"+item.relationId).remove();
              var count = parseInt($("#inviteCounter").html());
              $("#inviteCounter").html(count-1);


            });
          }
        });

      }
      else{
        console.log(item.displayName);
        var link = "<li id='"+item.spaceId+"'>";
        link += "<div class='spaceInvitePicture' ><a href='#'><img src='"+item.avatarUrl+"'></a></div>";
        link += "<div class='spaceInviteInfo'>";
        link += "<div class='spaceInviteName'><a href='/portal/intranet/invitationSpace' target='_parent'>"+item.displayName+"</a></div>";
        link += "<div class='spaceproperties'><div class='spacevisibility'>"+item.visibility+" space - "+item.number+" Members </div><div class='spaceInviteAction' style='visibility:hidden;' ><button type='button' class='accept'>Accept</button> | <a class='deny' href='#' onclick='return false'><img src='/homepage-portlets/style/images/deny.png'></a></div> </div>"
        link += "</div></li>";

        $("#requests").append(link);
        $("#"+item.spaceId).mouseover(function() {  $("#"+item.spaceId+" .spacevisibility").addClass("actionSpaceAppears"); $("#"+item.spaceId+" .spaceInviteAction").css('visibility','visible'); });
        $("#"+item.spaceId).mouseout(function() { $("#"+item.spaceId+" .spacevisibility").removeClass("actionSpaceAppears"); $("#"+item.spaceId+" .spaceInviteAction").css('visibility','hidden'); });

        $("#"+item.spaceId+" button.accept").live("click", function(){
          $.getJSON("/rest/homepage/intranet/spaces/accept/"+item.spaceId, null);

          if($("#requests").children().length == 1) {
            $("#peopleInvite").fadeOut(500, function () {
              $("#"+item.spaceId).remove();
              $("#peopleInvite").hide();

            });
          }
          else {
            $("#"+item.spaceId).fadeOut(500, function () {
              $("#"+item.spaceId).remove();
              var count = parseInt($("#inviteCounter").html());
              $("#inviteCounter").html(count-1);
            });
          }
        });

        $("#"+item.spaceId+" a.deny").live("click", function(){
          $.getJSON("/rest/homepage/intranet/spaces/deny/"+item.spaceId, null);

          if($("#requests").children().length == 1) {
            $("#peopleInvite").fadeOut(500, function () {
              $("#"+item.spaceId).remove();
              $("#peopleInvite").hide();
            });
          }
          else {
            $("#"+item.spaceId).fadeOut(500, function () {
              $("#"+item.spaceId).remove();
              var count = parseInt($("#inviteCounter").html());
              $("#inviteCounter").html(count-1);
            });
          }
        });


      }
      $("#inviteCounter").html(i+1);
    });
  });
}

  $(document).ready(function() {
    initIncoming();
  });






