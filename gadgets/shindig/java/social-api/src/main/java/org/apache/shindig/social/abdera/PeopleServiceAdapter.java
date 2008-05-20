/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/
package org.apache.shindig.social.abdera;

import org.apache.shindig.gadgets.GadgetToken;
import org.apache.shindig.social.opensocial.PeopleService;
import org.apache.shindig.social.opensocial.model.Person;

import com.google.inject.Inject;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;

import java.util.Date;
import java.util.logging.Logger;

/**
 * All "people" requests are processed here.
 *
 */
@SuppressWarnings("unchecked")
public class PeopleServiceAdapter extends RestServerCollectionAdapter {
  private static Logger logger =
    Logger.getLogger(PeopleServiceAdapter.class.getName());
  private PeopleService handler;

  @Inject
  public PeopleServiceAdapter(PeopleService handler) {
    this.handler = handler;
  }

  /**
   * Does not handle any urls.
   */
  public ResponseContext getFeed(RequestContext request) {
    throw new UnsupportedOperationException();
  }

  /**
   * Handles the following URLs:
   *       /people/{uid}/@self
   */
  public ResponseContext getEntry(RequestContext request) {
    String uid = request.getTarget().getParameter("uid");
    GadgetToken token = getGadgetToken(request, uid);
    Person person = handler.getPerson(uid, token).getResponse();

    // TODO: how is entry id determined. check.
    String entryId = request.getUri().toString();
    Date updated = (person != null) ? person.getUpdated() : null;
    logger.fine("updated = " + updated);
    return returnEntry(request, person, entryId, updated);
  }
}
