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
package org.exoplatform.portal.webui.skin;

import org.exoplatform.services.resources.Orientation;

import java.util.Map;
import java.util.EnumMap;

/**
 * Created by The eXo Platform SAS
 * Jan 19, 2007  
 */
public class SkinConfig {

  private final String module_ ;
  private final String cssPath_ ;
  private final String id_ ;
  private final Orientation orientation_;
  private final String virtualCSSPath_;

  public SkinConfig(String module, String cssPath) {
    this(module, cssPath, Orientation.LT);
  }

  public SkinConfig(String module, String cssPath, Orientation orientation) {
    module_ = module;
    cssPath_ = cssPath;
    orientation_ = orientation;
    id_  = module.replace('/', '_') ;
    virtualCSSPath_ = cssPath_.replaceAll("\\.css$", SkinService.getSuffix(getOrientation()));
  }

  /**
  * This method is used to compute the virtual path of a CSS, which is the
  * actual CSS path in the war file, augmented with an orientation suffix.
  * (e.g : "/portal/templates/skin/webui/component/UIHomePagePortlet/DefaultStylesheet-lt.css")
  * This virtual path with be used by the browser to retrieve the CSS
  * corresponding to the appopriate orientation.
  *
  * @return the augmented CSS path, containing the orientation suffix.
  */
  public String getVirtualCSSPath() { return virtualCSSPath_;  }

  public String getId() { return id_ ; }
  public String getModule(){ return module_; }
  public String getCSSPath(){ return cssPath_; }
  public Orientation getOrientation() { return orientation_; }
}