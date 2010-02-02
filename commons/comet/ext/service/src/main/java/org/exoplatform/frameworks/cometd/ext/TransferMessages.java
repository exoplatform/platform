/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.frameworks.cometd.ext;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.ws.frameworks.cometd.transport.ContinuationServiceDelegate;
import org.exoplatform.ws.frameworks.cometd.transport.DelegateMessage;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

@Path("/transfer/")
public class TransferMessages
   implements ResourceContainer
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("ws.CometdTestSendMessage");

   @POST
   @Path("/ext/sendprivatemessage/")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response sendMessage(DelegateMessage transportData)
   {
      ContinuationServiceDelegate transport = getCometdTransport();
      transport.sendMessage(transportData.getExoId(), transportData.getChannel(), transportData.getMessage(),
               transportData.getId());
      return Response.ok().build();
   }

   @POST
   @Path("/ext/sendbroadcastmessage/")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response sendBroadcastMessage(DelegateMessage data)
   {
      ContinuationServiceDelegate transport = getCometdTransport();
      transport.sendBroadcastMessage(data.getChannel(), data.getMessage(), data.getId());
      return Response.ok().build();
   }

   private ContinuationServiceDelegate getCometdTransport()
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      ContinuationServiceDelegate transport =
               (ContinuationServiceDelegate) container.getComponentInstanceOfType(ContinuationServiceDelegate.class);
      return transport;
   }

}
