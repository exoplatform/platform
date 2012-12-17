/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */

@Application
@Portlet
@Assets(
        scripts = {@Script(src = "js/jquery.tipTip.minified.js"),@Script(id = "jquery", src = "js/jquery-1.3.2.min.js")},
        stylesheets = {@Stylesheet(src = "style/online.css"),@Stylesheet(src = "style/tipTip.css") })


package org.exoplatform.platform.portlet.juzu.whosonline;
import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;

