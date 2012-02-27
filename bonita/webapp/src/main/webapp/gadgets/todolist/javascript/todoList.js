var host = window.location.protocol + "//" + window.location.host;
var userName=parent.eXo.env.portal.userName;
var rest=parent.eXo.env.portal.rest;

init=function(){
 
  getXhr();
    xhr.onreadystatechange = function()
    {
    if(xhr.readyState == 4 && xhr.status == 200)
     {
		getList(xhr);
     }
	};
	
  serviceUrl =host+  "/" + rest + "/BonitaService/sendList?ServiceUrl=/bonita-server-rest/API/queryRuntimeAPI/getTaskListByUserIdAndActivityState/"+userName+"/READY";
  xhr.open("GET",serviceUrl, true);  
  xhr.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
  xhr.send();
  return false;
};
 
getXhr=function (){
	
	if(window.XMLHttpRequest){
		xhr = new XMLHttpRequest();
	} else if(window.ActiveXObject){
		try {
			xhr = new ActiveXObject("Msxml2.XMLHTTP");
		}catch (e){
			xhr = new ActiveXObject("Microsoft.XMLHTTP");
		}
	}else{
		alert("Votre navigateur ne supporte pas les objets XMLHTTPRequest, veuillez le mettre à jour");
		xhr = false;
	}
};

getList=function(xhr){
 
    var xml= xhr.responseXML;
    var baseURL = host + "/portal/bpm/workflow?url=";
    var urlXp = encodeURIComponent(host+"/bonita-todo/console/BonitaConsole.html?#CaseList/lab:Inbox");
	//document.getElementById('more').innerHTML ="<a id='More' href='/portal/private/intranet/bonitaTODO' target='_parent' class='IconDropDown'>"+eXo.social.Locale.getMsg('more_link_label')+"</a><div class='ContTit'>"+eXo.social.Locale.getMsg('my_todos')+"</div>";
	var activityinstance=xml.getElementsByTagName('ActivityInstance');
    var str='';
	if(activityinstance.length!=0){
		for (i=0 ; i<activityinstance.length ; i++)	{

		  var activityDefinitionUUID=activityinstance[i].getElementsByTagName('rootInstanceUUID');
		  var instanceUUID=activityinstance[i].getElementsByTagName('instanceUUID')[0].getElementsByTagName('value')[0].textContent;                            
		  var label=activityinstance[i].getElementsByTagName('label')[0].textContent;
		  var priority=activityinstance[i].getElementsByTagName('priority')[0].textContent;
		  var activityDefinitionUUIDvalue=activityDefinitionUUID[0].getElementsByTagName('value')[0].textContent;
		  var uuid=  activityinstance[i].getElementsByTagName('uuid')[0].getElementsByTagName('value')[0].textContent;
		  var tab=activityDefinitionUUIDvalue.split("--");
		  var process='';
		  var doclink;
		  var path='';
		  if(tab[0]=="PublicationProcess"){
			  doclink =activityinstance[i].getElementsByTagName('instanceUUID')[0].getElementsByTagName('doclink')[0].textContent;   
			  var titledoc=doclink .split("/");
			  var title=titledoc[titledoc.length-1];   
			  //process=tab[2]+" | "+label+" : \""+title+"\"";
			  process=label+" : \""+title+"\"";
			  doclink=doclink.substring(11);
			  path="&path="+doclink;
		  }else{
			 //process=tab[0]+"-"+tab[2]+" : "+label;
			 process=label;
		  }   
		  var url=encodeURIComponent(host+"/bonita/console/BonitaConsole.html?task="+uuid+"&"+"bonitaLocale="+ "en&mode=form")+path;
		  str+="<div><a id='Task' class='link_"+priority+"' href='"+ baseURL +url+"'"+ "target='_parent'>"+process+"</a></div>";
		 
		}
		document.getElementById('TodosList').innerHTML = str;
	}else{
		document.getElementById('TodosList').innerHTML="<div class='light_message'>"+eXo.social.Locale.getMsg('notask')+"</div>";
	}
	gadgets.window.adjustHeight();
	return;		
};