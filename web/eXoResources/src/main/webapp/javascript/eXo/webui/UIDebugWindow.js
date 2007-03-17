eXo.require('eXo.webui.UIPopupWindow');

function UIDebugWindow() {
	this.numberOfMessage = 0 ;
}

UIDebugWindow.prototype.getDebugWindow = function() {
	var debugWindow = document.getElementById('debugMessage');
	if ( debugWindow != null ) {
		debugWindow.style.display = 'block';
		return debugWindow;
	}
	var strPopupWindow = 	
	  '<div class="UIPopupWindow" id="debugMessage" style="width: 250px;">' + 
		'  <div class="VioletDecorator">' + 
		'		<div class="TopLeftCornerDecorator">' +
		'		  <div class="TopRightCornerDecorator">' +
		'		    <div class="TopCenterDecorator">' +
		'          <div class="PopupTitle">Debug Window</div>' +
		'          <div class="CloseButton">' +
		'            <div class="BlueCloseButton16x16Icon"><span></span></div>' +
		'          </div>' +
		'        </div>' +
		'		  </div>' +
		'		</div>' +
		    
		'	  <div class="MiddleLeftSideDecorator">' +
		'      <div class="MiddleRightSideDecorator">' +
		'        <div class="MiddleCenterDecorator">' +
		'					 <div class="DebugMessage" style="height: 400px; width: 230px; overflow: auto;"></div>' +
		'				 </div>' +
		'      </div>' +
		'    </div>' +
		    
		'		<div class="BottomLeftCornerDecorator">' +
		'		  <div class="BottomRightCornerDecorator">' +
		'		    <div class="BottomCenterDecorator"><span></span></div>' +
		'		  </div>' +
		'		</div>' +
	
		'  </div>' +
		'</div>';
	var divObj = document.createElement("div");
	divObj.innerHTML = strPopupWindow;
	debugWindow = divObj.firstChild;
	document.body.appendChild(debugWindow);
	eXo.webui.UIPopupWindow.init('debugMessage');
	eXo.webui.UIPopupWindow.show('debugMessage');
	eXo.webui.UIPopupWindow.setPosition('debugMessage', 2);
	return debugWindow;
}

UIDebugWindow.prototype.appendMessage = function(objText) {
	this.numberOfMessage += 1 ;
	var  debugWindow =  this.getDebugWindow() ;
	var debugObj = eXo.core.DOMUtil.findFirstDescendantByClass(debugWindow, 'div' ,'DebugMessage');
	debugObj.innerHTML += "<div>" + this.numberOfMessage + ") "+ objText + "</div>" ;
}

UIDebugWindow.prototype.clearMessage = function() {
	this.numberOfMessage = 0 ;
	var  debugWindow =  this.getDebugWindow() ;
	var debugObj = eXo.core.DOMUtil.findFirstDescendantByClass(debugWindow, 'div' ,'DebugMessage');
	debugObj.innerHTML = '';
}

eXo.webui.UIDebugWindow = new UIDebugWindow();