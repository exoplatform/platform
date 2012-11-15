eXo.require("eXo.projects.Module") ;
eXo.require("eXo.projects.Product") ;

function getProduct(version) {
  var product = new Product();
  
  product.name = "eXoPlatform" ;
  product.portalwar = "portal.war" ;
  product.codeRepo = "platform" ;//module in modules/portal/module.js
  product.serverPluginVersion = "${org.exoplatform.portal.version}"; // was project.version
  product.useWorkflow = false;
  product.useContentvalidation = false;

  //com.sun.xml.stream
  product.sunXmlStreamVersion = "${version.sun.xml.stream}";
  var kernel = Module.GetModule("kernel") ;
  var core = Module.GetModule("core") ;
  var ws = Module.GetModule("ws", {kernel : kernel, core : core});
  var eXoJcr = Module.GetModule("jcr", {kernel : kernel, core : core, ws : ws}) ;
  var portal = Module.GetModule("portal", {kernel : kernel, ws:ws, core : core, eXoJcr : eXoJcr});
  var platform = Module.GetModule("platform", {kernel : kernel, ws:ws, core : core, eXoJcr : eXoJcr});
  var calendar = Module.GetModule("calendar", {portal:portal, ws:ws});
  var FORUM = Module.GetModule("forum", {portal:portal, ws:ws});
  var wiki = Module.GetModule("wiki", {portal:portal, ws:ws});
  var social = Module.GetModule("social", {kernel : kernel, ws:ws, core : core, eXoJcr : eXoJcr, portal:portal});
  var ecms = Module.GetModule("ecms", {kernel : kernel, core : core, ws : ws, eXoJcr : eXoJcr, portal : portal});

  
  /* COMMON - GATEIN */
  product.addDependencies(portal.web.rest) ;
  product.addDependencies(portal.portlet.exoadmin) ;
  product.addDependencies(portal.portlet.web) ;
  product.addDependencies(portal.portlet.dashboard) ;
  product.addDependencies(portal.eXoGadgetServer) ;
  product.addDependencies(portal.eXoGadgets) ;
  product.addDependencies(portal.webui.portal);  
  product.addDependencies(portal.web.eXoResources);
  product.addDependencies(portal.web.portal) ;
  
  portal.starter = new Project("org.exoplatform.portal", "exo.portal.starter.war", "war", portal.version);
  portal.starter.deployName = "starter"; // was zzstarter
  product.addDependencies(portal.starter);  

    
  /* PLATFORM */
  product.addDependencies(platform.fck);
  product.addDependencies(platform.cometd);
  product.addDependencies(platform.config); 
  product.addDependencies(platform.extension.webapp);
  product.addDependencies(platform.extension.resources);
  product.addDependencies(platform.component.common);
  product.addDependencies(platform.extension.portlets.platformNavigation);
  product.addDependencies(platform.common.webui); 
  product.addDependencies(platform.common.resources);
  product.addDependencies(platform.common.extension);

  // crash
  product.addDependencies(platform.crash.webapp);
  
  // default website
  product.addDependencies(platform.sample.defaultWebsite.webapp);

  // acme website
  product.addDependencies(platform.sample.acme.webapp);
  product.addDependencies(platform.sample.acme.resources);
  
  // acme social intranet
  product.addDependencies(platform.sample.acmeIntranet.webapp);
  
    // acme social intranet portlet
  product.addDependencies(platform.sample.acmeIntranet.portlet);
  
  /* IDE */
  product.addDependencies(platform.ide.webapp);
  /* WebOS */
  product.addDependencies(platform.webos.ext);
  /* Gadgets */
  product.addDependencies(platform.sample.GadgetSample.gadgets);
  product.addDependencies(platform.gadgetpack.gadgets);

  /* ECMS */

    product.addDependencies(ecms.portlet.ecmadmin);
    product.addDependencies(ecms.portlet.ecmexplorer);
    product.addDependencies(ecms.gadgets);
    product.addDependencies(ecms.core.war);
    product.addDependencies(ecms.extension.war);
    product.addDependencies(ecms.portlet.webpresentation);
    product.addDependencies(ecms.portlet.websearches);
    product.addDependencies(ecms.portlet.seo);
    product.addDependencies(ecms.web.eXoWCMResources) ;
    product.addDependencies(ecms.waitemplate.war);
    product.addDependencies(ecms.authoring.war);
  
  // rest-ecmdemo.war not deployed  

  /* CALENDAR */
    product.addDependencies(calendar.calendar); // exo.cs.eXoApplication.calendar.service-2.0.0-SNAPSHOT.jar + calendar.war
    product.addDependencies(calendar.common);
    product.addDependencies(calendar.web.resources);
    product.addDependencies(calendar.web.webservice); // exo.cs.web.webservice-2.0.0-SNAPSHOT.jar
    product.addDependencies(calendar.extension.webapp);
   

  /* FORUM */

    product.addDependencies(FORUM.component.common);
    product.addDependencies(FORUM.component.rendering);
    product.addDependencies(FORUM.component.bbcode);
    product.addDependencies(FORUM.extension.webapp);
    product.addDependencies(FORUM.application.common);
    product.addDependencies(FORUM.application.forumGadgets);
    product.addDependencies(FORUM.answer);
    product.addDependencies(FORUM.forum);
    product.addDependencies(FORUM.poll);
    product.addDependencies(FORUM.web.forumResources);

  /* WIKI */

    product.addDependencies(wiki.upgrade);
    product.addDependencies(wiki.rendering);
    product.addDependencies(wiki.wiki);
    product.addDependencies(wiki.extension.webapp);


  /* SOCIAL */
  product.addDependencies(social.component.common); // # exo.social.component.common-1.0.0-GA.jar
  product.addDependencies(social.component.core); // # exo.social.component.core.jar
  product.addDependencies(social.component.service); // # exo.social.component.service.jar
  product.addDependencies(social.component.opensocial); // # exo.social.component.opensocial-1.0.0-GA.jar
  product.addDependencies(social.component.webui); // # exo.social.component.webui.jar
  product.addDependencies(social.webapp.opensocial) ; // social.war
  product.addDependencies(social.webapp.portlet); // social-portlet.war
  product.addDependencies(social.webapp.resources); // social-resources.war
  product.addDependencies(social.extras.feedmash); // # exo.social.extras.feedmash-1.0.0-GA.jar
  product.addDependencies(social.extras.linkComposerPlugin); // #exo.social.extras.link-composer-plugin-1.1.0-SNAPSHOT.jar
  product.addDependencies(social.extension.war) ; // social-ext.war
  
  // integration project
  product.addDependencies(platform.integ.ecmsSocial) ; // integration ecms-social  

  product.addServerPatch("tomcat", platform.patch.tomcat) ;
//  product.addServerPatch("tomcat", portal.server.tomcat.patch) ;
//  product.addServerPatch("tomcat", ks.server.tomcat.patch) ;
//  product.addServerPatch("tomcat", cs.server.tomcat.patch) ;
//  product.addServerPatch("tomcat", social.server.tomcat.patch);
//  product.addServerPatch("tomcat", wcm.server.tomcat.patch) ;


//  product.addServerPatch("jboss",  portal.server.jboss.patch) ;
//  product.addServerPatch("jbossear",  portal.server.jbossear.patch) ;
//  product.addServerPatch("jonas",  portal.server.jonas.patch) ;
//  product.addServerPatch("ear",  portal.server.websphere.patch) ;

	
	// upgrade plugins
  product.addDependencies(platform.upgrade.platform);

  //Platform UI
  //Replace GateIn's jar with platform-ui jar
  product.removeDependency(portal.web.eXoResources);
  product.removeDependency(portal.webui.core);
  product.addDependencies(platform.platformUI.webuiCore);
  product.addDependencies(platform.platformUI.eXoResources);

  /* cleanup duplicated lib */
  product.removeDependency(new Project("commons-httpclient", "commons-httpclient", "jar", "3.0"));
  product.removeDependency(new Project("javax.mail", "mail", "jar", "1.4"));
  product.removeDependency(new Project("commons-beanutils", "commons-beanutils", "jar", "1.6"));
  product.removeDependency(new Project("commons-digester", "commons-digester", "jar", "1.6"));
  product.removeDependency(new Project("xstream", "xstream", "jar", "1.0.2"));
  product.removeDependency(new Project("ical4j", "ical4j", "jar", "0.9.20"));
  product.removeDependency(new Project("commons-lang", "commons-lang", "jar", "2.3"));
  product.removeDependency(new Project("org.codehaus.swizzle", "swizzle-jira", "jar", "1.6.1"));
  
  /* remove extensions config . We don't need them because PLF declares a global container config in exo.platform.config */
  product.removeDependency(new Project("org.exoplatform.ecms", "ecms-packaging-ecmdemo-config", "jar", ecms.version));
  product.removeDependency(new Project("org.exoplatform.ecms", "ecms-packaging-wcm-config", "jar", ecms.version));
  product.removeDependency(new Project("org.exoplatform.social", "social-extension-config", "jar", social.version));
  product.removeDependency(new Project("org.exoplatform.forum", "forum-extension-config", "jar", FORUM.version));
  product.removeDependency(new Project("org.exoplatform.forum", "forum-extension-config", "jar", FORUM.version));
  product.removeDependency(new Project("org.exoplatform.calendar", "calendar-extension-config", "jar", calendar.version));

  product.addDependencies(new Project("commons-httpclient", "commons-httpclient", "jar", "3.1"));
  product.addDependencies(new Project("findbugs", "annotations", "jar", "1.0.0"));
  product.addDependencies(new Project("com.sun.xml.stream", "sjsxp", "jar", product.sunXmlStreamVersion));

  product.module = portal ;
  product.dependencyModule = [kernel, core, ws, eXoJcr, calendar, FORUM, wiki, social, ecms];

  // Use new version of commons-logging override Product.preDeploy()
  product.preDeploy = function() { 
    product.removeDependency(new Project("commons-logging", "commons-logging", "jar", "1.0.4"));
    product.addDependencies(new Project("commons-logging", "commons-logging", "jar", "1.1.1"));
  }

  return product ;
}
