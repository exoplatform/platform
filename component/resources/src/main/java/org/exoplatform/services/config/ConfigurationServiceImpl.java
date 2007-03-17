/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.config;

import java.util.Iterator;

import org.exoplatform.container.xml.ObjectParam;
import org.exoplatform.container.xml.ServiceConfiguration;
import org.exoplatform.services.database.HibernateService;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
/**
 * @author Tuan Nguyen (tuan08@users.sourceforge.net)
 * @since Dec 5, 2004
 * @version $Id: ConfigurationServiceImpl.java 5799 2006-05-28 17:55:42Z geaz $
 */
public class ConfigurationServiceImpl implements ConfigurationService {
  
  private HibernateService hservice_ ;
  private XStream xstream_ ; 
  
  public ConfigurationServiceImpl(HibernateService service ) {
    hservice_ = service ;
    xstream_ = new XStream(new XppDriver());
  }
  
  public Object getServiceConfiguration(Class serviceType) throws Exception {
    ConfigurationData impl = 
      (ConfigurationData) hservice_.findOne(ConfigurationData.class, serviceType.getName()) ;
    Object obj = null ;
    if(impl == null) {
      obj = loadDefaultConfig(serviceType) ;
      saveServiceConfiguration(serviceType, obj) ;
    } else {
      obj = xstream_.fromXML(impl.getData()) ;
    }
    return obj;
  }

  public void saveServiceConfiguration(Class serviceType, Object config) throws Exception {
    ConfigurationData configData = 
      (ConfigurationData) hservice_.findOne(ConfigurationData.class, serviceType.getName()) ;
    String xml = xstream_.toXML(config) ;
    if(configData == null) {
      configData = new ConfigurationData();
      configData.setServiceType(serviceType.getName()) ;
      configData.setData(xml) ;
      hservice_.create(configData) ;
    } else {
      configData.setData(xml) ;
      hservice_.update(configData) ;
    }
  }

  public void removeServiceConfiguration(Class serviceType) throws Exception {
    hservice_.remove(serviceType, serviceType.getName()) ;
  }
  
  @SuppressWarnings("unused")
  private Object loadDefaultConfig(Class serviceType) throws Exception {
//    ServiceConfiguration sconf = manager_.getServiceConfiguration(serviceType) ;
//    Iterator i = sconf.values().iterator() ;
//    ObjectParam param = (ObjectParam) i.next() ;
//    return param.getObject() ;
    return null;
  }
  
}