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
package org.exoplatform.web.application.widget;

import java.io.Writer;
import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.web.application.Application;
/**
 * Created by The eXo Platform SAS
 * Apr 23, 2007  
 */
abstract public class WidgetApplication<T> extends Application {
  
  public String getApplicationType() { return "eXoWidget" ; }
  
  abstract public void processRender(T uiWidget, Writer w) throws Exception ;
  
  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    throw new Exception("This method is not supported") ;
  }

  public ResourceBundle getResourceBundle(Locale locale) throws Exception {
    throw new Exception("This method is not supported") ;
  }
}
