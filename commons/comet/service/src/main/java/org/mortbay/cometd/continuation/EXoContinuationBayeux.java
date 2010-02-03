package org.mortbay.cometd.continuation;

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
import dojox.cometd.Channel;
import dojox.cometd.SecurityPolicy;
import dojox.cometd.Client;
import dojox.cometd.Message;

import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;
import org.mortbay.cometd.ClientImpl;
import org.picocontainer.Startable;

import javax.servlet.ServletContext;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

public class EXoContinuationBayeux
   extends ContinuationBayeux implements Startable
{

   /**
    * Map for userToken.
    */
   private static Map<String, String> userToken = new HashMap<String, String>();

   /**
    * Generate userToken.
    */
   transient Random random;

   /**
    * Timeout.
    */
   private long timeout;

   /**
    * Logger.
    */
   private static Log log = ExoLogger.getLogger("ws.EXoContinuationBayeux");

   /**
    * Default constructor.
    */
   public EXoContinuationBayeux()
   {
      super();
      this.setSecurityPolicy(new EXoSecurityPolicy());
   }

   /**
    * {@inheritDoc}
    */
   public ClientImpl newRemoteClient()
   {
      EXoContinuationClient client = new EXoContinuationClient(this);
      return client;
   }

   /**
    * {@inheritDoc}
    */
   public void setTimeout(long timeout)
   {
      this.timeout = timeout;
   }

   /**
    * {@inheritDoc}
    */
   public long getTimeout()
   {
      return timeout;
   }

   /**
    * {@inheritDoc}
    */
   long getRandom(long variation)
   {
      long l = random.nextLong() ^ variation;
      return l < 0 ? -l : l;
   }

   /**
    * @param eXoId the client id.
    * @return token for client
    */
   public String getUserToken(String eXoId)
   {
      if (userToken.containsKey(eXoId))
      {
         return (String) userToken.get(eXoId);
      }
      String token = Long.toString(this.getRandom(System.identityHashCode(this) ^ System.currentTimeMillis()), 36);
      userToken.put(eXoId, token);
      return token;
   }

   /* ------------------------------------------------------------ */
   /**
    * {@inheritDoc}
    */
   protected void initialize(ServletContext context)
   {
      super.initialize(context);
      try
      {
         random = SecureRandom.getInstance("SHA1PRNG");
      }
      catch (Exception e)
      {
         context.log("Could not get secure random for ID generation", e);
         random = new Random();
      }
      random.setSeed(random.nextLong() ^ hashCode() ^ (context.hashCode() << 32) ^ Runtime.getRuntime().freeMemory());
      if (log.isDebugEnabled())
         log.debug("Initialized");
   }

   /**
    * @param eXoID the id of client.
    * @return client with eXoID
    */
   public EXoContinuationClient getClientByEXoId(String eXoID)
   {
      Set<String> ids = getClientIDs();
      for (String id : ids)
      {
         Client client = getClient(id);
         if (client instanceof EXoContinuationClient)
         {
            EXoContinuationClient exoClient = (EXoContinuationClient) client;
            if (exoClient.getEXoId() != null && exoClient.getEXoId().equals(eXoID))
               return exoClient;
         }
      }
      return null;
   }

   /**
    * @param channel the id of channel.
    * @param data the message
    * @param msgId the message id
    */
   public void sendBroadcastMessage(String channel, Object data, String msgId)
   {
      ClientImpl fromClient = (ClientImpl) newClient("EXoContinuationBayeux");
      Channel ch = getChannel(channel);
      if (ch != null)
      {
         ch.publish(fromClient, data, msgId);
         if (log.isDebugEnabled())
            log.debug("Send broadcast message " + data.toString() + " on channel " + channel);
      }
      else
      {
         if (log.isDebugEnabled())
            log.debug("Message " + data.toString() + " not send. Channel " + channel + " not exist!");
      }
   }

   /**
    * Send data to a individual client. The data passed is sent to the client as
    * the "data" member of a message with the given channel and id. The message
    * is not published on the channel and is thus not broadcast to all channel
    * subscribers. However to the target client, the message appears as if it was
    * broadcast.
    * <p>
    * Typcially this method is only required if a service method sends
    * response(s) to channels other than the subscribed channel. If the response
    * is to be sent to the subscribed channel, then the data can simply be
    * returned from the subscription method.
    * 
    * @param eXoId the id of target client
    * @param channel The id of channel the message is for
    * @param data The data of the message
    * @param id The id of the message (or null for a random id).
    */
   public void sendMessage(String eXoId, String channel, Object data, String id)
   {
      EXoContinuationClient toClient = getClientByEXoId(eXoId);
      ClientImpl fromClient = (ClientImpl) this.newClient("EXoContinuationBayeux");
      if (toClient != null)
      {
         toClient.deliver(fromClient, channel, data, id);
         if (log.isDebugEnabled())
            log.debug("Send message " + data.toString() + " on channel " + channel + " to client " + eXoId);
      }
      else
      {
         if (log.isDebugEnabled())
            log.debug("Message " + data.toString() + " not send on channel " + channel + " client " + eXoId
                     + " not exist!");
      }
   }

   /* ------------------------------------------------------------ */
   /**
    * @author vetal
    *
    */
   public static class EXoSecurityPolicy
      implements SecurityPolicy
   {

      /**
       * 
       */
      private static Log log = ExoLogger.getLogger("ws.EXoContinuationBayeux.EXoSecurityPolicy");

      /**
       * 
       */
      public EXoSecurityPolicy()
      {
         super();
      }

      /**
       * {@inheritDoc}
       */
      public boolean canHandshake(Message message)
      {
         return checkUser(message);
      }

      /**
       * {@inheritDoc}
       */
      public boolean canCreate(Client client, String channel, Message message)
      {
         Boolean b = client != null && !channel.startsWith("/meta/");
         return b;
      }

      /**
       * {@inheritDoc}
       */
      public boolean canSubscribe(Client client, String channel, Message message)
      {
         Boolean b = client != null && !channel.startsWith("/meta/");
         if (!checkUser(message))
         {
            return false;
         }
         // We set the eXoID
         if (((EXoContinuationClient) client).getEXoId() == null)
         {
            ((EXoContinuationClient) client).setEXoId((String) message.get("exoId"));
         }

         return client != null && !channel.startsWith("/meta/");
      }

      /**
       * {@inheritDoc}
       */
      public boolean canPublish(Client client, String channel, Message message)
      {
         Boolean b = client != null && !channel.startsWith("/meta/");
         return b;
      }

      /**
       * @param message the cometd message.
       * @return true if user valid else false.
       */
      private boolean checkUser(Message message)
      {
         String userId = (String) message.get("exoId");
         String eXoToken = (String) message.get("exoToken");
         return (userId != null && userToken.containsKey(userId) && userToken.get(userId).equals(eXoToken));
      }

   }

   public void start()
   {
   }

   public void stop()
   {
   }

}
