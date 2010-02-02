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

import java.util.List;

import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class CMessages
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("ws.Messages");

   private List<CMessage> cometdMessages;

   /**
    * @return the cometdMessages
    */
   public List<CMessage> getCometdMessages()
   {
      return cometdMessages;
   }

   /**
    * @param cometdMessages the cometdMessages to set
    */
   public void setCometdMessages(List<CMessage> cometdMessages)
   {
      this.cometdMessages = cometdMessages;
   }

}
