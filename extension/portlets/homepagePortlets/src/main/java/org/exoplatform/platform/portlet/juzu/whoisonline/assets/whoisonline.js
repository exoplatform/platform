$(function() {
  var refresh = function() {
    $("#OnlinePortlet").each(function() {
      $(this).jzLoad("WhoIsOnLineController.users()");
    });
  };
  // Wait 1/2 second (not realistic of course)
  // And we should use setInterval with 60 seconds
  setTimeout(refresh, 500);
});