eXo.require('eXo.widget.UIExoWidget');

UICalculatorWidget.prototype = eXo.widget.UIExoWidget;
UICalculatorWidget.prototype.constructor = UICalculatorWidget;

function UICalculatorWidget() {
	this.init("UICalculatorWidget", "calculator");
}

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.calculator == null) eXo.widget.web.calculator = {};
eXo.widget.web.calculator.UICalculatorWidget = new UICalculatorWidget()  ;


