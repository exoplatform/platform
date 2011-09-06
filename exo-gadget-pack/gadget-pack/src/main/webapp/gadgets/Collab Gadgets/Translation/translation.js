  var insertedScript = null;
  function translate(sLan, dLan) {
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    sText = document.translationform.srcText.value;
    if (sText == null || sText.length==0 ) return;

    sLanguage = document.getElementById('srcLanguage').getAttribute('selectedLang');
    dLanguage = document.getElementById('desLanguage').getAttribute('selectedLang');
    if (sLanguage==dLanguage) {
      document.getElementById("resultTransalated").innerHTML = sText
      return;
    }
    document.getElementsByTagName('head')[0].appendChild(newScript);
    var url = 'https://www.googleapis.com/language/translate/v2?key=AIzaSyD0Zbp-L8mCz1SKayZqhw143FTxebkbudo&source=' + sLanguage + '&target=' + dLanguage + '&callback=translateText&q=' + escape(sText);
    newScript.src = url;
    insertedScript = newScript;
  }
  function translateText(response) {
    if (response.data ==null ) {
      document.getElementById("resultTransalated").innerHTML = "Traffic jum, see you tommorow";
    }else {
      document.getElementById("resultTransalated").innerHTML = response.data.translations[0].translatedText;
    }
    var header = document.getElementsByTagName('head')[0];
  if (insertedScript != null)
  {
      header.removeChild(insertedScript);
      insertedScript = null;
    }
  }

  function selectLanguage(sLan, sText, slDes, sDiv) {
    var selectedDiv = document.getElementById(slDes);
    selectedDiv.innerHTML = sText;
    selectedDiv.setAttribute('selectedLang', sLan);
    document.getElementById(sDiv).style.display="none";
    isManualShow = false;
  }

  function showOption(optID, parentDiv, hiddenID, eventHandle) {
    // pop event bubble
    if (!eventHandle) var eventHandle = window.event;
    if (eventHandle) eventHandle.cancelBubble = true;
    if (eventHandle.stopPropagation) eventHandle.stopPropagation();
    var showElement = document.getElementById(optID);
    var hiddenElement = document.getElementById(hiddenID);
    if (hiddenElement !=null) {
      hiddenElement.style.display="none";
    }
    if (showElement.style.display =="block")
    {
      showElement.style.display = "none";
      return;
    }
    showElement.style.display="block";
    var posX = getPositionX(parentDiv);
    var deltaY = document.getElementById(parentDiv).clientHeight;
    var posY = getPositionY(parentDiv);
    showElement.style.top = (posY + deltaY) + "px";
    showElement.style.left = posX + "px";
    isManualShow = true;
    
    
    return false;
  }
  function populateAvailableLanguages(des, desOption) {
    var langOptions = [
                        ["af","Afrikaans"],["sq","Albanian"],["ar","Arabic"], ["hy","Armenian"],["az","Azerbaijani"], ["eu", "Basque"], ["be", "Belarusian"],
                        ["bg","Bulgarian"],["ca","Catalan"],["zh-CN","Chinese"], ["hr","Croatian"],["cs","Czech"], ["da", "Danish"], ["nl", "Dutch"],
                        ["en","English"],["et","Estonian"],["tl","Filipino"], ["fi","Finnish"],["fr","French"], ["gl", "Galician"], ["ka", "Georgian"],
                        ["de","German"],["el","Greek"],["ht","Haitian Creole"], ["fi","Finnish"],["iw","Hebrew"], ["hi", "Hindi"], ["hu", "Hungarian"],
                        ["is","Icelandic"],["id","Indonesian"],["ga","Irish"], ["it","Italian"],["ja","Japanese"], ["ko", "Korean"],
                        ["lv","Latvian"],["lt","Lithuanian"],["mk","Macedonian"], ["ms","Malay"],["mt","Maltese"], ["no", "Norwegian"], ["fa", "Persian"],
                        ["pl","Polish"],["pt","Portuguese"],["ro","Romanian"], ["ru","Russian"],["sr","Serbian"], ["sk", "Slovak"], ["sl", "Slovenian"],
                        ["es","Spanish"],["sw","Swahili"],["sv","Swedish"], ["th","Thai"],["tr","Turkish"], ["uk", "Ukrainian"], ["ur", "Urdu"],
                        ["vi","Vietnamese"],["cy","Welsh"],["yi","Yiddish"]
                      ];
    var divElement = document.getElementById(des);
    i=0;
    do
    {
     if (langOptions[i] == null) break;
     if (langOptions[i] == null) break;
     var myLan = document.createElement('DIV');
     myLan.setAttribute('onClick','selectLanguage("' + langOptions[i][0] + '", "' + langOptions[i][1] +  '",  "' +  desOption + '", "' + des + '");');
     myLan.setAttribute('class', 'langugageOption');
     myLan.setAttribute('onmouseover', 'this.className = "langugageOption_hover"');
     myLan.setAttribute('onmouseout', 'this.className = "langugageOption"');
     myLan.innerHTML = langOptions[i][1];
     divElement.appendChild(myLan);
     i++;
    }while (true);



  }
  function getPositionY(elementID) {
    var iReturnValue = 0;
    elementid=document.getElementById(elementID)
    while( elementid != null ){
      iReturnValue += elementid.offsetTop;
      elementid = elementid.offsetParent;
    }
    return iReturnValue;
  }
  function getPositionX(elementID) {
    var iReturnValue = 0;
    elementid=document.getElementById(elementID)
    while( elementid != null ){
      iReturnValue += elementid.offsetLeft;
      elementid = elementid.offsetParent;
    }
    return iReturnValue;
  }
  function closeOption(objHandle, evtHandle) {
    var elementid=document.getElementById('showAvailableSourceLanguage');
    if (elementid != null ) elementid.style.display="none";
    var elementid=document.getElementById('showAvailableDesLanguage');
    if (elementid != null ) elementid.style.display="none";
  }

