/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.shindig.social.abdera;

import junit.framework.Assert;

import org.apache.shindig.social.JettyServer;
import org.apache.shindig.social.RestServerServlet;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;


public class SocialApiProviderLargeTest extends Assert {

  private static JettyServer server;
  private static Abdera abdera = Abdera.getInstance();
  private static AbderaClient client = new AbderaClient();

  private static String BASE = "http://localhost:9002/social/rest/";

  @BeforeClass
  public static void setUp() throws Exception {
    try {
      server = new JettyServer();
      server.start(new RestServerServlet(), "/social/rest/*");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static void tearDown() throws Exception {
    server.stop();
  }

// TODO this test cannot pass without the state file resource.
//      the XmlStateFileFetcher needs to be mocked out
//
  @Test
  public void testGetConnectionsForJohnDoe() throws IOException {
//    ClientResponse resp = client.get(BASE + "people");
//    checkForGoodAtomResponse(resp);
//    Document<Feed> doc = resp.getDocument();
//    Feed feed = doc.getRoot();
//    assertEquals(feed.getTitle(), "People Collection title");
//    //prettyPrint(doc);
//    resp.release();
  }

// TODO this test cannot pass without the state file resource.
//      the XmlStateFileFetcher needs to be mocked out
//
//  @Test
//  public void testGetJaneDoeProfileForJohnDoe() throws IOException {
//    ClientResponse resp = client.get(BASE + "people/john.doe/@all/jane.doe");
//    checkForGoodAtomResponse(resp);
//    Document<Entry> doc = resp.getDocument();
//    Entry entry = doc.getRoot();
//    assertEquals(entry.getTitle(), "Jane Doe");
//    prettyPrint(doc);
//    resp.release();
//  }

  protected void checkForGoodAtomResponse(ClientResponse response){
    assertNotNull(response);
    assertEquals(ResponseType.SUCCESS, response.getType());
    assertTrue(MimeTypeHelper.isMatch(response.getContentType().toString(),
        Constants.ATOM_MEDIA_TYPE));
  }

  protected void prettyPrint(Base doc) throws IOException {
    WriterFactory writerFactory = abdera.getWriterFactory();
    Writer writer = writerFactory.getWriter("prettyxml");
    writer.writeTo(doc, System.out);
    System.out.println();
  }

}
