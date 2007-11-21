eXo.require('eXo.widget.UIExoWidget');

UICalculatorWidget.prototype = eXo.widget.UIExoWidget;
UICalculatorWidget.prototype.constructor = UICalculatorWidget;

function UICalculatorWidget() {
	this.init("UICalculatorWidget", "calculator");
}

UICalculatorWidget.prototype.padNum = function(obj) {
	var numValue = obj.innerHTML ;
	if(numValue==':') numValue='/' ;
	if(numValue=='x') numValue='*' ;
	var DOMUtil = eXo.core.DOMUtil ;
	var mlCal = DOMUtil.findAncestorByClass(obj, 'MLCalculator') ;
	var mdViewBox = DOMUtil.findFirstDescendantByClass(mlCal,'div','MiddleViewBox') ;
	var tmp = mdViewBox.innerHTML ;
	if(tmp == 0) tmp = numValue ;
	else tmp += numValue ;
	mdViewBox.innerHTML = tmp ;
}

UICalculatorWidget.prototype.clearScreen = function(obj) {
	var DOMUtil = eXo.core.DOMUtil ;
	var mlCal = DOMUtil.findAncestorByClass(obj, 'MLCalculator') ;
	var mdViewBox = DOMUtil.findFirstDescendantByClass(mlCal,'div','MiddleViewBox') ;
	mdViewBox.innerHTML = 0 ;
}

UICalculatorWidget.prototype.calculate = function(obj) {
	var DOMUtil = eXo.core.DOMUtil ;
	var mlCal = DOMUtil.findAncestorByClass(obj, 'MLCalculator') ;
	var mdViewBox = DOMUtil.findFirstDescendantByClass(mlCal,'div','MiddleViewBox') ;
	mdViewBox.innerHTML = eval(mdViewBox.innerHTML) ;
}

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.calculator == null) eXo.widget.web.calculator = {};
eXo.widget.web.calculator.UICalculatorWidget = new UICalculatorWidget()  ;


