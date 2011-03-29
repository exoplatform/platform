/**
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.util.List;
import java.util.ArrayList;
import org.exoplatform.portal.webui.portal.PortalTemplateConfigOption ;
import org.exoplatform.webui.core.model.SelectItemCategory;

List options = new ArrayList();

  SelectItemCategory acme = new SelectItemCategory("ACMEPortal");
  acme.addSelectItemOption(
      new PortalTemplateConfigOption("ACMEPortal", "acme", "ACME Portal", "ACMEPortal").addGroup("/platform/guests")
  );
  options.add(acme);  
  
  SelectItemCategory intranet = new SelectItemCategory("IntranetPortal");
  intranet.addSelectItemOption(
      new PortalTemplateConfigOption("IntranetPortal", "intranet", "Intranet Portal", "IntranetPortal").addGroup("/platform/guests")
  );
  options.add(intranet);
  
  SelectItemCategory basic = new SelectItemCategory("EmptyPortal");
  basic.addSelectItemOption(
      new PortalTemplateConfigOption("EmptyPortal", "empty", "Empty Portal", "EmptyPortal").addGroup("/platform/guests")
  );
  options.add(basic);
  
return options ;
