/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */

@Application
@Portlet
@Assets(
        scripts = { @Script(id= "jquery", src = "jquery-1.7.2.min.js"),@Script( src = "suggest.js")},
        stylesheets = {@Stylesheet(src = "style/suggestionsPeopleSpace/people.css"),@Stylesheet(src = "style/suggestionsPeopleSpace/space.css") })


package org.exoplatform.platform.portlet.juzu.suggestions;
import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;