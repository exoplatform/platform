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

import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.UserPref;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Substitutes user prefs into the spec.
 */
public class UserPrefSubstituter {
  public static void addSubstitutions(Substitutions substituter,
      GadgetSpec spec, UserPrefs values) {
    for (UserPref pref : spec.getUserPrefs()) {
      String name = pref.getName();
      String value = values.getPref(name);
      if (value == null) {
        value = pref.getDefaultValue();
        if (value == null) {
          value = "";
        }
      }
      substituter.addSubstitution(Substitutions.Type.USER_PREF, name,
          StringEscapeUtils.escapeHtml(value));
    }
  }
}
