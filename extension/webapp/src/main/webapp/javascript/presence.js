(function($) {
    var obj = {};

    var url = eXo.env.server.context + "/" + eXo.env.portal.rest + "/state/ping";
    obj.sendPing = function(frequency) {
        $.ajax({
            url: url,
            dataType: "json",
            context: this,
            success: function(data){
                setTimeout(obj.sendPing, frequency, frequency);
            },
            error: function(xhr, status){
                if (xhr.status >= 500) {
                    setTimeout(obj.sendPing, frequency * 2, frequency);
                } else {
                    console.log("Last ping returns a status code " + xhr.status + ", stopping");
                }
            }
        });
    }

    return obj;
})($);
