package org.exoplatform.platform.component.jmxclient;

import java.util.Map;

public class CommandArguments {
  private Map<String, Object> environmentMap = null;

  private String              hostname;

  private String              hostport;

  private String              beanName;

  private String              command;

  private String[]            mBeanArguments;

  public Map<String, Object> getEnvironmentMap() {
    return environmentMap;
  }

  public void setEnvironmentMap(Map<String, Object> environmentMap) {
    this.environmentMap = environmentMap;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public String getHostport() {
    return hostport;
  }

  public void setHostport(String hostport) {
    this.hostport = hostport;
  }

  public String getBeanName() {
    return beanName;
  }

  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String[] getmBeanArguments() {
    return mBeanArguments;
  }

  public void setmBeanArguments(String[] mBeanArguments) {
    this.mBeanArguments = mBeanArguments;
  }
}
