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
package org.exoplatform.services.html;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 30, 2006
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NodeConfig {  
  Name name() ;
  Class<?> type();
  
  boolean only () default false;  
  boolean hidden () default false;
  boolean block() default false;
  
  MoveType move() default MoveType.ADD;
  
  Tag start () default Tag.REQUIRED;
  Tag end () default Tag.REQUIRED;  
  Class<?> [] end_types() default {};
  Name [] end_names() default {};
  
  
  Name [] parent() default {};
  Name [] children() default {};
  Class<?> [] children_types () default {}; 
}
