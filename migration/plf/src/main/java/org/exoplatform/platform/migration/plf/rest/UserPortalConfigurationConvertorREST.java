package org.exoplatform.platform.migration.plf.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.exoplatform.platform.migration.common.aio.object.Application;
import org.exoplatform.platform.migration.common.aio.object.Container;
import org.exoplatform.platform.migration.common.aio.object.Gadgets;
import org.exoplatform.platform.migration.common.aio.object.Page;
import org.exoplatform.platform.migration.common.aio.object.PageBody;
import org.exoplatform.platform.migration.common.aio.object.PageNavigation;
import org.exoplatform.platform.migration.common.aio.object.PageNode;
import org.exoplatform.platform.migration.common.aio.object.PortalConfig;
import org.exoplatform.platform.migration.common.aio.object.PortletPreferences;
import org.exoplatform.platform.migration.common.aio.object.Page.PageSet;
import org.exoplatform.platform.migration.common.aio.object.PortletPreferences.PortletPreferencesSet;
import org.exoplatform.platform.migration.plf.object.Portlet;
import org.exoplatform.platform.migration.plf.object.Preference;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.PortalProperties;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

@Path("/userPortalConfigurationConvertor")
public class UserPortalConfigurationConvertorREST implements ResourceContainer {

  final private static String PORTAL_FILE_NAME = "portal.xml";

  final private static String PAGES_FILE_NAME = "pages.xml";

  final private static String NAVIGATION_FILE_NAME = "navigation.xml";

  final private static String GADGET_FILE_NAME = "gadgets.xml";

  final private static String PORTLET_PREFERENCES_FILE_NAME = "portlet-preferences.xml";

  final private static Map<String, Class<?>> unmarshelledObjectTypes = new HashMap<String, Class<?>>();

  static {
    unmarshelledObjectTypes.put(PORTAL_FILE_NAME, PortalConfig.class);
    unmarshelledObjectTypes.put(PAGES_FILE_NAME, PageSet.class);
    unmarshelledObjectTypes.put(NAVIGATION_FILE_NAME, PageNavigation.class);
    unmarshelledObjectTypes.put(GADGET_FILE_NAME, Gadgets.class);
    unmarshelledObjectTypes.put(PORTLET_PREFERENCES_FILE_NAME, PortletPreferencesSet.class);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response importProfiles() throws Exception {
    StringBuffer responseStringBuffer = new StringBuffer();
    responseStringBuffer.append("<html><body><form action='/portal/rest/userPortalConfigurationConvertor/convert/' enctype='application/x-www-form-urlencoded' method='POST'>");
    responseStringBuffer.append("  <input type='text' name='filePath'/>");
    responseStringBuffer.append("  <input type='submit'/>");
    responseStringBuffer.append("</form></body></html>");
    return Response.ok().entity(responseStringBuffer.toString()).build();
  }

  @POST
  @Path("/convert/")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response convert(@FormParam("filePath") String filePath) {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    try {
      // Map of user, portal and group navigation objects
      Map<String, Map<String, Map<String, Object>>> unmarshelledUserPortalConfigurations = new HashMap<String, Map<String, Map<String, Object>>>(3);

      FileInputStream fin = new FileInputStream(filePath);
      ZipInputStream zin = new ZipInputStream(fin);
      ZipEntry ze = null;
      while ((ze = zin.getNextEntry()) != null) {
        String[] entries = ze.getName().split("/");
        String ownerType = entries[0];
        String entryFileName = entries[entries.length - 1];
        Class<?> fileTypeToObjectClassType = unmarshelledObjectTypes.get(entryFileName);
        if (fileTypeToObjectClassType == null) {
          continue;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 1; i < entries.length - 1; i++) {
          if (i != 1) {
            stringBuffer.append("/");
          }
          stringBuffer.append(entries[i]);
        }
        String ownerId = stringBuffer.toString();

        Object unmarshelledObject = fromXML(readEntry(zin), fileTypeToObjectClassType);
        addUserPortalConfiguration(unmarshelledObject, unmarshelledUserPortalConfigurations, ownerType, ownerId, entryFileName);
      }
      zin.close();

      ZipOutputStream zos = new ZipOutputStream(result);

      for (Map.Entry<String, Map<String, Map<String, Object>>> portalConfigEntry : unmarshelledUserPortalConfigurations.entrySet()) {
        String ownerType = portalConfigEntry.getKey();
        Map<String, Map<String, Object>> ownersConfigurationObjects = portalConfigEntry.getValue();
        for (Map.Entry<String, Map<String, Object>> ownerEntry : ownersConfigurationObjects.entrySet()) {
          String ownerId = ownerEntry.getKey();
          String portalConfigForlder = ownerType + "/" + ownerId + "/";
          Map<String, Object> ownerObjects = ownerEntry.getValue();

          // pages.xml & portlet-preferences.xml conversion
          PageSet pageSet = (PageSet) ownerObjects.get(PAGES_FILE_NAME);
          PortletPreferencesSet portletPreferencesSet = (PortletPreferencesSet) ownerObjects.get(PORTLET_PREFERENCES_FILE_NAME);
          org.exoplatform.platform.migration.plf.object.Page.PageSet convertedPageSet = convertPageSet(pageSet, portletPreferencesSet);
          putEntry(zos, portalConfigForlder + PAGES_FILE_NAME, convertedPageSet);

          // portal.xml conversion
          if (PortalConfig.PORTAL_TYPE.equals(ownerType)) {
            PortalConfig portalConfig = (PortalConfig) ownerObjects.get(PORTAL_FILE_NAME);
            org.exoplatform.platform.migration.plf.object.PortalConfig convertedPortalConfig = convertPortalConfig(portalConfig, portletPreferencesSet);
            putEntry(zos, portalConfigForlder + PORTAL_FILE_NAME, convertedPortalConfig);
          }
          {
            PageNavigation pageNavigation = (PageNavigation) ownerObjects.get(NAVIGATION_FILE_NAME);
            org.exoplatform.portal.config.model.PageNavigation convertedPageNavigation = convertNavigation(pageNavigation);
            putEntry(zos, portalConfigForlder + NAVIGATION_FILE_NAME, convertedPageNavigation);
          }
          // {
          // Gadgets gadgets = (Gadgets) ownerObjects.get(GADGET_FILE_NAME);
          // if(gadgets !=null) {
          // Gadgets convertedGadgets = convertGadgets(gadgets);
          // putEntry(zos, portalConfigForlder + NAVIGATION_FILE_NAME, convertedPageNavigation);
          // }
          // }
        }
      }
      zos.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return Response.ok().header("Content-disposition", "attachment; filename=ConvertedUserPortalConfigurationServiceFiles.zip").entity(new ByteArrayInputStream(result.toByteArray())).build();
  }

  private org.exoplatform.portal.config.model.PageNavigation convertNavigation(PageNavigation pageNavigation) {
    org.exoplatform.portal.config.model.PageNavigation convertedPageNavigation = new org.exoplatform.portal.config.model.PageNavigation();
    convertedPageNavigation.setModifiable(true);
    convertedPageNavigation.setOwnerId(pageNavigation.getOwnerId());
    convertedPageNavigation.setOwnerType(pageNavigation.getOwnerType());
    convertedPageNavigation.setPriority(pageNavigation.getPriority());

    ArrayList<org.exoplatform.portal.config.model.PageNode> convertedPageNodes = convertPageNodes(pageNavigation.getNodes());
    convertedPageNavigation.setNodes(convertedPageNodes);
    return convertedPageNavigation;
  }

  private ArrayList<org.exoplatform.portal.config.model.PageNode> convertPageNodes(List<PageNode> pageNodes) {
    if (pageNodes == null || pageNodes.size() == 0) {
      return null;
    }
    ArrayList<org.exoplatform.portal.config.model.PageNode> convertedPageNodes = new ArrayList<org.exoplatform.portal.config.model.PageNode>();
    for (PageNode pageNode : pageNodes) {
      org.exoplatform.portal.config.model.PageNode convertedPageNode = new org.exoplatform.portal.config.model.PageNode();
      convertedPageNode.setChildren(convertPageNodes(pageNode.getChildren()));
      convertedPageNode.setModifiable(true);
      convertedPageNode.setEndPublicationDate(pageNode.getEndPublicationDate());
      convertedPageNode.setStartPublicationDate(pageNode.getStartPublicationDate());
      convertedPageNode.setIcon(pageNode.getIcon());
      convertedPageNode.setLabel(pageNode.getLabel());
      convertedPageNode.setName(pageNode.getName());
      convertedPageNode.setPageReference(pageNode.getPageReference());
      convertedPageNode.setUri(pageNode.getUri());
      convertedPageNode.setVisibility(pageNode.getVisible() ? Visibility.DISPLAYED : Visibility.HIDDEN);
      convertedPageNodes.add(convertedPageNode);
    }
    return convertedPageNodes;
  }

  private org.exoplatform.platform.migration.plf.object.Page.PageSet convertPageSet(PageSet pageSet, PortletPreferencesSet portletPreferencesSet) {
    org.exoplatform.platform.migration.plf.object.Page.PageSet convertedPageSet = new org.exoplatform.platform.migration.plf.object.Page.PageSet();
    ArrayList<org.exoplatform.platform.migration.plf.object.Page> convertedPages = new ArrayList<org.exoplatform.platform.migration.plf.object.Page>();
    ArrayList<Page> pages = pageSet.getPages();
    for (Page page : pages) {
      org.exoplatform.platform.migration.plf.object.Page convertedPage = new org.exoplatform.platform.migration.plf.object.Page();
      convertedPage.setModifiable(true);
      convertedPage.setAccessPermissions(page.getAccessPermissions());
      convertedPage.setDecorator(page.getDecorator());
      convertedPage.setDescription(page.getDescription());
      convertedPage.setEditPermission(page.getEditPermission());
      convertedPage.setFactoryId(page.getFactoryId());
      convertedPage.setHeight(page.getHeight());
      convertedPage.setIcon(page.getIcon());
      convertedPage.setId(page.getId());
      convertedPage.setName(page.getName());
      convertedPage.setShowMaxWindow(page.isShowMaxWindow());
      convertedPage.setTitle(page.getTitle());
      convertedPage.setTemplate(page.getTemplate());
      convertedPage.setWidth(page.getWidth());
      convertedPages.add(convertedPage);

      ArrayList<Object> children = page.getChildren();
      convertedPage.setChildren(convertUIComponents(children, portletPreferencesSet, page.getAccessPermissions()));
    }
    convertedPageSet.setPages(convertedPages);
    return convertedPageSet;
  }

  private ArrayList<ModelObject> convertUIComponents(List<?> children, PortletPreferencesSet portletPreferencesSet, String[] accessPermissions) {
    if (children == null || children.size() == 0) {
      return null;
    }
    ArrayList<ModelObject> convertedPageChildren = new ArrayList<ModelObject>();
    for (Object child : children) {
      if (child instanceof Container) {
        Container container = (Container) child;
        org.exoplatform.platform.migration.plf.object.Container convertedContainer = new org.exoplatform.platform.migration.plf.object.Container();
        convertedContainer.setAccessPermissions(accessPermissions);
        convertedContainer.setChildren(convertUIComponents(container.getChildren(), portletPreferencesSet, accessPermissions));
        convertedContainer.setDecorator(container.getDecorator());
        convertedContainer.setDescription(container.getDescription());
        convertedContainer.setFactoryId(container.getFactoryId());
        convertedContainer.setHeight(container.getHeight());
        convertedContainer.setIcon(container.getHeight());
        convertedContainer.setId(container.getId());
        convertedContainer.setName(container.getName());
        convertedContainer.setTemplate(container.getTemplate());
        convertedContainer.setTitle(container.getTitle());
        convertedContainer.setWidth(container.getWidth());

        convertedPageChildren.add(convertedContainer);
      } else if (child instanceof Application) {
        Application application = (Application) child;
        org.exoplatform.platform.migration.plf.object.Application convertedApplication = new org.exoplatform.platform.migration.plf.object.Application();
        convertedApplication.setModifiable(true);
        convertedApplication.setAccessPermissions(accessPermissions);
        convertedApplication.setDescription(application.getDescription());
        convertedApplication.setHeight(application.getHeight());
        convertedApplication.setIcon(application.getIcon());
        convertedApplication.setId(application.getId());
        if(application.getProperties() != null && application.getProperties().size() > 0) {
          convertedApplication.setProperties(application.getProperties());
        }
        convertedApplication.setShowApplicationMode(application.isShowApplicationMode());
        convertedApplication.setShowApplicationState(application.isShowApplicationState());
        convertedApplication.setShowInfoBar(application.isShowInfoBar());
        convertedApplication.setTheme(application.getTheme());
        convertedApplication.setTitle(application.getTitle());
        convertedApplication.setWidth(application.getWidth());

        if (application.getInstanceId() == null || application.getInstanceId().length() == 0) {
          continue;
        }
        org.exoplatform.platform.migration.plf.object.PortletPreferences portletPreferences = getPortletPreferences(portletPreferencesSet, application.getInstanceId());
        String[] intanceNames = application.getInstanceId().split(":")[1].split("/");
        Portlet portlet = new Portlet(intanceNames[1], intanceNames[2]);
        portlet.setPreferences(portletPreferences);
        convertedApplication.setPortlet(portlet);
        convertedPageChildren.add(convertedApplication);
      } else if (child instanceof PageBody) {
        org.exoplatform.portal.config.model.PageBody pageBody = new org.exoplatform.portal.config.model.PageBody();
        convertedPageChildren.add(pageBody);
      }
    }
    return convertedPageChildren;
  }

  private org.exoplatform.platform.migration.plf.object.PortletPreferences getPortletPreferences(PortletPreferencesSet portletPreferencesSet, String instanceId) {
    for (PortletPreferences portlet : portletPreferencesSet.getPortlets()) {
      if (portlet.getWindowId().equals(instanceId)) {
        org.exoplatform.platform.migration.plf.object.PortletPreferences convertedPortletPreferences = new org.exoplatform.platform.migration.plf.object.PortletPreferences();
        for (Object object : portlet.getPreferences()) {
          org.exoplatform.platform.migration.common.aio.object.Preference preference = (org.exoplatform.platform.migration.common.aio.object.Preference) object;
          Preference convertedPreference = new Preference();
          convertedPreference.setName(preference.getName());
          convertedPreference.setValues(preference.getValues());
          convertedPreference.setReadOnly(preference.isReadOnly());
          convertedPortletPreferences.setPreference(convertedPreference);
        }
        return convertedPortletPreferences;
      }
    }
    return null;
  }

  private org.exoplatform.platform.migration.plf.object.PortalConfig convertPortalConfig(PortalConfig portalConfig, PortletPreferencesSet portletPreferencesSet) {
    org.exoplatform.platform.migration.plf.object.PortalConfig convertedPortalConfig = new org.exoplatform.platform.migration.plf.object.PortalConfig();
    convertedPortalConfig.setAccessPermissions(portalConfig.getAccessPermissions());
    convertedPortalConfig.setEditPermission(portalConfig.getEditPermission());
    convertedPortalConfig.setLocale(portalConfig.getLocale());
    convertedPortalConfig.setModifiable(true);
    convertedPortalConfig.setName(portalConfig.getName());
    convertedPortalConfig.setSessionAlive(PortalProperties.SESSION_ON_DEMAND);
    convertedPortalConfig.setSkin(portalConfig.getSkin());
    convertedPortalConfig.setType(PortalConfig.PORTAL_TYPE);

    // Convert Portal Layout
    Container portalLayoutContainer = portalConfig.getPortalLayout();
    List<?> uiCompomponents = Collections.singletonList(portalLayoutContainer);
    ArrayList<ModelObject> convertedPortalLayout = convertUIComponents(uiCompomponents, portletPreferencesSet, portalConfig.getAccessPermissions());
    assert convertedPortalLayout != null && convertedPortalLayout.size() == 1;
    convertedPortalConfig.setPortalLayout((org.exoplatform.platform.migration.plf.object.Container) convertedPortalLayout.get(0));

    return convertedPortalConfig;
  }

  private void putEntry(ZipOutputStream zos, String filePath, Object objectToMarshall) throws Exception {
    zos.putNextEntry(new ZipEntry(filePath));
    byte[] bytes = toXML(objectToMarshall);
    zos.write(bytes);
    zos.closeEntry();
  }

  private void addUserPortalConfiguration(Object unmarshelledObject, Map<String, Map<String, Map<String, Object>>> userPortalConfigurations, String ownerType, String ownerId, String fileNameType) {
    Map<String, Object> portalConfigObjects = null;
    if (userPortalConfigurations.get(ownerType) == null) {
      Map<String, Map<String, Object>> portalsConfig = new HashMap<String, Map<String, Object>>();
      userPortalConfigurations.put(ownerType, portalsConfig);
      portalConfigObjects = new HashMap<String, Object>();
      portalsConfig.put(ownerId, portalConfigObjects);
    } else {
      Map<String, Map<String, Object>> portalsConfig = userPortalConfigurations.get(ownerType);
      portalConfigObjects = portalsConfig.get(ownerId);
      if (portalConfigObjects == null) {
        portalConfigObjects = new HashMap<String, Object>();
        portalsConfig.put(ownerId, portalConfigObjects);
      }
    }
    portalConfigObjects.put(fileNameType, unmarshelledObject);
  }

  private byte[] readEntry(ZipInputStream zin) throws IOException {
    ByteArrayOutputStream fout = new ByteArrayOutputStream();
    for (int c = zin.read(); c != -1; c = zin.read()) {
      fout.write(c);
    }
    zin.closeEntry();
    return fout.toByteArray();
  }

  protected byte[] toXML(Object obj) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      mctx.marshalDocument(obj, "UTF-8", null, out);
      return out.toByteArray();
    } catch (Exception ie) {
      throw ie;
    }
  }

  private <T> T fromXML(byte[] bytes, Class<T> clazz) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    IBindingFactory bfact = BindingDirectory.getFactory(clazz);
    UnmarshallingContext uctx = (UnmarshallingContext) bfact.createUnmarshallingContext();
    uctx.setDocument(is, null, "UTF-8", false);
    return clazz.cast(uctx.unmarshalElement());
  }

}