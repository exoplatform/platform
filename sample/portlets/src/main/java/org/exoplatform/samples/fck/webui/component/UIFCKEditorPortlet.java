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
package org.exoplatform.samples.fck.webui.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.wysiwyg.UIFormWYSIWYGInput;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Dec 8, 2008  
 */

@ComponentConfig(
                 lifecycle = UIApplicationLifecycle.class
)
public class UIFCKEditorPortlet extends UIPortletApplication{

  public UIFCKEditorPortlet() throws Exception {
    addChild(UIEditor.class, null, null);
  }
  
  @ComponentConfig(
                   lifecycle = UIFormLifecycle.class
  )
  static public class UIEditor extends UIForm {
    
    static final public String TEXT_FIELD = "text";
    public UIEditor() throws Exception {
      UIFormWYSIWYGInput uiInputFCKEditor = new UIFormWYSIWYGInput(TEXT_FIELD, null, null);
      uiInputFCKEditor.setWidth("99%");
      addUIFormInput(uiInputFCKEditor);
    }
  }

}
