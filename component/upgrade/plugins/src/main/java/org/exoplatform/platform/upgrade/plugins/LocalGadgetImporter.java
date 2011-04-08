package org.exoplatform.platform.upgrade.plugins;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.application.gadget.LocalImporter;
import org.exoplatform.application.gadget.impl.GadgetRegistry;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.gatein.common.io.IOTools;

public class LocalGadgetImporter extends LocalImporter {
  ConfigurationManager configurationManager;

  public LocalGadgetImporter(String name, GadgetRegistry registry, String gadgetPath, ConfigurationManager configurationManager) {
    super(name, registry, gadgetPath, true);
    this.configurationManager = configurationManager;
  }

  @Override
  public String getName(String resourcePath) throws IOException {
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

  @Override
  public String getParent(String resourcePath) throws IOException {
    try {
      File file = new File(configurationManager.getURL(resourcePath).getPath());
      return file.getParent();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  @Override
  public byte[] getContent(String filePath) throws IOException {
    InputStream in;
    try {
      if (!filePath.startsWith("classpath:") && !filePath.startsWith("jar:") && !filePath.startsWith("war:")
          && !filePath.startsWith("system:")) {
        filePath = "file:/" + filePath;
      }
      in = configurationManager.getInputStream(filePath);
    } catch (Exception exception) {
      return null;
    }
    if (in == null) {
      return null;
    } else {
      return IOTools.getBytes(in);
    }
  }

  @Override
  public Iterable<String> getChildren(String folderPath) throws IOException {
    try {
      File file = new File(folderPath);
      File[] children = file.listFiles();
      List<String> childList = new ArrayList<String>();
      for (File fileChild : children) {
        childList.add(fileChild.getAbsolutePath());
      }
      return childList;
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean isFile(String resourcePath) throws IOException {
    return !resourcePath.endsWith("/") && !resourcePath.endsWith("\\");
  }

  @Override
  public String getMimeType(String fileName) {
    return URLConnection.guessContentTypeFromName(fileName);
  }
}