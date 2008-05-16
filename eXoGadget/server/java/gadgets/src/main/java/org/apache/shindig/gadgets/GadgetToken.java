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
package org.apache.shindig.gadgets;

/**
 * An abstract representation of a signing token.
 * Use in conjunction with @code GadgetTokenDecoder.
 */
public interface GadgetToken {

  /**
   * Serializes the token into a string. This can be the exact same as
   * toString; using a different name here is only to force interface
   * compliance.
   *
   * @return A string representation of the token.
   */
  public String toSerialForm();

  /**
   * @return the owner from the token, or null if there is none.
   */
  public String getOwnerId();

  /**
   * @return the viewer from the token, or null if there is none.
   */
  public String getViewerId();

  /**
   * @return the application id from the token, or null if there is none.
   */
  public String getAppId();
  
  /**
   * @return the domain from the token, or null if there is none.
   */
  public String getDomain();

  /**
   * @return the URL of the application
   */
  public String getAppUrl();

  /**
   * @return the module ID of the application
   */
  public long getModuleId();
}
