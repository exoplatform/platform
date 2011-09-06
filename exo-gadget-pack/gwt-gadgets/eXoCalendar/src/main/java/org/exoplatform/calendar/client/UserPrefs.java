/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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
package org.exoplatform.calendar.client;

import com.google.gwt.gadgets.client.StringPreference;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.UserPreferences.PreferenceAttributes.Options;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 13, 2011  
 */
public interface UserPrefs extends UserPreferences {
  
  @PreferenceAttributes(display_name="Rest URL", default_value = "http://localhost:8080/rest", options = Options.REQUIRED)
  StringPreference restURL();
}
