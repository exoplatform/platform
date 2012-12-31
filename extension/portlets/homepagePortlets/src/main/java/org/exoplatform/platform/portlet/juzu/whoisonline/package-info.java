@Portlet
@Application(name = "WhoIsOnline")
@Assets(scripts = {
    @Script(id= "jquery", src = "jquery-1.7.2.min.js"),
    @Script(src = "whoisonline.js", depends = "jquery", location = AssetLocation.CLASSPATH) ,@Script(src = "jquery.tipTip.minified.js" ,depends = "jquery", location = AssetLocation.CLASSPATH)},
    stylesheets = {@Stylesheet(src = "online.css"),@Stylesheet(src = "tipTip.css") }
)
package org.exoplatform.platform.portlet.juzu.whoisonline;

import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;