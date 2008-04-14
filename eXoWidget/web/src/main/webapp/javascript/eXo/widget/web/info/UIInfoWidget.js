eXo.require('eXo.widget.UIExoWidget');

function UIInfoWidget() {
	this.init("UIInfoWidget", "info");
}

UIInfoWidget.inherits(eXo.widget.UIExoWidget);

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.info == null) eXo.widget.web.info = {};
eXo.widget.web.info.UIInfoWidget = new UIInfoWidget()  ;


