/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.page;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIFormTabPane;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Nov 1, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
      lifecycle = UIFormLifecycle.class,
      template =  "system:/groovy/webui/form/UIFormTabPane.gtmpl"
  ),
  @ComponentConfig(
      id = "PageTemplateOption",
      type = UIPageTemplateOptions.class,
      template = "app:/groovy/portal/webui/page/UIWizardPageSelectLayoutForm.gtmpl",
      initParams = @ParamConfig(
          name = "PageLayout",
          value = "app:/WEB-INF/conf/uiconf/portal/webui/page/PageConfigOptions.groovy"
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
