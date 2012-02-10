/*
 * Copyright (C) 2003-2011 eXo Platform.
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
package org.exoplatform.platform.gadget.services.newspaces;

import org.exoplatform.platform.gadget.services.newspaces.IntranetSpace;

import java.util.List;

/**
 * 
 * @author <a href="tungdt@exoplatform.com">Do Thanh Tung </a>
 * @version $Revision$
 */
public interface IntranetSpaceService {
  
/**
 * Get latest created space from maxday ago
 * @param maxday is the days ago
 * @return
 */
  public List<IntranetSpace> getLatestCreatedSpace (int maxday, String language, List<String> allGroupAndMembershipOfUser ) ;

}