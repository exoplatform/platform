eXo.require("eXo.projects.Project")  ;
eXo.require("eXo.projects.Product")  ;

if(eXo.module.tools  == null) eXo.load('pom.js', eXo.env.eXoProjectsDir + "/tools/trunk" ) ;
if(eXo.module.kernel == null) eXo.load('pom.js', eXo.env.eXoProjectsDir + "/kernel/trunk" ) ;
if(eXo.module.core   == null) eXo.load('pom.js', eXo.env.eXoProjectsDir + "/core/trunk" ) ;
if(eXo.module.pc     == null) eXo.load('pom.js', eXo.env.eXoProjectsDir + "/portlet-container/branches/2.0" ) ;
if(eXo.module.jcr     == null) eXo.load('pom.js', eXo.env.eXoProjectsDir + "/jcr/trunk" ) ;

function Portal(kernel, core, pc, jcr, version) {
  var portal = this ;
  this.version =  version ;
  this.relativeMavenRepo =  "org/exoplatform/portal" ;
  this.relativeSRCRepo =  "portal/trunk" ;
  this.name =  "portal" ;
  
  this.component = {} ;
  this.component.jcrext = 
    new Project("org.exoplatform.portal", "exo.portal.component.jcrext", "jar", version) ;
  this.component.portal  = 
    new Project("org.exoplatform.portal", "exo.portal.component.portal", "jar", version) ;
  this.component.web = 
    new Project("org.exoplatform.portal", "exo.portal.component.web", "jar", version) ;
  this.component.applicationRegistry  = 
    new Project("org.exoplatform.portal", "exo.portal.component.application-registry", "jar", version) ;
  this.component.resources = 
    new Project("org.exoplatform.portal", "exo.portal.component.resources", "jar", version) ;
    
  this.component.xmlParser = 
    new Project("org.exoplatform.portal", "exo.portal.component.xml-parser", "jar", version).
    addDependency(new Project("commons-httpclient", "commons-httpclient", "jar", "3.0")).
    addDependency(new Project("commons-codec", "commons-codec", "jar", "1.3"));
    
  this.component.scripting =
    new Project("org.exoplatform.portal", "exo.portal.component.scripting", "jar", version).
    addDependency(new Project("rhino", "js", "jar", "1.6R5")) ;

  this.webui = {};
  this.webui.core = 
    new Project("org.exoplatform.portal", "exo.portal.webui.core", "jar", version) ;
  this.webui.eXo = 
    new Project("org.exoplatform.portal", "exo.portal.webui.eXo", "jar", version) ;

  this.webui.portal = 
    new Project("org.exoplatform.portal", "exo.portal.webui.portal", "jar", version).
    addDependency(this.webui.core) .
    addDependency(this.webui.eXo) .
    addDependency(this.component.web).
    addDependency(this.component.jcrext) .
    addDependency(this.component.resources) .
    addDependency(this.component.applicationRegistry) .
    addDependency(this.component.portal). 
    addDependency(this.component.scripting). 
    
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
    addDependency(pc.services.jsr168) ;

  this.portlet = {};
  this.portlet.content =  
    new Project("org.exoplatform.portal", "exo.portal.portlet.content", "exo-portlet", version).
    addDependency(this.component.xmlParser) ;

  portal.portlet.exoadmin = 
    new Project("org.exoplatform.portal", "exo.portal.portlet.exoadmin", "exo-portlet", version);
    
  portal.portlet.site = 
    new Project("org.exoplatform.portal", "exo.portal.portlet.site", "exo-portlet", version);
    
  portal.portlet.web = 
    new Project("org.exoplatform.portal", "exo.portal.portlet.web", "exo-portlet", version);
    
  portal.portlet.test = 
    new Project("org.exoplatform.portal", "exo.portal.portlet.test", "exo-portlet", version);

  
  portal.eXoApplication = {};
  portal.eXoApplication.web = 
    new Project("org.exoplatform.portal", "exo.portal.eXoApplication.web", "war", version);
  portal.eXoApplication.web.deployName = "eXoAppWeb";
    
  portal.sample = {};
  portal.sample.framework = 
    new Project("org.exoplatform.portal", "exo.portal.sample.framework", "war", version);
  portal.sample.framework.deployName = "eXoSampleFramework" ;
  
  portal.eXoWidget = {};
  portal.eXoWidget.web = 
    new Project("org.exoplatform.portal", "exo.portal.eXoWidget.web", "war", version);
  portal.eXoWidget.web.deployName = "eXoWidgetWeb" ;
  
  portal.web = {}
  portal.web.eXoResources = 
    new Project("org.exoplatform.portal", "exo.portal.web.eXoResources", "war", version);
  portal.web.eXoMacSkin = 
    new Project("org.exoplatform.portal", "exo.portal.web.eXoMacSkin", "war", version);
  portal.web.eXoVistaSkin = 
    new Project("org.exoplatform.portal", "exo.portal.web.eXoVistaSkin", "war", version);
    
  portal.web.portal = 
    new Project("org.exoplatform.portal", "exo.portal.web.portal", "exo-portal", version).
    addDependency(portal.webui.portal) .
    addDependency(portal.web.eXoResources).
    addDependency(portal.web.eXoMacSkin).
    addDependency(portal.web.eXoVistaSkin).
    addDependency(jcr.frameworks.web).
    addDependency(jcr.frameworks.command) ;
  
  portal.server = {}
  portal.server.tomcat = {}
  portal.server.tomcat.patch = 
    new Project("org.exoplatform.portal", "exo.portal.server.tomcat.patch", "jar", version);
         
  portal.server.jboss = {}
  portal.server.jboss.patch = 
    new Project("org.exoplatform.portal", "exo.portal.server.jboss.patch", "jar", version);

  portal.server.jonas = {}
  portal.server.jonas.patch = 
    new Project("org.exoplatform.portal", "exo.portal.server.jonas.patch", "jar", version);
}

eXo.module.portal = new Portal(eXo.module.kernel, eXo.module.core, eXo.module.pc, eXo.module.jcr, "2.0") ;

function eXoPortalProduct() { 
  var product = new Product();
  product.name = "eXoPortal" ;
  product.portalwar = "portal.war" ;

  var tool = eXo.module.tools  ;
  var kernel = eXo.module.kernel ;
  var core = eXo.module.core ;
  var eXoPortletContainer = eXo.module.pc ;
  var eXoJcr = eXo.module.jcr ;
  var portal = eXo.module.portal ;

  
  product.addDependencies(portal.web.portal) ;
  product.addDependencies(portal.portlet.content) ;
  product.addDependencies(portal.portlet.exoadmin) ;
  product.addDependencies(portal.portlet.web) ;
  product.addDependencies(portal.portlet.site) ;
  product.addDependencies(portal.portlet.test) ;

  product.addDependencies(portal.eXoApplication.web) ;
  product.addDependencies(portal.eXoWidget.web) ;
  product.addDependencies(portal.sample.framework) ;

  product.addServerPatch("tomcat", portal.server.tomcat.patch) ;
  product.addServerPatch("jboss",  portal.server.jboss.patch) ;
  product.addServerPatch("jonas",  portal.server.jonas.patch) ;

  product.codeRepo = "portal/trunk" ;

  product.dependencyCodeRepos = "tools/trunk,kernel/trunk,core/trunk";

  product.module = portal ;
  product.dependencyModule = [ tool, kernel, core, eXoPortletContainer, eXoJcr];

  return product ;
}

eXo.product = {} ;
eXo.product.eXoProduct = eXoPortalProduct() ;
