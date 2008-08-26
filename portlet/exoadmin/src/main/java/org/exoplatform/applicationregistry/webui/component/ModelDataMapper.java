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
package org.exoplatform.applicationregistry.webui.component;

import java.util.Map;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.web.application.gadget.GadgetApplication;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Aug 26, 2008  
 */
public class ModelDataMapper {

  static final public Gadget toGadgetModel(GadgetApplication gadgetApp) throws Exception {
    Gadget gadget = new Gadget();
    Map<String, String> metaData = gadgetApp.getMapMetadata() ;
    gadget.setUrl(gadgetApp.getUrl()) ;
    String title = metaData.get("directoryTitle") ;
    if(title == null || title.trim().length() < 1) title = metaData.get("title") ;
    gadget.setTitle(title) ;
    String name = gadgetApp.getApplicationName() ;
    if(name == null || name.trim().length() < 1) name = title.replace(' ', '_') ;
    gadget.setName(name);
    gadget.setDescription(metaData.get("description")) ;
    gadget.setReferenceUrl(metaData.get("titleUrl")) ;
    gadget.setThumbnail(metaData.get("thumbnail")) ;
    return gadget ;
  }

}
