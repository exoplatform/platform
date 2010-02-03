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
package org.exoplatform.ws.frameworks.cometd.transport;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.ws.frameworks.cometd.ContinuationService;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
@Path("/continuation/")
public class RESTContinuationService
   implements ResourceContainer
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("ws.RestServiceForCometdTransport");

   private final ContinuationService continuation;

   public RESTContinuationService(ContinuationService continuationService)
   {
      this.continuation = continuationService;
   }

   /**
    * @param exoID the id of client. 
    * @return userToken for user 
    */
   @GET
   @Path("/gettoken/{exoID}/")
   @Produces(MediaType.TEXT_PLAIN)
   public Response getToken(@PathParam("exoID") String exoID)
   {
      String token = continuation.getUserToken(exoID);
      if (log.isDebugEnabled())
         log.debug("Client with exoId " + exoID + " get token " + token);
      return Response.ok(token, MediaType.TEXT_PLAIN).header("Content-Length", Integer.toString(token.length()))
               .build();
   }

   /**
    * @param exoID the id of client.
    * @param channel the id of channel
    * @return true if client subscribed on channel else false
    */
   @GET
   @Path("/issubscribed/{exoID}/")
   @Produces(MediaType.TEXT_PLAIN)
   public Response isSubscribed(@PathParam("exoID") String exoID, @QueryParam("channel") String channel)
   {
      Boolean b = continuation.isSubscribe(exoID, channel);
      if (log.isDebugEnabled())
         log.debug("Is subcribed client " + exoID + " on channel " + channel + " " + b);
      return Response.ok(b.toString(), MediaType.TEXT_PLAIN).build();
   }

   /**
    * @param channel the id of channel
    * @return true if channel exist else false
    */
   @GET
   @Path("/haschannel/")
   @Produces(MediaType.TEXT_PLAIN)
   public Response hasChannel(@QueryParam("channel") String channel)
   {
      Boolean b = continuation.hasChannel(channel);
      if (log.isDebugEnabled())
         log.debug("Has channel " + channel + " " + b);
      return Response.ok(b.toString(), MediaType.TEXT_PLAIN).header("Content-Length",
               Integer.toString(b.toString().length())).build();
   }

   /**
    * @param data content message, clientId, channel. 
    * @return Response with status 
    */
   @POST
   @Path("/sendprivatemessage/")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response sendMessage(DelegateMessage data)
   {
      continuation.sendMessage(data.getExoId(), data.getChannel(), data.getMessage(), data.getId());
      if (log.isDebugEnabled())
         log.debug("Send private message " + data.getMessage() + " on channel " + data.getChannel() + " to client "
                  + data.getExoId());
      return Response.ok().build();
   }

   /**
    * @param data content message, clientId, channel.
    * @return Response with status
    */
   @POST
   @Path("/sendbroadcastmessage/")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response sendBroadcastMessage(DelegateMessage data)
   {
      continuation.sendBroadcastMessage(data.getChannel(), data.getMessage(), data.getId());
      if (log.isDebugEnabled())
         log.debug("Send broadcast message " + data.getMessage() + " on channel " + data.getChannel());
      return Response.ok().build();
   }

}
