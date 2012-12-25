/*
 * Copyright (C) 2012 eXo Platform SAS.
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


$(function() {

    $.getJSON("/rest/homepage/intranet/getting-started/get", function(items){
        var rate = 0;
        $.each(items, function(i, item){

            var id = item.name.substring(4);
            if(item.value == "true"){
               $("#"+id).addClass("done");
                rate +=20;
            }
            else {
                $("#"+id).removeClass("done");
            }
        });
        updateProgress(rate);
    });

    function updateProgress(rate){

        if (rate >= 100){
            rate = 100;
            $('.delete-action').css("display", "inline");
            $("#" + "DeleteLink").css("display", "block");
            $.getJSON("rest/homepage/intranet/getting-started/deletePortlet/setDelete");
        }
        var width= Math.round(160/100*rate);
        $("#progress-rate").attr("style", "width: "+width+"px;");
        $("#progress-label").html(rate+"%");
    };
});