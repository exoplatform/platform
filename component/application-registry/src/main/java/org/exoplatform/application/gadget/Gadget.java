/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.application.gadget;

import org.exoplatform.web.application.gadget.GadgetApplication;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jul 28, 2008  
 */
public class Gadget {
  
  private String name ;
  private String url ;
  private String title ;
  private String description ;
  private String referenceUrl ;
  private String thumbnail ;
  private boolean isRemote = true ;
  
  public Gadget() {}
  public Gadget(GadgetApplication app) {
    name = app.getApplicationName() ;
    title = name ;
    url = app.getUrl() ;
  }
  
  public String getName() { return name; }
  public void setName(String n) { name = n; }
  
  public String getUrl() { return url; }
  public void setUrl(String u) { url = u; }
  
  public boolean isRemote() { return isRemote; }
  public void setRemote(Boolean b) { isRemote = b.booleanValue(); }
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  
  public String getReferenceUrl() { return referenceUrl; }
  public void setReferenceUrl(String referenceUrl) { this.referenceUrl = referenceUrl; }
  
  public String getThumbnail() { return thumbnail; }
  public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
  
  public GadgetApplication toGadgetApplication() {
    return new GadgetApplication(name, url) ;
  }
  
}
