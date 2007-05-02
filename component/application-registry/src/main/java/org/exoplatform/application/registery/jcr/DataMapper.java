/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.registery.jcr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;

import org.exoplatform.application.registery.Application;
import org.exoplatform.application.registery.ApplicationCategory;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 9, 2007  
 */
class DataMapper {
  
  private final static String ID = "exo:id" ;
  private final static String NAME = "exo:name";
  private final static String DISPLAY_NAME = "exo:displayName";
  private final static String DESCRIPTION = "exo:description";
  private final static String CATEGORY_NAME = "exo:categoryName";
  
  private final static String CREATED_DATE = "exo:createdDate";
  private final static String MODIFIED_DATE = "exo:modifiedDate";
  
  private final static String APPLICATION_NAME = "exo:applicationName";
  private final static String APPLICATION_GROUP = "exo:applicationGroup";
  private final static String APPLICATION_TYPE= "exo:applicationType";
  
  private final static String ACCESS_GROUP = "exo:accessGroup";
  private final static String MIN_WIDTH_RESOLUTION = "exo:minWidthResolution";
  

  ApplicationCategory nodeToApplicationCategory(Node node) throws Exception {
    ApplicationCategory data = new ApplicationCategory();
    if(!node.hasProperty(NAME)) return null;
    data.setName(node.getProperty(NAME).getString().trim());
    data.setDisplayName(node.getProperty(DISPLAY_NAME).getString());
    if(node.hasProperty(DESCRIPTION)) {
      data.setDescription(node.getProperty(DESCRIPTION).getString());
    }
    data.setCreatedDate(node.getProperty(CREATED_DATE).getDate().getTime());
    data.setModifiedDate(node.getProperty(MODIFIED_DATE).getDate().getTime());
    return data;
  }

  void applicationCategoryToNode(ApplicationCategory data, Node node) throws Exception {
    node.setProperty(NAME, data.getName());
    node.setProperty(DISPLAY_NAME, data.getDisplayName());
    node.setProperty(DESCRIPTION, data.getDescription());
    
    Calendar calendar = Calendar.getInstance();
    if(data.getCreatedDate() != null) calendar.setTime(data.getCreatedDate());
    node.setProperty(CREATED_DATE, calendar);
    
    calendar = Calendar.getInstance();
    if(data.getModifiedDate() != null) calendar.setTime(data.getModifiedDate());
    node.setProperty(MODIFIED_DATE, calendar);
  }

  Application nodeToApplication(Node node) throws Exception {
    Application application = new Application();
    if(!node.hasProperty(ID)) return null;
    application.setId(node.getProperty(ID).getString().trim());
    if(node.hasProperty(DISPLAY_NAME)) {
      application.setDisplayName(node.getProperty(DISPLAY_NAME).getString());
    }
    if(node.hasProperty(DESCRIPTION)) {
      application.setDescription(node.getProperty(DESCRIPTION).getString());
    }
    application.setCategoryName(node.getProperty(CATEGORY_NAME).getString());
    application.setApplicationName(node.getProperty(APPLICATION_NAME).getString());
    application.setApplicationType(node.getProperty(APPLICATION_TYPE).getString());
    application.setApplicationGroup(node.getProperty(APPLICATION_GROUP).getString());
    
    if(node.hasProperty(ACCESS_GROUP)) {
      List<String> values = new ArrayList<String>();
      PropertyIterator iterator  = node.getProperties();
      while(iterator.hasNext()) {
        values.add(iterator.next().toString());
      }
      String [] accessGroups = new String[values.size()];
      values.toArray(accessGroups);
      application.setAccessGroup(accessGroups);
    }
    
    application.setMinWidthResolution((int)node.getProperty(MIN_WIDTH_RESOLUTION).getLong());
    application.setCreatedDate(node.getProperty(CREATED_DATE).getDate().getTime());
    application.setModifiedDate(node.getProperty(MODIFIED_DATE).getDate().getTime());
    return application;
  }

  void applicationToNode(Application application, Node node) throws Exception {
    node.setProperty(ID, application.getId());
    node.setProperty(DISPLAY_NAME, application.getDisplayName());
    node.setProperty(DESCRIPTION, application.getDescription());
    node.setProperty(CATEGORY_NAME, application.getCategoryName());
    node.setProperty(MIN_WIDTH_RESOLUTION, application.getMinWidthResolution());
    node.setProperty(ACCESS_GROUP, application.getAccessGroup());
    
    node.setProperty(APPLICATION_NAME, application.getApplicationName());
    node.setProperty(APPLICATION_GROUP, application.getApplicationGroup());
    node.setProperty(APPLICATION_TYPE, application.getApplicationType());
    
    Calendar calendar = Calendar.getInstance();
    if(application.getCreatedDate() != null) calendar.setTime(application.getCreatedDate());
    node.setProperty(CREATED_DATE, calendar);

    calendar = Calendar.getInstance();
    if(application.getModifiedDate() != null) calendar.setTime(application.getModifiedDate());
    node.setProperty(MODIFIED_DATE, calendar);   
  }
  
}
