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
package org.apache.shindig.social.opensocial;

import org.apache.shindig.social.ResponseItem;
import org.apache.shindig.gadgets.GadgetToken;

import java.util.List;
import java.util.Map;

public interface DataService {

  /**
   * Fetch data for a list of ids.
   * @param ids The list of ids
   * @param keys The list of keys to fetch
   * @param token The GadgetToken for this request
   * @return ResponseItem a response item with the error code set if
   *     there was a problem
   */
  public ResponseItem<Map<String, Map<String, String>>> getPersonData(
      List<String> ids, List<String> keys, GadgetToken token);

  /**
   * Updates the data key for the given person with the new value.
   *
   * @param id The person the data is for.
   * @param key The key of the data.
   * @param value The new value of the data.
   * @param token The GadgetToken for this request
   * @return ResponseItem a response item with the error code set if
   *     there was a problem
   */
  public ResponseItem updatePersonData(String id, String key,
      String value, GadgetToken token);
}
