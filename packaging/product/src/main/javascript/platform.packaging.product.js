eXo.require("eXo.projects.Module") ;
eXo.require("eXo.projects.Product") ;

function getProduct(version) {
  var product = new Product();
  
  product.name = "eXoPlatform" ;
  product.portalwar = "portal.war" ;
  product.codeRepo = "platform" ;//module in modules/portal/module.js
  product.serverPluginVersion = "${org.exoplatform.portal.version}"; // was project.version
  product.useWorkflow = true;
  
  // all WCM ext. have the same version number
  product.workflowVersion = "${org.exoplatform.ecms.version}" ;
  product.contentvalidationVersion = "${org.exoplatform.ecms.version}";
  product.workflowJbpmVersion = "${org.jbpm.jbpm3}";
  product.workflowBonitaVersion = "${bonita.version}";


  var kernel = Module.GetModule("kernel") ;
  var core = Module.GetModule("core") ;
  var ws = Module.GetModule("ws", {kernel : kernel, core : core});
  var eXoJcr = Module.GetModule("jcr", {kernel : kernel, core : core, ws : ws}) ;
  var portal = Module.GetModule("portal", {kernel : kernel, ws:ws, core : core, eXoJcr : eXoJcr});
  var platform = Module.GetModule("platform", {kernel : kernel, ws:ws, core : core, eXoJcr : eXoJcr});
  var cs = Module.GetModule("cs", {portal:portal, ws:ws});
  var ks = Module.GetModule("ks", {portal:portal, ws:ws});
  var social = Module.GetModule("social", {kernel : kernel, ws:ws, core : core, eXoJcr : eXoJcr, portal:portal}); 
  var workflow = Module.GetModule("workflow", {kernel : kernel, core : core, ws : ws, eXoJcr : eXoJcr, portal : portal});
  var dms = Module.GetModule("dms", {kernel : kernel, core : core, ws : ws, eXoJcr : eXoJcr, portal : portal});
  var wcm = Module.GetModule("wcm", {kernel : kernel, core : core, ws : ws, eXoJcr : eXoJcr, portal : portal, dms : dms});


  
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
//  product.addDependencies(platform.fck);
  product.addDependencies(platform.cometd);
  product.addDependencies(platform.config);
  product.addDependencies(platform.extension.webapp);
  product.addDependencies(platform.component.common);
  product.addDependencies(platform.office.webapp);
  product.addDependencies(platform.office.officeResources);
  product.addDependencies(platform.office.portlets.construction);
  
  /* IDEALL - waiting for availability in maven repo */
/*  product.addDependencies(platform.ideall.webapp);
  product.addDependencies(platform.ideall.extension); */

  
  /* ECMS */
  product.addDependencies(workflow.web.eXoWorkflowResources);
  product.addDependencies(workflow.web.eXoStaticResources) ;
  product.addDependencies(workflow.portlet.workflow);
  product.addDependencies(workflow.extension.webapp);

  product.addDependencies(dms.web.eXoDMSResources);
  product.addDependencies(dms.portlet.ecmadmin);
  product.addDependencies(dms.portlet.ecmexplorer);
  product.addDependencies(dms.portlet.ecmbrowsecontent);
  product.addDependencies(dms.gadgets);

  product.addDependencies(wcm.extension.war);
  product.addDependencies(wcm.portlet.webpresentation);
  product.addDependencies(wcm.portlet.websearches); 
  product.addDependencies(wcm.portlet.newsletter); 
  product.addDependencies(wcm.portlet.formgenerator);
  product.addDependencies(wcm.web.eXoWCMResources) ;
  product.addDependencies(wcm.web.eXoStaticResources) ;
  // rest-ecmdemo.war not deployed

  /* CS* */
  product.addDependencies(cs.eXoApplication.calendar); // exo.cs.eXoApplication.calendar.service-2.0.0-SNAPSHOT.jar + calendar.war
  product.addDependencies(cs.eXoApplication.contact); // exo.cs.eXoApplication.contact.service-2.0.0-SNAPSHOT.jar + contact.war
  product.addDependencies(cs.eXoApplication.mail); // exo.cs.eXoApplication.mail.service-2.0.0-SNAPSHOT.jar + mail.war
  product.addDependencies(cs.eXoApplication.chat); // exo.cs.eXoApplication.chat.service-2.0.0-SNAPSHOT.jar + chat.war + exo.cs.eXoApplication.organization.client.openfire-2.0.0-SNAPSHOT.jar + exo.cs.eXoApplication.organization.service-2.0.0-SNAPSHOT.jar
  product.addDependencies(cs.eXoApplication.chatbar); // chatbar.war
  product.addDependencies(cs.eXoApplication.content); // exo.cs.eXoApplication.content.service-2.0.0-SNAPSHOT.jar
  product.addDependencies(cs.web.csResources); // csResources.war
  product.addDependencies(cs.web.webservice); // exo.cs.web.webservice-2.0.0-SNAPSHOT.jar
  product.addDependencies(cs.extension.webapp); // exo.cs.extension.config-2.0.0-SNAPSHOT.jar + cs-extension.war

  /* KS */
  product.addDependencies(ks.component.common); // exo.ks.component.common-2.0.0-GA.jar
  product.addDependencies(ks.component.rendering); // exo.ks.component.rendering-2.0.0-GA.jar
  product.addDependencies(ks.component.bbcode); // exo.ks.component.bbcode-2.0.0-GA.jar
  product.addDependencies(ks.eXoApplication.common); // exo.ks.eXoApplication.common-2.0.0-GA.jar
  product.addDependencies(ks.eXoApplication.faq); // exo.ks.eXoApplication.faq.service-2.0.0-GA.jar + faq.war
  product.addDependencies(ks.eXoApplication.forum); // exo.ks.eXoApplication.forum.service-2.0.0-GA.jar + forum.war
  product.addDependencies(ks.web.ksResources); // ksResources.war
  product.addDependencies(ks.extension.webapp); // ks-extension.war
  product.addDependencies(ks.eXoApplication.poll); // poll.war	

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
  product.addDependencies(social.extension.war) ; // social-ext.war

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



  /* cleanup duplicated lib */
  product.removeDependency(new Project("commons-httpclient", "commons-httpclient", "jar", "3.0"));
  product.removeDependency(new Project("commons-collections", "commons-collections", "jar", "3.1"));
  product.removeDependency(new Project("javax.mail", "mail", "jar", "1.4"));
  product.removeDependency(new Project("commons-beanutils", "commons-beanutils", "jar", "1.6"));
  product.removeDependency(new Project("commons-collections", "commons-collections", "jar", "2.1"));
  product.removeDependency(new Project("commons-collections", "commons-collections", "jar", "3.1"));
  product.removeDependency(new Project("commons-digester", "commons-digester", "jar", "1.6"));
  product.removeDependency(new Project("xstream", "xstream", "jar", "1.0.2"));
  product.removeDependency(new Project("commons-lang", "commons-lang", "jar", "2.3"));
  
  /* remove extensions config . We don't need them because PLF declares a global container config in exo.platform.config */
  product.removeDependency(new Project("org.exoplatform.ecms", "exo-ecms-packaging-ecmdemo-config", "jar", wcm.version));
  product.removeDependency(new Project("org.exoplatform.ecms", "exo-ecms-packaging-wcm-config", "jar", wcm.version));
  product.removeDependency(new Project("org.exoplatform.ecms", "exo-ecms-packaging-workflow-config", "jar", wcm.version));
  product.removeDependency(new Project("org.exoplatform.social", "exo.social.extension.config", "jar", social.version));
  product.removeDependency(new Project("org.exoplatform.ks", "exo.ks.extension.config", "jar", ks.version));
  product.removeDependency(new Project("org.exoplatform.cs", "exo.cs.extension.config", "jar", cs.version));

  product.addDependencies(new Project("commons-beanutils", "commons-beanutils", "jar", "1.7.0"));
  product.addDependencies(new Project("commons-beanutils", "commons-beanutils-core", "jar", "1.7.0"));
  product.addDependencies(new Project("commons-digester", "commons-digester", "jar", "1.7"));
  product.addDependencies(new Project("commons-httpclient", "commons-httpclient", "jar", "3.1"));
  product.addDependencies(new Project("findbugs", "annotations", "jar", "1.0.0"));


  product.module = portal ;
  product.dependencyModule = [kernel, core, ws, eXoJcr, cs, ks, social, workflow, dms, wcm];

  // Use new version of commons-logging override Product.preDeploy()
  product.preDeploy = function() { 
	  product.removeDependency(new Project("commons-logging", "commons-logging", "jar", "1.0.4"));
	  product.addDependencies(new Project("commons-logging", "commons-logging", "jar", "1.1.1"));
  }


  return product ;
}
