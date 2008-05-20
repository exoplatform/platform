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

import org.apache.shindig.util.InputStreamConsumer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Implementation of a {@code RemoteObjectFetcher} using standard java.net
 * classes. Only one instance of this should be present at any time, so we
 * annotate it as a Singleton to resolve Guice injection limitations.
 */
@Singleton
public class BasicRemoteContentFetcher implements ContentFetcher {
  private static final int CONNECT_TIMEOUT_MS = 5000;
  private static final int DEFAULT_MAX_OBJECT_SIZE = 1024 * 1024;

  private final int maxObjSize;
  private final ContentCache cache;

  /**
   * Creates a new fetcher capable of retrieving objects {@code maxObjSize}
   * bytes or smaller in size.
   * @param maxObjSize Maximum size, in bytes, of object to fetch
   */
  public BasicRemoteContentFetcher(ContentCache cache, int maxObjSize) {
    this.maxObjSize = maxObjSize;
    this.cache = cache;
  }

  /**
   * Creates a new fetcher using the default maximum object size.
   */
  @Inject
  public BasicRemoteContentFetcher(ContentCache cache) {
    this(cache, DEFAULT_MAX_OBJECT_SIZE);
  }

  /**
   * Initializes the connection.
   *
   * @param request
   * @return The opened connection
   * @throws IOException
   */
  private URLConnection getConnection(RemoteContentRequest request)
      throws IOException {
    URLConnection fetcher;
    fetcher = request.getUri().toURL().openConnection();
    fetcher.setConnectTimeout(CONNECT_TIMEOUT_MS);
    fetcher.setRequestProperty("Accept-Encoding", "gzip, deflate");
    if (fetcher instanceof HttpURLConnection) {
      ((HttpURLConnection)fetcher).setInstanceFollowRedirects(true);
      Map<String, List<String>> reqHeaders = request.getAllHeaders();
      for (Map.Entry<String, List<String>> entry : reqHeaders.entrySet()) {
        List<String> value = entry.getValue();
        if (value.size() == 1) {
          fetcher.setRequestProperty(entry.getKey(), value.get(0));
        } else {
          StringBuilder headerList = new StringBuilder();
          boolean first = false;
          for (String val : value) {
            if (!first) {
              first = true;
            } else {
              headerList.append(',');
            }
            headerList.append(val);
          }
          fetcher.setRequestProperty(entry.getKey(), headerList.toString());
        }
      }
    }
    fetcher.setDefaultUseCaches(!request.getOptions().ignoreCache);
    return fetcher;
  }

  /**
   * @param fetcher
   * @return A RemoteContent object made by consuming the response of the
   *     given HttpURLConnection.
   */
  private RemoteContent makeResponse(URLConnection fetcher)
      throws IOException {
    Map<String, List<String>> headers = fetcher.getHeaderFields();
    int responseCode;
    if (fetcher instanceof HttpURLConnection) {
      responseCode = ((HttpURLConnection)fetcher).getResponseCode();
    } else {
      responseCode = RemoteContent.SC_OK;
    }

    String encoding = fetcher.getContentEncoding();
    InputStream is = null;
    // Create the appropriate stream wrapper based on the encoding type.
    if (encoding == null) {
      is =  fetcher.getInputStream();
    } else if (encoding.equalsIgnoreCase("gzip")) {
      is = new GZIPInputStream(fetcher.getInputStream());
    } else if (encoding.equalsIgnoreCase("deflate")) {
      Inflater inflater = new Inflater(true);
      is = new InflaterInputStream(fetcher.getInputStream(), inflater);
    }

    byte[] body = InputStreamConsumer.readToByteArray(is, maxObjSize);
    return new RemoteContent(responseCode, body, headers);
  }

  /** {@inheritDoc} */
  public RemoteContent fetch(RemoteContentRequest request) {
    RemoteContent content = cache.getContent(request);
    if (content != null) return content;
    try {
      URLConnection fetcher = getConnection(request);
      if ("POST".equals(request.getMethod()) &&
          fetcher instanceof HttpURLConnection) {
        ((HttpURLConnection)fetcher).setRequestMethod("POST");
        fetcher.setRequestProperty("Content-Length",
                                   String.valueOf(request.getPostBodyLength()));
        fetcher.setUseCaches(false);
        fetcher.setDoInput(true);
        fetcher.setDoOutput(true);
        InputStreamConsumer.pipe(request.getPostBody(),
                                 fetcher.getOutputStream());
      }
      content = makeResponse(fetcher);
      cache.addContent(request, content);
      return content;
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        return RemoteContent.NOT_FOUND;
      }
      return RemoteContent.ERROR;
    }
  }
}
