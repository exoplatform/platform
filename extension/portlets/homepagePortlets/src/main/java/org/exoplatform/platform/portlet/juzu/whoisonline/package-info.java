@Portlet
@Application(name = "WhoIsOnline")
@Assets(
    scripts = {
        @Script(id = "jquery", src = "js/common/jquery-1.8.3.js", location = AssetLocation.SERVER),
        @Script(src = "js/whoisonline.js", depends = "jquery", location = AssetLocation.CLASSPATH),
        @Script(src = "js/jquery.tipTip.minified.js", depends = "jquery", location = AssetLocation.CLASSPATH)
    },
    stylesheets = {
        @Stylesheet(src = "css/online.css"),
        @Stylesheet(src = "css/tipTip.css")
    }
)
@Bindings(@Binding(value = WhoIsOnline.class, implementation = WhoIsOnlineImpl.class))
package org.exoplatform.platform.portlet.juzu.whoisonline;

import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;

