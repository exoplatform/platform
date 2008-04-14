eXo.require('eXo.widget.UIExoWidget');

function UICalculatorWidget() {
	this.init("UICalculatorWidget", "calculator");
	this.Result = 0 ;		// final result
	this.Operator = 0 ; // get operator of calculator, 0: none
	this.Second = 0 ; 	// use for get final operation such as: '... + + - - : :' we only get ':' operator like calculator in window
	this.Ready = 0 ;		// allow use operator to get result such as: 3 + 3 + = 6
	this.Done = 1 ;			// new screen for pad button
	this.Integer ; 			// detemine display value is integer or uninteger
	this.CurrentValue ;	// temp result
}

UICalculatorWidget.inherits(eXo.widget.UIExoWidget);

UICalculatorWidget.prototype.padNum = function(obj) {
	var numValue = obj.innerHTML ;
	var DOMUtil = eXo.core.DOMUtil ;
	var mlCal = DOMUtil.findAncestorByClass(obj, 'MLCalculator') ;
	var mdViewBox = DOMUtil.findFirstDescendantByClass(mlCal,'div','MiddleViewBox') ;
	
	this.CurrentValue = mdViewBox.innerHTML ;
	
	// key's number
	if (numValue.length == 1 && numValue>='0' && numValue<='9') {
		eXo.widget.web.calculator.UICalculatorWidget.setValue('');
		if(this.CurrentValue=='0') this.CurrentValue='';
		this.CurrentValue += numValue;
		mdViewBox.innerHTML = this.CurrentValue;
	}
	// key's '.'
	if(numValue == '.') {
		eXo.widget.web.calculator.UICalculatorWidget.setValue('0');
		if(this.Integer) {
			this.CurrentValue += numValue;
			mdViewBox.innerHTML = this.CurrentValue;
		}
	}
	
	// key's '-/+'
	if(numValue=='-/+') mdViewBox.innerHTML = eval(-this.CurrentValue);
	
	// key's 'C'
	if(numValue=='C') eXo.widget.web.calculator.UICalculatorWidget.resetValue(mdViewBox,0);
	
	// key's 'BackSpace'
	if(numValue == 'BS') {
		this.CurrentValue = this.CurrentValue.substring(0,this.CurrentValue.length - 1) ;
		if(this.CurrentValue == '') this.CurrentValue = 0 ;
		mdViewBox.innerHTML = this.CurrentValue;
	}
	
	// key's 'Sqrt'
	if(numValue == 'sqrt') {
//		this.Result = Math.sqrt(this.CurrentValue);
//		eXo.widget.web.calculator.UICalculatorWidget.resetValue(mdViewBox,this.Result);	
		this.CurrentValue = Math.sqrt(this.CurrentValue) ;
		mdViewBox.innerHTML = this.CurrentValue ;
		this.Done = 1;	
	}
	
	// key's x<sup>2</sup>
	if(numValue == 'x<sup>2</sup>') {
//		this.Result = Math.pow(this.CurrentValue,2);
//		eXo.widget.web.calculator.UICalculatorWidget.resetValue(mdViewBox,this.Result);
		this.CurrentValue = Math.pow(this.CurrentValue,2) ;
		mdViewBox.innerHTML = this.CurrentValue ;
		this.Done = 1;		
	}
	
	// key's '1/x'
	if(numValue == '1/x') {
		//this.Result = eval('1/' + this.CurrentValue) ;
		//eXo.widget.web.calculator.UICalculatorWidget.resetValue(mdViewBox,this.Result);
		this.CurrentValue = eval('1/' + this.CurrentValue) ;
		mdViewBox.innerHTML = this.CurrentValue ;
		this.Done = 1;	
	}
	
	// key's '*' - ':' - '+' - '-'
	if(numValue=='-' || numValue=='+' || numValue==':' || numValue=='x') {
		if(numValue == ':') numValue = '/' ;
		if(numValue == 'x') numValue = '*' ;
		if(this.Second) {	
			this.Operator = numValue ;
		}
		else {
			if(!this.Ready) {
				this.Operator = numValue;
				this.Result = this.CurrentValue;
				this.Ready=1;
			}
			else {
					this.Result = eval(this.Result + this.Operator + this.CurrentValue);
					this.Operator = numValue; mdViewBox.innerHTML = this.Result;
			} this.Second = 1;
		}
	}
	
	// key's '='
	if(numValue=='='&& this.Operator!='0')	{
		var cal = eval(this.Result + this.Operator + this.CurrentValue);
		eXo.widget.web.calculator.UICalculatorWidget.resetValue(mdViewBox,cal);	
	}
	
	// key's '%'
	if(numValue=='%') {
		this.CurrentValue = this.Result * this.CurrentValue / 100 ;
		mdViewBox.innerHTML = this.CurrentValue ;
		this.Done = 1;
	}
}

UICalculatorWidget.prototype.setValue = function(value) {
	this.Integer = 1 ;
	if(this.Second || this.Done) {
		this.Second = 0 ;
		this.Done = 0 ;
		this.CurrentValue = value ;
	}
	for(var i=0; i<this.CurrentValue.length; i++)
		if (this.CurrentValue[i]=='.') this.Integer=0;
}

UICalculatorWidget.prototype.resetValue = function(obj, value) {
	obj.innerHTML = value;
	this.Result = 0, this.Operator = 0, this.Second = 0, this.Ready = 0; this.Done = 1;
}

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.calculator == null) eXo.widget.web.calculator = {};
eXo.widget.web.calculator.UICalculatorWidget = new UICalculatorWidget()  ;


