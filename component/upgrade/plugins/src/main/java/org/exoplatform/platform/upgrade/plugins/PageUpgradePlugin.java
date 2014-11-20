/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.upgrade.plugins;

import java.util.ArrayList;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2014  
 */
public class PageUpgradePlugin extends UpgradeProductPlugin {
  
  private static final Log LOG = ExoLogger.getLogger(PageUpgradePlugin.class.getName());
  private static final String INTRANET = "intranet";
  private static final String NAVIGATION_BODY = "NavigationBody";
  private static final String LEFT_NAVIGATION = "LeftNavigation";
  private static final String BOTTOM_LEFT_NAVIGATION = "bottom-leftNavigation-container";
  private static final String ADDON_CONTAINER = "addonContainer";
  
  private static final String ADDON_TEMPLATE = "system:/groovy/portal/webui/container/UIAddOnContainer.gtmpl";
  private static final String CONTAINER_TEMPLATE = "system:/groovy/portal/webui/container/UIContainer.gtmpl";
  
  private static final String[] PERMISSION = new String[]{"*:/platform/users"}; 
  
  
  private static final String OFFICE_BODY = "Officebody";
  private static final String OFFICE_RIGHT = "OfficeRight";
  private static final String BOTTOM_WIKI_CONTAINER = "bottom-wiki-container";
  
  private static final String DOCUMENT_CONTAINER = "DocumentContainer";
  private static final String DOCUMENT_TOP_CONTAINER = "DocumentTopContainer";
  private static final String BOTTOM_DOCUMENT_CONTAINER = "bottom-document-container";
  
  private static final String TOP_SOCIAL_CONTAINER = "top-social-container";
  private static final String MIDDLE_SOCIAL_CONTAINER = "middle-social-container";
  private static final String TOP_APPLICATION_CONTAINER = "top-application-container";
  private static final String BOTTOM_APPLICATION_CONTAINER = "bottom-application-container";
  
  private static final String OFFICE_MIDDLE_CLV_CONTAINER = "OfficeMiddleCLVContainer";
  private static final String OFFICE_MIDDLE_AS_CONTAINER = "OfficeMiddleASContainer";
  private static final String OFFICE_RIGHT_MIDDLE_CONTAINER = "OfficeRightMiddle";
  
  private DataStorage dataStorage;

  public PageUpgradePlugin(DataStorage dataStorage, InitParams initParams) {
    super(initParams);
    this.dataStorage = dataStorage;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Start " + this.getClass().getName() + ".............");
    }
    try {
      RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
      addTopApplicationContainer();
      addBottomLeftNavigationContainer();
      //addBottomApplicationContainer();
      //addTopSocialContainer();
      addBottomWikiContainer();
      addBottomDocumentContainer();
      if (LOG.isInfoEnabled()) {
        LOG.info(this.getClass().getName() + " finished successfully!");
      }
    } catch (AlreadyUpgradeException aue) {
      if (LOG.isInfoEnabled()) {
        LOG.info("Data was already upgraded!");
        LOG.info(this.getClass().getName() + " finished successfully!");
      }
    } catch (WrongModelObjectException we) {
      if (LOG.isWarnEnabled()) {
        StringBuilder sb = 
        new StringBuilder().append("An unexpected error occurs when migrating pages. ")
            .append(we.getObjectId())
            .append(" is not found or not in its original position.")
            .append(" If you have modified the intranet home page layout by yourself, ")
            .append("please add the dynamic containers manually, following these links:\n")
            .append("http://blog.exoplatform.com/en/2014/09/18/super-easy-guide-inject-portlets-wherever-want and\n")
            .append("http://docs.exoplatform.com/public/topic/PLF41/PLFDevGuide.DevelopingApplications.")
            .append("DevelopingPortlet.Deployment.Injection.html");
        LOG.warn(sb.toString());
      }
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("An unexpected error occurs when migrating pages:", e);        
      }
    } finally {
      RequestLifeCycle.end();
    }
  }
  
  private <T extends ModelObject>T getModelObject(ArrayList<ModelObject> list, Class<T> type, int position,
                                                  String objectId) throws Exception{
    if (list != null && list.size() > position) {
      ModelObject rest = list.get(position);
      if (type.isInstance(rest)) {
        return type.cast(rest);
      }
    }
    throw new WrongModelObjectException(objectId);
  }
  
  private void addBottomLeftNavigationContainer() throws Exception {
    PortalConfig config = dataStorage.getPortalConfig(INTRANET);
    Container navigationBody = getModelObject(config.getPortalLayout().getChildren(), Container.class, 0, NAVIGATION_BODY); 
        //(Container)config.getPortalLayout().getChildren().get(0);
    if (NAVIGATION_BODY.equals(navigationBody.getId())) {
      Container leftNavigation = getModelObject(navigationBody.getChildren(), Container.class, 0, LEFT_NAVIGATION); 
          //(Container)navigationBody.getChildren().get(0);
      if (LEFT_NAVIGATION.equals(leftNavigation.getId())) {
        Container bottomLeftNavigation = 
            createContainer(BOTTOM_LEFT_NAVIGATION, ADDON_TEMPLATE, PERMISSION, ADDON_CONTAINER); 
        ArrayList<ModelObject> childs = leftNavigation.getChildren();
        childs.add(bottomLeftNavigation);
        leftNavigation.setChildren(childs);
        dataStorage.save(config);
      }
    }
    if (LOG.isInfoEnabled()) {
      LOG.info(BOTTOM_LEFT_NAVIGATION + " created!");
    }
  }
  
  private void addBottomWikiContainer() throws Exception {
    SiteKey siteKey = new SiteKey(SiteType.PORTAL, INTRANET);
    PageKey pageKey = new PageKey(siteKey, "wiki");
    Page page = dataStorage.getPage(pageKey.format());

    Container bottomWikiContainer = 
        createContainer(BOTTOM_WIKI_CONTAINER, ADDON_TEMPLATE, PERMISSION, ADDON_CONTAINER);

    ArrayList<ModelObject> children = page.getChildren();
    children.add(bottomWikiContainer);
    page.setChildren(children);
    
    dataStorage.save(page);
    
    if (LOG.isInfoEnabled()) {
      LOG.info(BOTTOM_WIKI_CONTAINER + " created!");
    }
  }
  
  private void addBottomDocumentContainer() throws Exception {
    SiteKey siteKey = new SiteKey(SiteType.PORTAL, INTRANET);
    PageKey pageKey = new PageKey(siteKey, "documents");
    Page page = dataStorage.getPage(pageKey.format());
    
    ArrayList<ModelObject> children = page.getChildren();

    Container documentContainer = 
        createContainer(DOCUMENT_CONTAINER,CONTAINER_TEMPLATE,PERMISSION, null);

    Container documentTopContainer = 
        createContainer(DOCUMENT_TOP_CONTAINER, CONTAINER_TEMPLATE, PERMISSION, null);
    
    documentTopContainer.setChildren(children);
    
    Container bottomDocumentContainer = 
        createContainer(BOTTOM_DOCUMENT_CONTAINER,ADDON_TEMPLATE, PERMISSION, ADDON_CONTAINER);
    
    ArrayList<ModelObject> childContainers = new ArrayList<ModelObject>();
    childContainers.add(documentTopContainer);
    childContainers.add(bottomDocumentContainer);

    documentContainer.setChildren(childContainers);
    
    ArrayList<ModelObject> pageChilds = new ArrayList<ModelObject>();
    pageChilds.add(documentContainer);
    page.setChildren(pageChilds);
    
    dataStorage.save(page);
    
    if (LOG.isInfoEnabled()) {
      LOG.info(BOTTOM_DOCUMENT_CONTAINER + " created!");
    }
  }  
  
  private void addTopApplicationContainer() throws Exception {
    for (String homepage : new String[] {"homepage-demo", "homepage"}) {
      SiteKey siteKey = new SiteKey(SiteType.PORTAL, INTRANET);
      PageKey pageKey = new PageKey(siteKey, homepage);
      Page page = dataStorage.getPage(pageKey.format());
      if (page == null) continue;
      Container officebody = getModelObject(page.getChildren(), Container.class, 0, "OfficeBody"); 
          //(Container)page.getChildren().get(0);
      Container officeMiddle = getModelObject(officebody.getChildren(), Container.class, 0, "OfficeMiddle");
          //(Container)officebody.getChildren().get(0);
      Container officeRight = getModelObject(officebody.getChildren(), Container.class, 1, "OfficeRight");
          //(Container)officebody.getChildren().get(1);
      //test if data was overwritten
      ArrayList<ModelObject> mol = officeMiddle.getChildren();
      if (mol.size() > 0) {
        ModelObject mo = mol.get(0);
        if (mo instanceof Container) {
          if (TOP_SOCIAL_CONTAINER.equals(((Container)mo).getId())) {
            throw new AlreadyUpgradeException();
          }
        }
      }
      Application<?> clv = getModelObject(officeMiddle.getChildren(), Application.class, 0, "UICLVPortlet");
          //(Application<?>)officeMiddle.getChildren().get(0);
      Application<?> as  = getModelObject(officeMiddle.getChildren(), Application.class, 1, "UIUserActivityStreamPortlet"); 
          //(Application<?>)officeMiddle.getChildren().get(1);
      ArrayList<ModelObject> rightAppList = officeRight.getChildren();
      //----------------------------------------------------------------
        ArrayList<ModelObject> middleAppContainers = new ArrayList<ModelObject>();
        Container topSocialContainer = 
            createContainer(TOP_SOCIAL_CONTAINER, ADDON_TEMPLATE, PERMISSION, ADDON_CONTAINER);
        middleAppContainers.add(topSocialContainer);
      
        Container officeMiddleCLVContainer = 
            createContainer(OFFICE_MIDDLE_CLV_CONTAINER, CONTAINER_TEMPLATE, PERMISSION, null);
          ArrayList<ModelObject> midCLVapp = new ArrayList<ModelObject>();
          midCLVapp.add(clv);
          officeMiddleCLVContainer.setChildren(midCLVapp);
        middleAppContainers.add(officeMiddleCLVContainer);
        
        Container middleSocialContainer = 
            createContainer(MIDDLE_SOCIAL_CONTAINER, ADDON_TEMPLATE, PERMISSION, ADDON_CONTAINER);
        middleAppContainers.add(middleSocialContainer);
        
        Container officeMiddleASContainer = 
            createContainer(OFFICE_MIDDLE_AS_CONTAINER, CONTAINER_TEMPLATE, PERMISSION, null);
          ArrayList<ModelObject> midASapp = new ArrayList<ModelObject>();
          midASapp.add(as);
          officeMiddleASContainer.setChildren(midASapp);
        middleAppContainers.add(officeMiddleASContainer);
      
      officeMiddle.setChildren(middleAppContainers);
      //----------------------------------------------------------------
        ArrayList<ModelObject> rightAppContainers = new ArrayList<ModelObject>();
  
        Container topAppContainer = 
            createContainer(TOP_APPLICATION_CONTAINER, ADDON_TEMPLATE, PERMISSION, ADDON_CONTAINER);
        rightAppContainers.add(topAppContainer);
        
        Container officeRightMiddleContainer = 
            createContainer(OFFICE_RIGHT_MIDDLE_CONTAINER, CONTAINER_TEMPLATE, PERMISSION, null);
        officeRightMiddleContainer.setChildren(rightAppList);
        rightAppContainers.add(officeRightMiddleContainer);
        
        Container bottomAppContainer = 
            createContainer(BOTTOM_APPLICATION_CONTAINER, ADDON_TEMPLATE, PERMISSION, ADDON_CONTAINER);
        rightAppContainers.add(bottomAppContainer);
        
      officeRight.setChildren(rightAppContainers);
      //----------------------------------------------------------------
      
      dataStorage.save(page);
    }
    
    if (LOG.isInfoEnabled()) {
      LOG.info(TOP_SOCIAL_CONTAINER + " created!");
      LOG.info(TOP_APPLICATION_CONTAINER + " created!");
      LOG.info(BOTTOM_APPLICATION_CONTAINER + " created!");
    }
  }
  
  private Container createContainer(String id, String template, String[] permissions, String factoryId) {
    Container container = new Container();
    container.setId(id);
    container.setTemplate(template);
    container.setName(id);
    container.setAccessPermissions(permissions);
    if (factoryId != null) {
      container.setFactoryId(factoryId);
    }
    return container;
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion,previousVersion);
  }

  private class WrongModelObjectException extends Exception {
    private static final long serialVersionUID = 6546836038718678185L;
    private String objectId;
    public WrongModelObjectException(String id) {
      super();
      objectId = id;
    }
    
    public String getObjectId() {
      return objectId;
    }
  }
  
  private class AlreadyUpgradeException extends Exception {

    private static final long serialVersionUID = 2396696144814043287L;
    
  }
}
