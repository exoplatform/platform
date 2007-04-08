/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.ParamConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Nov 1, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
      lifecycle = UIFormLifecycle.class,
      template =  "system:/groovy/webui/component/UIFormTabPane.gtmpl"
  ),
  @ComponentConfig(
      id = "PageTemplateOption",
      type = UIPageTemplateOptions.class,
      template = "app:/groovy/portal/webui/component/customization/UIWizardPageSelectLayoutForm.gtmpl",
      initParams = @ParamConfig(
          name = "PageLayout",
          value = "app:/WEB-INF/conf/uiconf/portal/webui/component/customization/PageConfigOptions.groovy"
      )
  )  
})
public class UIWizardPageSelectLayoutForm extends UIFormTabPane {
  
  @SuppressWarnings("unchecked")
  public UIWizardPageSelectLayoutForm() throws Exception {
    super("UIWizardPageSelectLayoutForm",false);
    super.setWithRenderTab(false);
    
    UIPageTemplateOptions uiTemplateConfig = createUIComponent(UIPageTemplateOptions.class,"PageTemplateOption", null);    
    addUIComponentInput(uiTemplateConfig) ;  
  }

}
