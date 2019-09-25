package org.exoplatform.platform.portlet.rss;

import com.google.common.base.Strings;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.exoplatform.platform.portlet.rss.model.FeedItem;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.portlet.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RssReaderPortlet extends GenericPortlet {

    private static final Log LOG = ExoLogger.getExoLogger(RssReaderPortlet.class);

    public static final String RSS_URL = "RSS_URL";
    public static final String LIMIT_ENTRIES = "LIMIT_ENTRIES";

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletPreferences preferences = request.getPreferences();

        String url = preferences.getValue(RSS_URL, "http://feeds.feedburner.com/gatein");
        int limit;
        try {
            limit = Integer.parseInt(preferences.getValue(LIMIT_ENTRIES, "10"));
        } catch (NumberFormatException ex) {
            limit = 10;
        }
        if (limit < 1) {
            limit = 1;
        }
        if (limit > 100) {
            limit = 100;
        }

        String feedFavicon = this.getFavicon(url);
        String feedTitle = "";
        String feedDescription = "";
        String feedLink = "";
        List<FeedItem> entries = new ArrayList<>();

        try {
            URL feedSource = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            feedTitle = Strings.nullToEmpty(feed.getTitle());
            feedDescription = Strings.nullToEmpty(feed.getDescription());
            feedLink = Strings.nullToEmpty(feed.getLink());
            entries = this.getEntries(feed, limit);

        } catch (IOException | FeedException ex) {

        }

        request.setAttribute("feedFavicon", feedFavicon);
        request.setAttribute("feedEntries", entries);
        request.setAttribute("feedTitle", feedTitle);
        request.setAttribute("feedDescription", feedDescription);
        request.setAttribute("feedLink", feedLink);

        PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/rss-reader/index.jsp");
        prDispatcher.include(request, response);
    }

    /**
     *
     * @param feed
     * @param limit
     * @return
     */
    private List<FeedItem> getEntries(SyndFeed feed, int limit) {
        List<FeedItem> entries = new ArrayList<>();

        for (Object obj : feed.getEntries()) {

            if (entries.size() >= limit) {
                break;
            }

            SyndEntry e = (SyndEntry) obj;
            FeedItem entry = new FeedItem();

            //
            entry.setTitle(e.getTitle());


            //
            String link = e.getLink();
            if (link == null) {
                List<SyndLink> links = e.getLinks();
                if (links != null && !links.isEmpty()) {
                    link = links.get(0).getHref();
                }
            }
            entry.setLink(link);

            //
            String summary;
            if (e.getContents() != null && !e.getContents().isEmpty()) {
                summary = ((SyndContent) e.getContents().get(0)).getValue();
            } else {
                summary = e.getDescription() != null ? e.getDescription().getValue() : "";
            }
            entry.setSummary(summary);

            //
            Date date;
            if (e.getUpdatedDate() != null) {
                date = e.getUpdatedDate();
            } else if (e.getPublishedDate() != null) {
                date = e.getPublishedDate();
            } else {
                date = null;
            }
            entry.setDate(date);

            entries.add(entry);
        }

        return entries;
    }

    private String getFavicon(String feedUrl) {
        Pattern p = Pattern.compile(":\\/\\/(www\\.)?([^\\/:]+)");

        Matcher m = p.matcher(feedUrl);
        String favicon = m.find(2) ? m.group(2) : "";
        favicon = "http://"+favicon+"/favicon.ico";
        return favicon;
    }
}
