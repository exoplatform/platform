    function eXoLastPostsGadget(){      
    } ;

    eXoLastPostsGadget.prototype.getPrefs = function(){
      var prefs = new gadgets.Prefs();
      var nbOfPosts = prefs.getString("nbOfPosts");
      var forumUrl = prefs.getString("forumUrl");
      return {
        "nbOfPosts": nbOfPosts && parseInt(nbOfPosts) > 0 ? nbOfPosts: 5,
        "forumUrl"  : forumUrl ? forumUrl : "/forum"
      }
    }
    
    eXoLastPostsGadget.prototype.onLoadHander = function() {
      eXoLastPostsGadget.getData();
    }

    eXoLastPostsGadget.prototype.createServiceRequestUrl = function(){
      var prefs = eXoLastPostsGadget.getPrefs();
      var serviceUrl = window.location.protocol + "//" + window.location.host;
      if(typeof(parent.eXo) != "undefined") {
        serviceUrl += parent.eXo.env.server.context + "/" + parent.eXo.env.portal.rest + "/ks/forum/getmessage/" + prefs.nbOfPosts + "/";
      } else {
        serviceUrl += "/portal/rest/ks/forum/getmessage/" + prefs.nbOfPosts + "/";
      }
      serviceUrl += "?" + new Date().getTime();
      return serviceUrl;
    }

    eXoLastPostsGadget.prototype.getForumUrl = function(){
      var prefs = eXoLastPostsGadget.getPrefs();
      var forumUrl = "";
      if(typeof(parent.eXo) != "undefined") {
        forumUrl = parent.eXo.env.server.context + "/" + parent.eXo.env.portal.accessMode + "/" + parent.eXo.env.portal.portalName + prefs.forumUrl;
      } else {
        forumUrl = "/portal/public/intranet" + prefs.forumUrl;
      }
      return forumUrl;
    }      
      
    eXoLastPostsGadget.prototype.createPostUrl = function(postId, topicId){
      var prefs = eXoLastPostsGadget.getPrefs();
      var postUrl = eXoLastPostsGadget.getForumUrl() + "/" + topicId + "/" + postId;
      return postUrl;
    }
      
      
    eXoLastPostsGadget.prototype.getData = function(){           
      var url = eXoLastPostsGadget.createServiceRequestUrl();          
      eXoLastPostsGadget.ajaxAsyncGetRequest(url,eXoLastPostsGadget.render);
      if(typeof(requestInterval) == "undefined") requestInterval = setInterval(eXoLastPostsGadget.getData,300000);
    }      

    eXoLastPostsGadget.prototype.render =  function(obj){
      data = obj.data.jsonList;
      if(!data || data.length == 0){
        return;
      }
      var cont = document.getElementById("latestForumPostsContainer");  
      var prefs = eXoLastPostsGadget.getPrefs();
      var html = '';
      var len = (prefs.nbOfPosts < data.length)? prefs.nbOfPosts:data.length;
      for(var i = 0 ; i < len; i++){  
        var item = data[i];
        //var postLink = eXoLastPostsGadget.createPostUrl(item.id, item.topicId);
        html += '<div><a target="_blank" href=\"'+ item.url + '\" class="IconLink">' + item.title +'</a></div>';
        html += '<div class="PostDetail">' + item.authors + ' - ' + item.date +'</div>';
      }
      html += '';
      cont.innerHTML = html;
      eXoLastPostsGadget.setForumLink();
      gadgets.window.adjustHeight();
    }
      
    eXoLastPostsGadget.prototype.ajaxAsyncGetRequest = function(url, callback) {
        var params = {};  
        params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.SIGNED;
        params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.GET;
        params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
        gadgets.io.makeRequest(url, callback, params);
        return;    
     }

    eXoLastPostsGadget.prototype.setForumLink = function(){
      var forumUrl = eXoLastPostsGadget.getForumUrl();
      var a = document.getElementById("ShowAll");
      a.href = forumUrl;
    }
        
    eXoLastPostsGadget.prototype.notify = function(){
      var msg = gadgets.Prefs().getMsg("nopost");
      document.getElementById("latestForumPostsContainer").innerHTML = '<div class="Warning">' + msg + '</div>';
      eXoLastPostsGadget.setForumLink();
    }
  
        
    eXoLastPostsGadget.prototype.createHttpRequest = function(){
      var xhr = new XMLHttpRequest();
      if(!xhr) xhr = new ActiveXObject("Msxml2.XMLHTTP");
      return xhr;
    }

      
    eXoLastPostsGadget =  new eXoLastPostsGadget();

    gadgets.util.registerOnLoadHandler(eXoLastPostsGadget.onLoadHander);

