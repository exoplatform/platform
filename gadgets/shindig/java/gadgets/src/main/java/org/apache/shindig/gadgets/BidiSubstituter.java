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

/**
 * Provides static hangman substitutions for bidirectional language support.
 * Useful for generating internationalized layouts using CSS.
 */
public class BidiSubstituter {

  public static void addSubstitutions(Substitutions substituter, String dir) {
    boolean rtl = "rtl".equals(dir);
    substituter.addSubstitution(Substitutions.Type.BIDI, "START_EDGE",
                          rtl ? "right" : "left");
    substituter.addSubstitution(Substitutions.Type.BIDI, "END_EDGE",
                          rtl ? "left" : "right");
    substituter.addSubstitution(Substitutions.Type.BIDI, "DIR",
                          rtl ? "rtl" : "ltr");
    substituter.addSubstitution(Substitutions.Type.BIDI, "REVERSE_DIR",
                          rtl ? "ltr" : "rtl");
  }
}
