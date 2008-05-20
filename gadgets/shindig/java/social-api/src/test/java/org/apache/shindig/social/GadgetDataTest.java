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
package org.apache.shindig.social;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.shindig.social.opensocial.model.*;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class GadgetDataTest extends TestCase {
  private Person johnDoe;
  private Activity activity;

  @Override
  public void setUp() throws Exception {
    johnDoe = new Person("johnDoeId", new Name("John Doe"));
    List<Phone> phones = new ArrayList<Phone>();
    phones.add(new Phone("+33H000000000", "home"));
    phones.add(new Phone("+33M000000000", "mobile"));
    phones.add(new Phone("+33W000000000", "work"));
    johnDoe.setPhoneNumbers(phones);

    List<Address> addresses = new ArrayList<Address>();
    addresses.add(new Address("My home address"));
    johnDoe.setAddresses(addresses);

    List<Email> emails = new ArrayList<Email>();
    emails.add(new Email("john.doe@work.bar", "work"));
    emails.add(new Email("john.doe@home.bar", "home"));
    johnDoe.setEmails(emails);

    activity = new Activity("activityId", johnDoe.getId());

    List<MediaItem> mediaItems = new ArrayList<MediaItem>();
    mediaItems.add(new MediaItem("image/jpg", MediaItem.Type.IMAGE,
        "http://foo.bar"));
    activity.setMediaItems(mediaItems);
  }

  public static class SpecialPerson extends Person {
    private String newfield;

    public SpecialPerson(String id, String name, String newfield) {
      super(id, new Name(name));
      this.newfield = newfield;
    }

    public String getNewfield() {
      return newfield;
    }
  }

  public void testToJsonOnInheritedClass() throws Exception {
    SpecialPerson cassie = new SpecialPerson("5", "robot", "nonsense");

    JSONObject result = cassie.toJson();
    assertEquals(cassie.getId(), result.getString("id"));
    assertEquals(cassie.getNewfield(), result.getString("newfield"));
  }

  public void testPersonToJson() throws Exception {
    JSONObject result = johnDoe.toJson();

    assertEquals(johnDoe.getId(), result.getString("id"));

    assertEquals(johnDoe.getName().getUnstructured(),
        result.getJSONObject("name").getString("unstructured"));

    assertEquals(johnDoe.getAddresses().get(0).getUnstructuredAddress(),
        result.getJSONArray("addresses").getJSONObject(0)
            .getString("unstructuredAddress"));

    JSONArray phoneArray = result.getJSONArray("phoneNumbers");
    assertEquals(3, phoneArray.length());

    for (int i = 0; i < johnDoe.getPhoneNumbers().size(); i++) {
      Phone expectedPhone = johnDoe.getPhoneNumbers().get(i);
      JSONObject actualPhone = phoneArray.getJSONObject(i);
      assertEquals(expectedPhone.getType(), actualPhone.getString("type"));
      assertEquals(expectedPhone.getNumber(), actualPhone.getString("number"));
    }

    JSONArray emailArray = result.getJSONArray("emails");
    assertEquals(2, emailArray.length());

    for (int i = 0; i < johnDoe.getEmails().size(); i++) {
      Email expectedEmail = johnDoe.getEmails().get(i);
      JSONObject actualEmail = emailArray.getJSONObject(i);
      assertEquals(expectedEmail.getType(), actualEmail.getString("type"));
      assertEquals(expectedEmail.getAddress(),
          actualEmail.getString("address"));
    }
  }

  public void testActivityToJson() throws Exception {
    JSONObject result = activity.toJson();
    assertEquals(activity.getUserId(), result.getString("userId"));
    assertEquals(activity.getId(), result.getString("id"));

    JSONArray mediaItemsArray = result.getJSONArray("mediaItems");
    assertEquals(1, mediaItemsArray.length());

    MediaItem expectedItem = activity.getMediaItems().get(0);
    JSONObject actualItem = mediaItemsArray.getJSONObject(0);

    assertEquals(expectedItem.getUrl(), actualItem.getString("url"));
    assertEquals(expectedItem.getMimeType(), actualItem.getString("mimeType"));
    assertEquals(expectedItem.getType().toString(),
        actualItem.getString("type"));
  }

  public void testMapsToJson() throws Exception {
    Map<String, Map<String, String>> map =
        new HashMap<String, Map<String, String>>();

    Map<String, String> item1Map = new HashMap<String, String>();
    item1Map.put("value", "1");
    map.put("item1", item1Map);

    Map<String, String> item2Map = new HashMap<String, String>();
    item2Map.put("value", "2");
    map.put("item2", item2Map);

    ResponseItem response
        = new ResponseItem<Map<String, Map<String, String>>>(map);
    JSONObject result = response.toJson();

    JSONObject jsonMap = result.getJSONObject("response");
    assertEquals("1", jsonMap.getJSONObject("item1").getString("value"));
    assertEquals("2", jsonMap.getJSONObject("item2").getString("value"));
  }

  public void testMandatoryFields() throws Exception {
    Person noIdMan = new Person(null, new Name("noIdMan"));
    try {
      noIdMan.toJson();
      fail("Expected a person without an id to throw an exception");
    } catch (Exception e) {
      // The exception should be thrown
      assertEquals("id is a mandory value, it should not be null",
          e.getMessage());
    }
  }

}
