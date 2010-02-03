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

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.NVPair;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.ws.frameworks.cometd.loadbalancer.LoadBalancer;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

public class ContinuationServiceRemoteDelegate implements ContinuationServiceDelegate
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("ws.ContinuationServiceRemoteDelegate");

   private final LoadBalancer loadBalancer;

   /** 
    * The name of the rest context
    */
   private final String restContextName;

   public ContinuationServiceRemoteDelegate(LoadBalancer loadBalancer, ExoContainerContext context)
   {
      this.loadBalancer = loadBalancer;
      this.restContextName = context.getRestContextName();
   }

   /**
    * @param exoID the id of client.
    * @return base URL of cometd server for user with exoID.
    */
   private String getBaseCometdURL(String exoID)
   {
      return loadBalancer.connection(exoID);
   }

   /**
    * {@inheritDoc}
    */
   public Boolean isSubscribed(String exoID, String channel)
   {
      try
      {
         String baseURICometdServer = getBaseCometdURL(exoID);
         URL url =
            new URL(baseURICometdServer + "/" + restContextName + "/issubscribed/" + exoID + "/" + channel + "/");
         HTTPConnection connection = new HTTPConnection(url);
         HTTPResponse response = connection.Get(url.getFile());
         String bol = new String(response.getData());
         if (log.isDebugEnabled())
            log.debug("Check user " + exoID + " subscription to channel " + channel);
         return new Boolean(bol);
      }
      catch (Exception e)
      {
         log.error("Check user subscription error ", e);
         return null;
      }
   }

   /**
    * {@inheritDoc}
    */
   public void sendMessage(String exoID, String channel, String message, String msgId)
   {
      try
      {
         String baseURICometdServer = getBaseCometdURL(exoID);
         URL url = new URL(baseURICometdServer + "/" + restContextName + "/continuation/sendprivatemessage/");
         HTTPConnection connection = new HTTPConnection(url);
         DelegateMessage transportData = new DelegateMessage(channel, exoID, message, msgId);
         JsonGeneratorImpl generatorImpl = new JsonGeneratorImpl();
         JsonValue json = generatorImpl.createJsonObject(transportData);
         NVPair[] pairs = new NVPair[1];
         pairs[0] = new NVPair("Content-Type", MediaType.APPLICATION_JSON);
         HTTPResponse response = connection.Post(url.getFile(), json.toString(), pairs);
         if (response.getStatusCode() == HTTPStatus.OK)
         {
            if (log.isDebugEnabled())
               log.debug("Send private message : " + message + " to client " + exoID + " by cahnnel " + channel
                  + " success");
         }
         else
         {
            if (log.isDebugEnabled())
               log.debug("Send private message : " + message + " to client " + exoID + " by cahnnel " + channel
                  + " fail!");
         }
      }
      catch (Exception e)
      {
         log.error("Send message error ", e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void sendBroadcastMessage(String channel, String message, String msgId)
   {
      try
      {
         List<String> us = getCometdURLsByChannel(channel);
         if (us != null)
         {
            for (String u : us)
            {
               URL url = new URL(u + "/" + restContextName + "/continuation/sendbroadcastmessage/");
               DelegateMessage transportData = new DelegateMessage(channel, message, msgId);
               JsonGeneratorImpl generatorImpl = new JsonGeneratorImpl();
               JsonValue json = generatorImpl.createJsonObject(transportData);
               HTTPConnection connection = new HTTPConnection(url);
               NVPair[] pairs = new NVPair[1];
               pairs[0] = new NVPair("Content-Type", MediaType.APPLICATION_JSON);
               HTTPResponse response = connection.Post(url.getFile(), json.toString(), pairs);
               if (response.getStatusCode() == HTTPStatus.OK)
               {
                  if (log.isDebugEnabled())
                     log.debug("Send public message : " + message + " to channel " + channel + " success");
               }
               else
               {
                  if (log.isDebugEnabled())
                     log.debug("Send public message : " + message + " to channel " + channel + " fail!");
               }
            }
         }
      }
      catch (Exception e)
      {
         log.error("Send message error ", e);
      }
   }

   /**
    * @param channel id of channel.
    * @return Array of URL of cometd server there exist users subscribed on channel
    */
   private List<String> getCometdURLsByChannel(String channel)
   {
      try
      {
         Collection<String> curls = loadBalancer.getAliveNodesURL();
         List<String> urls = new ArrayList<String>();
         for (String curl : curls)
         {
            String u = new String(curl + "/" + restContextName + "/continuation/haschannel?channel=" + channel);
            URL url = new URL(u);
            HTTPConnection connection = new HTTPConnection(url);
            HTTPResponse response = connection.Get(url.getFile());
            boolean b = Boolean.parseBoolean(new String(response.getData()));
            if (b)
               urls.add(curl);
         }
         return urls;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

 
}
