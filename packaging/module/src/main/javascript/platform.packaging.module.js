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
   var cometVersion = "${org.exoplatform.commons.version}";
   var coreVersion = "${org.exoplatform.core.version}";
   var gwtframeworkVersion = "${org.exoplatform.gwtframework.version}";
   var ideVersion = "${org.exoplatform.ide.version}";
   var xcmisVersion = "${org.xcmis.version}";
   var ecmsVersion = "${org.exoplatform.ecms.version}";
   var crashVersion = "${org.crsh.version}";

   // fck editor required for KS & CS
   module.fck = new Project("org.exoplatform.commons", "exo.platform.commons.fck", "war", cometVersion);
   module.fck.deployName = "fck";
   
   // cometd required by KS & CS
   module.cometd = new Project("org.exoplatform.commons", "exo.platform.commons.comet.webapp", "war", cometVersion).
   addDependency(new Project("org.mortbay.jetty", "cometd-bayeux", "jar", "${org.mortbay.jetty.cometd-bayeux.version}")).
   addDependency(new Project("org.mortbay.jetty", "jetty-util", "jar", "${org.mortbay.jetty.jetty-util.version}")).
   addDependency(new Project("org.mortbay.jetty", "cometd-api", "jar", "${org.mortbay.jetty.cometd-api.version}")).
   addDependency(new Project("org.exoplatform.commons", "exo.platform.commons.comet.service", "jar", cometVersion));  	
   module.cometd.deployName = "cometd";
	
   // main portal container config	
   module.config =  new Project("org.exoplatform.platform", "exo.platform.extension.config", "jar", module.version);
   
   // platform extension
   module.extension = {};
   module.extension.webapp = 
      new Project("org.exoplatform.platform", "exo.platform.extension.webapp", "war", module.version).
      addDependency(new Project("org.exoplatform.platform", "exo.platform.webui.components", "jar", module.version)).
      // xCMIS dependencies
      addDependency(new Project("org.xcmis", "xcmis-renditions", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-restatom", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-search-model", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-search-parser-cmis", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-search-service", "jar", xcmisVersion)).
      addDependency(new Project("org.xcmis", "xcmis-spi", "jar", xcmisVersion)).
      addDependency(new Project("org.apache.tika", "tika-core", "jar", "0.7")).    
      addDependency(new Project("org.apache.tika", "tika-parsers", "jar", "0.7")).
      addDependency(new Project("org.exoplatform.ecms", "exo-ecms-ext-xcmis-sp", "jar", ecmsVersion)).
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
   
   
   module.extension.portlets = {};
   module.extension.portlets.platformNavigation =  new Project("org.exoplatform.platform", "exo.platform.extension.portlets.platformNavigation", "war", module.version);


   // platform commons
   module.component = {};
   module.component.common = new Project("org.exoplatform.platform", "exo.platform.component.common", "jar", module.version);


   // office portal
   module.office = {};
   module.office.webapp =  new Project("org.exoplatform.platform", "exo.platform.office.webapp", "war", module.version).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.office.config", "jar", module.version));
   module.office.webapp.deployName = "office-portal";
   module.office.officeResources =  new Project("org.exoplatform.platform", "exo.platform.office.officeResources", "war", module.version);
      
   
   module.patch = {};
   module.patch.tomcat =
      new Project("org.exoplatform.platform", "exo.platform.server.tomcat.patch", "jar", module.version);
   
   // eXo IDE
   module.ide = {};
   
   module.ide.smartgwt =
       new Project("org.exoplatform.gwt", "exo-gwtframework-smartgwt", "war", gwtframeworkVersion);
   module.ide.smartgwt.deployName = "SmartGWT";
   
   module.ide.webapp =
       new Project("org.exoplatform.ide", "exo-ide-client", "war", ideVersion).
        addDependency(new Project("org.exoplatform.core", "exo.core.component.script.groovy", "jar", coreVersion)).
        addDependency(module.ide.smartgwt).
        addDependency(new Project("org.exoplatform.ide", "exo-ide-module-gadget-server", "jar", ideVersion)).
        addDependency(new Project("org.exoplatform.ide", "exo-ide-module-netvibes-server", "jar", ideVersion)).
        addDependency(new Project("org.exoplatform.ide", "exo-ide-module-groovy-server", "jar", ideVersion)).
        addDependency(new Project("org.exoplatform.ide", "exo-ide-server", "jar", ideVersion)).
        addDependency(new Project("org.apache.commons", "commons-compress", "jar", "1.0"));
   module.ide.webapp.deployName = "IDE";
   
    // acme website
   module.sample = {};
   
   module.sample.acme = {};
   
   module.sample.acme.webapp =  new Project("org.exoplatform.platform", "exo.platform.sample.acme-website.webapp", "war", module.version).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.sample.acme-website.config", "jar", module.version));
   module.sample.acme.webapp.deployName = "acme-website";
   
    // default website
   module.sample.defaultWebsite = {};
   
   module.sample.defaultWebsite.webapp =  new Project("org.exoplatform.platform", "exo.platform.sample.default-website.webapp", "war", module.version).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.sample.default-website.config", "jar", module.version));
   module.sample.defaultWebsite.webapp.deployName = "default-website";
   
    // Crash
   module.crash = {};
   module.crash.webapp = new Project("org.crsh","crsh.core", "war", crashVersion);
   module.crash.webapp.deployName = "crash";

   
   return module;
}
