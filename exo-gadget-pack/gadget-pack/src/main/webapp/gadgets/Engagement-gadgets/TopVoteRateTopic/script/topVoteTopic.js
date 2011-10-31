    function eXoTopVoteTopicGadget(){      
    } ;

    eXoTopVoteTopicGadget.prototype.getPrefs = function(){
      var prefs = new gadgets.Prefs();
      var maxcount = prefs.getString("maxcount");
      return {
        "maxcount": maxcount && parseInt(maxcount) > 0 ? maxcount: 5
      }
    }
    
    eXoTopVoteTopicGadget.prototype.onLoadHander = function() {
      eXoTopVoteTopicGadget.getData();
    }

    eXoTopVoteTopicGadget.prototype.createServiceRequestUrl = function(){
      var prefs = eXoTopVoteTopicGadget.getPrefs();
      var serviceUrl = window.location.protocol + "//" + window.location.host;
      if(typeof(parent.eXo) != "undefined") {
        serviceUrl += parent.eXo.env.server.context + "/" + parent.eXo.env.portal.rest + "/forumsService/forums/toprate/" + prefs.maxcount + "/";
      } else {
        serviceUrl += "/portal/rest/forumsService/forums/toprate/" + prefs.maxcount + "/";
      }
      return serviceUrl;
    }
      
      
      
      
    eXoTopVoteTopicGadget.prototype.getData = function(){           
      var url = eXoTopVoteTopicGadget.createServiceRequestUrl();
      eXoTopVoteTopicGadget.ajaxAsyncGetRequest(url,eXoTopVoteTopicGadget.render);
      if(typeof(requestInterval) == "undefined") requestInterval = setInterval(eXoTopVoteTopicGadget.getData,300000);
    }      

    eXoTopVoteTopicGadget.prototype.render =  function(obj){
      var topicList = new Array();
      data = obj;
      $.each(data, function(i, listTopics) {
          $.each(listTopics, function(key, topics) {
            $.each(topics, function(index, item) {
                      topicList[index]= item;
            });

          });
      });
      
      if(!data || data.length == 0){
        return;
      }
      var cont = document.getElementById("topVoteTopicContainer");  
      var prefs = eXoTopVoteTopicGadget.getPrefs();
      var html = '';
      var len = topicList.length;

      for(var i = 0 ; i < len; i++){  
        var item = topicList[i];
        var voteRating = item.voteRating;
        var roundVoteRating = Math.round(Number(item.voteRating));
        //var displayVoteRating = Math.round(voteRating*100)/100;
        var displayVoteRating = voteRating + "";
        if(displayVoteRating.length >=3){
          displayVoteRating = displayVoteRating.substring(0, 3)
        }
        var numberUnvoteStart = 5-roundVoteRating;
        var halfStar = roundVoteRating - voteRating;
        var displayHalfStar = 0;
        if(halfStar > 0.25 && halfStar <= 0.75) {
          roundVoteRating = roundVoteRating-1;
          displayHalfStar = 1;
        }

        html += '<div><a target="_blank" href=\"'+ item.link + '\" class="IconLink">' + item.title +'</a></div>';
        html += '<div class = "ClearFix">';
        html += '<div class="RatingInfoContainer" title="'+ gadgets.Prefs().getMsg("topic.top.vote.rate")  + ' '+ displayVoteRating + '/5 ' + gadgets.Prefs().getMsg("topic.top.vote.with") + ' ' +  item.numberOfUserVoteRating + ' ' + gadgets.Prefs().getMsg("topic.top.vote.votes") + '">';
        html += '<div class="AvgRatingImages ClearFix">';
        
        for(var j = 1 ; j <= roundVoteRating; j++){
          html += '<div class="VoteIcon Voted"><span></span></div>';
        }
        if(displayHalfStar == 1){
          html += '<div class="VoteIcon HalfVoted"><span></span></div>';
        }
        for(var k = roundVoteRating + displayHalfStar; k < 5; k++){
           html += '<div class="VoteIcon Unvoted"><span></span></div>';
        }
        
        html += '</div>';
        html += '</div>';
        var cratedDate = new Date(item.createDate.time);
        var createdDateStr = (cratedDate.format("yyyy/mm/dd"));
        html += '<div class="TopicDetail">' + item.owner + ' - ' + createdDateStr + '</div>';
        html += '</div>';
      }
      html += '';
      cont.innerHTML = html;
      gadgets.window.adjustHeight($("#TopVotedTopic-Gadget").get(0).offsetHeight);
    }
      
    eXoTopVoteTopicGadget.prototype.ajaxAsyncGetRequest = function(url, callback) {
      $.getJSON(url, callback);
      return;    
    }   

        
    eXoTopVoteTopicGadget.prototype.notify = function(){
      var msg = gadgets.Prefs().getMsg("topic.top.vote.notopic");
      document.getElementById("topVoteTopicContainer").innerHTML = '<div class="Warning">' + msg + '</div>';
    }
  
    eXoTopVoteTopicGadget =  new eXoTopVoteTopicGadget();

    gadgets.util.registerOnLoadHandler(eXoTopVoteTopicGadget.onLoadHander);

