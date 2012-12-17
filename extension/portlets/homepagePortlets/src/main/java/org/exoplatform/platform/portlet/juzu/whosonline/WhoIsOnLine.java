package org.exoplatform.platform.portlet.juzu.whosonline;

import juzu.Controller;
import juzu.Path;
import juzu.View;

import javax.inject.Inject;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class WhoIsOnLine extends Controller{

  @Inject
    @Path("index.gtmpl")
    org.exoplatform.platform.portlet.juzu.whosonline.templates.index index;

    @View
  public void index() {

      index.render();

  }
}
