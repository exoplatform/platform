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
package org.exoplatform.services.html.util;

import java.net.URL;

import org.exoplatform.services.common.ServiceConfig;
import org.exoplatform.services.common.ServiceConfig.ServiceType;
@ServiceConfig(type = ServiceType.SINGLE_FINAL)
public class URLCreator {
  
  public synchronized String createURL(URL url, String link) {
    link = createURL(url.getFile(), link);
    return createURL(url.getHost(), url.getPort(), url.getProtocol(), link);
  }

  public synchronized String createURL(String host, int port, String protocol, String link) {
    if(link.startsWith("http")
        || link.startsWith("https")
        || link.startsWith("ftp"))  return link;

    String url =protocol+"://"+host;
    if( port >= 0)
      url += ":"+String.valueOf( port);
    url += link;
    return url;
  }

  public synchronized String createURL(String address, String link)  {
    if(link.startsWith("http")
       || link.startsWith("https")
       || link.startsWith("ftp")
       || link.startsWith("/"))  return link;
    String file = "";
    try{
      file = (new URL(address)).getFile();
    }catch( Exception exp){
      file = address;
    }
    
    if(file.trim().length() < 1)  return '/'+link;
    String value ;
    if(file.endsWith("/"))
      value = file+link;
    else if(file.endsWith("?") || link.startsWith("?"))
      value =  file+link;
    else
      value =  file.trim().substring(0, file.lastIndexOf("/")+1)+link;
    value = value.trim();
    if(value.charAt(0) != '/') value = '/'+value;
    return value;
  }
}
