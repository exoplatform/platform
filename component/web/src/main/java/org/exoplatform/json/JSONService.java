/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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