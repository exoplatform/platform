@Application
@Portlet
@Assets(
        scripts = {
                @Script(id = "jquery",src ="js/jquery-1.8.3.js")
               ,@Script(src = "js/calendar/search.js",depends = "jquery")
        }  ,
        stylesheets = {
                @Stylesheet(src = "style/calendar/calendar.css")
        }      ,
        location = juzu.asset.AssetLocation.SERVER
)
package org.exoplatform.platform.portlet.juzu.calendar;

import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;