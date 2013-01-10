/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */

@Application
@Portlet
@Assets(
        scripts = {
                @Script(id = "jquery",src ="js/common/jquery-1.8.3.js",location = AssetLocation.SERVER),
                @Script( src = "js/suggest.js")},
        stylesheets = {
                @Stylesheet(src = "css/people.css"),
                @Stylesheet(src = "css/space.css")
        }
)


package org.exoplatform.platform.portlet.juzu.suggestions;
import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;