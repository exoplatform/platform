(function($) {
    var obj = {};
    var url = eXo.env.server.context + "/" + eXo.env.portal.rest + "/state/ping";
    obj.sendPing = function(frequency) {
        obj.sendSinglePing();
        setInterval(function() {
            if(!obj.error) {
                obj.sendSinglePing();
            }}, frequency);
    }
    obj.sendSinglePing = function() {
        $.ajax({
            url: url,
            context: this,
            error: function(xhr){
                if (xhr && xhr.status != 200) {
                    console.log("Last ping returns a status code " + xhr.status + ", stopping");
                    this.error = true;
                }
            }
        });
    };
    return obj;
})($);
