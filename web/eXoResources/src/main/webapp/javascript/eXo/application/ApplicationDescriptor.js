function ApplicationDescriptor(appId, application) {
	this.appId = appId ;
	this.application = application ;
} ;

/**
 * This method  should  return a window block, the  root node should have the property
 * applicationDescriptor
 */
ApplicationDescriptor.prototype.createApplication = function() {
	return this.application.createApplicationInstance(this) ;
} ;

/**
 * This method  should remove the window  block and destroy all the resources that relate  to
 * the application instance
 */
ApplicationDescriptor.prototype.destroyApplication = function() {
	return this.application.destroyApplicationInstance(this) ;
} ;

eXo.application.ApplicationDescriptor = ApplicationDescriptor.prototype.constructor ;