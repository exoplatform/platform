/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.portal.mail.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.portal.mail.EmailNotifyPlugin;
import org.exoplatform.portal.mail.MailService;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@yahoo.com
 * Jul 24, 2008  
 */
public class MailServiceImpl implements MailService{

  private Map<String, String> serverConfig_ = new HashMap<String, String>();
  
  public void addPlugin(ComponentPlugin plugin) throws Exception {
    // TODO Auto-generated method stub
    try{
      serverConfig_ = ((EmailNotifyPlugin)plugin).getServerConfiguration() ;
    }catch(Exception e) {
      e.printStackTrace() ;
    }
  }
  
  public void sendMessage(String[] recipients, String subject, String message, String from) throws Exception {
    Properties props = new Properties();
    props.put("mail.smtp.user", serverConfig_.get("smtp.auth.user"));
    props.put("mail.smtp.host", serverConfig_.get("smtp.host.name"));
    props.put("mail.smtp.port", serverConfig_.get("smtp.host.port"));
    props.put("mail.smtp.starttls.enable","true");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.debug", "false");
    props.put("mail.smtp.socketFactory.port", serverConfig_.get("smtp.host.port"));
    props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");
    try {
      Session session = Session.getDefaultInstance(props, null);
      session.setDebug(false);
      MimeMessage msg = new MimeMessage(session);
      msg.setText(message);
      msg.setSubject(subject);
      msg.setFrom(new InternetAddress(from));
      for(int i=0; i<recipients.length; i++){
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients[i]));
      }
      msg.saveChanges();
      Transport transport = session.getTransport("smtp");
      transport.connect(serverConfig_.get("smtp.host.name"), serverConfig_.get("smtp.auth.user"), serverConfig_.get("smtp.auth.password"));
      transport.sendMessage(msg, msg.getAllRecipients());
      transport.close();
    }catch (Exception mex){
        mex.printStackTrace();
    }
  }
}
