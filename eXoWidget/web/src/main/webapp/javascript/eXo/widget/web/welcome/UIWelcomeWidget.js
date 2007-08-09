eXo.require('eXo.widget.UIExoWidget');

UIWelcomeWidget.prototype = eXo.widget.UIExoWidget;
UIWelcomeWidget.prototype.constructor = UIWelcomeWidget;

function UIWelcomeWidget() {
	var attrs = new Array("userName");
	this.init("UIWelcomeWidget", "welcome", attrs);
}

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.welcome == null) eXo.widget.web.welcome = {};
eXo.widget.web.welcome.UIWelcomeWidget = new UIWelcomeWidget()  ;
