function WidgetDescriptor(widgetId, widget) {
	this.widgetId = widgetId ;
	this.widget = widget ;
} ;

WidgetDescriptor.prototype.createWidget = function() {
	return this.widget.createWidgetInstance(this) ;
} ;

WidgetDescriptor.prototype.destroyApplication = function() {
	return this.widget.destroyWidgetInstance(this) ;
} ;

eXo.widgets.WidgetDescriptor = WidgetDescriptor.prototype.constructor ;