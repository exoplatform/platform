/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.web.login;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.web.security.Credentials;
import org.exoplatform.web.security.Token;
import org.picocontainer.Startable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Created by The eXo Platform SAS
 * Author : Tran The Trong
 *          trong.tran@exoplatform.com
 * Apr 21, 2009  
 */

@Managed
@NameTemplate({
  @Property(key = "view", value = "cookie"),
  @Property(key = "service", value = "management"),
  @Property(key="type", value="token")
})
@ManagedDescription("Skin service")
public class CookieTokenService implements Startable {
  private RegistryService regService_ ;
  
  public static final String SERVICE_NAME = "cookieToken" ;
  
  public static final long DELAY_TIME = 600 ;
  
  public CookieTokenService(InitParams initParams, RegistryService rService) {
    regService_ = rService ;
  }
  
  public void saveToken(String tokenId, Token token) {
    if(tokenId == null || tokenId.length() == 0 || token == null) {
      return ;
    }
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    String servicePath = getServiceRegistryPath() ;
    String entryPath = servicePath + "/" + tokenId ;
    RegistryEntry entry ;
    try {
      try {
        entry = regService_.getEntry(sessionProvider, entryPath) ;
      } catch (PathNotFoundException e) {
        entry = new RegistryEntry(tokenId) ;
        regService_.createEntry(sessionProvider, servicePath, entry) ;
      }
      Document doc = entry.getDocument() ;
      map(doc, token) ;
      regService_.recreateEntry(sessionProvider, servicePath, entry);
    } catch(Exception e) {
    } finally {
      sessionProvider.close() ;
    }
  }
  
  public Token getToken(String id) throws PathNotFoundException, RepositoryException {
    String entryPath = getServiceRegistryPath() + "/" + id ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath) ;
      return toToken(entry.getDocument()) ;
		} finally {
      sessionProvider.close() ;
    }
  }
  
  @Managed
  @ManagedDescription ("Delete a token by id")
  public Token deleteToken(String id) throws PathNotFoundException, RepositoryException {
    Token data = getToken(id) ;
    if(data == null) return null ; 
    String entryPath = getServiceRegistryPath() + "/" + id ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      regService_.removeEntry(sessionProvider, entryPath) ;
      return data ;
    } finally {
      sessionProvider.close() ;
    }
  }
  
  public void clearAll() {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    String entryPath = getServiceRegistryPath() ;
    RegistryEntry entry;
		try {
			entry = regService_.getEntry(sessionProvider, entryPath);
			Element docEle = entry.getDocument().getDocumentElement() ;
			NodeList childNodes = docEle.getChildNodes() ;
			while(childNodes.getLength() > 0) {
				Node node = childNodes.item(0) ;
				docEle.removeChild(node) ;
			}
			regService_.recreateEntry(sessionProvider, RegistryService.EXO_SERVICES, entry) ;
		} catch (Exception e) {
		} finally {
			sessionProvider.close();
		}
  }
  
  @Managed
  @ManagedDescription ("The list of all tokens")
  public String [] getAllTokens() {
  	SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
  	try {
      javax.jcr.Node regNode = regService_.getRegistry(sessionProvider).getNode();
      NodeIterator itr = regNode.getNode(getServiceRegistryPath()).getNodes();
      ArrayList<String> list = new ArrayList<String>();
      while (itr.hasNext()) {
        javax.jcr.Node node = itr.nextNode();
        list.add(node.getName());
      }
      return list.toArray(new String [] {});
  	} catch (RepositoryException e) {
  		return null;
		} 
  	finally {
  		sessionProvider.close();
  	}
    
  }
  
  @Managed
  @ManagedDescription ("The number of tokens")
  public long getNumberTokens() throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      javax.jcr.Node regNode = regService_.getRegistry(sessionProvider).getNode();
      NodeIterator itr = regNode.getNode(getServiceRegistryPath()).getNodes();
      return itr.getSize();
    } catch (Exception ex) {
    	return 0;
    } finally {
    	sessionProvider.close();
    }
  }
  
  @Managed
  @ManagedDescription ("Clean all tokens are expired")
  public void cleanExpiredTokens() throws PathNotFoundException, RepositoryException {
    String [] ids = getAllTokens();
    for(String s : ids) {
      Token token = getToken(s);
      if(token.isExpired()) {
        deleteToken(s);
      }
    }
  }

  
  private String getServiceRegistryPath() {
    return RegistryService.EXO_SERVICES + "/" + SERVICE_NAME ;
  }
  
  public Token toToken(Document document) {
    Element root = document.getDocumentElement() ;
    String userName = root.getAttribute(Token.USERNAME) ;
    String password = root.getAttribute(Token.PASSWORD) ;
    long time = Long.parseLong(root.getAttribute(Token.EXPIRE_MILI)) ;
    Credentials payload = new Credentials(userName, password) ;
    return new Token(time, payload) ;
  }
  
  public void map(Document document, Token token) {
    Element root = document.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(Token.EXPIRE_MILI, "" + token.getExpirationTimeMillis()) ;
    root.setAttribute(Token.USERNAME, token.getPayload().getUsername()) ;
    root.setAttribute(Token.PASSWORD, token.getPayload().getPassword()) ;
  }
  
  private void prepareXmlNamespace(Element element) {
    setXmlNameSpace(element, "xmlns:exo", "http://www.exoplatform.com/jcr/exo/1.0") ;
    setXmlNameSpace(element, "xmlns:jcr", "http://www.jcp.org/jcr/1.0") ;
  }
  
  private void setXmlNameSpace(Element element, String key, String value) {
    String xmlns = element.getAttribute(key) ; 
    if(xmlns == null || xmlns.trim().length() < 1) {
      element.setAttribute(key, value) ;
    }    
  }

  public void start() {
    
    // start a thread, garbage expired cookie token every [DELAY_TIME]
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleWithFixedDelay(new ExpireCookieToken(), 0, DELAY_TIME, TimeUnit.SECONDS);
  }

  public void stop() {
    // TODO Auto-generated method stub
    
  }
}
