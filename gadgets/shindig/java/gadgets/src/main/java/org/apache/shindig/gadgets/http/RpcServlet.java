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

import org.apache.shindig.util.InputStreamConsumer;

import com.google.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles RPC metadata requests.
 */
public class RpcServlet extends InjectedServlet {
  private static final int MAX_REQUEST_SIZE = 1024 * 128;
  private static final Logger logger
      = Logger.getLogger("org.apache.shindig.gadgets");

  private JsonRpcHandler jsonHandler;
  @Inject
  public void setJsonRpcHandler(JsonRpcHandler jsonHandler) {
    this.jsonHandler = jsonHandler;
  }


  @Override
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    int length = request.getContentLength();
    if (length <= 0) {
      logger.info("No Content-Length specified.");
      response.setStatus(HttpServletResponse.SC_LENGTH_REQUIRED);
      return;
    }
    if (length > MAX_REQUEST_SIZE) {
      logger.info("Request size too large: " + length);
      response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
      return;
    }

    ServletInputStream is = request.getInputStream();
    byte[] body = InputStreamConsumer.readToByteArray(is, length);
    if (body.length != length) {
      logger.info("Wrong size. Length: " + length + " real: " + body.length);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    try {
      String encoding = request.getCharacterEncoding();
      if (encoding == null) {
        encoding = "UTF-8";
      }
      JSONObject req = new JSONObject(new String(body, encoding));

      JSONObject resp = jsonHandler.process(req);
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json; charset=utf-8");
      response.setHeader("Content-Disposition", "attachment;filename=rpc.txt");
      response.getWriter().write(resp.toString());
    } catch (UnsupportedEncodingException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("Unsupported input character set");
      logger.log(Level.INFO, e.getMessage(), e);
    } catch (JSONException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("Malformed JSON request.");
    } catch (RpcException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().write(e.getMessage());
      logger.log(Level.INFO, e.getMessage(), e);
    }
  }
}
