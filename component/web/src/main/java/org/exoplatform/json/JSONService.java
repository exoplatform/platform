/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.json;

import java.util.HashMap;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 26, 2005
 */

public class JSONService {
  private HashMap<Class, ObjectToJSONConverterPlugin> plugins_ ;
  
  public JSONService() throws Exception {
    plugins_ = new HashMap<Class, ObjectToJSONConverterPlugin>() ;
  }
  
  public <T extends ObjectToJSONConverterPlugin>  void register(Class clazz, T plugin) {
    if(plugins_.containsKey(clazz)) {
      plugins_.remove(clazz);
      plugins_.put(clazz, plugin);
    }else {
      plugins_.put(clazz, plugin);
    }
  }
  
  public  void unregister(Class clazz) {
    if(!plugins_.containsKey(clazz)) return ;
    plugins_.remove(clazz);
  }
  
  public <T>  void toJSONScript(T object, StringBuilder b, int indentLevel) throws Exception {
    if(!plugins_.containsKey(object.getClass())){
      ObjectToJSONConverterPlugin plugin = plugins_.get(Object.class);
      plugin.toJSONScript(plugins_, object, b, indentLevel);
      return ;
    }
    ObjectToJSONConverterPlugin plugin = plugins_.get(object.getClass());
    plugin.toJSONScript(plugins_, object, b, indentLevel);
  }
}