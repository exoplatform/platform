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
package org.exoplatform.download;

import java.io.InputStream;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 26, 2005
 */
public class InputStreamDownloadResource extends DownloadResource {
  private InputStream is_ ;
  
  public InputStreamDownloadResource(InputStream is, String resourceMimeType) {
    this(null, is, resourceMimeType) ;
  }
  
  public InputStreamDownloadResource(String downloadType, InputStream is, String resourceMimeType) {
    super(downloadType,resourceMimeType) ;
    is_ = is ;
  }
  
  public InputStream getInputStream()  {  return is_ ; }
}