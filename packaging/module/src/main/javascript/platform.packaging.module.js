eXo.require("eXo.projects.Module");
eXo.require("eXo.projects.Product");

function getModule(params)
{

   var kernel = params.kernel;
   var core = params.core;
   var jcr = params.eXoJcr;
   var ws = params.ws;
   var module = new Module();
   module.version = "${project.version}";
   module.relativeMavenRepo = "org/exoplatform/platform";
   module.relativeSRCRepo = "platform";
   module.name = "platform";
   
   var mopVersion =  "${org.gatein.mop.version}";
   var chromatticVersion =  "${version.chromattic}";
   var reflectVersion =  "${version.reflect}";
   var idmVersion = "${org.jboss.identity.idm}";
   var pcVersion = "${org.gatein.pc.version}";
   var wciVersion = "${org.gatein.wci.version}";
   var commonVersion = "${org.gatein.common.version}";
   var wsrpVersion = "${org.gatein.wsrp.version}";
   var shindigVersion = "${org.shindig.version}";
   var cometVersion = "${org.exoplatform.cometd.version}";
   var ideallVersion = "${org.exoplatform.ideall.version}";

   // fck editor required for KS & CS
   module.fck = new Project("org.exoplatform.platform", "exo.platform.web.fck", "war", module.version);
   module.fck.deployName = "fck";
   
   // cometd required by KS & CS
   module.cometd = new Project("org.exoplatform.platform", "exo.platform.commons.comet.webapp", "war", cometVersion).
   addDependency(new Project("org.mortbay.jetty", "cometd-bayeux", "jar", "${org.mortbay.jetty.cometd-bayeux.version}")).
   addDependency(new Project("org.mortbay.jetty", "jetty-util", "jar", "${org.mortbay.jetty.jetty-util.version}")).
   addDependency(new Project("org.mortbay.jetty", "cometd-api", "jar", "${org.mortbay.jetty.cometd-api.version}")).
   addDependency(new Project("org.exoplatform.platform", "exo.platform.commons.comet.service", "jar", cometVersion));  	
   module.cometd.deployName = "cometd";
	
   // main portal container config	
   module.config =  new Project("org.exoplatform.platform", "exo.platform.extension.config", "jar", module.version);
   
   // platform extension
   module.extension = {};
   module.extension.webapp =  new Project("org.exoplatform.platform", "exo.platform.extension.webapp", "war", module.version);
   module.extension.config =  new Project("org.exoplatform.platform", "exo.platform.extension.config", "jar", module.version);
   module.extension.webapp.deployName = "platform-extension";

   // platform commons
   module.component = {};
   module.component.common = new Project("org.exoplatform.platform", "exo.platform.component.common", "jar", module.version);

   

   // office portal
   module.office = {};
   module.office.webapp =  new Project("org.exoplatform.platform", "exo.platform.office.webapp", "war", module.version).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.office.config", "jar", module.version));
   module.office.webapp.deployName = "office-portal";
   module.office.officeResources =  new Project("org.exoplatform.platform", "exo.platform.office.officeResources", "war", module.version);
   
   module.office.portlets = {};
   module.office.portlets.construction =  new Project("org.exoplatform.platform", "exo.platform.office.portlets.construction", "war", module.version);
   
   
   module.patch = {};
   module.patch.tomcat =
      new Project("org.exoplatform.platform", "exo.platform.server.tomcat.patch", "jar", module.version);
   
   // IDEAll
   module.ideall = {};
   module.ideall.extension =
       new Project("org.exoplatform.ideall", "exo.ideall.extension.webapp", "war", ideallVersion).
        addDependency(new Project("org.exoplatform.ideall", "exo.ideall.extension.config", "jar", ideallVersion));
   module.ideall.extension.deployName = "ideall-extension";
   
   module.ideall.smartgwt =
       // should be gwt.version
       new Project("org.exoplatform.gwt", "exo.gwtframework.smartgwt", "war", "1.0-Beta04");
   module.ideall.smartgwt.deployName = "SmartGWT";
   
   module.ideall.webapp =
       new Project("org.exoplatform.ideall", "exo.ideall.client", "war", ideallVersion).
        // should be core.version
        addDependency(new Project("org.exoplatform.core", "exo.core.component.script.groovy", "jar", "2.3.2-GA")).
        addDependency(module.ideall.smartgwt).
        addDependency(new Project("org.exoplatform.ideall", "exo.ideall.component.gadget", "jar", ideallVersion)).
        addDependency(new Project("org.exoplatform.ideall", "exo.ideall.component.netvibes", "jar", ideallVersion)).
        addDependency(new Project("org.exoplatform.ideall", "exo.ideall.component.commons", "jar", ideallVersion));
   module.ideall.webapp.deployName = "IDEAll";
   
   
   return module;
}
