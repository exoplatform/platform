/*******************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL All rights reserved. * Please look
 * at license.txt in info directory for more license detail. *
 ******************************************************************************/
package org.exoplatform.services.resources.impl;

import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.commons.utils.MapResourceBundle;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.database.DBObjectPageList;
import org.exoplatform.services.database.HibernateService;
import org.exoplatform.services.database.ObjectQuery;
import org.exoplatform.services.log.LogService;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;
import org.exoplatform.services.resources.ResourceBundleDescription;
import org.hibernate.Session;

public class ResourceBundleServiceImpl extends BaseResourceBundleService {
  
  private HibernateService hService_;

  public ResourceBundleServiceImpl(HibernateService service,
      LocaleConfigService localeService, LogService lservice, CacheService cService, InitParams params) throws Exception {
    log_ = lservice.getLog("org.exoplatform.services.resources");
    cache_ = cService.getCacheInstance(getClass().getName());    
    localeService_ = localeService;
    hService_ = service ;
    initParams(params);
  }

  public ResourceBundleData getResourceBundleData(String name) throws Exception {
    return (ResourceBundleData) hService_.findOne(ResourceBundleData.class, name);
  }

  public ResourceBundleData removeResourceBundleData(String id) throws Exception {
    ResourceBundleData data =  (ResourceBundleData) hService_.remove(ResourceBundleData.class, id);
    cache_.remove(data.getId());
    return data;
  }

  public PageList findResourceDescriptions(Query q) throws Exception {
    String name = q.getName();
    if (name == null || name.length() == 0)	name = "%";
    ObjectQuery oq = new ObjectQuery(ResourceBundleDescription.class);
    oq.addLIKE("name", name);
    oq.addLIKE("language", q.getLanguage());
    oq.setDescOrderBy("name");
    return new DBObjectPageList(hService_, oq);
  }

  public void saveResourceBundle(ResourceBundleData data) throws Exception {
    hService_.save(data);
    cache_.remove(data.getId());
  }

  ResourceBundle getResourceBundleFromDb(String id,	ResourceBundle parent, Locale locale) throws Exception {
    Session session = hService_.openSession();
    ResourceBundleData data = (ResourceBundleData) session.get(ResourceBundleData.class, id);
    if (data == null)  return null;
    ResourceBundle res = new ExoResourceBundle(data.getData(), parent);
    MapResourceBundle mres = new MapResourceBundle(res, locale) ;
    return mres;
  }

}
