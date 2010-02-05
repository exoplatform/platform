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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer.PortalContainerPostInitTask;
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
    * The portal container
    */
   private ExoContainer container;
   
   /**
    * {@inheritDoc}
    */
   public void init(final ServletConfig config) throws ServletException
   {
      final PortalContainerPostInitTask task = new PortalContainerPostInitTask()
      {

         public void execute(ServletContext context, PortalContainer portalContainer)
         {
            EXoContinuationCometdServlet.this.container = portalContainer;
            try
            {
               EXoContinuationCometdServlet.super.init(config);
            }
            catch (ServletException e)
            {
               log.error("Cannot initialize Bayeux", e);
            }
         }
      };
      PortalContainer.addInitTask(config.getServletContext(), task);
   }
   
   /**
    * {@inheritDoc}
    */
   protected EXoContinuationBayeux newBayeux()
   {
      try
      {
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
