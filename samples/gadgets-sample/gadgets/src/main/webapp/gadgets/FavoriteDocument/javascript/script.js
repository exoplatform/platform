
function DisplayTime() {};

DisplayTime.prototype.timeToPrettyString = function(B) {
    if (isNaN(B)) {
        return "an indeterminate amount of time ago"
    }
    time = (new Date().getTime() - B) / 1000;
    if (time < 60) {
        return "less than a minute ago"
    } else {
        if (time < 120) {
            return "about a minute ago"
        } else {
            if (time < 3600) {
                var A = Math.round(time / 60);
                return "about " + A + " minutes ago"
            } else {
                if (time < 7200) {
                    return "about an hour ago"
                } else {
                    if (time < 86400) {
                        var A = Math.round(time / 3600);
                        return "about " + A + " hours ago"
                    } else {
                        if (time < 172800) {
                            return "about a day ago"
                        } else {
                            if (time < 2592000) {
                                var A = Math.round(time / 86400);
                                return "about " + A + " days ago"
                            } else {
                                if (time < 5184000) {
                                    return "about a month ago"
                                } else {
                                    var A = Math.round(time / 2592000);
                                    return "about " + A + " months ago"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}