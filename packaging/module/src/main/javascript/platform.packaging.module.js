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
   var coreVersion = "${org.exoplatform.core.version}";
   var gwtframeworkVersion = "${org.exoplatform.gwtframework.version}";
   var ideallVersion = "${org.exoplatform.ideall.version}";
   var xcmisVersion = "${org.exoplatform.xcmis.version}";

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
   module.extension.webapp = 
      new Project("org.exoplatform.platform", "exo.platform.extension.webapp", "war", module.version).
      // xCMIS dependencies
      addDependency(new Project("org.xcmis", "xcmis-renditions", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-restatom", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-search-model", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-search-parser-cmis", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-search-service", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-spi", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-sp-inmemory", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-sp-jcr-exo", "jar", xcmisVersion)).
      addDependency(new Project("org.apache.abdera", "abdera-client", "jar", "0.4.0-incubating")).
      addDependency(new Project("org.apache.abdera", "abdera-core", "jar", "0.4.0-incubating")).
      addDependency(new Project("org.apache.abdera", "abdera-i18n", "jar", "0.4.0-incubating")).
      addDependency(new Project("org.apache.abdera", "abdera-parser", "jar", "0.4.0-incubating")).
      addDependency(new Project("org.apache.abdera", "abdera-server", "jar", "0.4.0-incubating")).
      addDependency(new Project("org.antlr", "antlr-runtime", "jar", "3.1.3")).
      addDependency(new Project("org.apache.ws.commons.axiom", "axiom-api", "jar", "1.2.5")).
      addDependency(new Project("org.apache.ws.commons.axiom", "axiom-impl", "jar", "1.2.5")).
      addDependency(new Project("jaxen", "jaxen", "jar", "1.1.1")).
      addDependency(new Project("org.apache.lucene", "lucene-regex", "jar", "2.4.1"));
   /*module.extension.config =  new Project("org.exoplatform.platform", "exo.platform.extension.config", "jar", module.version);*/
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
   //module.ideall.extension =
   //    new Project("org.exoplatform.ideall", "exo.ideall.extension.webapp", "war", ideallVersion).
   //     addDependency(new Project("org.exoplatform.ideall", "exo.ideall.extension.config", "jar", ideallVersion));
   //module.ideall.extension.deployName = "ideall-extension";
   
   module.ideall.smartgwt =
       new Project("org.exoplatform.gwt", "exo.gwtframework.smartgwt", "war", gwtframeworkVersion);
   module.ideall.smartgwt.deployName = "SmartGWT";
   
   module.ideall.webapp =
       new Project("org.exoplatform.ideall", "exo.ideall.client", "war", ideallVersion).
        // should be core.version
        addDependency(new Project("org.exoplatform.core", "exo.core.component.script.groovy", "jar", coreVersion)).
        addDependency(module.ideall.smartgwt).
        addDependency(new Project("org.exoplatform.ideall", "exo.ideall.component.gadget", "jar", ideallVersion)).
        addDependency(new Project("org.exoplatform.ideall", "exo.ideall.component.netvibes", "jar", ideallVersion)).
        addDependency(new Project("org.exoplatform.ideall", "exo.ideall.component.commons", "jar", ideallVersion));
   module.ideall.webapp.deployName = "IDEAll";
   
   
   return module;
}
