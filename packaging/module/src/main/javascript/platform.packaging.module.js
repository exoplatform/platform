eXo.require("eXo.projects.Module");
eXo.require("eXo.projects.Product");

function getModule(params)
{

   var kernel = params.kernel;
   var core = params.core;
   var jcr = params.eXoJcr;
   var ws = params.ws;
   var webos = params.webos;
   var module = new Module();
   module.version = "${project.version}";
   module.relativeMavenRepo = "org/exoplatform/platform";
   module.relativeSRCRepo = "platform";
   module.name = "platform";
   
   var mopVersion =  "${org.gatein.mop.version}";
   var chromatticVersion =  "${org.chromattic.version}";
   var reflectVersion =  "${version.reflect}";
   var idmVersion = "${org.jboss.identity.idm}";
   var pcVersion = "${org.gatein.pc.version}";
   var wciVersion = "${org.gatein.wci.version}";
   var commonVersion = "${org.gatein.common.version}";
   var wsrpVersion = "${org.gatein.wsrp.version}";
   var shindigVersion = "${org.shindig.version}";
   var commonsVersion = "${org.exoplatform.commons.version}";
   var coreVersion = "${org.exoplatform.core.version}";
   var gwtframeworkVersion = "${org.exoplatform.gwtframework.version}";
   var ideVersion = "${org.exoplatform.ide.version}";
   var ecmsVersion = "${org.exoplatform.ecms.version}";
   var crashVersion = "${org.crsh.version}";
   var webosVersion = "${org.exoplatform.webos.version}";
   var integVersion = "${org.exoplatform.integ.version}";
   var socialVersion = "${org.exoplatform.social.version}";
   var csVersion = "${org.exoplatform.cs.version}";
   var ksVersion = "${org.exoplatform.ks.version}";

   // fck editor required for KS & CS
   module.fck = new Project("org.exoplatform.commons", "exo.platform.commons.fck", "war", commonsVersion);
   module.fck.deployName = "fck";
   
   // cometd required by KS & CS
   module.cometd = new Project("org.exoplatform.commons", "exo.platform.commons.comet.webapp", "war", commonsVersion).
   addDependency(new Project("org.mortbay.jetty", "cometd-bayeux", "jar", "${org.mortbay.jetty.cometd-bayeux.version}")).
   addDependency(new Project("org.mortbay.jetty", "jetty-util", "jar", "${org.mortbay.jetty.jetty-util.version}")).
   addDependency(new Project("org.mortbay.jetty", "cometd-api", "jar", "${org.mortbay.jetty.cometd-api.version}")).
   addDependency(new Project("org.exoplatform.commons", "exo.platform.commons.comet.service", "jar", commonsVersion));
   module.cometd.deployName = "cometd";
	
   // main portal container config	
   module.config =  new Project("org.exoplatform.platform", "exo.platform.extension.config", "jar", module.version);
   
   // platform extension
   module.extension = {};
   module.extension.webapp = 
      new Project("org.exoplatform.platform", "exo.platform.extension.webapp", "war", module.version).
      addDependency(new Project("org.exoplatform.platform", "exo.platform.component.webui", "jar", module.version)).
      addDependency(new Project("org.exoplatform.platform", "exo.platform.component.gadgets", "jar", module.version)).
      addDependency(new Project("org.exoplatform.platform", "exo.platform.upgrade.plugins", "jar", module.version)).
      addDependency(new Project("org.exoplatform.platform", "exo.platform.component.organization", "jar", module.version)).
      addDependency(new Project("org.exoplatform.platform", "exo.platform.component.edition.enterprise", "jar", module.version));
   module.extension.webapp.deployName = "platform-extension";
   module.extension.resources = 
      new Project("org.exoplatform.platform", "exo.platform.extension.resources", "war", module.version);
   module.extension.resources.deployName = "eXoPlatformResources";
   
   module.extension.portlets = {};
   module.extension.portlets.platformNavigation =  new Project("org.exoplatform.platform", "exo.platform.extension.portlets.platformNavigation", "war", module.version);


   // platform commons
   module.component = {};
   module.component.common = new Project("org.exoplatform.platform", "exo.platform.component.common", "jar", module.version).
   addDependency(new Project("org.exoplatform.commons", "exo.platform.commons.component.upgrade", "jar", commonsVersion)).
   addDependency(new Project("org.exoplatform.commons", "exo.platform.commons.component.product", "jar", commonsVersion));

   module.common = {};
   module.common.webui = new Project("org.exoplatform.commons", "exo.platform.commons.webui", "jar", commonsVersion);

   module.common.resources = new Project("org.exoplatform.commons", "exo.platform.commons.resources", "war", commonsVersion);
   module.common.resources.deployName = "CommonsResources";

   module.common.extension = new Project("org.exoplatform.commons", "exo.platform.commons.extension.webapp", "war", commonsVersion);
   module.common.extension.deployName = "commons-extension";

   module.patch = {};
   module.patch.tomcat =
      new Project("org.exoplatform.platform", "exo.platform.server.tomcat.patch", "jar", module.version);
   
   // eXo IDE
   module.ide = {};
   
   module.ide.smartgwt =
       new Project("org.exoplatform.gwt", "exo-gwtframework-smartgwt", "war", gwtframeworkVersion);
   module.ide.smartgwt.deployName = "SmartGWT";

   module.ide.webapp =
       new Project("org.exoplatform.ide", "exo-ide-client-gadget", "war", ideVersion).
        addDependency(new Project("org.exoplatform.core", "exo.core.component.script.groovy", "jar", coreVersion)).
        addDependency(module.ide.smartgwt).
        addDependency(new Project("org.exoplatform.ide", "exo-ide-extension-gadget-server", "jar", ideVersion)).
        addDependency(new Project("org.exoplatform.ide", "exo-ide-extension-netvibes-server", "jar", ideVersion)).
        addDependency(new Project("org.exoplatform.ide", "exo-ide-extension-groovy-server", "jar", ideVersion)).
        addDependency(new Project("org.exoplatform.ide", "exo-ide-extension-chromattic-server", "jar", ideVersion)).
        addDependency(new Project("org.exoplatform.ide", "exo-ide-server", "jar", ideVersion)).
        addDependency(new Project("org.chromattic", "chromattic.groovy", "jar", chromatticVersion)).
        addDependency(new Project("org.chromattic", "chromattic.dataobject", "jar", chromatticVersion)).
        addDependency(new Project("org.chromattic", "chromattic.ext", "jar", chromatticVersion)).
        addDependency(new Project("org.apache.commons", "commons-compress", "jar", "1.0"));
   module.ide.webapp.deployName = "IDE";
   
    // acme website
   module.sample = {};
   
   module.sample.acme = {};
   
   module.sample.acme.webapp =  new Project("org.exoplatform.platform", "exo.platform.sample.acme-website.webapp", "war", module.version).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.sample.acme-website.component.file-explorer", "jar", module.version)).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.sample.acme-website.component.navigation-rest", "jar", module.version)).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.sample.acme-website.config", "jar", module.version));
   module.sample.acme.webapp.deployName = "acme-website";
   
   module.sample.acme.resources =  new Project("org.exoplatform.platform", "exo.platform.sample.acme-website.resources", "war", module.version);
   module.sample.acme.resources.deployName = "acme-websiteResources";
   
   // acme social intranet
   module.sample.acmeIntranet = {};
   
   module.sample.acmeIntranet.webapp =  new Project("org.exoplatform.platform", "exo.platform.sample.acme-intranet.webapp", "war", module.version).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.sample.acme-intranet.config", "jar", module.version));
   module.sample.acmeIntranet.webapp.deployName = "acme-intranet";
   
   module.sample.acmeIntranet.portlet =  new Project("org.exoplatform.platform", "exo.platform.sample.acme-intranet.portlet", "war", module.version);
   module.sample.acmeIntranet.portlet.deployName = "acme-intranet-portlet";
   
    // default website
   module.sample.defaultWebsite = {};
   
   module.sample.defaultWebsite.webapp =  new Project("org.exoplatform.platform", "exo.platform.sample.default-website.webapp", "war", module.version).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.sample.default-website.config", "jar", module.version));
   module.sample.defaultWebsite.webapp.deployName = "default-website";
   
    // Crash
   module.crash = {};
   module.crash.webapp = new Project("org.crsh","crsh.jcr.exo", "war", crashVersion);
   module.crash.webapp.deployName = "crash";
   
   // eXo WebOS
   module.webos = {};
   
   module.webos.webosadmin =
       new Project("org.exoplatform.webos", "exo.webos.portlet.webosadmin", "war", webosVersion);
   module.webos.webosadmin.deployName = "webosadmin";
   module.webos.webosResources =
       new Project("org.exoplatform.webos", "exo.webos.web.webosResources", "war", webosVersion);
   module.webos.webosResources.deployName = "webosResources";   
   module.webos.ext =
       new Project("org.exoplatform.webos", "exo.webos.extension.war", "war", webosVersion).
        addDependency(new Project("org.exoplatform.webos", "exo.webos.component.web", "jar", webosVersion)).
        addDependency(new Project("org.exoplatform.webos", "exo.webos.webui.webos", "jar", webosVersion)).
        addDependency(module.webos.webosadmin).
        addDependency(module.webos.webosResources);
   module.webos.ext.deployName = "webos-ext";
   
   // gadgets
   module.sample.GadgetSample = {};
   
   module.sample.GadgetSample.resources = 
        new Project("org.exoplatform.platform", "exo.platform.sample.gadgets-sample.exo-gadget-resources", "war", module.version);
   module.sample.GadgetSample.resources.deployName = "exo-gadget-resources";

   module.sample.GadgetSample.gadgets =  new Project("org.exoplatform.platform", "exo.platform.sample.gadgets-sample.gadgets", "war", module.version).
	   addDependency(new Project("org.exoplatform.platform", "exo.platform.sample.gadgets-sample.config", "jar", module.version)).
           addDependency(new Project("org.exoplatform.platform", "exo.platform.sample.gadgets-sample.service", "jar", module.version)).
           addDependency(module.sample.GadgetSample.resources);
   module.sample.GadgetSample.gadgets.deployName = "intranet-gadget";

   // gadget pack
   module.gadgetpack = {};
   module.gadgetpack.config = new Project("org.exoplatform.platform", "gadget-pack-config", "jar", module.version);
   module.gadgetpack.config.deployName = "gadget-pack-config";

   module.gadgetpack.services = new Project("org.exoplatform.platform", "gadget-pack-services", "jar", module.version);
   module.gadgetpack.services.deployName = "gadget-pack-services";

   module.gadgetpack.gadgets = new Project("org.exoplatform.platform", "gadget-pack", "war", module.version).
      addDependency(module.gadgetpack.config).
      addDependency(module.gadgetpack.services);
   module.gadgetpack.gadgets.deployName = "gadget-pack";
   
   // integration project
   module.integ = {};
   module.integ.ecmsSocial = new Project("org.exoplatform.integration", "integ-ecms-social", "jar", integVersion).
   addDependency(new Project("org.exoplatform.integration", "integ-ks-social", "jar", integVersion)).
   addDependency(new Project("org.exoplatform.integration", "integ-cs-social", "jar", integVersion)).
   addDependency(new Project("org.exoplatform.integration", "integ-social-ecms", "jar", integVersion));

   // upgrade plugins
   module.upgrade = {};
   module.upgrade.platform = new Project("org.exoplatform.platform","exo.platform.upgrade.plugins","jar", module.version).
   addDependency(new Project("org.exoplatform.commons","exo.platform.commons.component.upgrade","jar", commonsVersion)).
   addDependency(new Project("org.exoplatform.social","exo.social.extras.migration","jar", socialVersion)).
   addDependency(new Project("org.exoplatform.social","exo.social.extras.updater","jar", socialVersion)).
   addDependency(new Project("org.exoplatform.ecms","exo-ecms-upgrade-voting-nodetype","jar", ecmsVersion)).
   addDependency(new Project("org.exoplatform.ecms","exo-ecms-upgrade-thumbnails","jar", ecmsVersion)).
   addDependency(new Project("org.exoplatform.ecms","exo-ecms-upgrade-favorite","jar", ecmsVersion)).
   addDependency(new Project("org.exoplatform.ecms","exo-ecms-upgrade-templates","jar", ecmsVersion)).
   addDependency(new Project("org.exoplatform.ecms","exo-ecms-upgrade-portlet-preferences","jar", ecmsVersion)).
   addDependency(new Project("org.exoplatform.ecms","exo-ecms-upgrade-publication-publication-nodetype","jar", ecmsVersion)).
   addDependency(new Project("org.exoplatform.ecms","exo-ecms-upgrade-drive-data","jar", ecmsVersion)).
   addDependency(new Project("org.exoplatform.cs","exo.cs.component.upgrade","jar", csVersion)).
   addDependency(new Project("org.exoplatform.ks","exo.ks.component.upgrade","jar", ksVersion));
   
   return module;
}
