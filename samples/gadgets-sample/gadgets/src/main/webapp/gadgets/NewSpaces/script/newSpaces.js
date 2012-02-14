    function eXoNewSpaceGadget(){      
    } ;

    eXoNewSpaceGadget.prototype.truncText = function(str, nMaxChars) {
        if (str.length <= nMaxChars)
          return str;
 
        var xMaxFit = nMaxChars - 3;
        var xTruncateAt = str.lastIndexOf(' ', xMaxFit);
        if (xTruncateAt == -1 || xTruncateAt < nMaxChars / 2)
          xTruncateAt = maxFit;
 
        return str.substr(0, xTruncateAt) + "...";
    };

    eXoNewSpaceGadget.prototype.getPrefs = function(){
      var prefs = new gadgets.Prefs();
      var maxtime = prefs.getString("maxtime");
      return {
        "maxtime": maxtime && parseInt(maxtime) > 0 ? maxtime: 10
      }
    }
        
    eXoNewSpaceGadget.prototype.setSpaceUrl = function(){
      var spaceURL = "";
      if(typeof(parent.eXo) != "undefined") {
        spaceURL = parent.eXo.env.server.context + "/" + parent.eXo.env.portal.accessMode + "/" + parent.eXo.env.portal.portalName + "/all-spaces";
      } else {
        spaceURL = "/portal/intranet/all-spaces";
      }
      var a = document.getElementById("ShowAll");
      a.href = spaceURL;
    }      
    
    eXoNewSpaceGadget.prototype.onLoadHander = function() {
      eXoNewSpaceGadget.getData();
    }

    eXoNewSpaceGadget.prototype.createServiceRequestUrl = function(){
      var prefs = eXoNewSpaceGadget.getPrefs();
      var serviceUrl = window.location.protocol + "//" + window.location.host;
      var lang = new gadgets.Prefs().getLang();
      if(typeof(parent.eXo) != "undefined") {
        serviceUrl += parent.eXo.env.server.context + "/" + parent.eXo.env.portal.rest + "/intranetNewSpaceService/space/latestCreatedSpace/" + prefs.maxtime + "/" + lang;
      } else {
        serviceUrl += "/portal/rest/intranetNewSpaceService/space/latestCreatedSpace/"  + prefs.maxtime + "/" + lang;
      }
      return serviceUrl;
    }
         
    eXoNewSpaceGadget.prototype.getData = function(){           
      var url = eXoNewSpaceGadget.createServiceRequestUrl();
      eXoNewSpaceGadget.ajaxAsyncGetRequest(url,eXoNewSpaceGadget.render);
      if(typeof(requestInterval) == "undefined") requestInterval = setInterval(eXoNewSpaceGadget.getData,300000);
    }      

    eXoNewSpaceGadget.prototype.render =  function(obj){

      eXoNewSpaceGadget.setSpaceUrl();
      var spaceList = new Array();
      data = obj;

      $.each(data, function(i, listSpaces) {
          $.each(listSpaces, function(key, spaces) {
            $.each(spaces, function(index, item) {
                      spaceList[index]= item;
            });

          });
      });
      
      if(!data || spaceList.length == 0){
        eXoNewSpaceGadget.notify();
        return;
      }

      var cont = document.getElementById("newSpacesContainer");  
      var prefs = eXoNewSpaceGadget.getPrefs();
      var html = '';
      var len = spaceList.length;
      var portalURL = window.location.protocol + "//" + window.location.host + "/portal/private/intranet/";
      var avatarURL = window.location.protocol + "//" + window.location.host + "/portal";

      for(var i = 0 ; i < len; i++){  
        var item = spaceList[i];
        
        var spaceURL = portalURL + "";
        //switch link to for each space_type
        if(item.isMember){
           // spaceURL =  portalURL + item.url;
              spaceURL = window.location.protocol + "//" + window.location.host + "/portal/g/:spaces:" + item.url + "/" + item.url;
        }
        else if(item.isInvitedUser){
            spaceURL = portalURL + 'invitationSpace';
        }
        else if(item.isPendingUser){
            spaceURL = portalURL + 'pendingSpace';
        }
        else{
            spaceURL = portalURL + 'all-spaces';
        }
       
        html += '<div class="spaceDetail">'; //SpaceItem
        
        html += '<div class="spaceAvatar">';  //avatarImage

        html += '<a target="_parent" class="spaceAvatarLink" href="' + spaceURL + '">'; //link to space
        
        if(item.avatarURL != null){
          html += '<img src="' + avatarURL + item.avatarURL +  '" class="spaceAvatarImage" />';      
        }
        else{
           html += '<div class="spaceNoAvatarImage"></div>';
        }
        
        html += '</a>'; //link to space end
        
        html += '</div>';  //avatarImage end
        
        html += '<div class="spaceInfo">';
        
        if(item.isMember == false && item.registration == "open"){
           html += '<div>';
           html+= '<a class="spaceItemLink" target="_parent" title="' + item.description +  '" href=\"'+ spaceURL + '\">' + item.displayName +'</a>';
           html+= '<a  onclick= "javascript:eXoNewSpaceGadget.requestToJoinSpace(\'' + item.url  + '\')" title="' + gadgets.Prefs().getMsg("join_title") + " \'" + item.displayName + "\'" +  '" href="javascript:void(0)" class="joinLink">['+  gadgets.Prefs().getMsg("join") + ']</a>'
           html+= '</div>';
        }
        else{
          html += '<div><a class="spaceItemLink" target="_parent" title="' + item.description +  '" href=\"'+ spaceURL + '\">' + item.displayName +'</a></div>';
    	}
    
        html += '<div class = "ClearFix">';
        //var cratedDate = new Date(item.createdDate.time);
        //var createdDateStr = (cratedDate.format("yyyy/mm/dd"));
        var createdTimeAgo = item.createdTimeAgo;
        html += '<div class = "itemDetail">' + eXoNewSpaceGadget.truncText (item.description, 100)  + '</div>';
        html += '<div class = "itemDetail-italic">' + gadgets.Prefs().getMsg("Created") + " " +createdTimeAgo + '</div>';
        html += '</div>';
        html += '</div>';
        
        html += '</div>'; //SpaceItem end
      }
      html += '';
      cont.innerHTML = html;
     
      //set gadget title
      document.getElementById("newSpacesGadgetTitle").innerHTML = gadgets.Prefs().getMsg("title") + " (" + len + ")";

      gadgets.window.adjustHeight($("#NewSpaces-Gadget").get(0).offsetHeight);
    }
      
    eXoNewSpaceGadget.prototype.ajaxAsyncGetRequest = function(url, callback) {
      $.getJSON(url, callback);
      return;    
    }
    
    eXoNewSpaceGadget.prototype.requestToJoinSpace = function(spaceUrl)
    {
      var serviceUrl = "/portal/rest/intranetNewSpaceService/space/requestJoinSpace/"  + spaceUrl;
      $.getJSON(serviceUrl, eXoNewSpaceGadget.requestToJoinSpaceProcess);  //callback is requestToJoinSpaceProcess
      return;   
    }
    
    eXoNewSpaceGadget.prototype.requestToJoinSpaceProcess =  function(obj){
      var spaceList = new Array();
      data = obj;
      
      $.each(data, function(i, listSpaces) {
          $.each(listSpaces, function(key, spaces) {
            $.each(spaces, function(index, item) {
                      spaceList[index]= item;
            });

          });
      });
      
      //if data== empty, the request to join space was failure
      //todo: render list new spaces again
      if(!data || spaceList.length == 0){
        eXoNewSpaceGadget.onLoadHander();
        return;
      }
      
      //if data != empty, else the request to join space was successfully
      //redirect to the space
      var item = spaceList[0];
      //switch link to for each space_type
      if(item.isMember){
         spaceURL = window.location.protocol + "//" + window.location.host + "/portal/g/:spaces:" + item.url + "/" + item.url;
         parent.document.location=spaceURL;
      }
      
    }
        
    eXoNewSpaceGadget.prototype.notify = function(){
      var msg = gadgets.Prefs().getMsg("no_space");
      document.getElementById("newSpacesContainer").innerHTML = '<div class="noItem">' + msg + '</div>';
      document.getElementById("newSpacesGadgetTitle").innerHTML = gadgets.Prefs().getMsg("title");
      gadgets.window.adjustHeight($("#NewSpaces-Gadget").get(0).offsetHeight);
    }
  
    eXoNewSpaceGadget =  new eXoNewSpaceGadget();

    gadgets.util.registerOnLoadHandler(eXoNewSpaceGadget.onLoadHander);