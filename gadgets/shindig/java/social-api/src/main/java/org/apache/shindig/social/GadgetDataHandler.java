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

public interface GadgetDataHandler {
  /**
   * Determines whether this handler should be used to process the request
   *
   * @param requestType The type of request made
   * @return true if this handler should be called to create a response item for
   *     this json request
   */
  boolean shouldHandle(String requestType);

  /**
   * Constructs a ResponseItem based on the parameters in the RequestItem
   *
   * @param request The request from the json
   * @return The corresponding response item
   */
  ResponseItem handleRequest(RequestItem request);
}
