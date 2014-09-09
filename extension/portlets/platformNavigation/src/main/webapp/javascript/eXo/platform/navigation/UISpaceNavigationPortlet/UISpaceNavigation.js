(function($) {
    var UISpaceNavigation = {
        init: function(uicomponentId, mySpaceRestUrl, defaultValueForTextSearch,noSpace, selectSpaceAction) {
            var me = this;
            me.mySpaceRestUrl = mySpaceRestUrl;
            me.lastSearchKeyword = "";
            me.defaultValueForTextSearch = defaultValueForTextSearch;
            me.selectSpaceAction = selectSpaceAction;
            me.noSpace=noSpace;
            var me = this;
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
                } else{
                    textField.select();
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


        },
        requestData: function(keyword, uicomponentId) {
            var me = this;

            $.ajax({
                async : false,
                url : me.mySpaceRestUrl + "?keyword=" + keyword,
                type : 'GET',
                data : '',
                success : function(data) {
                    me.render(data, uicomponentId);
                }
            });
        },
        render: function(dataList, uicomponentId) {
            var me = this;
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
                    imageUrl = "/eXoSkin/skin/images/social/skin/ShareImages/SpaceAvtDefault.png";
                }
                var spaceDiv = "<li class='spaceItem'>"+"<a class='spaceIcon avatarMini'"
                        + "' href='" + spaceUrl + "' title='" + name + "'><img src='"+imageUrl+"'/>"
                        + name + "</a></li>";
                groupSpaces += spaceDiv;
            }
            if(groupSpaces!=''){
                spacesListREsult.innerHTML = groupSpaces;
            }else{
                spacesListREsult.innerHTML= "<li class='noSpace'>" + me.noSpace + "</li>" ;
            }
        },
        onTextSearchChange: function(uicomponentId) {
            var me = this;
            var navigationSpaceSearch = document.getElementById(uicomponentId);
            var textSearch = $(navigationSpaceSearch).find("input.searchText")[0].value;

            if (textSearch != me.lastSearchKeyword) {
                me.lastSearchKeyword = textSearch;
                me.requestData(textSearch, uicomponentId);
                $(".moreSpace").hide();

            }
        },
        ajaxRedirect: function (url) {
            if(self == top) {            
                window.parent.location.href = url;
            } else {
                //Iframe case
                window.location.href = url;
            }
        }
    };

    return UISpaceNavigation;
})($);
