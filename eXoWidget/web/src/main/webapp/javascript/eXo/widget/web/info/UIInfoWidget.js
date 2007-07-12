eXo.require('eXo.widget.UIExoWidget');

UIInfoWidget.prototype = eXo.widget.UIExoWidget;
UIInfoWidget.prototype.constructor = UIInfoWidget;
//UIInfoWidget.superclass = eXo.widget.UIExoWidget.prototype;

function UIInfoWidget() {
	this.init("UIInfoWidget", "info");
}

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.info == null) eXo.widget.web.info = {};
eXo.widget.web.info.UIInfoWidget = new UIInfoWidget()  ;
