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
package org.exoplatform.commons.testing.webui;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;

/**
 * Convenience TestCase made to test UIComponent classes.
 * Creates a new UIComponent for each call to {@link #setUp()}
 * @param T type of the UIComponent
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
public abstract class AbstractUIComponentTestCase<T extends UIComponent> extends AbstractWebuiTestCase {

  public AbstractUIComponentTestCase() throws Exception {
    super();
    
  }


  protected T component;
  
  
  public void doSetUp() {  
    try {
      initComponent();
    } catch (Exception e) {
      fail("failed to initialize UIComponent: " + e.getMessage());
    }
  }
  
  private void initComponent() throws Exception {
    this.component = createComponent();
    this.component.setParent(new MockUIApplication());
  }

  protected abstract T createComponent() throws Exception;
  
  protected void assertApplicationMessage(String key) {
    UIApplication app = component.getAncestorOfType(UIApplication.class);
    boolean found = false;
    for (ApplicationMessage message : app.getUIPopupMessages().getWarnings()) {
      if(key.equals(message.getMessageKey())) {
        found = true;
      }
    }
    
    for (ApplicationMessage message : app.getUIPopupMessages().getInfos()) {
      if(key.equals(message.getMessageKey())) {
        found = true;
      }
    }
    
    for (ApplicationMessage message : app.getUIPopupMessages().getErrors()) {
      if(key.equals(message.getMessageKey())) {
        found = true;
      }
    }
    
    assertTrue("Message not found <" + key +">", found);
    
  }
  
  
    class MockUIApplication extends UIApplication {

    public MockUIApplication() throws Exception {
      super();
      
    }

  }
  
}
