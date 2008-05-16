/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.gadgets.http;

import org.apache.shindig.gadgets.GadgetContentFilter;
import org.apache.shindig.gadgets.GadgetException;

import com.google.caja.lexer.ExternalReference;
import com.google.caja.opensocial.DefaultGadgetRewriter;
import com.google.caja.opensocial.GadgetContentRewriter;
import com.google.caja.opensocial.UriCallback;
import com.google.caja.opensocial.GadgetRewriteException;
import com.google.caja.opensocial.UriCallbackException;
import com.google.caja.opensocial.UriCallbackOption;
import com.google.caja.reporting.SimpleMessageQueue;
import com.google.caja.reporting.MessageQueue;
import com.google.caja.reporting.Message;
import com.google.caja.reporting.MessageContext;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class CajaContentFilter implements GadgetContentFilter {
  private final URI retrievedUri;

  public CajaContentFilter(URI retrievedUri) {
    this.retrievedUri = retrievedUri;
  }

  public String filter(String content) throws GadgetException {
    UriCallback cb = new UriCallback() {
      public UriCallbackOption getOption(ExternalReference externalReference,
                                         String string) {
        return UriCallbackOption.REWRITE;
      }

      public Reader retrieve(ExternalReference externalReference, String string)
          throws UriCallbackException {
        throw new UriCallbackException(externalReference);
      }

      public URI rewrite(ExternalReference externalReference, String string) {
        return externalReference.getUri();
      }
    };

    MessageQueue mq = new SimpleMessageQueue();
    GadgetContentRewriter rw = new DefaultGadgetRewriter(mq);
    Readable input = new StringReader(content);
    Appendable output = new StringBuilder();

    try {
      rw.rewriteContent(retrievedUri, input, cb, output);
    } catch (GadgetRewriteException e) {
      throwCajolingException(e, mq);
    } catch (UriCallbackException e) {
      throwCajolingException(e, mq);
    } catch (IOException e) {
      throwCajolingException(e, mq);
    }

    return output.toString();
  }

  private void throwCajolingException(Exception cause, MessageQueue mq)
      throws GadgetException {
    StringBuilder errbuilder = new StringBuilder();
    MessageContext mc = new MessageContext();
    List<GadgetException> exceptions = new ArrayList<GadgetException>();

    if (cause != null) {
      errbuilder.append(cause).append('\n');
    }

    for (Message m : mq.getMessages()) {
      errbuilder.append(m.format(mc)).append('\n');
    }

    throw new GadgetException(
        GadgetException.Code.MALFORMED_FOR_SAFE_INLINING,
        errbuilder.toString());
  }
}
