eXo.require("eXo.projects.Module") ;
eXo.require("eXo.projects.Product") ;

function getModule(params) {

  var kernel = params.kernel;
  var core = params.core;
  var eXoPortletContainer = params.eXoPortletContainer;
  var jcr = params.eXoJcr;

  var module = new Module();

  module.version =  "trunk" ;
  module.relativeMavenRepo =  "org/exoplatform/portal" ;
  module.relativeSRCRepo =  "portal/trunk" ;
  module.name =  "portal" ;
    
  module.component = {}
  module.component.jcrext = 
    new Project("org.exoplatform.portal", "exo.portal.component.jcrext", "jar", module.version) ;
  module.component.portal  = 
    new Project("org.exoplatform.portal", "exo.portal.component.portal", "jar", module.version) ;
  module.component.web = 
    new Project("org.exoplatform.portal", "exo.portal.component.web", "jar", module.version) ;
  module.component.applicationRegistry  = 
    new Project("org.exoplatform.portal", "exo.portal.component.application-registry", "jar", module.version).
  addDependency(new Project("com.sun.xml.stream", "sjsxp", "jar", "1.0")) ;
  module.component.resources = 
    new Project("org.exoplatform.portal", "exo.portal.component.resources", "jar", module.version) ;
      
  module.component.xmlParser = 
    new Project("org.exoplatform.portal", "exo.portal.component.xml-parser", "jar", module.version).
    addDependency(new Project("commons-httpclient", "commons-httpclient", "jar", "3.0")).
    addDependency(new Project("commons-codec", "commons-codec", "jar", "1.3"));
     
  module.component.scripting =
    new Project("org.exoplatform.portal", "exo.portal.component.scripting", "jar", module.version).
    addDependency(new Project("rhino", "js", "jar", "1.6R5")) ;

  module.webui = {};
  module.webui.core = 
    new Project("org.exoplatform.portal", "exo.portal.webui.core", "jar", module.version) ;
  module.webui.eXo = 
    new Project("org.exoplatform.portal", "exo.portal.webui.eXo", "jar", module.version) ;

  module.webui.portal = 
    new Project("org.exoplatform.portal", "exo.portal.webui.portal", "jar", module.version).
    addDependency(module.webui.core) .
    addDependency(module.webui.eXo) .
    addDependency(module.component.web).
    addDependency(module.component.jcrext) .
    addDependency(module.component.resources) .
    addDependency(module.component.applicationRegistry) .
    addDependency(module.component.portal).
    addDependency(module.component.xmlParser).
    addDependency(module.component.scripting). 
    
    addDependency(kernel.container) .
    addDependency(kernel.component.common) .
    addDependency(kernel.component.remote) .
    addDependency(kernel.component.cache) .
    addDependency(kernel.component.command) .
    
    addDependency(core.component.database) .
    addDependency(core.component.organization) .
    addDependency(core.component.security) .
    addDependency(core.component.xmlProcessing) .
    addDependency(core.component.documents).
    addDependency(core.component.resources).
    
    addDependency(jcr.services.jcr) .
    
    addDependency(eXoPortletContainer.services.jsr168) ;

  module.portlet = {};
    
  module.portlet.exoadmin = 
    new Project("org.exoplatform.portal", "exo.portal.portlet.exoadmin", "exo-portlet", module.version);
      
  module.portlet.web = 
    new Project("org.exoplatform.portal", "exo.portal.portlet.web", "exo-portlet", module.version);

  module.sample = {};
  module.sample.framework = 
    new Project("org.exoplatform.portal", "exo.portal.sample.framework", "war", module.version);
  module.sample.framework.deployName = "eXoSampleFramework" ;
    
  module.eXoWidget = {};
  module.eXoWidget.web = 
    new Project("org.exoplatform.portal", "exo.portal.eXoWidgetWeb", "war", module.version);
  module.eXoWidget.web.deployName = "eXoWidgetWeb" ;
    
  module.web = {}
  module.web.eXoResources = 
    new Project("org.exoplatform.portal", "exo.portal.web.eXoResources", "war", module.version);
  module.web.eXoMacSkin = 
    new Project("org.exoplatform.portal", "exo.portal.web.eXoMacSkin", "war", module.version);
  module.web.eXoVistaSkin = 
    new Project("org.exoplatform.portal", "exo.portal.web.eXoVistaSkin", "war", module.version);
      
  module.web.portal = 
    new Project("org.exoplatform.portal", "exo.portal.web.portal", "exo-portal", module.version).
    addDependency(module.webui.portal) .
    addDependency(module.web.eXoResources).
    addDependency(module.web.eXoMacSkin).
    addDependency(module.web.eXoVistaSkin).
    addDependency(jcr.frameworks.web).
    addDependency(jcr.frameworks.command) ;
  
  module.server = {}
  
  module.server.tomcat = {}
  module.server.tomcat.patch = 
    new Project("org.exoplatform.portal", "exo.portal.server.tomcat.patch", "jar", module.version);
             
  module.server.jboss = {}
  module.server.jboss.patch = 
    new Project("org.exoplatform.portal", "exo.portal.server.jboss.patch", "jar", module.version);
  module.server.jonas = {}
  module.server.jonas.patch = 
    new Project("org.exoplatform.portal", "exo.portal.server.jonas.patch", "jar", module.version);
  
  return module;
}