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
package org.exoplatform.ks.test.mock;

import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;

/**
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
public class MockWebuiApplication extends WebuiApplication {

  @Override
  public String getApplicationInitParam(String name) {

    return null;
  }

  @Override
  public String getApplicationGroup() {

    return null;
  }

  @Override
  public String getApplicationId() {

    return null;
  }

  @Override
  public String getApplicationName() {

    return null;
  }

  @Override
  public String getApplicationType() {

    return null;
  }

  @Override
  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {

    return null;
  }

  @Override
  public ResourceBundle getResourceBundle(Locale locale) throws Exception {

    return rb;
  }
  
  ResourceBundle rb;
  
  public void setResourceBundle(ResourceBundle rb) {
    this.rb = rb;
  }
  
  public <T extends UIComponent> T createUIComponent(Class<T> type, String configId, String id,
                                                     WebuiRequestContext context) throws Exception {
    return type.getConstructor().newInstance();
    
  }

}
