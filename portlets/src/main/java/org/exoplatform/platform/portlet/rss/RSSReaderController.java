/**
 * Copyright ( C ) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.platform.portlet.rss;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import com.google.common.base.Strings;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.template.Template;
import org.exoplatform.platform.portlet.rss.model.FeedItem;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RSSReaderController {

    private static final Log LOG = ExoLogger.getExoLogger(RSSReaderController.class);

    public static final String RSS_URL = "RSS_URL";
    public static final String LIMIT_ENTRIES = "LIMIT_ENTRIES";

    @Inject
    javax.portlet.PortletPreferences preferences;

    @Inject
    @Path("index.gtmpl")
    org.exoplatform.platform.portlet.rss.templates.index index;

    @View
    public Response index() {
        String url = this.preferences.getValue(RSS_URL, "http://feeds.feedburner.com/gatein");
        int limit;
        try {
            limit = Integer.parseInt(this.preferences.getValue(LIMIT_ENTRIES, "10"));
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

        return index.with()
                .feedUrl(url)
                .feedFavicon(feedFavicon)
                .feedTitle(feedTitle)
                .feedDescription(feedDescription)
                .feedLink(feedLink)
                .feedEntries(entries)
                .ok();
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
