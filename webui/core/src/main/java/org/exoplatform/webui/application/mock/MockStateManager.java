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
package org.exoplatform.webui.application.mock;

import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.StateManager;
import org.exoplatform.webui.core.UIApplication;

public class MockStateManager extends StateManager {
  
  @SuppressWarnings("unused")
  public UIApplication restoreUIRootComponent(WebuiRequestContext context) {
    return null;
  }
  
  @SuppressWarnings("unused")
  public void storeUIRootComponent(WebuiRequestContext context) {
  }
  
  @SuppressWarnings("unused")
  public void expire(String sessionId, WebuiApplication app) {    
  
  }
}
