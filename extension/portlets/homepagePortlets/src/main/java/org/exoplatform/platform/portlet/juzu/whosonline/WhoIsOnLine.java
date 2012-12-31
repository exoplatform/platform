package org.exoplatform.platform.portlet.juzu.whosonline;

import juzu.Path;
import juzu.View;
import juzu.template.Template;

import javax.inject.Inject;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class WhoIsOnLine {

  @Inject
    @Path("index.gtmpl")
  Template index;

    @View
  public void index() {

      index.render();

  }
}
