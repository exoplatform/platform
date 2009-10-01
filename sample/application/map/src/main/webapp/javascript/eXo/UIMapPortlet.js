Function.prototype.bind = function(object) {
  var method = this;
  return function() {
    method.apply(object, arguments);
  }
}

if (!window['eXo']) {
	window['eXo'] = {};
}

//TODO is it the right path for putting the JS of the portlet?
eXo.UIMapPortlet = new function() {
	this.maps = {};

	this.initMap = function(/*String*/ compId) {
		var map = new eXo.UIMapPortlet.Map();
		map.initialize(compId);
	  	parent.window['eXo'].core.Topic.subscribe("/eXo/portlet/map/displayAddress", map.changePlace.bind(map));
		parent.window['eXo'].UIMapPortlet.maps[window.location.hash] = map;
	}
}

eXo.UIMapPortlet.Map = function(){
	var map = null;
	var geocoder = null;
}
	
eXo.UIMapPortlet.Map.prototype.initialize = function() {
 	this.map = new google.maps.Map2(document.getElementById("map"));
	this.map.setCenter(new google.maps.LatLng(37.4419, -122.1419), 15);
    this.map.addControl(new google.maps.SmallMapControl());
	this.map.addControl(new google.maps.MapTypeControl());
}

eXo.UIMapPortlet.Map.prototype.createMarker = function(/*google.maps.Point*/ point,/*String*/ html) {
	var marker = new google.maps.Marker(point);
	google.maps.Event.addListener(marker, "click", function() {
		marker.openInfoWindowHtml(html);
	});
	return marker;
}

eXo.UIMapPortlet.Map.prototype.showAddress = function(/*String*/address, /*String*/message) {
   	if (!this.geocoder) {
		this.geocoder = new google.maps.ClientGeocoder();
	}
   
	this.geocoder.getLatLng(
		address,
		function(point) {
			if (point) {
				this.map.setCenter(point, 15);
				var marker = new google.maps.Marker(point);
				this.map.addOverlay(marker);
				marker.openInfoWindowHtml(message);
				google.maps.Event.addListener(marker, "click", function() {
					marker.openInfoWindowHtml(message);
				});
			}
		}.bind(this)
	);
}

eXo.UIMapPortlet.Map.prototype.changePlace = function(/*Object*/event){
	this.showAddress(event.message.address, event.message.text);	
}