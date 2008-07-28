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
package org.exoplatform.application.gadget.jcr;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.web.application.gadget.GadgetStorage;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jul 28, 2008  
 */
public class GadgetStorageImpl implements GadgetStorage {
  
  private GadgetRegistryService gadgetRegistryService_ ;
  
  public GadgetStorageImpl(GadgetRegistryService service) {
    gadgetRegistryService_ = service ;
  }

  public void addGadget(GadgetApplication app) throws Exception {
    Gadget gadget = new Gadget(app) ;
    gadgetRegistryService_.addGadget(gadget) ;
  }

  public GadgetApplication getGadget(String name) throws Exception {
    Gadget gadget = gadgetRegistryService_.getGadget(name) ;
    return (gadget == null ? null : gadget.toGadgetApplication()) ;
  }

}
