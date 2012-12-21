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
if (!eXo.navigation)
    eXo.navigation = {};
function UISpaceNavigation() {
};
UISpaceNavigation.prototype.init = function(uicomponentId, mySpaceRestUrl, defaultValueForTextSearch, selectSpaceAction) {
    var me = eXo.navigation.UISpaceNavigation;
    me.mySpaceRestUrl = mySpaceRestUrl;
    me.lastSearchKeyword = "";
    me.defaultValueForTextSearch = defaultValueForTextSearch;
    me.selectSpaceAction = selectSpaceAction;

    var me = eXo.navigation.UISpaceNavigation;
    var navigationSpaceSearch = document.getElementById(uicomponentId);
    var textField = $(navigationSpaceSearch).find("input.searchText")[0];
    textField.value = defaultValueForTextSearch;

    textField.onkeydown = function() {
        me.onTextSearchChange(uicomponentId);
    };

    textField.onkeypress = function() {
        me.onTextSearchChange(uicomponentId);
    };

    textField.onkeyup = function() {
        me.onTextSearchChange(uicomponentId);
    };

    textField.onfocus = function() {
        if (textField.value == me.defaultValueForTextSearch) {
            textField.value = "";
        }
        textField.className="searchText Focus"
    };

    textField.onclick = function(event) {

        if (event.stopPropagation){
            event.stopPropagation();
        } else if(window.event){
            window.event.cancelBubble=true;
        }
    };

    // When textField lost focus
    textField.onblur = function() {
        if (textField.value == "") {
            textField.value = me.defaultValueForTextSearch;
            textField.className="searchText LostFocus";
        }
    };

};
UISpaceNavigation.prototype.requestData = function(keyword, uicomponentId) {
    var me = eXo.navigation.UISpaceNavigation;

    $.ajax({
        async : false,
        url : me.mySpaceRestUrl + "?keyword=" + keyword,
        type : 'GET',
        data : '',
        success : function(data) {
            me.render(data, uicomponentId);
        }
    });

};
UISpaceNavigation.prototype.render = function(dataList, uicomponentId) {
    var me = eXo.navigation.UISpaceNavigation;
    me.dataList = dataList;

    var navigationSpaceSearch = document.getElementById(uicomponentId);
    var spacesListREsult = $(navigationSpaceSearch).find('ul.spaceNavigation')[0];
    //var spaces = dataList.jsonList;
    var spaces = dataList;
    var groupSpaces = '';
    for (i = 0; i < spaces.length; i++) {
        var spaceId = spaces[i].id;
        var spaceUrl = window.location.protocol + "//" + window.location.host + "/" + spaces[i].url;
        var name = spaces[i].displayName;
        var imageUrl=spaces[i].avatarUrl;
        if (imageUrl == null) {
            imageUrl = "/social-resources/skin/ShareImages/SpaceImages/SpaceLogoDefault_61x61.gif";
        }
        var spaceDiv = "<li class='spaceItem'>"+"<a class='spaceIcon'"
            + "' href='" + spaceUrl + "'><img src='"+imageUrl+"'/>"
            + name + "</a></li><br/>";
        groupSpaces += spaceDiv;
    }
    if(groupSpaces!=''){
        spacesListREsult.innerHTML = groupSpaces;
    }else{
        spacesListREsult.innerHTML='No spaces'
    }

};
UISpaceNavigation.prototype.onTextSearchChange = function(uicomponentId) {
    var me = eXo.navigation.UISpaceNavigation;
    var navigationSpaceSearch = document.getElementById(uicomponentId);
    var textSearch = $(navigationSpaceSearch).find("input.searchText")[0].value;

    if (textSearch != me.lastSearchKeyword) {
        me.lastSearchKeyword = textSearch;
        me.requestData(textSearch, uicomponentId);

    }
};

eXo.navigation.UISpaceNavigation = new UISpaceNavigation();
_module.UISpaceNavigation = eXo.navigation.UISpaceNavigation;
