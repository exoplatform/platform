function init() {

  createPollDiv();
  // Adding eXo Platform container information
  var opts = {};
  opts[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [
      "portalName",
      "restContext",
      "host"];
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER, opts), 'viewer');
  req.send(createURL);
  $('.SettingButton').click(function(){
    config();
  });
}

function createPollDiv() {
  var prefs = new gadgets.Prefs();
  var forumURL = window.location.protocol + "//" + window.location.host + "/portal/intranet/forum";
  document.getElementById("createpoll").innerHTML = prefs.getMsg("createPoll") + " <a target='_parent' href='" + forumURL + "'>forums</a>";
  adjustHeight();  
}

function createURL(data) {
  this.viewer = data.get('viewer').getData();  
  var hostName = viewer.getField('hostName');
  var portalName = viewer.getField('portalName');
  var restContext = viewer.getField('restContextName');
  baseURL = hostName + "/" + restContext + "/ks/poll/";
  var url = baseURL + "viewpoll/pollid";
  $.getJSON(url,createPollList);
}

function createPollList(data){
  var prefs = new gadgets.Prefs();
  
  var pollIds = data.pollId;
  var pollNames = data.pollName;
  var len = pollIds.length;
  
  if (data.isAdmin == "true") {
    var html = [];
    html.push('<select class="PollList" name="pollname" onchange="changeVote(this);">');
    for (var i = 0 ; i < len; i++) {
      html.push('<option value="' + pollIds[i] + '">' + pollNames[i] + '</option>');
    }
    html.push('</select>');
    $('#listpoll').html(html.join(''));
  }
  var randomPollId  = 0;
  var url = baseURL + "viewpoll/" + pollIds[randomPollId];

  if(len == 0){
	document.getElementById("poll").innerHTML = "<div class='light_message'>" + prefs.getMsg("nopoll") + "</div>";
	adjustHeight();
	return;

  }
  
  $.getJSON(url,function(data){
    showPoll(data, false);
  });
}


function showPoll(data, isVoteAgain){
  var options = data.option;
  var prefs = new gadgets.Prefs();
  var lblVote = prefs.getMsg("vote");
  var question = data.question;
  var pollId = data.id;
  var parentPath = data.parentPath;
  var haveTopic = parentPath.indexOf("ForumData/CategoryHome"); //check topic of poll if toptic is exist  
  var discussUrl = "#";
  if(!data.showVote || isVoteAgain){    
    html = [];
      if(haveTopic){
          var prefs = new gadgets.Prefs();
          var topicId= pollId.replace("poll","topic");
          var topicURL = window.location.protocol + "//" + window.location.host + "/portal/intranet/forum/topic/" + topicId;
          html.push('<h4><a  target="_parent" class="Question" title = "' + prefs.getMsg('discuss') + '" target ="_parent" href="'+ topicURL + '">' + question + '</a></h4>');
        discussUrl = "<a class='Discuss' title='" + prefs.getMsg("discuss") + "'  target='_parent'  href='"+ topicURL + "'>" + prefs.getMsg("discuss") + "</a>";
      }
      else{
          html. push('<h4 class="Question">' + question + '</h4>');
      }
    html.push('<form>');
    html.push('<input type="hidden" name="pollid" value="'+ data.id +'"/>')
    if(data.isMultiCheck){
      for(var i = 0, len = options.length; i < len; i++){
        html.push('<div><input class="radio" type="checkbox" name="rdoVote" value="' + i + '"><span>' + options[i] + '</span></div>');
      }
    } else {
      for(var i = 0, len = options.length; i < len; i++){
        html.push('<div><input class="radio" type="radio" name="rdoVote" value="' + i + '"><span>' + options[i] + '</span></div>');
      }
    }
    html.push("<center style='margin-top: 5px'><input type='button' onclick='doVote(this);' name='btnVote' value='" + lblVote + "'/></center>");
        html.push("</form>");
      if(haveTopic){
          html.push(discussUrl);
        //document.getElementById("createpoll").innerHTML = prefs.getMsg('createPoll') + ' <a target="_parent" href="' + forumURL + '">forums</a>';
      }
    $('#poll').html(html.join(''));
  }else{
    showResult(data);
  }
  adjustHeight();
}

function showResult(data){
  var prefs = new gadgets.Prefs();
  var voters = data.infoVote[data.infoVote.length-1];
  var options = data.option;
  var vote = data.vote;
  var question = data.question;
  var msgOption = prefs.getMsg('option');
  var msgPercent = prefs.getMsg('percent');
  var msgVoter = prefs.getMsg('voter');
  var msgTotal = prefs.getMsg('total');
  var pollId = data.id;
  var parentPath = data.parentPath;
  var haveTopic = parentPath.indexOf("ForumData/CategoryHome"); //check topic of poll if toptic is exist
  var discussUrl = "#";
  var tbl = [];
  
  if(haveTopic){
      var prefs = new gadgets.Prefs();
    var topicId= pollId.replace("poll","topic");
      var topicURL = window.location.protocol + "//" + window.location.host + "/portal/intranet/forum/topic/" + topicId;
    tbl.push('<h4><a class="Question" title = "' + prefs.getMsg('discuss') + '"  target="_parent"  href="'+ topicURL + '">' + question + '</a></h4>');
    discussUrl = '<a class="Discuss" title = "' + prefs.getMsg('discuss') + '"  target="_parent"  href="'+ topicURL + '">' + prefs.getMsg('discuss') + '</a>';
  }
  else{
    tbl.push('<h4 class="Question">' + question + '</h4>');
  }
    
  tbl.push('<table class="VoteResult">');
  tbl.push('<tbody >');
  for(var i = 0, len = options.length; i < len; i++){
    var result = Math.round(vote[i]);
    var style ="";
    if(result>5){
      var style = 'color:white; text-align:center;width:' + result + '%; background-color:#226ab4';
    }
    else{
      var style = 'color:black; text-align:center;width:' + result + '%; background-color:#226ab4';
    }
    
    tbl.push('<tr><td width="50%">' + options[i] + '</td><td><div class="HorizontalBar" style="' + style + '">' + result + '%</div></td></tr>');
  }
  tbl.push('</tbody>');
  tbl.push('</table>');
  tbl.push('<strong style="display: inline-block; margin-bottom: 5px;"> '+ msgTotal +': ' + voters + ' ' + msgVoter +'</strong>');
  tbl.push("<center style='margin-top: 5px; margin-bottom: 12px;'><input type='button' id='btnVoteAgain' value='" + "Vote again" + "'/></center>");
  $("#btnVoteAgain").live("click", function(){
      showPoll(data, true);
  });
  if(haveTopic){
       tbl.push(discussUrl);
  }
  $("#poll").html(tbl.join(''));
  adjustHeight();
}

function doVote(el){
  var votes = [];
  $(".radio:checked").each(function(){
    votes.push($(this).val());
  });
  
  if(votes.length < 1) return;
    
  var pollId = el.form.elements["pollid"].value;
  var url = baseURL + "votepoll/" + pollId + "/" + votes.join(":");
  $.getJSON(url,showResult);
}

function changeVote(obj){
  var selectedValue = obj.options[obj.selectedIndex].value;
  var url = baseURL + "viewpoll/" + selectedValue;
  $.getJSON(url,function(data){
    showPoll(data, false);
  });
  config();
}

function config(){
  if($('#listpoll').is(':visible')) 
    $('#listpoll').fadeOut("fast",adjustHeight);    
  else 
    $('#listpoll').fadeIn("fast",adjustHeight);
  adjustHeight();
}
function adjustHeight(){
    gadgets.window.adjustHeight($('.UIGadgetThemes').outerHeight());  
}
gadgets.util.registerOnLoadHandler(init);
