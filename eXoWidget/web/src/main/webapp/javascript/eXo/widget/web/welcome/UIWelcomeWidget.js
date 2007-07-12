eXo.require('eXo.widget.UIExoWidget');

UIWelcomeWidget.prototype = eXo.widget.UIExoWidget;
UIWelcomeWidget.prototype.constructor = UIWelcomeWidget;

function UIWelcomeWidget() {
	this.init("UIWelcomeWidget", "welcome");
}

UIWelcomeWidget.prototype.createAppDescriptor = function(appDescriptor, appElement) {
	appDescriptor.widget = {
		positionX : appElement.getAttribute('posX'),
		positionY : appElement.getAttribute('posY'),
		zIndex : appElement.getAttribute('zIndex'),
		
		uiWidget : {
			temporaty : appElement,
			appId : appElement.getAttribute('applicationId'),
			userName : appElement.getAttribute('userName')
		}
	};
}

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.welcome == null) eXo.widget.web.welcome = {};
eXo.widget.web.welcome.UIWelcomeWidget = new UIWelcomeWidget()  ;
