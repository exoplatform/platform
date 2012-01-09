package org.exoplatform.platform.upgrade.plugins;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.chromattic.ext.ntdef.NTFolder;
import org.chromattic.ext.ntdef.Resource;
import org.exoplatform.application.gadget.EncodingDetector;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.gadget.GadgetImporter;
import org.exoplatform.application.gadget.impl.GadgetDefinition;
import org.exoplatform.application.gadget.impl.LocalGadgetData;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.gatein.common.io.IOTools;

public class LocalGadgetImporter extends GadgetImporter {

  private static Log logger = ExoLogger.getExoLogger(LocalGadgetImporter.class);
  private ConfigurationManager configurationManager;
  private PortalContainer container;
  private String gadgetPath;
  private String gadgetRootAbsolutePath;
  
  /** Used temporarily when importing resources. */
  private NTFolder folder;
  private GadgetRegistryService gadgetService;

  public LocalGadgetImporter(String name, GadgetRegistryService registryService, String gadgetPath,
      ConfigurationManager configurationManager, PortalContainer container) {
    super(name, gadgetPath);
    this.configurationManager = configurationManager;
    this.container = container;
    this.gadgetPath = gadgetPath;
    this.gadgetService = registryService;

    if (gadgetPath.startsWith("war:")) {
      gadgetPath = gadgetPath.replace("war:", "");
      this.gadgetRootAbsolutePath = container.getPortalContext().getRealPath(gadgetPath);
      this.gadgetRootAbsolutePath = this.gadgetRootAbsolutePath.replace("\\", "/");
      int gadgetRootPathIndex = this.gadgetRootAbsolutePath.lastIndexOf(gadgetPath);
      this.gadgetRootAbsolutePath = this.gadgetRootAbsolutePath.substring(0, gadgetRootPathIndex);
    }
  }

  @Override
  protected void process(String gadgetURI, GadgetDefinition def) throws Exception {
    def.setLocal(true);
    LocalGadgetData data = (LocalGadgetData)def.getData();

    //
    String fileName = getName(gadgetPath);
    data.setFileName(fileName);

    // Import resource
    folder = data.getResources();
    String folderPath = getParent(gadgetPath);
    visitChildren(gadgetPath, folderPath);
    folder = null;
  }

  @Override
  protected byte[] getGadgetBytes(String gadgetURI) throws IOException {
    return getContent(gadgetURI);
  }

  @Override
  protected String getGadgetURL() throws Exception {
    return gadgetService.getGadgetURL(getGadgetName());
  }

  private void visit(String uri, String resourcePath) throws Exception
  {
     String name = getName(resourcePath);
     if (isFile(resourcePath))
     {
        byte[] content = getContent(resourcePath);

        //
        if (content != null)
        {
           String mimeType = getMimeType(name);

           //
           if (mimeType == null)
           {
              mimeType = "application/octet-stream";
           }

           // We can detect encoding for XML files
           String encoding = null;
           if ("application/xml".equals(mimeType))
           {
              encoding = EncodingDetector.detect(new ByteArrayInputStream(content));
           }

           // Correct mime type for gadgets
           if (resourcePath.equals(uri)) {
              mimeType = LocalGadgetData.GADGET_MIME_TYPE;
           }

           //
           folder.createFile(name, new Resource(mimeType, encoding, content));
        }
     }
     else
     {
        folder = folder.createFolder(name);
        visitChildren(uri, resourcePath);
        folder = folder.getParent();
     }
  }

  private void visitChildren(String gadgetURI, String folderPath) throws Exception
  {
     for (String childPath : getChildren(folderPath))
     {
        visit(gadgetURI, childPath);
     }
  }

  private String getName(String resourcePath) {
    // It's a directory, remove the trailing '/'
    resourcePath = resourcePath.replace("\\", "/");
    if (resourcePath.endsWith("/")) {
      resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
    }

    // Get index of last '/'
    int index = resourcePath.lastIndexOf('/');

    // Return name
    return resourcePath.substring(index + 1);
  }

  private String getParent(String resourcePath) throws IOException {
    String separator = getUsedSeparator(resourcePath);
    if (resourcePath.indexOf("\\") >= 0) {// local path with an OS that
                                          // uses \\ as separator
      separator = "\\";
    } else {
      separator = "/";
    }
    // It's a directory, remove the trailing separator
    if (resourcePath.endsWith(separator)) {
      resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
    }
    // Get index of last separator
    int index = resourcePath.lastIndexOf(separator);
    // Return the parent that ends with a separator
    return resourcePath.substring(0, index + 1);
  }

  private String getRealPath(String resourcePath) throws Exception {
    if (!resourcePath.startsWith("war:") && !resourcePath.startsWith("classpath:") && !resourcePath.startsWith("jar:")) {
      File file = new File(resourcePath);
      return file.getAbsolutePath();
    } else if (resourcePath.startsWith("war:")) {
      if (!resourcePath.equals(gadgetPath)) {
        resourcePath = resourcePath.replace("war:", "");
        return gadgetRootAbsolutePath + resourcePath;
      }
      File file = new File(container.getPortalContext().getRealPath(resourcePath));
      return file.getAbsolutePath();
    } else {
      File file = new File(configurationManager.getURL(resourcePath).getPath());
      return file.getAbsolutePath();
    }
  }

  private byte[] getContent(String filePath) throws IOException {
    InputStream in;
    try {
      if (!filePath.startsWith("classpath:") && !filePath.startsWith("jar:") && !filePath.startsWith("war:")
          && !filePath.startsWith("system:")) {
        if (filePath.startsWith("")) {
          filePath = "file:" + filePath;
        } else {
          filePath = "file:/" + filePath;
        }
      }
      if (filePath.startsWith("war:")) {
        filePath = filePath.replace("war:", "");
        in = container.getPortalContext().getResourceAsStream(filePath);
      } else {
        in = configurationManager.getInputStream(filePath);
      }
    } catch (Exception exception) {
      return null;
    }
    if (in == null) {
      return null;
    } else {
      return IOTools.getBytes(in);
    }
  }

  private String getUsedSeparator(String filePath) {
    String separator = null;
    if (filePath.indexOf("\\") >= 0) {// local path with an OS that
                                      // uses \\ as separator
      separator = "\\";
    } else {
      separator = "/";
    }
    return separator;
  }

  private Iterable<String> getChildren(String folderPath) throws IOException {
    try {
      String absoluteFolderPath = getRealPath(folderPath);
      File file = new File(absoluteFolderPath);
      File[] children = file.listFiles();
      List<String> childList = new ArrayList<String>();
      if (!folderPath.endsWith("/") && !folderPath.endsWith("\\")) {
        folderPath += getUsedSeparator(folderPath);
      }
      for (File fileChild : children) {
        childList.add(folderPath + fileChild.getName());
      }
      return childList;
    } catch (Exception exception) {
        logger.error("Exception when getting Children",exception);
    }
    return null;
  }

  private boolean isFile(String resourcePath) {
    Boolean isFile = null;
    try {
      File file = new File(getRealPath(resourcePath));
      if (file.exists()) {
        isFile = file.isFile();
      }
    } catch (Exception exception) {
      // Nothing to do, the file doesn't exist or may be the path has
      // another format
    }
    if (isFile == null) {
      return !resourcePath.endsWith("/") && !resourcePath.endsWith("\\");
    } else {
      return isFile;
    }
  }

  private String getMimeType(String fileName) {
    String mimeType = URLConnection.guessContentTypeFromName(fileName);
    if (mimeType == null) {
      mimeType = container.getPortalContext().getMimeType(fileName);
    }
    return mimeType;
  }
}