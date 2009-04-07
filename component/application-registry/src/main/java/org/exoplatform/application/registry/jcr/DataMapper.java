/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.registry.jcr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          thanhtungty@gmail.com
 * Nov 23, 2007  
 */
public class DataMapper {
  
  final static String EXO_REGISTRYENTRY_NT = "exo:registryEntry" ;
  final static String PRIMARY_TYPE = "jcr:primaryType" ;

  static final String TYPE = "exo:type" ;
  
  static final String CATEGORY_NAME = "exo:name" ;
  
  static final String DESCRIPTION = "exo:description" ;
  static final String DISPLAY_NAME = "exo:displayName" ;
  static final String CREATED_DATE = "exo:createdDate" ;
  static final String MODIFIED_DATE = "exo:modifiedDate" ;
  static final String CATEGORY_ACCESS_PERMISSTION = "exo:categoryAccessPermissions" ;
  
  static final String APPLICATION_NAME = "exo:applicationName" ;
  static final String APPLICATION_TYPE = "exo:applicationType" ;
  static final String APPLICATION_GROUP = "exo:applicationGroup" ;
  static final String APPLICATION_CATEGORY_NAME = "exo:categoryName" ;  
  static final String APPLICATION_MIN_WITH_RESOLUTION = "exo:minWidthResolution" ;
  static final String APPLICATION_ACCESS_PERMISSTION = "exo:accessPermissions" ;
  
  private DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd'T'HH:mm:ss.SSS Z");
  
  public void map(Document doc, ApplicationCategory category) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(PRIMARY_TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(TYPE, category.getClass().getSimpleName()) ;
    root.setAttribute(CATEGORY_NAME, category.getName()) ;
    root.setAttribute(DISPLAY_NAME, category.getDisplayName()) ;
    root.setAttribute(DESCRIPTION, category.getDescription()) ;
    Date dateTime = category.getCreatedDate() ;
    if(dateTime == null) dateTime = new Date() ;
    root.setAttribute(CREATED_DATE, dateFormat.format(dateTime)) ;
    dateTime = category.getModifiedDate() ;
    if(dateTime == null) dateTime = new Date() ;
    root.setAttribute(MODIFIED_DATE, dateFormat.format(dateTime)) ;
    root.setAttribute(CATEGORY_ACCESS_PERMISSTION, toMultiValue(category.getAccessPermissions())) ;
  }
  
  public ApplicationCategory toApplicationCategory(Document doc) throws Exception {
    ApplicationCategory category = new ApplicationCategory() ;
    Element root = doc.getDocumentElement() ;
    category.setName(root.getAttribute(CATEGORY_NAME)) ;
    category.setDisplayName(root.getAttribute(DISPLAY_NAME)) ;
    category.setDescription(root.getAttribute(DESCRIPTION)) ;
    category.setCreatedDate(parse(root.getAttribute(CREATED_DATE))) ;
    category.setModifiedDate(parse(root.getAttribute(MODIFIED_DATE))) ;
    category.setAccessPermissions(fromMultiValue(root.getAttribute(CATEGORY_ACCESS_PERMISSTION))) ;
    return category ;
  }
  
  public void map(Document doc, Application application) throws Exception {
    Element root = doc.getDocumentElement() ;
    prepareXmlNamespace(root) ;
    root.setAttribute(PRIMARY_TYPE, EXO_REGISTRYENTRY_NT) ;
    root.setAttribute(TYPE, application.getClass().getSimpleName()) ;
    root.setAttribute(APPLICATION_NAME, application.getApplicationName()) ;
    root.setAttribute(APPLICATION_TYPE, application.getApplicationType()) ;
    root.setAttribute(APPLICATION_GROUP, application.getApplicationGroup()) ;
    root.setAttribute(DESCRIPTION, application.getDescription()) ;
    root.setAttribute(DISPLAY_NAME, application.getDisplayName()) ;
    root.setAttribute(APPLICATION_CATEGORY_NAME, application.getCategoryName()) ;
    root.setAttribute(APPLICATION_MIN_WITH_RESOLUTION, String.valueOf(application.getMinWidthResolution())) ;
    Date dateTime = application.getCreatedDate() ;
    if(dateTime == null) dateTime = new Date() ;
    root.setAttribute(CREATED_DATE, dateFormat.format(dateTime)) ;
    dateTime = application.getModifiedDate() ;
    if(dateTime == null) dateTime = new Date() ;
    root.setAttribute(MODIFIED_DATE, dateFormat.format(dateTime)) ;
    root.setAttribute(APPLICATION_ACCESS_PERMISSTION, toMultiValue(application.getAccessPermissions())) ;
  }
  
  public Application toApplication(Document doc) throws Exception {
    Application application = new Application() ;
    Element root = doc.getDocumentElement() ;
    application.setApplicationName(root.getAttribute(APPLICATION_NAME)) ;
    application.setApplicationType(root.getAttribute(APPLICATION_TYPE)) ;
    application.setApplicationGroup(root.getAttribute(APPLICATION_GROUP)) ;
    application.setDisplayName(root.getAttribute(DISPLAY_NAME)) ;
    application.setDescription(root.getAttribute(DESCRIPTION)) ;
    application.setCategoryName(root.getAttribute(APPLICATION_CATEGORY_NAME)) ;
    application.setMinWidthResolution(Integer.parseInt(root.getAttribute(APPLICATION_MIN_WITH_RESOLUTION))) ;
    application.setCreatedDate(parse(root.getAttribute(CREATED_DATE))) ;
    application.setModifiedDate(parse(root.getAttribute(MODIFIED_DATE))) ;
    application.setAccessPermissions(fromMultiValue(root.getAttribute(APPLICATION_ACCESS_PERMISSTION))) ;
    
    return application ;
  }
  
  private synchronized Date parse(String dateString) throws ParseException {
    return dateFormat.parse(dateString) ;
  }
  
  private String toMultiValue(List<String> list) {
    StringBuilder builder = new StringBuilder() ;
    int size = list.size() ;
    for(int i = 0; i < size; i++) {
      builder.append(list.get(i)) ;
      if(i < (size - 1)) builder.append(" ") ;
    }
    return builder.toString() ;
  }
  
  private ArrayList<String> fromMultiValue(String str) {
    ArrayList<String> list = new ArrayList<String>() ;
    String[] fragments = str.split(" ") ;
    for(String ele : fragments) {
      list.add(ele) ;
    }
    return list ;
  }
  
  private void prepareXmlNamespace(Element element) {
    setXmlNameSpace(element, "xmlns:exo", "http://www.exoplatform.com/jcr/exo/1.0") ;
    setXmlNameSpace(element, "xmlns:jcr", "http://www.jcp.org/jcr/1.0") ;
  }
  
  private void setXmlNameSpace(Element element, String key, String value) {
    String xmlns = element.getAttribute(key) ; 
    if(xmlns == null || xmlns.trim().length() < 1) {
      element.setAttribute(key, value) ;
    }    
  }
  
}
