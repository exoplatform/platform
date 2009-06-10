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
package org.exoplatform.web.security.security;

import java.util.ArrayList;

import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.web.login.InitiateLoginServlet;
import org.exoplatform.web.security.Credentials;
import org.exoplatform.web.security.Token;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by The eXo Platform SAS Author : liem.nguyen ncliam@gmail.com Jun 5,
 * 2009
 */
public class CookieTokenService extends AbstractTokenService {

  private RegistryService  regService_; 

  public CookieTokenService(InitParams initParams, RegistryService rService) {
    super(initParams);
    regService_ = rService;
  }

  public String createToken(Credentials credentials) {
    if (validityMillis < 0) {
      throw new IllegalArgumentException();
    }
    if (credentials == null) {
      throw new NullPointerException();
    }
    String tokenId = InitiateLoginServlet.COOKIE_NAME + random.nextInt();
    long expirationTimeMillis = System.currentTimeMillis() + validityMillis;
    this.saveToken(tokenId, new Token(expirationTimeMillis, credentials));
    return tokenId;
  }  
  
  @Override
  public Token getToken(String id) throws PathNotFoundException, RepositoryException {
    String entryPath = getServiceRegistryPath() + "/" + id;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath);
      return toToken(entry.getDocument());
    } finally {
      sessionProvider.close();
    }
  }

  @Override
  public Token deleteToken(String id) throws PathNotFoundException, RepositoryException {
    Token data = getToken(id);
    if (data == null)
      return null;
    String entryPath = getServiceRegistryPath() + "/" + id;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      regService_.removeEntry(sessionProvider, entryPath);
      return data;
    } finally {
      sessionProvider.close();
    }
  }
  
  private void saveToken(String tokenId, Token token) {
    if (tokenId == null || tokenId.length() == 0 || token == null) {
      return;
    }
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    String servicePath = getServiceRegistryPath();
    String entryPath = servicePath + "/" + tokenId;
    RegistryEntry entry;
    try {
      try {
        entry = regService_.getEntry(sessionProvider, entryPath);
      } catch (PathNotFoundException e) {
        entry = new RegistryEntry(tokenId);
        regService_.createEntry(sessionProvider, servicePath, entry);
      }
      Document doc = entry.getDocument();
      map(doc, token);
      regService_.recreateEntry(sessionProvider, servicePath, entry);
    } catch (Exception e) {
    } finally {
      sessionProvider.close();
    }
  }

  private Token toToken(Document document) {
    Element root = document.getDocumentElement();
    String userName = root.getAttribute(Token.USERNAME);
    String password = root.getAttribute(Token.PASSWORD);
    long time = Long.parseLong(root.getAttribute(Token.EXPIRE_MILI));
    Credentials payload = new Credentials(userName, password);
    return new Token(time, payload);
  }

  private void map(Document document, Token token) {
    Element root = document.getDocumentElement();
    prepareXmlNamespace(root);
    root.setAttribute(Token.EXPIRE_MILI, "" + token.getExpirationTimeMillis());
    root.setAttribute(Token.USERNAME, token.getPayload().getUsername());
    root.setAttribute(Token.PASSWORD, token.getPayload().getPassword());
  }

  private void prepareXmlNamespace(Element element) {
    setXmlNameSpace(element, "xmlns:exo", "http://www.exoplatform.com/jcr/exo/1.0");
    setXmlNameSpace(element, "xmlns:jcr", "http://www.jcp.org/jcr/1.0");
  }

  private void setXmlNameSpace(Element element, String key, String value) {
    String xmlns = element.getAttribute(key);
    if (xmlns == null || xmlns.trim().length() < 1) {
      element.setAttribute(key, value);
    }
  }

  private String getServiceRegistryPath() {
    return RegistryService.EXO_SERVICES + "/" + name;
  }

  @Override
  public String[] getAllTokens() {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      javax.jcr.Node regNode = regService_.getRegistry(sessionProvider).getNode();
      NodeIterator itr = regNode.getNode(getServiceRegistryPath()).getNodes();
      ArrayList<String> list = new ArrayList<String>();
      while (itr.hasNext()) {
        javax.jcr.Node node = itr.nextNode();
        list.add(node.getName());
      }
      return list.toArray(new String[] {});
    } catch (RepositoryException e) {
      return null;
    } finally {
      sessionProvider.close();
    }
  }

  @Override
  public long getNumberTokens() throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
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
}
