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

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.mortbay.cometd.AbstractBayeux;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

public class EXoContinuationCometdServlet
   extends ContinuationCometdServlet
{

   /**
    * 
    */
   private static final long serialVersionUID = 9204910608302112814L;
   /**
    * Logger.
    */
   private static Log log = ExoLogger.getLogger("ws.EXoContinuationCometdServlet");

   /**
    * {@inheritDoc}
    */
   protected EXoContinuationBayeux newBayeux()
   {
      try
      {
         ExoContainer container;
         if (getInitParameter("containerName") != null)
            container = ExoContainerContext.getContainerByName(getInitParameter("containerName"));
         else
            container = ExoContainerContext.getCurrentContainer();
         if (log.isDebugEnabled())
            log.debug("EXoContinuationCometdServlet - Current Container-ExoContainer: " + container);
         EXoContinuationBayeux bayeux =
                  (EXoContinuationBayeux) container.getComponentInstanceOfType(AbstractBayeux.class);
         bayeux.setTimeout(Long.parseLong(getInitParameter("timeout")));
         if (log.isDebugEnabled())
            log.debug("EXoContinuationCometdServlet - -->AbstractBayeux=" + bayeux);
         return bayeux;
      }
      catch (Exception e)
      {
         log.error("Error new Bayeux creation ", e);
         return null;
      }
   }
}
