(function ($) {

    function sortByContacts(a, b){
        return b.number - a.number;
    } ;

    function dynamicSort(property) {
        var sortOrder = 1;
        if(property[0] === "-") {
            sortOrder = -1;
            property = property.substr(1, property.length - 1);
        }
        return function (a,b) {
            var result = (a[property].toLowerCase() < b[property].toLowerCase()) ? -1 : (a[property].toLowerCase() > b[property].toLowerCase()) ? 1 : 0;
            return result ;
        }
    } ;

    function sortByCreatedDate(a, b){
        return b.createdDate - a.createdDate;
    } ;

    Array.prototype.shuffle = function() {
        var len = this.length;
        var i = len;
        while (i--) {
            var p = parseInt(Math.random()*len);
            var t = this[i];
            this[i] = this[p];
            this[p] = t;
        }
    };


    var member;
    var connect ;
    var connection;
    var privateLabel;
    var publicLabel;
    var spaceMember;
    var requestLabel;
    var joinLabel;

    $(".bundle").each(function() {
        member = $(this).data("member");
        connect = $(this).data("connect");
        connection = $(this).data("connection");
        privateLabel = $(this).data("private");
        publicLabel=  $(this).data("public");
        spaceMember=  $(this).data("spacemember");
        joinLabel=  $(this).data("joinlabel");
        requestLabel=  $(this).data("requestlabel");
    });

    return {
        initSuggestion: function() {
            $.getJSON("/rest/homepage/intranet/people/contacts/suggestions", function(list){

                if (list.items.length > 0){
                    $("#content").show();
                    $("#peopleSuggest").show();

                }
                var newUser=true;
                for(var k= 0; k < list.items.length; k++)
                {
                    if(list.items[k].number!=0){
                        newUser=false;
                    }
                }

                if(newUser==true || list.noConnections==0){
                    list.items.sort(sortByCreatedDate) ;
                }else{
                    list.items.sort(dynamicSort("suggestionName"));
                    // sort my most contacts instead of random
                    list.items.sort(sortByContacts);
                }

                $.each(list.items, function(i, item){

                    var link = "";
                    if (i < 2)
                    { link += "<li class='clearfix' id='"+item.suggestionId+"'>";}
                    else
                    { link += "<li class='clearfix' style='display:none;' id='"+item.suggestionId+"'>" }

                    link += "<div class='peoplePicture pull-left'><div class='avatarXSmall'><img src='"+item.avatar+"'></div></div>";
                    link += "<div class='peopleInfo'>";
                    link += "<div class='peopleName'><a href='"+item.profile+"' target='_parent'>"+item.suggestionName+"</a></div>";
                    link += "<div style='display:none;' class='peopleAction' ><a class='connect btn-primary btn btn-mini' href='#' onclick='return false'>"+connect+"</a><a class='ignore' href='#' onclick='return false'><i class='uiIconClose'></i></a></div>";
                    link +="<div class='peoplePosition'>"+item.title+"</div><div class='peopleConnection'>"+item.number+"&nbsp;"+connection+"</div>";
                    link += "</div></li>";

                    $("#suggestions").append(link);

                    $("#"+item.suggestionId).mouseover(function(){
                        var $item = $(this);
                        $item.find(".peopleName, .peoplePosition, .peopleConnection").addClass("actionAppears");
                        $item.find(".peopleAction").show();
                    });
                    $("#"+item.suggestionId).mouseout(function(){
                        var $item = $(this);
                        $item.find(".peopleName, .peoplePosition, .peopleConnection").removeClass("actionAppears");
                        $item.find(".peopleAction").hide();
                    });

                    $("#"+item.suggestionId+" a.connect").live("click", function(){
                        $.getJSON("/rest/homepage/intranet/people/contacts/connect/"+item.suggestionId, null);

                        if($("#suggestions").children().length == 1) {
                            $("#peopleSuggest").fadeOut(500, function () {
                                $("#"+item.relationId).remove();
                                $("#peopleSuggest").hide();
                                if ($("#spaceSuggest").is(":hidden")){
                                    $("#content").hide();
                                }


                            });
                        }
                        else {
                            $("#"+item.suggestionId).fadeOut(500, function () {
                                $("#"+item.suggestionId).remove();
                                $('#suggestions li:hidden:first').fadeIn(500, function() {});

                            });
                        }
                    });

                    $("#"+item.suggestionId+" a.ignore").live("click", function(){
                        //$.getJSON("/rest/homepage/intranet/people/contacts/ignore/"+item.suggestionId, null);
                        if($("#suggestions").children().length == 1) {
                            $("#peopleSuggest").fadeOut(500, function () {
                                $("#"+item.relationId).remove();
                                $("#peopleSuggest").hide();
                                if ($("#spaceSuggest").is(":hidden")){
                                    $("#content").hide();
                                }


                            });
                        }
                        else {
                            $("#"+item.suggestionId).fadeOut(500, function () {
                                $("#"+item.suggestionId).remove();
                                $('#suggestions li:hidden:first').fadeIn(500, function() {});

                            });
                        }
                    });

                });
            });





            $.getJSON("/rest/homepage/intranet/spaces/suggestions", function(list){

                if (list.items.length > 0){
                    $("#content").show();
                    $("#spaceSuggest").show();
                }

                list.items.shuffle();
                var newUser=true;
                for(var k= 0; k < list.items.length; k++)
                {
                    if(list.items[k].number!=0){
                        newUser=false;
                    }
                }

                if(newUser==true || list.noConnections==0){
                    list.items.sort(sortByCreatedDate) ;
                }else{
                    list.items.sort(dynamicSort("displayName"));
                    // sort my most contacts instead of random
                    list.items.sort(sortByContacts);
                }
                $.each(list.items, function(i, item){

                    var link = "";

                    if (i < 2)
                    { link += "<li class='clearfix' id='"+item.spaceId+"'>";}
                    else
                    { link += "<li class='clearfix'' style='display:none;' id='"+item.spaceId+"'>" }

                    link += "<div class='spacePicture pull-left'><div class='avatarXSmall'><img src='"+item.avatarUrl+"'></div></div>";
                    link += "<div class='spaceInfo'>";
                    link += "<div class='spaceName'>"+item.displayName+"</div>";
                    if(item.privacy=="Private")
                        link += "<div class='spacePrivacy'><i class='uiIconSocGroup uiIconSocLightGray'></i>"+privateLabel+"&nbsp;-&nbsp;"+item.members+"&nbsp;"+spaceMember+"</div>";
                    else
                        link += "<div class='spacePrivacy'><i class='uiIconSocGroup uiIconSocLightGray'></i>"+publicLabel+"&nbsp;-&nbsp;"+item.members+"&nbsp;"+spaceMember+"</div>";
                    if(item.registration == "open")
                        link += "<div class='spaceAction' ><a class='connect btn-primary btn btn-mini' href='#' onclick='return false'>"+joinLabel+"</a>";
                    else
                        link += "<div class='spaceAction' ><a class='connect btn-primary btn btn-mini' href='#' onclick='return false'>"+requestLabel+"</a>";

                    link += "<a class='ignore' href='#' onclick='return false'><i class='uiIconClose'></i></a></div>";
                    link += "<div class='spaceCommon'>"+item.number+"&nbsp;"+member+"</div>";
                    link += "</div></li>";

                    $("#suggestionsspace").append(link);

                    $("#"+item.spaceId).mouseover(function(){
                        var $item = $(this);
                        $item.find(".spacePrivacy, .spaceCommon, .spaceName").addClass("actionspaceAppears");;
                        $item.find(".spaceAction").show();
                    });
                    $("#"+item.spaceId).mouseout(function(){
                        var $item = $(this);
                        $item.find(".spacePrivacy, .spaceCommon, .spaceName").removeClass("actionspaceAppears");;
                        $item.find(".spaceAction").hide();
                    });


                    $("#"+item.spaceId+" a.connect").live("click", function(){
                        $.getJSON("/rest/homepage/intranet/spaces/request/"+item.spaceId, null);

                        if($("#suggestionsspace").children().length == 1) {
                            $("#spaceSuggest").fadeOut(500, function () {
                                $("#"+item.spaceId).remove();
                                $("#spaceSuggest").hide();
                                if ($("#peopleSuggest").is(":hidden")){
                                    $("#content").hide();
                                }


                            });
                        }
                        else {
                            $("#"+item.spaceId).fadeOut(500, function () {
                                $("#"+item.spaceId).remove();
                                $('#suggestionsspace li:hidden:first').fadeIn(500, function() {});

                            });
                        }
                    });

                    $("#"+item.spaceId+" a.ignore").live("click", function(){
                        //$.getJSON("/rest/homepage/intranet/people/contacts/ignore/"+item.suggestionId, null);
                        if($("#suggestionsspace").children().length == 1) {
                            $("#spaceSuggest").fadeOut(500, function () {
                                $("#"+item.spaceId).remove();
                                $("#spaceSuggest").hide();
                                if ($("#peopleSuggest").is(":hidden")){
                                    $("#content").hide();
                                }


                            });
                        }
                        else {
                            $("#"+item.spaceId).fadeOut(500, function () {
                                $("#"+item.spaceId).remove();
                                $('#suggestionsspace li:hidden:first').fadeIn(500, function() {});

                            });
                        }
                    });

                });
            });
        }
    };
})($);