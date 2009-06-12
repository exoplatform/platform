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
package org.exoplatform.groovyscript.text;

import groovy.lang.Writable;
import groovy.text.Template;

import java.io.InputStream;

import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.resolver.ResourceResolver;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

/**
 * Created by The eXo Platform SAS Dec 26, 2005
 */
@Managed
@NameTemplate( { @Property(key = "view", value = "portal"),
    @Property(key = "service", value = "management"), @Property(key = "type", value = "template") })
@ManagedDescription("Template management service")
public class TemplateService {

  private SimpleTemplateEngine       engine_;

  private ExoCache<String, Template> templatesCache_;

  private TemplateStatisticService   statisticService;

  private boolean                    cacheTemplate_ = true;

  public TemplateService(InitParams params,
                         TemplateStatisticService statisticService,
                         CacheService cservice) throws Exception {
    engine_ = new SimpleTemplateEngine();
    this.statisticService = statisticService;
    templatesCache_ = cservice.getCacheInstance(TemplateService.class.getName());
    getTemplatesCache().setLiveTime(10000);
  }

  public void merge(String name, BindingContext context) throws Exception {
    long startTime = System.currentTimeMillis();

    Template template = getTemplate(name, context.getResourceResolver());
    context.put("_ctx", context);
    context.setGroovyTemplateService(this);
    Writable writable = template.make(context);
    writable.writeTo(context.getWriter());

    long endTime = System.currentTimeMillis();

    TemplateStatistic templateStatistic = statisticService.getTemplateStatistic(name);
    templateStatistic.setTime(endTime - startTime);
    templateStatistic.setResolver(context.getResourceResolver());
  }

  @Deprecated
  public void merge(Template template, BindingContext context) throws Exception {
    context.put("_ctx", context);
    context.setGroovyTemplateService(this);
    Writable writable = template.make(context);
    writable.writeTo(context.getWriter());
  }

  public void include(String name, BindingContext context) throws Exception {
    if (context == null)
      throw new Exception("Binding cannot be null");
    context.put("_ctx", context);
    Template template = getTemplate(name, context.getResourceResolver());
    Writable writable = template.make(context);
    writable.writeTo(context.getWriter());

  }

  final public Template getTemplate(String name, ResourceResolver resolver) throws Exception {
    return getTemplate(name, resolver, cacheTemplate_);
  }

  final public Template getTemplate(String url, ResourceResolver resolver, boolean cacheable) throws Exception {
    Template template = null;
    if (cacheable) {
      String resourceId = resolver.createResourceId(url);
      template = (Template) getTemplatesCache().get(resourceId);
    }
    if (template != null)
      return template;
    InputStream is;
    byte[] bytes = null;
    is = resolver.getInputStream(url);
    bytes = IOUtil.getStreamContentAsBytes(is);
    is.close();

    String text = new String(bytes);
    template = engine_.createTemplate(text);

    if (cacheable) {
      String resourceId = resolver.createResourceId(url);
      getTemplatesCache().put(resourceId, template);
    }

    return template;
  }

  final public void invalidateTemplate(String name, ResourceResolver resolver) throws Exception {
    String resourceId = resolver.createResourceId(name);
    getTemplatesCache().remove(resourceId);
  }

  public void setTemplatesCache(ExoCache<String, Template> templatesCache_) {
    this.templatesCache_ = templatesCache_;
  }

  public ExoCache<String, Template> getTemplatesCache() {
    return templatesCache_;
  }

  /*
   * Clear the templates cache
   */
  @Managed
  @ManagedDescription("Clear the template cache")
  public void reloadTemplates() {
    try {
      templatesCache_.clearCache();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * Clear the template cache by name
   */
  @Managed
  @ManagedDescription("Clear the template cache for a specified template identifier")
  public void reloadTemplate(@ManagedDescription("The template id") @ManagedName("templateId") String name) {
    try {
      TemplateStatistic app = statisticService.apps.get(name);
      ResourceResolver resolver = app.getResolver();
      templatesCache_.remove(resolver.createResourceId(name));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
