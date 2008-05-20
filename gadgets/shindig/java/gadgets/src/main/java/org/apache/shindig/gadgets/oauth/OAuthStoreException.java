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
package org.apache.shindig.gadgets.oauth;

import org.apache.shindig.gadgets.GadgetException;

/**
 * Base class of exception that can be thrown when interacting with the
 * OAuthStore.
 */
public class OAuthStoreException extends GadgetException {

  public OAuthStoreException(String message) {
    super(GadgetException.Code.OAUTH_STORAGE_ERROR, message);
  }

  public OAuthStoreException(String message, Throwable cause) {
    super(GadgetException.Code.OAUTH_STORAGE_ERROR, message, cause);
  }

  public OAuthStoreException(Throwable cause) {
    super(GadgetException.Code.OAUTH_STORAGE_ERROR, cause);
  }
}
