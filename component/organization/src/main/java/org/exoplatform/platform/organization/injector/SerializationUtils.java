package org.exoplatform.platform.organization.injector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.ExternalComponentPlugins;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationConfig;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

public class SerializationUtils {
  private static final Log logger_ = ExoLogger.getLogger(SerializationUtils.class);

  public static final int MAX_USERS_IN_FILE_PARAM_NAME = 100;

  public static final String PROFILE_FILE_SUFFIX = "_profile.xml";

  public static final String USER_FILE_SUFFIX = "_user.xml";

  public static final String PROFILES_FOLDER_NAME = "profiles/";

  public static final String USERS_FOLDER_NAME = "users/";

  public static final String EMPTY_FIELD_REGULAR_EXPRESSION = "<field name=\"([a-z|A-Z]*)\"/>";

  public static void addEntry(ZipOutputStream zos, byte[] bytes, String entryName) throws IOException {
    zos.putNextEntry(new ZipEntry(entryName));
    zos.write(bytes);
    zos.closeEntry();
    if (logger_.isDebugEnabled()) {
      logger_.debug("Adding entry: " + entryName);
    }
  }

  public static Configuration buildOrganizationServiceConfiguration(OrganizationConfig organizationConfig) {
    Configuration configuration = new Configuration();

    ExternalComponentPlugins externalComponentPlugins = new ExternalComponentPlugins();

    ComponentPlugin componentPlugin = new ComponentPlugin();
    InitParams initParams = new InitParams();
    ObjectParameter objectParam = new ObjectParameter();
    objectParam.setName("configuration");
    objectParam.setObject(organizationConfig);

    initParams.addParameter(objectParam);

    externalComponentPlugins.setTargetComponent(DataInjectorService.class.getName());
    componentPlugin.setName("injector.Data.plugin");
    componentPlugin.setSetMethod("addDataPlugin");
    componentPlugin.setType(DataPlugin.class.getName());
    componentPlugin.setInitParams(initParams);
    ArrayList<ComponentPlugin> componentPlugins = new ArrayList<ComponentPlugin>();
    componentPlugins.add(componentPlugin);
    externalComponentPlugins.setComponentPlugins(componentPlugins);
    configuration.addExternalComponentPlugins(externalComponentPlugins);
    return configuration;
  }

  public static OrganizationConfig getOrganizationConfig(List<OrganizationConfig.Group> groups,
      List<OrganizationConfig.MembershipType> membershipTypes, List<OrganizationConfig.User> users) {
    OrganizationConfig organizationConfig = new OrganizationConfig();
    organizationConfig.setGroup(groups);
    organizationConfig.setMembershipType(membershipTypes);
    organizationConfig.setUser(users);
    return organizationConfig;
  }

  public static byte[] toXML(Object obj) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(obj, "UTF-8", null, out);
    return out.toByteArray();
  }

  public static <T> T fromXML(byte[] bytes, Class<T> clazz) throws Exception {
    ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
    IBindingFactory bfact = BindingDirectory.getFactory(clazz);
    UnmarshallingContext uctx = (UnmarshallingContext) bfact.createUnmarshallingContext();
    Object obj = uctx.unmarshalDocument(baos, "UTF-8");
    return clazz.cast(obj);
  }

}
