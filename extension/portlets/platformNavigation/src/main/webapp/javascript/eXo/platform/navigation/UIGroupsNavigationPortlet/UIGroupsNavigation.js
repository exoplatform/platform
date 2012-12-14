/**
 * Copyright ( C ) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

function initGroupNavigationPortlet(id)
{
    require(['SHARED/jquery'], function($){
        $("#" + id).on("click", "img.GroupsNavigationExpandIcon, img.GroupsNavigationCollapseIcon", function(event) {
            collapseExpand(this);

            event.stopPropagation();
        });


        function collapseExpand(node)
        {
            var jqNode = $(node);

            var subGroup = jqNode.parent().parent().children("div.ChildrenContainer");
            if(subGroup.css("display") == "none")
            {
                if(jqNode.hasClass("GroupsNavigationExpandIcon"))
                {
                    jqNode.attr("class", "GroupsNavigationCollapseIcon ClearFix");
                    jqNode.parent().children("img").attr("src","/platformNavigation/skin/platformNavigation/UIGroupsNavigationPortlet/background/icon_collapse.png");
                }
                subGroup.css("display", "block");
            }
            else
            {
                if(jqNode.hasClass("GroupsNavigationCollapseIcon"))
                {
                    jqNode.attr("class", "GroupsNavigationExpandIcon ClearFix");
                    jqNode.parent().children("img").attr("src","/platformNavigation/skin/platformNavigation/UIGroupsNavigationPortlet/background/icon_expand.gif");
                }

                subGroup.css("display", "none");
            }
        };
    });
}