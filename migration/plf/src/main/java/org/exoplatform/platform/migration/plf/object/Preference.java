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

package org.exoplatform.platform.migration.plf.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by The eXo Platform SARL
 * Author : Mestrallet Benjamin
 *          benjmestrallet@users.sourceforge.net
 * Date: Jul 27, 2003
 * Time: 9:21:41 PM
 */
public class Preference
{

   private String name;

   private List<String> values = new ArrayList<String>(3);

   private boolean readOnly = false;

   public Preference(String name, String value)
   {
      this.name = name;
      this.values = Collections.singletonList(value);
   }

   public Preference()
   {
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public List<String> getValues()
   {
      return values;
   }

   public void setValues(List<String> values)
   {
      this.values = values;
   }

   public void addValue(String value)
   {
      values.add(value);
   }

   public boolean isReadOnly()
   {
      return readOnly;
   }

   public void setReadOnly(boolean b)
   {
      readOnly = b;
   }

}