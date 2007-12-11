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
package org.exoplatform.json;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 26, 2005
 */

public class JSONService {
  
  public static int NUMBER_SPACE = 3;

  private HashMap<Class, BeanToJSONPlugin> plugins_ ;

  public JSONService() throws Exception {
    plugins_ = new HashMap<Class, BeanToJSONPlugin>() ;
    register(ReflectToJSONPlugin.class, new ReflectToJSONPlugin());
    register(ArrayToJSONPlugin.class, new ArrayToJSONPlugin());
    register(MapToJSONPlugin.class, new MapToJSONPlugin());
  }

  public void register(Class clazz, BeanToJSONPlugin plugin) {
    plugin.setService(this);
    plugins_.put(clazz, plugin);
  }

  public  void unregister(Class clazz) {
    if(!plugins_.containsKey(clazz)) return ;
    plugins_.remove(clazz);
  }

  @SuppressWarnings("unchecked")
  public <T>  void toJSONScript(T bean, StringBuilder b, int indentLevel) throws Exception {
    BeanToJSONPlugin plugin = getConverterPlugin(bean);
    plugin.toJSONScript(bean, b, indentLevel);
    int s = b.lastIndexOf(",");
    int k = b.lastIndexOf(":");
    if(s > k) b.deleteCharAt(s);
  }
  
  public ArrayToJSONPlugin getArrayToJSONPlugin(){
    return (ArrayToJSONPlugin)plugins_.get(ArrayToJSONPlugin.class);
  }
  
  public BeanToJSONPlugin getConverterPlugin(Object object) throws Exception {
    Class clazz = object.getClass();
    BeanToJSONPlugin plugin = null;
    if(plugins_.containsKey(clazz)) plugin = plugins_.get(clazz);
    if(plugin != null) return plugin;
    if(object instanceof Map || object instanceof JSONMap){
      plugin = plugins_.get(MapToJSONPlugin.class);
    }
    if(plugin != null) return plugin;
    if(clazz.isArray()) plugin = plugins_.get(ArrayToJSONPlugin.class);   
    if(plugin != null) return plugin;
    return plugins_.get(ReflectToJSONPlugin.class);
  }
  
  public BeanToJSONPlugin getConverterPlugin(Class clazz) throws Exception {
    if(clazz.isArray()) return plugins_.get(ArrayToJSONPlugin.class);    
    if(plugins_.containsKey(clazz)) return plugins_.get(clazz);
    return plugins_.get(ReflectToJSONPlugin.class);
  }
}