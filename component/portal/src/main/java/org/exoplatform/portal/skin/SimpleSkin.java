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
package org.exoplatform.portal.skin;

import org.exoplatform.services.resources.Orientation;

/**
 * An implementation of the skin config.
 *
 * Created by The eXo Platform SAS
 * Jan 19, 2007
 */
class SimpleSkin implements SkinConfig {

  private final SkinService service_;
  private final String module_ ;
  private final String name_;
  private final String cssPath_ ;
  private final String id_ ;

  public SimpleSkin(SkinService service, String module, String name, String cssPath) {
    service_ = service;
    module_ = module;
    name_ = name;
    cssPath_ = cssPath;
    id_  = module.replace('/', '_') ;
  }

  public String getId() { return id_ ; }
  public String getModule(){ return module_; }
  public String getCSSPath(){ return cssPath_; }
  public String getName() { return name_; }

  public SkinURL createURL() {
    return new SkinURL() {

      Orientation orientation = null;

      public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
      }

      @Override
      public String toString() {
        return cssPath_.replaceAll("\\.css$", service_.getSuffix(orientation));
      }
    };
  }
}