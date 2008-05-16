/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.shindig.gadgets.spec;

import org.apache.shindig.gadgets.Substitutions;
import org.apache.shindig.util.XmlUtil;

import junit.framework.TestCase;

public class UserPrefTest extends TestCase {
  public void testBasic() throws Exception {
    String xml = "<UserPref" +
                 " name=\"name\"" +
                 " display_name=\"display_name\"" +
                 " default_value=\"default_value\"" +
                 " required=\"true\"" +
                 " datatype=\"hidden\"/>";
    UserPref userPref = new UserPref(XmlUtil.parse(xml));
    assertEquals("name", userPref.getName());
    assertEquals("display_name", userPref.getDisplayName());
    assertEquals("default_value", userPref.getDefaultValue());
    assertEquals(true, userPref.getRequired());
    assertEquals(UserPref.DataType.HIDDEN, userPref.getDataType());
  }

  public void testEnum() throws Exception {
    String xml = "<UserPref name=\"foo\" datatype=\"enum\">" +
                 " <EnumValue value=\"0\" display_value=\"Zero\"/>" +
                 " <EnumValue value=\"1\"/>" +
                 "</UserPref>";
    UserPref userPref = new UserPref(XmlUtil.parse(xml));
    assertEquals(2, userPref.getEnumValues().size());
    assertEquals("Zero", userPref.getEnumValues().get("0"));
    assertEquals("1", userPref.getEnumValues().get("1"));
  }

  public void testSubstitutions() throws Exception {
    String xml = "<UserPref name=\"name\" datatype=\"enum\"" +
                 " display_name=\"__MSG_display_name__\"" +
                 " default_value=\"__MSG_default_value__\">" +
                 " <EnumValue value=\"0\" display_value=\"__MSG_dv__\"/>" +
                 "</UserPref>";
    String displayName = "This is the display name";
    String defaultValue = "This is the default value";
    String displayValue = "This is the display value";
    Substitutions substituter = new Substitutions();
    substituter.addSubstitution(Substitutions.Type.MESSAGE,
        "display_name", displayName);
    substituter.addSubstitution(Substitutions.Type.MESSAGE,
        "default_value", defaultValue);
    substituter.addSubstitution(Substitutions.Type.MESSAGE, "dv", displayValue);
    UserPref userPref
        = new UserPref(XmlUtil.parse(xml)).substitute(substituter);
    assertEquals(displayName, userPref.getDisplayName());
    assertEquals(defaultValue, userPref.getDefaultValue());
    assertEquals(displayValue, userPref.getEnumValues().get("0"));
  }

  public void testMissingName() throws Exception {
    String xml = "<UserPref datatype=\"string\"/>";
    try {
      UserPref pref = new UserPref(XmlUtil.parse(xml));
      fail("No exception thrown when name is missing");
    } catch (SpecParserException e) {
      // OK
    }
  }

  public void testMissingDataType() throws Exception {
    String xml = "<UserPref name=\"name\"/>";
    UserPref pref = new UserPref(XmlUtil.parse(xml));
    assertEquals(UserPref.DataType.STRING, pref.getDataType());
  }

  public void testMissingEnumValue() throws Exception {
    String xml = "<UserPref name=\"foo\" datatype=\"enum\">" +
                 " <EnumValue/>" +
                 "</UserPref>";
    try {
      UserPref pref = new UserPref(XmlUtil.parse(xml));
      fail("No exception thrown when EnumValue@value is missing");
    } catch (SpecParserException e) {
      // OK
    }
  }
}
