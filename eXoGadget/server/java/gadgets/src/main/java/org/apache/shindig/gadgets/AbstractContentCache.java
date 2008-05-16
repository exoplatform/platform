/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.gadgets;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Base class for content caches. Defines cache expiration rules and
 * and restrictions on allowed content.
 *
 * TODO: Move cache checking code into HttpUtil
 */
public abstract class AbstractContentCache implements ContentCache {

  /**
   * Used to parse Expires: header.
   */
  private final static DateFormat dateFormat
      = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

  public final RemoteContent getContent(RemoteContentRequest request) {
    if (canCacheRequest(request)) {
      return getContent(request.getUri());
    }
    return null;
  }

  public final RemoteContent getContent(URI uri) {
    if (uri == null) return null;
    return checkContent(getContentImpl(uri));
  }

  protected abstract RemoteContent getContentImpl(URI uri);

  public void addContent(RemoteContentRequest request, RemoteContent content) {
    if (canCacheRequest(request)) {
      addContent(request.getUri(), content);
    }
  }

  public void addContent(URI uri, RemoteContent content) {
    content = checkContent(content);
    if (uri == null || content == null) return;
    // Clone the URI to prevent outside references from preventing collection
    addContentImpl(URI.create(uri.toString()), content);
  }

  protected abstract void addContentImpl(URI uri, RemoteContent content);

  public RemoteContent removeContent(RemoteContentRequest request) {
    return removeContent(request.getUri());
  }

  public RemoteContent removeContent(URI uri) {
    if (uri == null) return null;
    RemoteContent content = getContentImpl(uri);
    removeContentImpl(uri);
    return checkContent(content);
  }

  protected abstract RemoteContent removeContentImpl(URI uri);

  /**
   * Utility function to verify that an entry is cacheable and not expired
   * Returns null if the content is no longer cacheable.
   *
   * @param request
   * @return content or null
   */
  protected boolean canCacheRequest(RemoteContentRequest request) {
    return ("GET".equals(request.getMethod()) &&
        !request.getOptions().ignoreCache);
  }

  /**
   * Utility function to verify that an entry is cacheable and not expired
   * Returns null if the content is no longer cacheable.
   *
   * @param content
   * @return content or null
   */
  protected RemoteContent checkContent(RemoteContent content) {
    if (content == null) return null;

    if (content.getHttpStatusCode() != 200) return null;

    long now = System.currentTimeMillis();

    String expires = content.getHeader("Expires");
    if (expires != null) {
      try {
        Date expiresDate = dateFormat.parse(expires);
        long expiresMs = expiresDate.getTime();
        if (expiresMs > now) {
          return content;
        } else {
          return null;
        }
      } catch (ParseException e) {
        return null;
      }
    }

    // Cache-Control headers may be an explicit max-age, or no-cache, which
    // means we use a default expiration time.
    String cacheControl = content.getHeader("Cache-Control");
    if (cacheControl != null) {
      String[] directives = cacheControl.split(",");
      for (String directive : directives) {
        directive = directive.trim();
        // boolean params
        if (directive.equals("no-cache")) {
          return null;
        }
        if (directive.startsWith("max-age")) {
          String[] parts = directive.split("=");
          if (parts.length == 2) {
            try {
              // Record the max-age and store it in the content as an
              // absolute expiration
              long maxAgeMs = Long.parseLong(parts[1]) * 1000;
              Date newExpiry = new Date(now + maxAgeMs);
              content.getAllHeaders()
                  .put("Expires", Arrays.asList(dateFormat.format(newExpiry)));
              return content;
            } catch (NumberFormatException e) {
              return null;
            }
          }
        }
      }
    }

    // Look for Pragma: no-cache. If present, return null.
    List<String> pragmas = content.getHeaders("Pragma");
    if (pragmas != null) {
      for (String pragma : pragmas) {
        if ("no-cache".equals(pragma)) {
          return null;
        }
      }
    }

    // Assume the content is cacheable for the default TTL
    // if no other directives exist
    Date newExpiry = new Date(now + getDefaultTTL());
    content.getAllHeaders()
        .put("Expires", Arrays.asList(dateFormat.format(newExpiry)));
    return content;
  }

  /**
   * Default TTL for an entry in the cache that does not have any
   * cache controlling headers
   * @return default TTL for cache entries
   */
  protected long getDefaultTTL() {
    // 5 mins
    return 5L * 60L * 1000L;
  }
}
