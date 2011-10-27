/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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
package org.exoplatform.platform.cloud.services.multitenancy;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.exoplatform.services.jcr.config.QueryHandlerParams;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.ValueStorageEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.impl.storage.value.fs.FileValueStorage;
import org.exoplatform.services.naming.InitialContextBinder;
import org.exoplatform.services.naming.InitialContextInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Support for JCR Value Storage in eXo Cloud. It's cop-paste of Cloud Management's TenantResourcesManager. It wasn't
 * possible to extend it due to use of private fields.
 * 
 * @author <a href="mailto:pnedonosko@exoplatform.com">Peter Nedonosko</a>
 * @version $Id: PlatformTenantResourcesManager.java 00001 2011-09-05 10:06:10Z pnedonosko $
 */
public class PlatformTenantResourcesManager {

  private static final String FILE_SEPARATOR = System.getProperty("file.separator");

  public static final String ZIP_INDEX_DIRECTORY = "jcr" + FILE_SEPARATOR + "index" + FILE_SEPARATOR;
  
  public static final String ZIP_VALUES_DIRECTORY = "jcr" + FILE_SEPARATOR + "values" + FILE_SEPARATOR;

  public static final String ZIP_LOG_DIRECTORY = "log" + FILE_SEPARATOR;

  public static final String PATH_TO_LOG_DIRECTORY = System.getProperty("tenant.logs.dir");

  public static final String SYSTEM_WORKSPACE_SUFFIX = "_system";

  public static final String PATH_TO_TENANT_DATA_DIR = System.getProperty("tenant.data.dir");

  /**
   * Tag names in bind-references.xml file
   */
  private static final String BIND_REFERENCES_FILE_NAME = "bind-references.xml";

  private static final String REFERENCE_TAG = "reference";

  private static final String BIND_NAME_TAG = "bind-name";

  private static final String CLASS_NAME_TAG = "class-name";

  private static final String FACTORY_NAME_TAG = "factory-name";

  private static final String FACTORY_LOCATION_TAG = "factory-location";

  private static final String PROPERTY_TAG = "property";

  private static final Logger LOG = LoggerFactory.getLogger(PlatformTenantResourcesManager.class);

  private static final Integer DEFAULT_BUFFER_SIZE = 4096;

  private final Map<String, String> indexDirectories;
  
  private final Map<String, String> valuesDirectories;

  private final String repositoryName;

  /**
   * serialization resources 
   */
  private FileOutputStream dest = null;

  private ZipOutputStream out = null;

  private File zipFile = null;

  public PlatformTenantResourcesManager(RepositoryEntry repositoryEntry) throws RepositoryConfigurationException
  {
     String systemWorkspaceName = repositoryEntry.getSystemWorkspaceName();
     repositoryName = repositoryEntry.getName();

     // get path to index directories 
     List<WorkspaceEntry> workspaces = repositoryEntry.getWorkspaceEntries();
     indexDirectories = new HashMap<String, String>(workspaces.size() + 1);

     for (WorkspaceEntry workspaceEntry : workspaces)
     {
        indexDirectories.put(workspaceEntry.getName(),
           workspaceEntry.getQueryHandler().getParameterValue(QueryHandlerParams.PARAM_INDEX_DIR));
        if (systemWorkspaceName.equals(workspaceEntry.getName()))
        {
           indexDirectories.put(workspaceEntry.getName() + SYSTEM_WORKSPACE_SUFFIX, workspaceEntry.getQueryHandler()
              .getParameterValue(QueryHandlerParams.PARAM_INDEX_DIR) + SYSTEM_WORKSPACE_SUFFIX);
        }
     }
     
     valuesDirectories = new HashMap<String, String>(workspaces.size());
     for (WorkspaceEntry workspaceEntry : workspaces)
     {
        for (ValueStorageEntry vsEntry : workspaceEntry.getContainer().getValueStorages()) {
          valuesDirectories.put(workspaceEntry.getName() + FILE_SEPARATOR + vsEntry.getId(), vsEntry.getParameterValue(FileValueStorage.PATH));
        }
     }
  }

  /**
   * This method initializes ZipOutputStream which will contain tenant resources
   * 
   * @throws IOException
   */
  public void initSerialization() throws IOException
  {
     zipFile = File.createTempFile("tenant." + repositoryName, ".zip");
     zipFile.deleteOnExit();

     dest = new FileOutputStream(zipFile);
     out = new ZipOutputStream(new BufferedOutputStream(dest));
  }

  /**
   * Closes Streams which were created during resources compressing
   * 
   * @return File with zipped tenant artifacts
   * @throws IOException
   */
  public File completeSerialization() throws IOException
  {
     if (out != null)
     {
        out.close();
     }
     if (dest != null)
     {
        dest.close();
     }
     return zipFile;
  }

  /**
   * Adds file with DataSource configuration of tenant in zip archive
   * 
   * @param initialContextInitializer
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws TransformerException
   */
  public void serializeDataSource(InitialContextInitializer initialContextInitializer) throws IOException,
     ParserConfigurationException, TransformerException
  {
     // add bind-references.xml file with DataSource configuration to zip
     zipStream(getBindReferencesConfig(initialContextInitializer), BIND_REFERENCES_FILE_NAME, out);
  }

  /**
   * Adds index of tenant in zip archive.
   * 
   * @throws IOException
   */
  public void serializeIndex() throws IOException
  {
     for (Entry<String, String> entry : indexDirectories.entrySet())
     {
        zipDir(entry.getValue(), ZIP_INDEX_DIRECTORY + entry.getKey() + FILE_SEPARATOR, out);
     }
  }
  
  /**
   * Add values of tenant in zip archive.
   * 
   * @throws IOException
   */
  public void serializeValues() throws IOException
  {
     for (Entry<String, String> entry : valuesDirectories.entrySet())
     {
        zipDir(entry.getValue(), ZIP_VALUES_DIRECTORY + entry.getKey() + FILE_SEPARATOR, out);
     }
  }

  /**
   * Adds log files of tenant in zip archive.
   * 
   * @throws IOException
   */
  public void serializeLogs() throws IOException
  {
     // save log files 
     zipDir(PATH_TO_LOG_DIRECTORY + FILE_SEPARATOR + repositoryName, ZIP_LOG_DIRECTORY + repositoryName
        + FILE_SEPARATOR, out);
  }

  /**
   * Restores from specified zip archive index, values, logs and DataSource of tenant.
   * 
   * @param pathToZip
   * @param initialContextInitializer
   * @throws IOException
   * @throws XMLStreamException
   * @throws NamingException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void deserialize(InputStream io, InitialContextInitializer initialContextInitializer)
     throws ParserConfigurationException, SAXException, IOException, NamingException, XMLStreamException
  {
     ZipInputStream zipInputStream = null;
     try
     {
        zipInputStream = new ZipInputStream(io);

        ZipEntry zipEntry = null;
        while ((zipEntry = zipInputStream.getNextEntry()) != null)
        {
           String entryName = zipEntry.getName();

           if (entryName.startsWith(ZIP_INDEX_DIRECTORY))
           {
              String resourceName = entryName.substring(ZIP_INDEX_DIRECTORY.length());
              if (resourceName.length() > 0)
              {
                 String workspaceName = resourceName.substring(0, resourceName.indexOf(FILE_SEPARATOR));
                 String newResourceName = resourceName.substring(workspaceName.length());

                 extractZipEntry(zipInputStream, zipEntry, indexDirectories.get(workspaceName) + newResourceName);
              }
              else
              {
                 LOG.warn("Entry {} will not unzipped", entryName);
              }
           }
           if (entryName.startsWith(ZIP_LOG_DIRECTORY))
           {
              extractZipEntry(zipInputStream, zipEntry,
                 PATH_TO_LOG_DIRECTORY + FILE_SEPARATOR + entryName.substring(ZIP_LOG_DIRECTORY.length()));
           }
           if (entryName.equals(BIND_REFERENCES_FILE_NAME))
           {
              // resume DataSource
              resumeDataSource(initialContextInitializer, copyInputStream(zipInputStream));
           }
           if (entryName.startsWith(ZIP_VALUES_DIRECTORY))
           {
              String resourceName = entryName.substring(ZIP_VALUES_DIRECTORY.length());
              if (resourceName.length() > 0)
              {
                 String vsName = resourceName.substring(0, resourceName.indexOf(FILE_SEPARATOR));
                 String newResourceName = resourceName.substring(vsName.length());

                 extractZipEntry(zipInputStream, zipEntry, valuesDirectories.get(vsName) + newResourceName);
              }
              else
              {
                 LOG.warn("Entry {} will not unzipped", entryName);
              }
           }
           zipInputStream.closeEntry();
        }
     }
     finally
     {
        if (zipInputStream != null)
        {
           zipInputStream.close();
        }
     }
  }

  /**
   * Clean index and log directories for tenant
   * 
   * @throws RepositoryConfigurationException
   */
  public void cleanTenantResources()
  {

     // remove index directories
     for (Entry<String, String> entry : indexDirectories.entrySet())
     {
        if (!removeDirectory(entry.getValue()))
        {
           LOG.warn("Index directory {} can't be completely removed on tenant {} suspend", entry.getValue(),
              repositoryName);
        }
     }
     
     // remove values directories
     for (Entry<String, String> entry : valuesDirectories.entrySet())
     {
        if (!removeDirectory(entry.getValue()))
        {
           LOG.warn("Values directory {} can't be completely removed on tenant {} suspend", entry.getValue(),
              repositoryName);
        }
     }

     // remove all data files
     // TODO: we know that all jcr resources of tenant saved in ${jcr.data.dit}/jcr/[tenantname] directory
     // it may be useful to change way detecting of this folder      

     if (!removeDirectory(PATH_TO_TENANT_DATA_DIR + FILE_SEPARATOR + "jcr" + FILE_SEPARATOR + repositoryName))
     {
        LOG.warn("Data directory can't be completely removed on tenant {} suspend", repositoryName);
     }

     // remove log files 
     if (!removeDirectory(PATH_TO_LOG_DIRECTORY + FILE_SEPARATOR + repositoryName))
     {
        LOG.warn("Log directory can't be completely removed on tenant {} suspend", repositoryName);
     }
     
     // TODO: remove DataSource
  }

  private void zipDir(String directoryToZip, String parentDir, ZipOutputStream out) throws IOException

  {
     File zipDir = new File(directoryToZip);

     if (!zipDir.exists())
     {
        return;
     }

     out.putNextEntry(new ZipEntry(parentDir));

     String[] dirList = zipDir.list();
     byte[] readBuffer = new byte[DEFAULT_BUFFER_SIZE];
     int bytesIn = 0;

     for (String element : dirList)
     {
        File file = new File(zipDir, element);
        if (file.isDirectory())
        {
           zipDir(file.getPath(), parentDir + file.getName() + FILE_SEPARATOR, out);
        }
        else
        {
           FileInputStream io = null;
           try
           {
              io = new FileInputStream(file);
              out.putNextEntry(new ZipEntry(parentDir + file.getName()));

              // write the content of the file to the ZipOutputStream 
              while ((bytesIn = io.read(readBuffer)) != -1)
              {
                 out.write(readBuffer, 0, bytesIn);
              }
           }
           catch (FileNotFoundException e)
           {
              LOG.warn("File not found when tenant resources zipped: {}", e.getMessage());
           }
           finally
           {
              if (io != null)
              {
                 io.close();
              }
           }
        }
     }
  }

  private void zipStream(InputStream streamToZip, String fileName, ZipOutputStream out) throws IOException
  {
     try
     {
        byte[] readBuffer = new byte[DEFAULT_BUFFER_SIZE];
        int bytesIn = 0;

        ZipEntry zipEntry = new ZipEntry(fileName);
        out.putNextEntry(zipEntry);

        // write the content of the file to the ZipOutputStream 
        while ((bytesIn = streamToZip.read(readBuffer)) != -1)
        {
           out.write(readBuffer, 0, bytesIn);
        }
     }
     finally
     {
        if (streamToZip != null)
        {
           streamToZip.close();
        }
     }
  }

  private void extractZipEntry(ZipInputStream zipInputStream, ZipEntry zipEntry, String extractPath)
     throws IOException
  {
     if (zipEntry.isDirectory())
     {
        File subDir = new File(extractPath);
        subDir.mkdirs();
     }
     else
     {
        // creare file
        FileOutputStream out = null;
        try
        {
           byte data[] = new byte[DEFAULT_BUFFER_SIZE];
           int count = 0;

           File file = new File(extractPath);
           file.getParentFile().mkdirs();
           file.createNewFile();

           out = new FileOutputStream(file);
           while ((count = zipInputStream.read(data, 0, DEFAULT_BUFFER_SIZE)) != -1)
           {
              out.write(data, 0, count);
           }
        }

        finally
        {
           if (out != null)
           {
              out.close();
           }
        }
     }
  }

  /**
   * Remove directory and all its sub-resources with specified path
   * 
   * @param pathToDir
   * @return
   */
  private boolean removeDirectory(String pathToDir)
  {
     File directory = new File(pathToDir);

     if (!directory.exists())
     {
        return true;
     }
     if (!directory.isDirectory())
     {
        return false;
     }

     String[] list = directory.list();

     if (list != null)
     {
        for (String element : list)
        {
           File entry = new File(directory, element);

           if (entry.isDirectory())
           {
              if (!removeDirectory(entry.getPath()))
              {
                 return false;
              }
           }
           else
           {
              if (!entry.delete())
              {
                 return false;
              }
           }
        }
     }

     return directory.delete();
  }

  private InputStream getBindReferencesConfig(InitialContextInitializer initialContextInitializer)
     throws ParserConfigurationException, TransformerException
  {
     InitialContextBinder initialContextBinder = initialContextInitializer.getInitialContextBinder();

     if (initialContextBinder == null)
     {
        return new ByteArrayInputStream(new byte[0]);
     }

     Reference bindReference = initialContextBinder.getReference(repositoryName);

     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     DocumentBuilder builder = factory.newDocumentBuilder();
     Document doc = builder.newDocument();

     Element referenceElem = doc.createElement(REFERENCE_TAG);
     referenceElem.setAttribute(BIND_NAME_TAG, repositoryName);
     referenceElem.setAttribute(CLASS_NAME_TAG, bindReference.getClassName());
     referenceElem.setAttribute(FACTORY_NAME_TAG, bindReference.getFactoryClassName());
     referenceElem.setAttribute(FACTORY_LOCATION_TAG, bindReference.getFactoryClassLocation());

     doc.appendChild(referenceElem);

     Enumeration<RefAddr> referenceProperties = bindReference.getAll();

     while (referenceProperties.hasMoreElements())
     {
        RefAddr property = referenceProperties.nextElement();
        Element propertyTag = doc.createElement(PROPERTY_TAG);
        propertyTag.setAttribute(property.getType(), (String)property.getContent());

        referenceElem.appendChild(propertyTag);
     }

     TransformerFactory transfac = TransformerFactory.newInstance();
     Transformer trans = transfac.newTransformer();

     //create string from xml tree
     StringWriter sw = new StringWriter();
     StreamResult result = new StreamResult(sw);
     DOMSource source = new DOMSource(doc);
     trans.transform(source, result);

     return new ByteArrayInputStream(sw.toString().getBytes());
  }

  public void resumeDataSource(InitialContextInitializer initialContextInitializer, ByteArrayInputStream io)
     throws ParserConfigurationException, SAXException, IOException, NamingException, XMLStreamException
  {

     LOG.info("resumeDataSource {}", io.available());
     if (io.available() == 0)
     {
        return;
     }

     try
     {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(io);

        NodeList referenceNodes = doc.getElementsByTagName(REFERENCE_TAG);
        LOG.info("referenceNodes {}", referenceNodes.getLength());
        if (referenceNodes.getLength() == 0)
        {
           return;
        }

        Element referenceElem = (Element)referenceNodes.item(0);
        String bindName = referenceElem.getAttribute(BIND_NAME_TAG);
        String className = referenceElem.getAttribute(CLASS_NAME_TAG);
        String factoryName = referenceElem.getAttribute(FACTORY_NAME_TAG);
        String factoryLocation = referenceElem.getAttribute(FACTORY_LOCATION_TAG);
        LOG.info("bindName {} className{} factoryName {} factoryLocation {}", new String[]{bindName, className,
           factoryName, factoryLocation});
        Map<String, String> refAddr = new HashMap<String, String>();

        NodeList properties = referenceElem.getChildNodes();
        //old style binding
        if (properties.getLength() == 1)
        {
           Node element = properties.item(0);
           if (element.getNodeName().equals("ref-addr"))
           {
              properties = element.getChildNodes();
           }
        }

        for (int i = 0; i < properties.getLength(); i++)
        {
           Node element = properties.item(i);
           if (element.getNodeName().equals(PROPERTY_TAG))
           {
              NamedNodeMap attributes = element.getAttributes();
              if (attributes.getLength() > 0)
              {
                 refAddr.put(attributes.item(0).getNodeName(), attributes.item(0).getNodeValue());
              }
           }
        }
        LOG.info("refAddr {}", refAddr);
        InitialContextBinder initialContextBinder = initialContextInitializer.getInitialContextBinder();
        initialContextBinder.bind(bindName, className, factoryName, factoryLocation, refAddr);
        LOG.info("bind ok", refAddr);
     }
     finally
     {
        if (io != null)
        {
           io.close();
        }
     }

  }

  private ByteArrayInputStream copyInputStream(InputStream io) throws IOException
  {
     byte data[] = new byte[DEFAULT_BUFFER_SIZE];
     int count = 0;

     ByteArrayOutputStream out = new ByteArrayOutputStream();
     try
     {
        while ((count = io.read(data, 0, DEFAULT_BUFFER_SIZE)) != -1)
        {
           out.write(data, 0, count);
        }

        return new ByteArrayInputStream(out.toByteArray());
     }
     finally
     {
        out.close();
     }
  }

}
