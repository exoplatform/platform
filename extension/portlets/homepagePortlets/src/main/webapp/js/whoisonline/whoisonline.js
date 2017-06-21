(function($) {
  var glables = null;

  var refresh = function () {
    $.getJSON($("#OnlinePortlet").data("url"), function (users) {
      if (users.length > 0) {
        var txt = '';
        users.forEach(function (user) {
          txt += '<li id="' + user.id + '">' +
            '<a class="avatarXSmall" href="' + user.profileUrl + '"><img src="' + user.avatar + '" alt="image" /></a>' +
            '</li>';
        });
        $('#onlineList').html(txt);

        $("#OnlinePortlet").show();
      } else {
        $("#OnlinePortlet").hide();
      }

      // User Profile Popup initialize
      var portal = eXo.social.portal;
      var restUrl = '//' + window.location.host + portal.context + '/' + portal.rest + '/social/people/getPeopleInfo/{0}.json';

      $('#onlineList').find('a').each(function (idx, el) {
        $(el).userPopup({
          restURL: restUrl,
          labels: glables,
          content: false,
          defaultPosition: "left",
          keepAlive: true,
          maxWidth: "240px"
        });
      });
    });
  };

  // And we should use setInterval with 60 seconds
  setInterval(refresh, 60000);
  return {
    ajaxWhoIsOnLine: function (labels) {
      glables = labels;
      refresh();
    }
  };
})($);
