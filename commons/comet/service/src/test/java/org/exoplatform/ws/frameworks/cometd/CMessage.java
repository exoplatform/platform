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
package org.exoplatform.ws.frameworks.cometd;

import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class CMessage
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("ws.CometdServIncomMessage");

   private String channel;

   private String version;

   private String minimumVersion;

   private String[] supportedConnectionTypes;

   private String clientId;

   private Advice advice;

   private String connectionType;

   private String id;

   private String timestamp;

   private String data;

   private String connetionId;

   private Boolean successful;

   private String subscription;

   private String error;

   private String ext;

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
    * @return the version
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * @param version the version to set
    */
   public void setVersion(String version)
   {
      this.version = version;
   }

   /**
    * @return the minimumVersion
    */
   public String getMinimumVersion()
   {
      return minimumVersion;
   }

   /**
    * @param minimumVersion the minimumVersion to set
    */
   public void setMinimumVersion(String minimumVersion)
   {
      this.minimumVersion = minimumVersion;
   }

   /**
    * @return the supportedConnectionTypes
    */
   public String[] getSupportedConnectionTypes()
   {
      return supportedConnectionTypes;
   }

   /**
    * @param supportedConnectionTypes the supportedConnectionTypes to set
    */
   public void setSupportedConnectionTypes(String[] supportedConnectionTypes)
   {
      this.supportedConnectionTypes = supportedConnectionTypes;
   }

   /**
    * @return the clientId
    */
   public String getClientId()
   {
      return clientId;
   }

   /**
    * @param clientId the clientId to set
    */
   public void setClientId(String clientId)
   {
      this.clientId = clientId;
   }

   /**
    * @return the advice
    */
   public Advice getAdvice()
   {
      return advice;
   }

   /**
    * @param advice the advice to set
    */
   public void setAdvice(Advice advice)
   {
      this.advice = advice;
   }

   /**
    * @return the conectionType
    */
   public String getConnectionType()
   {
      return connectionType;
   }

   /**
    * @param conectionType the conectionType to set
    */
   public void setConnectionType(String conectionType)
   {
      this.connectionType = conectionType;
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the timestamp
    */
   public String getTimestamp()
   {
      return timestamp;
   }

   /**
    * @param timestamp the timestamp to set
    */
   public void setTimestamp(String timestamp)
   {
      this.timestamp = timestamp;
   }

   /**
    * @return the data
    */
   public String getData()
   {
      return data;
   }

   /**
    * @param data the data to set
    */
   public void setData(String data)
   {
      this.data = data;
   }

   /**
    * @return the connetionId
    */
   public String getConnetionId()
   {
      return connetionId;
   }

   /**
    * @param connetionId the connetionId to set
    */
   public void setConnetionId(String connetionId)
   {
      this.connetionId = connetionId;
   }

   /**
    * @return the successful
    */
   public Boolean getSuccessful()
   {
      return successful;
   }

   /**
    * @param successful the successful to set
    */
   public void setSuccessful(Boolean successful)
   {
      this.successful = successful;
   }

   /**
    * @return the subscription
    */
   public String getSubscription()
   {
      return subscription;
   }

   /**
    * @param subscription the subscription to set
    */
   public void setSubscription(String subscription)
   {
      this.subscription = subscription;
   }

   /**
    * @return the error
    */
   public String getError()
   {
      return error;
   }

   /**
    * @param error the error to set
    */
   public void setError(String error)
   {
      this.error = error;
   }

   /**
    * @return the ext
    */
   public String getExt()
   {
      return ext;
   }

   /**
    * @param ext the ext to set
    */
   public void setExt(String ext)
   {
      this.ext = ext;
   }

}
