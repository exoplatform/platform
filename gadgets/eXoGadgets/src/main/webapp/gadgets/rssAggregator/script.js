function RssAggregator() {
  this.feed = {};
}

RssAggregator.prototype.getFavicon = function(feedurl) {
    var favicon = feedurl.match( /:\/\/(www\.)?([^\/:]+)/ );
    favicon = favicon[2]?favicon[2]:'';
    favicon = "http://"+favicon+"/favicon.ico";
    return favicon;
}

RssAggregator.prototype.toggleDescription = function(elmnt_id) {
    if (_gel('more_'+elmnt_id).style.display == 'none') {
        _gel('more_'+elmnt_id).style.display = '';
        _gel('item_'+elmnt_id).className = 'item descriptionHighlight';
    } else {
        _gel('more_'+elmnt_id).style.display = 'none';
        _gel('item_'+elmnt_id).className = 'item';
    }
    gadgets.window.adjustHeight();
}

RssAggregator.prototype.timeToPrettyString = function(B) {
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

RssAggregator.prototype.renderFeed = function(feed) {
    this.feed = feed;
    gadgets.window.setTitle("RSS: " + feed.Title);
    var feedEl = _gel("feedContainer");
	var bullet = "<img src='" + this.getFavicon(feedurl) + "' alt='' border=0 align='absmiddle' style='height:16;width:16;' onerror='this.style.visibility=\"hidden\";'>&nbsp;&nbsp;";

    if (feed != null) {
        // Access the data for a given entry
        if (feed.Entry) {
            for (var i = 0; i < feed.Entry.length; i++) {
                var itemEl = document.createElement('div');
                var item_title = document.createElement('div');
                var item_more = document.createElement('div');
                var item_desc = document.createElement('div');
                var item_date = document.createElement('div');
                var item_link = document.createElement('div');

                itemEl.id = 'item_'+i;
                item_title.id = 'title_'+i;
                item_more.id = 'more_'+i;
                item_more.style.display='none';
                item_desc.id = 'desc_'+i;
                item_date.id = 'date_'+i;
                item_link.id = 'link_'+i;


				itemEl.className = 'item';
                item_title.className = 'title';
                item_more.className = 'more';
                item_desc.className = 'desc';
                item_date.className = 'date';
                item_link.className = 'link';

                item_title.innerHTML = bullet + "<a id='link_title_"+i+"' class='titlelink' href='" + feed.Entry[i].Link + "' onclick='rssAggregator.toggleDescription("+i+");return false;'>" + feed.Entry[i].Title + "</a>";
				item_date.innerHTML = this.timeToPrettyString(feed.Entry[i].Date);


				item_desc.innerHTML = feed.Entry[i].Summary;

                item_link.innerHTML = this.generateLinkContent(i);


                item_more.appendChild(item_date);
                item_more.appendChild(item_desc);
                item_more.appendChild(item_link);


                itemEl.appendChild(item_title);
                itemEl.appendChild(item_more);

                feedEl.appendChild(itemEl);
            }
		}
    } else {
        document.write("No feed found at " + feedurl);
    }
    gadgets.window.adjustHeight();
}

RssAggregator.prototype.generateLinkContent = function(i) {
  return "<a href='" + this.feed.Entry[i].Link + "' target='_blank'>view link &raquo;</a>";
}

RssAggregator.prototype.refreshFeed = function() {
	_IG_FetchFeedAsJSON(prefs.getString("rssurl"), function(feed) {rssAggregator.renderFeed(feed);}, entries, true);
}

