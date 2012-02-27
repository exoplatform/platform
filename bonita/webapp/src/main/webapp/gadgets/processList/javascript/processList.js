var host = window.location.protocol + "//" + window.location.host;

init = function() {

	getXhr();
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && xhr.status == 200) {
			getList(xhr);
		} 
	};

	serviceUrl = host+  "/" + parent.eXo.env.portal.rest + "/BonitaService/sendList?ServiceUrl=/bonita-server-rest/API/queryDefinitionAPI/getLightProcesses";
	//xhr.open("GET", "http://bpm-demo.exoplatform.org:8080/rest/BonitaService/sendList?ServiceUrl=/bonita-server-rest/API/queryDefinitionAPI/getLightProcesses", true);
	xhr.open("GET", serviceUrl, true);
	xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
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
	//document.getElementById('process').innerHTML=eXo.social.Locale.getMsg('label');
	var xml= xhr.responseXML;
	var baseURL = host + "/portal/bpm/workflow?url=";
	
	var lightProcessDefinition=xml.getElementsByTagName('LightProcessDefinition');
	var str='';
	var icon = '<div class="ArrowIcon"><img class="listIcon off CustomImage" src="/exo-gadget-resources/skin/exo-gadget/images/ArrowRight.gif" alt=""></img></div>';
	var nbreProcess = lightProcessDefinition.length;
	for (i=0 ; i < lightProcessDefinition.length ; i++){
		
		var uuid = lightProcessDefinition[i].getElementsByTagName('uuid')[0].getElementsByTagName('value')[0].textContent;
		var label = lightProcessDefinition[i].getElementsByTagName('label')[0].textContent;
		if (label == 'PublicationProcess'){
			nbreProcess--;
			continue;
		}
		var url=encodeURIComponent(host + "/bonita/console/BonitaConsole.html?process=" + uuid + "&Locale=en&mode=form");

		str+=icon;
		str+="<a class='link' href='"+ baseURL + url + "'"+ "target='_parent'>" + label + "</a>";
		str+="<br>";
	}
	if (nbreProcess==0){
		document.getElementById('ProcessList').innerHTML = eXo.social.Locale.getMsg("noprocess");
	}else{
		
		document.getElementById('ProcessList').innerHTML = str;
	}
    gadgets.window.adjustHeight();
	return;	
};
