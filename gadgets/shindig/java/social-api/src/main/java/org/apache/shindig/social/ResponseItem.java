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

/**
 * Represents the response items that get handed back as json within the
 * DataResponse
 */
public class ResponseItem<T> extends AbstractGadgetData {
  private ResponseError error;
  private String errorMessage;

  // Must be compatible with AbstractSocialData.toJson. This means it should be
  // an AbstractSocialData or a collection of AbstractSocialData
  private T response;

  public ResponseItem(ResponseError error, String errorMessage, T response) {
    this.error = error;
    this.errorMessage = errorMessage;
    this.response = response;
  }

  public ResponseItem(T response) {
    this(null, null, response);
  }

  public ResponseError getError() {
    return error;
  }

  public void setError(ResponseError error) {
    this.error = error;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Mandatory
  public T getResponse() {
    return response;
  }

  public void setResponse(T response) {
    this.response = response;
  }
}