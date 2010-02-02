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

import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

public class DelegateMessage
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("ws.TransportData");

   /**
    *  
    */
   private String message;

   /**
    * 
    */
   private String exoId;

   /**
    * 
    */
   private String channel;

   /**
    * 
    */
   private String id;

   /**
    * 
    */
   public DelegateMessage()
   {
   }

   /**
    * @param channel the id of channel.
    * @param exoId the client id (deliver)
    * @param message the message 
    * @param msgId id of message
    */
   public DelegateMessage(String channel, String exoId, String message, String msgId)
   {
      super();
      this.channel = channel;
      this.exoId = exoId;
      this.message = message;
      this.id = msgId;
   }

   /**
    * @param channel the id of channel.
    * @param message the message 
    * @param msgId id of message
    */
   public DelegateMessage(String channel, String message, String msgId)
   {
      super();
      this.channel = channel;
      this.message = message;
      this.id = msgId;
   }

   /**
    * @return the message
    */
   public String getMessage()
   {
      return message;
   }

   /**
    * @param message the message to set
    */
   public void setMessage(String message)
   {
      this.message = message;
   }

   /**
    * @return the exoId
    */
   public String getExoId()
   {
      return exoId;
   }

   /**
    * @param exoId the exoId to set
    */
   public void setExoId(String exoId)
   {
      this.exoId = exoId;
   }

   /**
    * @return the channel
    */
   public String getChannel()
   {
      return channel;
   }

   /**
    * @param channel the channel to set
    */
   public void setChannel(String channel)
   {
      this.channel = channel;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

}
