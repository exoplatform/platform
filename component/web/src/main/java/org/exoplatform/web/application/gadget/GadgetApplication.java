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

import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.ModulePrefs;
import org.exoplatform.web.application.Application;

/**
 * This class extends from Application, it represents an gadget application in eXo and used for registry
 * Gadgets.
 */
public class GadgetApplication extends Application {
  
  static final public String  EXO_GADGET_GROUP = "eXoGadgets" ;
  private String name_;
  private String url_;
  private boolean isLocal_;

  /**
   * Initializes a newly created <code>GadgetApplication</code> object
   * @param name an String that is the application id of gadget application
   * @param url an String that is the the url of gadget application such as 
   *        "http://www.google.com/ig/modules/horoscope.xml"
   */  
  public GadgetApplication(String name, String url) {
    this(name, url, true);
  }
  
  public GadgetApplication(String name, String url, boolean isLocal) {
    name_ = name;
    url_ = url;
    isLocal_ = isLocal;
  }
  
  /**
   * Gets gadget application type
   * @return org.exoplatform.web.application.Application.EXO_GAGGET_TYPE
   * @see org.exoplatform.web.application.Application
   */
  public String getApplicationType() {
    return EXO_GAGGET_TYPE;
  }
  
  /**
   * Gets group of gadget application
   * @return alway returns "eXoGadgets"
   */
  public String getApplicationGroup() {
    return EXO_GADGET_GROUP;
  }

  /**
   * Gets url of gadget application
   * @return string represents url of gadget application
   */
  public String getUrl() {
    return url_;
  }

  /**
   * Gets id of gadget application
   * @return the string represents id of gadget application
   */
  public String getApplicationId() {
    return EXO_GADGET_GROUP + "/" + name_;
  }

  /**
   * Gets name of gadget application
   * @return the string represents name of gadget application
   */
  public String getApplicationName() {
    return name_;
  }
  
  public boolean isLocal() { return isLocal_; }
  
  static public ModulePrefs getModulePreferences(Uri url, String xml) throws Exception {
    GadgetSpec spec = new GadgetSpec(url, xml);
    return spec.getModulePrefs();
  }

  /**
   * Gets owner resource bundle of gadget application
   * @param username remote username logged in portal
   * @param locale the location of logged user
   * @return always return null
   * @throws Exception if can't get the resource bundle by the ResouceBundleService
   */
  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    return null;
  }

  /**
   * Gets resource bundle of gadget application
   * @param locale the location of logged user
   * @return always return null
   * @throws Exception if can't get the resource bundle by the ResourceBundleService
   */
  public ResourceBundle getResourceBundle(Locale locale) throws Exception {
    return null;
  }
  
}