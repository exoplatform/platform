package org.exoplatform.bonitasoft.services.utils;

public class Constants {

  public static final int MAX_HOST_CONNECTIONS = 20;
  public static final String REST_USER = "restuser";
  public static final String REST_PASS = "restbpm";
  public static final int PORT = Integer.parseInt(System.getProperty("org.exoplatform.runtime.conf.gatein.port", "8080"));
  public static final String HOST = System.getProperty("org.exoplatform.runtime.conf.gatein.host", "localhost");

}
