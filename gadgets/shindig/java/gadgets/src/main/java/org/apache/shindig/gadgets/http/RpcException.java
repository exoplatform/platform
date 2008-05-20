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

package org.apache.shindig.gadgets.http;

import org.apache.shindig.gadgets.GadgetContext;

/**
 * Contains RPC-specific exceptions.
 */
public class RpcException extends Exception {
  private final GadgetContext context;

  public GadgetContext getContext() {
    return context;
  }

  public RpcException(String message) {
    super(message);
    context = null;
  }

  public RpcException(String message, Throwable cause) {
    super(message, cause);
    context = null;
  }

  public RpcException(GadgetContext context, Throwable cause) {
    super(cause);
    this.context = context;
  }

  public RpcException(GadgetContext context, String message) {
    super(message);
    this.context = context;
  }

  public RpcException(GadgetContext context, String message, Throwable cause) {
    super(message, cause);
    this.context = context;
  }
}
