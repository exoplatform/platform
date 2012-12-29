/**
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

if (!eXo.search)
  eXo.search = {};
function SearchAdminToolbar() {
}
;

$(document).ready(function () {
  var isAlt = false;
  var value = $("#adminkeyword").val();
  var isDefault = false;

  $("#adminkeyword").hide();
  $('#ToolBarSearch > a').click(function () {

    if ($("#adminkeyword").is(':hidden')) {

      if ($("#adminkeyword").val('')) {
        $("#adminkeyword").val(value);
        $("#adminkeyword").css('color', '#555');
        isDefault = true;
      }
      $("#adminkeyword").show();
    }
    else
    if (isDefault == true) {
      $("#adminkeyword").hide();
    }
    else
      eXo.search.SearchAdminToolbar.quickSearchOnClick();

  });
  $("#adminkeyword").focus(function () {
    if ($("#adminkeyword").val(value)) {
      $("#adminkeyword").val('');
      $("#adminkeyword").css('color', '#000');
      isDefault = false;
    }
  });


  $('body').click(function (evt) {
    if ($(evt.target).parents('#ToolBarSearch').length == 0) {
      $("#adminkeyword").hide();
    }
  });

  document.onkeyup = function (e) {
    if (e.which == 18) isAlt = false;
  }
  document.onkeydown = function (e) {
    if (e.which == 18) isAlt = true;
    if (isAlt == true && e.which == 32) {
      $("#adminkeyword").show();
      $("#adminkeyword").focus();
      return false;
    }
  }
});

SearchAdminToolbar.prototype.getKeynum = function (event) {
  var keynum = false;
  if (window.event) { /* IE */
    keynum = window.event.keyCode;
    event = window.event;
  } else if (event.which) { /* Netscape/Firefox/Opera */
    keynum = event.which;
  }
  if (keynum == 0) {
    keynum = event.keyCode;
  }
  return keynum;
};

SearchAdminToolbar.prototype.getHostName = function () {
  var parentLocation = window.parent.location;
  return parentLocation.href.substring(0, parentLocation.href.indexOf(parentLocation.pathname));
};

SearchAdminToolbar.prototype.quickSearchadminOnEnter = function (event, resultPageURI) {
  var keyNum = eXo.search.SearchAdminToolbar.getKeynum(event);
  if (keyNum == 13) {
    var searchBox = document.getElementById("adminkeyword");
    var keyword = encodeURI(searchBox.value);
    keyword = keyword.replace(/</g, "&lt;").replace(/>/g, "&gt;");
    keyword.replace(/[\"\'][\s]*javascript:(.*)[\"\']/gi, "\"\"");
    keyword = keyword.replace(/script(.*)/gi, "");
    keyword = keyword.replace(/eval\((.*)\)/gi, "");
    if (keyword != "") {
      var resultPageURIDefault = "searchResult";
      var params = "portal=" + eXo.env.portal.portalName + "&keyword=" + keyword;
      var baseURI = eXo.search.SearchAdminToolbar.getHostName() + eXo.env.portal.context + "/" + eXo.env.portal.portalName;
      if (resultPageURI != undefined) {
        baseURI = baseURI + "/" + resultPageURI;
      } else {
        baseURI = baseURI + "/" + resultPageURIDefault;
      }
      window.location = baseURI + "?" + params;
    }
  }
};

SearchAdminToolbar.prototype.quickSearchOnClick = function (resultPageURI) {
  var searchBox = document.getElementById("adminkeyword");
  var keyword = encodeURI(searchBox.value);
  keyword = keyword.replace(/</g, "&lt;").replace(/>/g, "&gt;");
  keyword.replace(/[\"\'][\s]*javascript:(.*)[\"\']/gi, "\"\"");
  keyword = keyword.replace(/script(.*)/gi, "");
  keyword = keyword.replace(/eval\((.*)\)/gi, "");
  if (keyword != "") {
    var resultPageURIDefault = "searchResult";
    var params = "portal=" + eXo.env.portal.portalName + "&keyword=" + keyword;
    var baseURI = eXo.search.SearchAdminToolbar.getHostName() + eXo.env.portal.context + "/" + eXo.env.portal.portalName;
    if (resultPageURI != undefined) {
      baseURI = baseURI + "/" + resultPageURI;
    } else {
      baseURI = baseURI + "/" + resultPageURIDefault;
    }
    window.location = baseURI + "?" + params;
  }
};

eXo.search.SearchAdminToolbar = new SearchAdminToolbar();
_module.SearchAdminToolbar = eXo.search.SearchAdminToolbar;