function init() {
	createPollDiv();
	// Adding eXo Platform container information
	var opts = {};
	opts[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = ["portalName", "restContext", "host" ];
	var req = opensocial.newDataRequest();
	req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER, opts), 'viewer');
	req.send(createURL);
	$('.settingBt').click(function() {
		config();
	});
}

function createPollDiv() {
	var prefs = new gadgets.Prefs();
	var forumURL = window.location.protocol + "//" + window.location.host + parent.parent.eXo.env.portal.context + "/" + parent.parent.eXo.env.portal.portalName + "/forum";
	adjustHeight();
}

function createURL(data) {
	this.viewer = data.get('viewer').getData();
	var hostName = viewer.getField('hostName');
	var portalName = viewer.getField('portalName');
	var restContext = viewer.getField('restContextName');
	baseURL = hostName + "/" + restContext + "/ks/poll/";
	var url = baseURL + "viewpoll/pollid";
	$.getJSON(url, createPollList);
}

function createPollList(data) {
	var prefs = new gadgets.Prefs();
	var pollIds = data.pollId;
	var pollNames = data.pollName;
	var len = pollIds.length;
	var votedValue = prefs.getString('votedValue');
	
	if (data.isAdmin == "true") {
		var html = [];
		html.push('<div class="form-horizontal">');
		html.push('<div class="control-group">');
		html.push('<label for="type" class="control-label"> Select another poll: </label>');
		html.push('<div class="controls">');
		html.push('<span class="uiSelectbox">');
		html.push('<select id="selVoteQuestions" class="selectbox" name="type" onchange="changeVote(this);">');
		for ( var i = 0; i < len; i++) {
			html.push('<option value="' + pollIds[i] + '">' + pollNames[i] + '</option>');
		}
		html.push('</select>');
		html.push('</span>');
		html.push('</div>');
		html.push('</div>');
		html.push('</div>');
		$('#listpoll').html(html.join(''));
	}

	var randomPollId = 0;
	var url = baseURL + "viewpoll/" + pollIds[randomPollId];
	if (len == 0) {
		document.getElementById("poll").innerHTML = "<div class='light_message'><i class='uiIconPoll'></i> " + prefs.getMsg("nopoll") + "</div>";
		adjustHeight();
		return;
	}
	
	// initialize values for vote options
	if (votedValue) {
		var selVote = document.getElementById("selVoteQuestions");
		selVote.value = votedValue;
		selVote.onchange();
	} else {
		$.getJSON(url, function(data) {
			showPoll(data, false);
		});
	}
}

function showPoll(data, isVoteAgain) {
	var options = data.option;
	var prefs = new gadgets.Prefs();
	var lblVote = prefs.getMsg("vote");
	var question = data.question;
	var pollId = data.id;
	var parentPath = data.parentPath;
	var haveTopic = parentPath.indexOf("ForumData/CategoryHome"); // check topic of poll if toptic is exist
	if (!data.showVote || isVoteAgain) {
		html = [];
		if (haveTopic) {
			var prefs = new gadgets.Prefs();
			var topicId = pollId.replace("poll", "topic");
			var topicURL = window.location.protocol + "//" + window.location.host + parent.parent.eXo.env.portal.context + "/" + parent.parent.eXo.env.portal.portalName + "/forum/topic/" + topicId;
			html.push('<h6 class="clearfix"><a class="question" title = "' + prefs.getMsg('discuss') + '" target ="_parent" href="' + topicURL + '"><i class="uiIconPoll"></i> ' + question + '</a><a class="discuss btn" type="button" title="' + prefs.getMsg("discuss") + '" target="_parent" href="' + topicURL + '">' + prefs.getMsg("discuss") + '</a></h6>');
		} else {
			html.push('<h6 class="question">' + question + '</h6>');
		}
		html.push('<form>');
		html.push('<input type="hidden" name="pollid" value="' + data.id + '"/>')
		if (data.isMultiCheck) {
			for ( var i = 0, len = options.length; i < len; i++) {
				html.push('<label class="uiCheckbox"><input type="checkbox"  class="checkbox"  id="rdoVote_' + i + '" name="rdoVote" value="' + i + '"><span title="' + options[i] + '" data-placement="bottom" rel="tooltip">' + options[i] + '</span></label>');
			}
		} else {
			for ( var i = 0, len = options.length; i < len; i++) {
				html.push('<label class="uiRadio"><input type="radio" class="radio" id="rdoVote_' + i + '" name="rdoVote" value="' + i + '"><span title="' + options[i] + '" data-placement="bottom" rel="tooltip">' + options[i] + '</span></label>');
			}
		}
		html.push("<div class='uiAction btnform'><button class='btn' type='button' onclick='doVote(this);' name='btnVote' value='" + lblVote + "'>Vote</button>");
		html.push("</form>");

		$('#poll').html(html.join(''));
	} else {
		showResult(data);
	}
	adjustHeight();
}

function showResult(data) {
	var prefs = new gadgets.Prefs();
	var voters = data.infoVote[data.infoVote.length - 1];
	var options = data.option;
	var vote = data.vote;
	var question = data.question;
	var msgOption = prefs.getMsg('option');
	var msgPercent = prefs.getMsg('percent');
	var msgVoter = prefs.getMsg('voter');
	var msgTotal = prefs.getMsg('total');
	var pollId = data.id;
	var parentPath = data.parentPath;
	var haveTopic = parentPath.indexOf("ForumData/CategoryHome"); // check topic of poll if toptic is exist
	var tbl = [];

	if (haveTopic) {
		var prefs = new gadgets.Prefs();
		var topicId = pollId.replace("poll", "topic");
		var topicURL = window.location.protocol + "//" + window.location.host + parent.parent.eXo.env.portal.context + "/" + parent.parent.eXo.env.portal.portalName + "/forum/topic/" + topicId;
		tbl.push('<h6 class="clearfix"><a class="question " title = "' + prefs.getMsg('discuss') + '"  target="_parent"  href="' + topicURL + '"><i class="uiIconPoll"></i> ' + question + '</a><a class="discuss btn" type="button" title = "' + prefs.getMsg('discuss') + '"  target="_parent"  href="' + topicURL + '">' + prefs.getMsg('discuss') + '</a></h6>');
	} else {
		tbl.push('<h6 class="question">' + question + '</h6>');
	}

	tbl.push('<table class="voteResult">');
	tbl.push('<tbody>');
	for ( var i = 0, len = options.length; i < len; i++) {
		var result = Math.round(vote[i]);
		var style = "";
		if (result > 5) {
			var style = 'width:' + result + '%;';
		} else {
			var style = 'width:' + result + '%;';
		}

		tbl.push('<tr><td><div class="label-vote">' + options[i] + '</div></td><td><div class="horizontalBG"><div class="horizontalBar" style="' + style + '">&nbsp;</div></div></td><td class="percent">' + result + '%</td></tr>');
	}
	tbl.push('</tbody>');
	tbl.push('</table>');
	tbl.push('<div class="clearfix btnform">');
	if (data.isAgainVote) {
		tbl.push("<span class='uiAction'><button class='btn' type='button' id='btnVoteAgain' value='" + prefs.getMsg("voteAgain") + "'>Vote Again</button></span>");
		$("#btnVoteAgain").live("click", function() {
			showPoll(data, true);
		});
	}
	tbl.push('<strong class="pull-right"> ' + msgTotal + ': ' + voters + ' ' + msgVoter + '</strong>');
	tbl.push('</div>');

	$("#poll").html(tbl.join(''));
	adjustHeight();
}

function doVote(el) {
	var votes = [];
	$(".radio:checked").each(function() {
		votes.push($(this).val());
	});

	if (votes.length < 1) {
		$("input:checked").each(function() {
			votes.push($(this).val());
		});
		if (votes.length < 1) {
			return;
		}
	}

	var pollId = el.form.elements["pollid"].value;
	var url = baseURL + "votepoll/" + pollId + "/" + votes.join(":");
	$.getJSON(url, showResult);
}

function changeVote(obj) {
	var selectedValue = obj.options[obj.selectedIndex].value;
	var url = baseURL + "viewpoll/" + selectedValue;
	var prefs = new gadgets.Prefs();
	prefs.set('votedValue', selectedValue);
	$.getJSON(url, function(data) {
		showPoll(data, false);
	});
	config();
}

function config() {
	if ($('#listpoll').is(':visible')) {
		$('#listpoll').fadeOut("fast", adjustHeight);
	} else {
		$('#listpoll').fadeIn("fast", adjustHeight);
	}
	adjustHeight();
}
function adjustHeight() {
	gadgets.window.adjustHeight($('.uiGadgetThemes').outerHeight());
}
gadgets.util.registerOnLoadHandler(init);