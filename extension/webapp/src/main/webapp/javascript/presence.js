(function($) {
    var obj = {};

    obj.sendPing = function(frequency) {
        $.ajax({
            url: "/rest/state/ping/",
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
