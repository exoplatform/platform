package org.exoplatform.platform.migration.common.constants;

public interface Constants {

  public static final String UTF_8 = "UTF-8";

  public static final String CONTAINER_FILE_PREFIX = "ExoContainer_Configuration_";

  public static final String LIFECYCLE_PLUGINS_XML_FILE_NAME = "LifecyclePlugins.xml";

  final public static String CLASS_URI_TEMPLE = "/containersConfiguration";

  final public static String GET_COMPONENT_METHOD_URI_TEMPLE = "/getComponentConfiguration/";

  final public static String GET_CONTAINERS_METHOD_URI_TEMPLE = "/ComponentsList/";

  final public static String GET_CONTAINER_CONFIGURATION_URI_TEMPLE = "/exportContainerComponents/";

  public static final String CONTAINER_ID_PARAM_NAME = "containerId";

  public static final String COMONENT_KEY_PARAM_NAME = "componentKey";

  public static final String ROOT_CONTAINER = "root";

  public static final String PROFILE_FILE_SUFFIX = "_profile.xml";

  public static final String USER_FILE_SUFFIX = "_user.xml";

  public static final String PROFILES_FOLDER_NAME = "profiles/";

  public static final String USERS_FOLDER_NAME = "users/";

  public static final String MAX_USERS_IN_FILE_PARAM_NAME = "max-users-per-file";

  public static final int DEFAULT_MAX_USERS_IN_FILE_PARAM_NAME = 100;

  public static final String RESURCE_BUNDLE_FILE_PROPERTIES = ".properties";

  public static final String APP_TYPE_GADGET = "eXoGadget";

  public static final String APP_TYPE_PORTLET = "portlet";

  public static final String PORTAL_FILE_NAME = "portal.xml";

  public static final String PAGES_FILE_NAME = "pages.xml";

  public static final String NAVIGATION_FILE_NAME = "navigation.xml";

  public static final String GADGET_FILE_NAME = "gadgets.xml";

  public static final String PORTLET_PREFERENCES_FILE_NAME = "portlet-preferences.xml";

  public static final String KERNEL_CONFIGURATION_1_1_URI = "<configuration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.exoplaform.org/xml/ns/kernel_1_1.xsd http://www.exoplaform.org/xml/ns/kernel_1_1.xsd\" xmlns=\"http://www.exoplaform.org/xml/ns/kernel_1_1.xsd\">";
}
