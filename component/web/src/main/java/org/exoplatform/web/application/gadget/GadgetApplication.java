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
package org.exoplatform.web.application.gadget;

import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.web.application.Application;
/**
 * Created by The eXo Platform SAS
 * Author : dang.tung
 *          tungcnw@gmail.com
 * May 06, 2008   
 */
public class GadgetApplication extends Application {
  private String appId_ ;
  private String url_ ;
  
  public GadgetApplication(String appId, String url) {
    appId_ = appId ;
    url_ = url ;
  }
  public String getApplicationType() { return "eXoGadget" ; }

  public String getApplicationGroup() {
    return "eXoGadgets";
  }
  
  public String getUrl() {
    return url_ ;
  }

  public String getApplicationId() {
    return appId_;
  }

  public String getApplicationName() {
    return appId_;
  }

  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    return null;
  }

  public ResourceBundle getResourceBundle(Locale locale) throws Exception {
    return null;
  }  
}
