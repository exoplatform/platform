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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.exoplatform.services.log.Log;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.exoplatform.common.http.client.CookieModule;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.NVPair;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.frameworks.cometd.transport.DelegateMessage;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.impl.BeanBuilder;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class LocalCometdTest
   extends TestCase
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("ws.LocalCometdTest");

   private String baseCometdURI; // =

   // "http://localhost:8080/cometd/cometd/"
   // ;

   private String baseURI; // =

   // "http://localhost:8080/rest4cometd/"
   // ;

   private String connectionType = "long-polling";

   private URL url;

   private int clients;

   private int repeat;

   private long sleepConnect;

   private long sleepSend;

   private long timeout;

   private int messages;

   private int totalI = 0;

   private Integer totalB = 0;

   private List<String> channels = new ArrayList<String>();

   private HashMap<String, String> individuals = new HashMap<String, String>();

   private HashMap<String, String> broadcasts = new HashMap<String, String>();

   private InstalledLocalContainer container;

   private boolean startContainer;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      Document document =
               DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                        "src/test/java/conf/cometd-test-conf.xml");
      Node cont = document.getElementsByTagName("container").item(0);
      NamedNodeMap map = cont.getAttributes();
      startContainer = Boolean.parseBoolean(map.getNamedItem("containerStart").getTextContent());
      if (!startContainer)
      {
         String port = map.getNamedItem("port").getTextContent();
         String home = map.getNamedItem("home").getTextContent();
         container = CargoContainer.cargoContainerStart(port, home);
      }
      map = document.getElementsByTagName("configuration").item(0).getAttributes();
      clients = Integer.parseInt(map.getNamedItem("clients").getTextContent());
      repeat = Integer.parseInt(map.getNamedItem("repeat").getTextContent());
      sleepConnect = Long.parseLong(map.getNamedItem("sleep-connection").getTextContent());
      sleepSend = Long.parseLong(map.getNamedItem("sleep-sending").getTextContent());
      NodeList nodeList = document.getElementsByTagName("channel");
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         channels.add(nodeList.item(i).getTextContent());
      }
      nodeList = document.getElementsByTagName("message");
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         NamedNodeMap namedNodeMap = nodeList.item(i).getAttributes();
         String id_ = namedNodeMap.getNamedItem("id").getTextContent();
         Boolean broadcast = new Boolean(namedNodeMap.getNamedItem("broadcast").getTextContent());
         if (broadcast)
            broadcasts.put(id_, nodeList.item(i).getTextContent());
         else
            individuals.put(id_, nodeList.item(i).getTextContent());
      }
      baseCometdURI = document.getElementsByTagName("cometd-url").item(0).getTextContent();
      baseURI = document.getElementsByTagName("base-url").item(0).getTextContent();
      messages = (individuals.size() + broadcasts.size()) * repeat;// *clients;
   }

   private String cometdConnect(String id)
   {
      try
      {
         HTTPConnection connection;
         String eXoId = id;
         String userToken = TestTools.getUserToken(baseURI + "continuation/gettoken/" + id + "/");
         // First message for getting Cookie BAYEUX_BROWSER
         // clientId be changed after getting from server
         String initData =
                  "message={\"channel\":\"/meta/connect\",\"clientId\":\"1\",\"connectionType\":\"long-polling\",\"id\":0}";
         NVPair[] pairs = new NVPair[5];
         pairs[0] = new NVPair("Keep-Alive", "300");
         pairs[1] = new NVPair("Connection", "keep-alive");
         pairs[2] = new NVPair("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
         pairs[3] = new NVPair("Content-Length", Integer.toString(initData.length()));
         url = new URL(baseCometdURI);
         System.out.println("LocalCometdTest.cometdConnect()" + url);
         connection = new HTTPConnection(url);
         connection.removeModule(CookieModule.class); // remove module because it
         // remove the cookie from
         // Headers
         HTTPResponse response = connection.Post(url.getFile(), initData.getBytes(), pairs);
         String bayeuxCookie = response.getHeader("Set-Cookie").split(";")[0];
         assertNotNull(bayeuxCookie);
         // handshake Request
         // A Bayeux client initiates a connection negotiation by sending a message
         // to the "/meta/handshake" channel
         String dataHandshake =
                  "message={\"channel\":\"/meta/handshake\",\"id\":1,\"exoId\":\"" + eXoId + "\",\"exoToken\":\""
                           + userToken + "\"}";
         
         System.out.println("LocalCometdTest.cometdConnect()" + dataHandshake);
         
         pairs[3] = new NVPair("Content-Length", Integer.toString(dataHandshake.length()));
         pairs[4] = new NVPair("Cookie", bayeuxCookie);
         response = connection.Post(url.getFile(), dataHandshake.getBytes(), pairs);
         String string = new String(response.getData());
         
         System.out.println("LocalCometdTest.cometdConnect()" + string);
         
         CMessage incomMessage = TestTools.stringToCMessage(string);
         assertNotNull(incomMessage);
         timeout = incomMessage.getAdvice().getTimeout();
         String clientId = incomMessage.getClientId();
         assertNotNull(clientId);
         assertTrue(incomMessage.getSuccessful());
         // After a Bayeux client has discovered the server's capabilities with a
         // handshake exchange, a connection
         // is established by sending a message to the "/meta/connect" channel.
         String dataConnect =
                  "message={\"channel\":\"/meta/connect\",\"clientId\":\"" + clientId + "\",\"connectionType\":\""
                           + connectionType + "\",\"id\":2}";
         pairs[3] = new NVPair("Content-Length", Integer.toString(dataConnect.length()));
         response = connection.Post(url.getFile(), dataConnect.getBytes(), pairs);
         incomMessage = TestTools.stringToCMessage(new String(response.getData()));
         assertTrue(incomMessage.getSuccessful());
         // A connected Bayeux client may send subscribe messages to register
         // interest in a channel
         // and to request that messages published to the subscribe channel are
         // delivered to the client.
         String dataSub = "message=[";
         for (String channel : channels)
         {
            dataSub =
                     dataSub.concat("{\"channel\":\"/meta/subscribe\",\"subscription\":\"" + channel
                              + "\",\"exoId\":\"" + eXoId + "\",\"exoToken\":\"" + userToken + "\",\"clientId\":\""
                              + clientId + "\",\"id\":\"3\"},");
         }
         dataSub = dataSub.substring(0, dataSub.lastIndexOf(","));
         dataSub = dataSub + "]";
         pairs[3] = new NVPair("Content-Length", Integer.toString(dataSub.length()));
         response = connection.Post(url.getFile(), dataSub.getBytes(), pairs);
         incomMessage = TestTools.stringToCMessage(new String(response.getData()));
         assertTrue(incomMessage.getSuccessful());
         return clientId;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Created cometClient in thread
    */
   public void testConnection()
   {
      try
      {
         int con = clients;
         CountDownLatch countDownLatch = new CountDownLatch(con);
         for (int i = 0; i < con; i++)
         {
            CClient tender = new CClient("exo" + String.valueOf(i), countDownLatch);
            tender.start();
            Thread.sleep(sleepConnect);
         }
         System.out.println("-----------------------LocalCometdTest-----------------------------------");
         System.out.println("Conected " + con + " clients");
        
         for (int j = 0; j < repeat; j++)
         {
            for (int i = 0; i < con; i++)
            {
               Set<String> keySet = individuals.keySet();
               for (String key : keySet)
               {
                  for (String channel : channels)
                  {
                     TestTools.sendMessage("exo" + String.valueOf(i), channel, individuals.get(key), key, baseURI
                              + "continuation/sendprivatemessage/");
                     Thread.sleep(sleepSend);
                  }
               }
            }
            Set<String> keySet = broadcasts.keySet();
            for (String key : keySet)
            {
               TestTools.sendBroadcastMessage(channels.get(0), broadcasts.get(key), key, baseURI
                        + "continuation/sendbroadcastmessage/");
               Thread.sleep(sleepSend);
            }
         }
         System.out.println("Send " + messages + " messages");
         System.out.println("Wait " + timeout + " ms....");
         countDownLatch.await();
         int t = totalB + totalI;
         System.out.println("Total get : " + t + " mesagess. " + totalB + " broadcast from them");
         System.out.println("------------------------------------------------------------------------");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   class CClient
      extends Thread
   {
      String id;

      CountDownLatch countDownLatch;

      public CClient(String id, CountDownLatch countDownLatch)
      {
         this.id = id;
         this.countDownLatch = countDownLatch;
      }

      @Override
      public void run()
      {
         try
         {
            String clientId = cometdConnect(id);
            boolean flag = true;
            int i = 0;
            int b = 0;
            while (messages > i + b)
            {
               HTTPConnection connection = new HTTPConnection(url);
               String dataConnect =
                        "message={\"channel\":\"/meta/connect\",\"clientId\":\"" + clientId
                                 + "\",\"connectionType\":\"" + connectionType + "\",\"id\":\"" + id + "\"}";
               NVPair[] pairs = new NVPair[5];
               pairs[0] = new NVPair("Keep-Alive", "300");
               pairs[1] = new NVPair("Connection", "keep-alive");
               pairs[2] = new NVPair("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
               pairs[3] = new NVPair("Content-Length", Integer.toString(dataConnect.length()));
               HTTPResponse cometdResponse = connection.Post(url.getFile(), dataConnect.getBytes(), pairs);
               String resString = new String(cometdResponse.getData());
               CMessages messages = TestTools.stringToCMessages(resString);
               List<CMessage> list = messages.getCometdMessages();
               flag = false;
               for (CMessage message : list)
               {
                  if (message.getData() != null)
                  {
                     flag = true;
                     String msgid = message.getId();
                     if (broadcasts.containsKey(msgid))
                     {
                        String msg = broadcasts.get(msgid);
                        synchronized (totalB)
                        {
                           totalB++;
                        }
                        assertEquals(msg, message.getData());
                        System.out.println("CClient.run()" + message.getData());
                        b++;
                     }
                     if (individuals.containsKey(msgid))
                     {
                        String msg = individuals.get(msgid);
                        assertEquals(msg, message.getData());
                        System.out.println("CClient.run()" + message.getData());
                        totalI++;
                        i++;
                     }
                  }
               }
            }
            countDownLatch.countDown();
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   @Override
   protected void tearDown() throws Exception
   {
      super.tearDown();
      if (!startContainer)
         CargoContainer.cargoContainerStop(container);
   }

}
