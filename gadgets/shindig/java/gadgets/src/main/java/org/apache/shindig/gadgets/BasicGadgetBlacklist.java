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

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Basic implementation of a {@code GadgetBlacklist}, reading blacklist data
 * from a text file.
 *
 * A single URL on a line blacklists this exact URL (case-insensitively).
 *
 * A regular expression prefixed by "REGEXP" and a space on a line blacklists
 * all URL's that (case-insensitively) match that pattern. The regular
 * expression syntax is PCRE-based (using java.util.regex, so there are a few
 * insignificant differences). Regular expressions should be used sparingly as
 * they influence performance of every single gadget being rendered.
 *
 * Lines starting with a "#" are comments.
 *
 * Example:
 *
 *   # Block the illegal bar.xml gadget
 *   http://foo.com/bar.xml
 *
 *   # Block all gadgets from the baz.com domain (including subdomains)
 *   REGEXP http://[^/]*baz.com/.*
 *
 */
public class BasicGadgetBlacklist implements GadgetBlacklist {

  private static final char COMMENT_MARKER = '#';
  private static final String REGEXP_PREFIX = "REGEXP";

  private final Set<String> exactMatches;
  private final List<Pattern> regexpMatches;

  /**
   * Constructs a new blacklist from the given file.
   *
   * @param blacklistFile file containing blacklist entries
   * @throws IOException if reading the file fails
   * @throws PatternSyntaxException if an invalid regular expression occurs in
   *    the file
   */
  public BasicGadgetBlacklist(File blacklistFile) throws IOException {
    exactMatches = new HashSet<String>();
    regexpMatches = new ArrayList<Pattern>();
    if (blacklistFile.exists()) {
      parseBlacklist(blacklistFile);
    }
  }

  @Inject
  public BasicGadgetBlacklist(@Named("blacklist.file") String file)
      throws IOException {
    this(new File(file));
  }

  private void parseBlacklist(File blacklistFile) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(blacklistFile));
    String line;
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0 || line.charAt(0) == COMMENT_MARKER) {
        continue;
      }

      String[] parts = line.split("\\s+");
      if (parts.length == 1) {
        exactMatches.add(line.toLowerCase());
      } else if (parts.length == 2
                 && parts[0].toUpperCase().equals(REGEXP_PREFIX)) {
        // compile will throw PatternSyntaxException on invalid patterns.
        regexpMatches.add(Pattern.compile(parts[1], Pattern.CASE_INSENSITIVE));
      }
    }
  }

  /** {@inheritDoc} */
  public boolean isBlacklisted(URI gadgetUri) {
    String uriString = gadgetUri.toString().toLowerCase();
    if (exactMatches.contains(uriString)) {
      return true;
    }
    for (Pattern pattern : regexpMatches) {
      if (pattern.matcher(uriString).matches()) {
        return true;
      }
    }
    return false;
  }

}
