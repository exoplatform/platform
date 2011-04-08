package org.exoplatform.platform.upgrade.plugins;

import java.io.Serializable;

public class GadgetUpgrade implements Serializable {
  private static final long serialVersionUID = -3348384641518707400L;

  private String name;
  private String path;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
