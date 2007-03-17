function Fade() {
	this.object = null ;
};

Fade.prototype.fadeIn = function(objectElement) {
	this.object = objectElement ;
	
	this.object.opacityFadeOut = 0.1 ;
	this.object.filterFadeOut = 10 ;
	
	setTimeout("eXo.animation.Fade.doFadeIn();", 30) ;
};

Fade.prototype.doFadeIn = function() {
	var object = eXo.animation.Fade.object ;
	
	object.style.opacity = object.opacityFadeOut ;
	object.style.filter = "alpha(opacity =" + object.filterFadeOut + ")" ;
	object.opacityFadeOut += 0.1 ;
	object.filterFadeOut += 10 ;
	
	object.style.display = "block" ;
	
	if(object.opacityFadeOut <= 1) {
		setTimeout("eXo.animation.Fade.doFadeIn();", 30) ;
	} else {
		eXo.animation.Fade.object = null ;
	}
};

Fade.prototype.fadeOut = function(objectElement) {
	this.object = objectElement ;
	this.object.opacityFadeOut = 1 ;
	this.object.filterFadeOut = 100 ;
	setTimeout("eXo.animation.Fade.doFadeOut()", 10) ;
};

Fade.prototype.doFadeOut = function() {
	var object = eXo.animation.Fade.object ;
	
	object.style.opacity = object.opacityFadeOut ;
	object.style.filter = "alpha(opacity =" + object.filterFadeOut + ")" ;
	object.opacityFadeOut -= 0.1 ;
	object.filterFadeOut -= 10 ;
	if(object.opacityFadeOut > 0) {
		setTimeout("eXo.animation.Fade.doFadeOut()", 10) ;
	} else {
		object.style.display = "none" ;
		eXo.animation.Fade.object = null ;
	}
};

eXo.animation.Fade = new Fade() ;