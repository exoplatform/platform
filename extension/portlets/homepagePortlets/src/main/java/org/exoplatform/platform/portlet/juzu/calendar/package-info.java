@Application
@Portlet
@Bindings({

        @Binding(value = org.exoplatform.calendar.service.CalendarService.class, implementation= GateInMetaProvider.class) ,
        @Binding(value = org.exoplatform.services.organization.OrganizationService.class, implementation= GateInMetaProvider.class)

})
@Assets(
        scripts = {
                @Script(id = "jquery",src ="js/jquery-1.7.2.min.js")
               ,@Script(src = "js/calendar/search.js",depends = "jquery")
        }  ,
        stylesheets = {
                @Stylesheet(src = "style/calendar/calendar.css")
        }
)
package org.exoplatform.platform.portlet.juzu.calendar;

import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;