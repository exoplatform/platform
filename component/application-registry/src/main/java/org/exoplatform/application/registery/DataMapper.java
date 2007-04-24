/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.registery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 9, 2007  
 */
class DataMapper {
  
  private final String id = "id" ;
  private final String name = "name";
  private final String displayName = "displayName";
  private final String aliasName = "aliasName";
  private final String description = "description";
  private final String categoryName = "categoryName";
  
  private final String createdDate = "createdDate";
  private final String modifiedDate = "modifiedDate";
  
  private final String applicationName = "applicationName";
  private final String applicationGroup = "applicationGroup";
  private final String applicationType= "applicationType";
  
  private final String accessGroup = "accessGroup";
  private final String minWidthResolution = "minWidthResolution";
  

  ApplicationCategory nodeToApplicationCategory(Node node) throws Exception {
    ApplicationCategory data = new ApplicationCategory();
    if(!node.hasProperty(name)) return null;
    data.setName(node.getProperty(name).getString().trim());
    
    if(node.hasProperty(displayName)) { 
      data.setDisplayName(node.getProperty(displayName).getString().trim());
    }
    if(node.hasProperty(description)) { 
      data.setDescription(node.getProperty(description).getString());
    }
    data.setCreatedDate(node.getProperty(createdDate).getDate().getTime());
    data.setModifiedDate(node.getProperty(modifiedDate).getDate().getTime());
    return data;
  }

  void applicationCategoryToNode(ApplicationCategory data, Node node) throws Exception {
    node.setProperty(name, data.getName());
    node.setProperty(displayName, data.getDisplayName());
    node.setProperty(description, data.getDescription());
    
    Calendar calendar = Calendar.getInstance();
    if(data.getCreatedDate() != null) calendar.setTime(data.getCreatedDate());
    node.setProperty(createdDate, calendar);
    
    calendar = Calendar.getInstance();
    if(data.getModifiedDate() != null) calendar.setTime(data.getModifiedDate());
    node.setProperty(modifiedDate, calendar);
  }

  Application nodeToApplication(Node node) throws Exception {
    Application application = new Application();
    if(!node.hasProperty(id)) return null;
    application.setId(node.getProperty(id).getString().trim());
    
    if(node.hasProperty(aliasName)) { 
      application.setAliasName(node.getProperty(aliasName).getString().trim());
    }
    if(node.hasProperty(displayName)) { 
      application.setDisplayName(node.getProperty(displayName).getString().trim());
    }
    if(node.hasProperty(description)) {
      application.setDescription(node.getProperty(description).getString());
    }    
    if(node.hasProperty(categoryName)) {
      application.setCategoryName(node.getProperty(categoryName).getString());
    }
    
    if(node.hasProperty(applicationName)) {
      application.setApplicationName(node.getProperty(applicationName).getString());
    }   
    if(node.hasProperty(applicationType)) {
      application.setApplicationType(node.getProperty(applicationType).getString());
    }
    if(node.hasProperty(applicationGroup)) {
      application.setApplicationGroup(node.getProperty(applicationGroup).getString());
    }
    
    if(node.hasProperty(accessGroup)) {
      List<String> values = new ArrayList<String>();
      PropertyIterator iterator  = node.getProperties();
      while(iterator.hasNext()) {
        values.add(iterator.next().toString());
      }
      String [] accessGroups = new String[values.size()];
      values.toArray(accessGroups);
      application.setAccessGroup(accessGroups);
    }
    
    if(node.hasProperty(minWidthResolution)) {
      application.setMinWidthResolution((int)node.getProperty(minWidthResolution).getLong());
    }
    application.setCreatedDate(node.getProperty(createdDate).getDate().getTime());
    application.setModifiedDate(node.getProperty(modifiedDate).getDate().getTime());
    return application;
  }

  void applicationToNode(Application application, Node node) throws Exception {
    node.setProperty(id, application.getId());
    node.setProperty(aliasName, application.getAliasName());
    node.setProperty(displayName, application.getDisplayName());
    node.setProperty(description, application.getDescription());
    
    node.setProperty(categoryName, application.getCategoryName());
    node.setProperty(minWidthResolution, application.getMinWidthResolution());
    node.setProperty(accessGroup, application.getAccessGroup());
    
    node.setProperty(applicationName, application.getApplicationName());
    node.setProperty(applicationGroup, application.getApplicationGroup());
    node.setProperty(applicationType, application.getApplicationType());
    
    Calendar calendar = Calendar.getInstance();
    if(application.getCreatedDate() != null) calendar.setTime(application.getCreatedDate());
    node.setProperty(createdDate, calendar);

    calendar = Calendar.getInstance();
    if(application.getModifiedDate() != null) calendar.setTime(application.getModifiedDate());
    node.setProperty(modifiedDate, calendar);   
  }
  
}
