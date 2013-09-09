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
        $("#" + id).on("click", "i", function(event) {
            collapseExpand(this);

            event.stopPropagation();
        });


        function collapseExpand(node)
        {
            var jqNode = $(node.parentNode);

            var subGroup = jqNode.parent().children("ul.childrenContainer");
            if(subGroup.css("display") == "none")
            {
                subGroup.css("display", "block");
            }
            else
            {
                subGroup.css("display", "none");
            }
        };
    });
}