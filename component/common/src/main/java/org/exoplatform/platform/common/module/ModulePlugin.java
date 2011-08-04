package org.exoplatform.platform.common.module;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class ModulePlugin extends BaseComponentPlugin {

  private static final Log LOG = ExoLogger.getExoLogger(ModulePlugin.class);

  private Module module = null;

  public ModulePlugin(InitParams initParams) {
    Object moduleObject = initParams.get("module");
    if (moduleObject == null || !(moduleObject instanceof Module)) {
      LOG.warn("No module definition specified in an init param with name 'module', " + this.getName()
          + " ModulePlugin will be ignored!");
    } else {
      this.module = (Module) moduleObject;
    }
  }

  public Module getModule() {
    return this.module;
  }
}
