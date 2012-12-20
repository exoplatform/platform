/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */

@Application
@Portlet
@Assets(
        scripts = {@Script(src = "js/whoIsOnLine/jquery.tipTip.minified.js"),@Script(id = "jquery", src = "js/jquery-1.7.2.min.js"),@Script(src = "js/whoIsOnLine/whoisonline.js")},
        stylesheets = {@Stylesheet(src = "style/whoIsOnLine/online.css"),@Stylesheet(src = "style/whoIsOnLine/tipTip.css") })


package org.exoplatform.platform.portlet.juzu.whosonline;
import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;

