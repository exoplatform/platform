package org.exoplatform.platform.common.module;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;

public class ModulePlugin extends BaseComponentPlugin {

  private Module module = null;

  public ModulePlugin(InitParams initParams) {
    this.module = (Module) initParams.getObjectParam("module").getObject();
  }

  public Module getModule() {
    return this.module;
  }
}
