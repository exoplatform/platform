/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */

@Application
@Portlet
@Assets(
        scripts = {@Script(src = "js/jquery.text-overflow.js"),@Script(id = "jquery", src = "js/jquery-1.3.2.min.js"),@Script( src = "js/suggestionsPeopleSpace/suggest.js")},
        stylesheets = {@Stylesheet(src = "style/suggestionsPeopleSpace/people.css"),@Stylesheet(src = "style/suggestionsPeopleSpace/space.css") })


package org.exoplatform.platform.portlet.juzu.suggestions;
import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.portlet.Portlet;