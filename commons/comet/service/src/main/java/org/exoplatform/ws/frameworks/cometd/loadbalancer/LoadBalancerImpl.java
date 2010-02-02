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
package org.exoplatform.ws.frameworks.cometd.loadbalancer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class LoadBalancerImpl
   implements LoadBalancer
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("ws.LoadBalancerImpl");

   /**
    * 
    */
   private ConcurrentHashMap<String, Node> nodes = new ConcurrentHashMap<String, Node>();

   /**
    * 
    */
   private ConcurrentHashMap<String, String> connectionMap = new ConcurrentHashMap<String, String>();

   /**
    * @param params the initial parameters.
    */
   public LoadBalancerImpl(InitParams params)
   {
      if (params != null)
      {
         ObjectParameter parameter = params.getObjectParam("cometd.lb.configuration");
         LoadBalancerConf conf = (LoadBalancerConf) parameter.getObject();
         List<Node> list = conf.getNodes();
         for (Node node : list)
         {
            nodes.put(node.getId(), node);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void addNode(Node node)
   {
      this.nodes.put(node.getId(), node);
   }

   /**
    * {@inheritDoc}
    */
   public String connection(String exoId)
   {
      return getNodeURL(exoId);
   }

   /**
    * {@inheritDoc}
    */
   public List<String> getAliveNodesURL()
   {
      List<String> urls = new ArrayList<String>();
      Collection<Node> ns = nodes.values();
      for (Node node : ns)
      {
         if (node.isAlive())
            urls.add(node.getUrl());
      }
      return urls;
   }

   /**
    * {@inheritDoc}
    */
   public boolean release(String exoId)
   {
      String id = connectionMap.get(exoId);
      if (id != null)
      {
         Node node = nodes.get(id);
         node.delConnection();
         connectionMap.remove(exoId);
         nodes.put(node.getId(), node);
         return true;
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public void removeNode(String id)
   {
      nodes.remove(id);
      Set<String> set = connectionMap.keySet();
      for (String key : set)
      {
         if (connectionMap.get(key).equals(id))
            connectionMap.remove(key);
      }
   }

   /**
    * @param exoId the user id.
    * @return URL for cometd connection.
    */
   private String getNodeURL(String exoId)
   {
      if (connectionMap.containsKey(exoId))
      {
         return nodes.get(connectionMap.get(exoId)).getUrl();
      }
      else
      {
         Collection<Node> ns = nodes.values();
         for (Node node : ns)
         {
            if (node.getConnected() < node.getMaxConnection())
            {
               node.addConnection();
               nodes.put(node.getId(), node);
               connectionMap.put(exoId, node.getId());
               return node.getUrl();
            }
         }
         if (log.isDebugEnabled())
            log.debug("Overflow new client cannot connect!");
         return null;
      }
   }

   /**
    * @author vetal
    *
    */
   public static class LoadBalancerConf
   {

      /**
      * List node of  cluster.
      */
      private List<Node> nodes = new ArrayList<Node>();

      /**
       * @return the nodes
       */
      public List<Node> getNodes()
      {
         return nodes;
      }

      /**
       * @param nodes the nodes to set
       */
      public void setNodes(List<Node> nodes)
      {
         this.nodes = nodes;
      }

   }
}
