/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.gadgets;

import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.regex.PatternSyntaxException;

public class BasicGadgetBlacklistTest extends TestCase {

  private URI someUri;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    someUri = new URI("http://bla.com/foo.xml");
  }

  private GadgetBlacklist createBlacklist(String contents) throws IOException {
    File temp = File.createTempFile("blacklist_test", ".txt");
    temp.deleteOnExit();
    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
    out.write(contents);
    out.close();
    return new BasicGadgetBlacklist(temp);
  }

  public void testEmptyBlacklist() throws Exception {
    GadgetBlacklist bl = createBlacklist("");
    assertFalse(bl.isBlacklisted(someUri));
  }

  public void testExactMatches() throws Exception {
    GadgetBlacklist bl = createBlacklist(someUri + "\nhttp://baz.com/foo.xml");
    assertFalse(bl.isBlacklisted(new URI("http://random.com/uri.xml")));
    assertTrue(bl.isBlacklisted(someUri));
  }

  public void testExactMatchesWithCaseMixture() throws Exception {
    GadgetBlacklist bl = createBlacklist(someUri + "\nhttp://BAZ.com/foo.xml");
    assertTrue(bl.isBlacklisted(someUri));
    assertTrue(bl.isBlacklisted(new URI("http://BLA.com/foo.xml")));
    assertTrue(bl.isBlacklisted(new URI("http://baz.com/foo.xml")));
  }

  public void testIgnoredCommentsAndWhitespace() throws Exception {
    GadgetBlacklist bl = createBlacklist(
        "# comment\n  \t" + someUri + " \n  # comment\n\n");
    assertTrue(bl.isBlacklisted(someUri));
  }

  public void testRegexpMatches() throws Exception {
    GadgetBlacklist bl = createBlacklist("REGEXP http://bla.com/.*");
    assertTrue(bl.isBlacklisted(someUri));
    assertTrue(bl.isBlacklisted(new URI("http://bla.com/bar.xml")));
    assertFalse(bl.isBlacklisted(new URI("http://blo.com/bar.xml")));
  }

  public void testInvalidRegularExpression() throws Exception {
    try {
      GadgetBlacklist bl = createBlacklist("REGEXP +http://bla.com/.*");
      fail();
    } catch (PatternSyntaxException ex) {
      // success
    }
  }

}
