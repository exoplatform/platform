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

   //TODO versions for gatein components
   
   module.component = {}
   module.component.resources =
   new Project("org.exoplatform.portal", "exo.portal.component.resources", "jar", module.version);

   module.component.common =
   new Project("org.exoplatform.portal", "exo.portal.component.common", "jar", module.version);

   module.component.gifbackport =
   new Project("org.exoplatform.portal", "exo.portal.component.gifbackport", "jar", module.version);

   module.component.pc =
   new Project("org.exoplatform.portal", "exo.portal.component.pc", "jar", module.version).
      addDependency(new Project("javax.portlet", "portlet-api", "jar", "2.0")).
      addDependency(new Project("javax.ccpp", "ccpp", "jar", "1.0")).
      addDependency(new Project("javax.xml.bind", "jaxb-api", "jar", "2.1")).
      addDependency(new Project("org.gatein.pc", "pc-api", "jar", pcVersion)).
      addDependency(new Project("org.gatein.pc", "pc-portlet", "jar", pcVersion)).
      addDependency(new Project("org.gatein.pc", "pc-mc", "jar", pcVersion)).
      addDependency(new Project("org.gatein.pc", "pc-controller", "jar", pcVersion)).
      addDependency(new Project("org.gatein.pc", "pc-federation", "jar", pcVersion)).
      addDependency(new Project("org.gatein.wci", "wci-wci", "jar", wciVersion)).
      addDependency(new Project("org.gatein.wci", "wci-tomcat", "jar", "2.0.0-SNAPSHOT")).
      addDependency(new Project("org.gatein.wci", "wci-exo", "jar", wciVersion)).
      addDependency(new Project("org.gatein.common", "common-common", "jar", commonVersion)).
      addDependency(new Project("log4j", "log4j", "jar", "1.2.14")).
      addDependency(new Project("org.jboss", "jbossxb", "jar", "2.0.1.GA")).
      addDependency(new Project("org.jboss.logging", "jboss-logging-spi", "jar", "2.0.5.GA")).
      addDependency(new Project("org.jboss", "jboss-common-core", "jar", "2.2.9.GA"));


   module.component.wsrp = new Project("org.exoplatform.portal", "exo.portal.component.wsrp", "jar", module.version)
      .addDependency(new Project("org.gatein.wsrp", "wsrp-producer", "war", wsrpVersion))
      .addDependency(new Project("org.gatein.wsrp", "wsrp-producer-lib", "jar",wsrpVersion))
      .addDependency(new Project("org.gatein.wsrp", "wsrp-common", "jar", wsrpVersion))
      .addDependency(new Project("org.gatein.wsrp", "wsrp-wsrp1-ws", "jar", wsrpVersion))
      .addDependency(new Project("org.gatein.wsrp", "wsrp-consumer", "jar", wsrpVersion))
      .addDependency(new Project("org.gatein.wsrp", "wsrp-integration-api", "jar", wsrpVersion));

   module.component.xmlParser =
   new Project("org.exoplatform.portal", "exo.portal.component.xml-parser", "jar", module.version).
      //addDependency(new Project("commons-httpclient", "commons-httpclient", "jar", "3.0")).
      addDependency(new Project("commons-codec", "commons-codec", "jar", "1.3"));

   module.component.scripting =
   new Project("org.exoplatform.portal", "exo.portal.component.scripting", "jar", module.version).
      addDependency(module.component.xmlParser).
      addDependency(new Project("rhino", "js", "jar", "1.6R5")).
      addDependency(new Project("org.codehaus.groovy", "groovy-all", "jar", "1.5.7"));

   module.component.web =
   new Project("org.exoplatform.portal", "exo.portal.component.web", "jar", module.version).
      addDependency(module.component.scripting);

   module.component.portal =
   new Project("org.exoplatform.portal", "exo.portal.component.portal", "jar", module.version).
      addDependency(new Project("org.gatein.mop", "mop-api", "jar", mopVersion)).
      addDependency(new Project("org.gatein.mop", "mop-spi", "jar", mopVersion)).
      addDependency(new Project("org.gatein.mop", "mop-core", "jar", mopVersion)).
      addDependency(new Project("org.chromattic", "chromattic.api", "jar", chromatticVersion)).
      addDependency(new Project("org.chromattic", "chromattic.common", "jar", chromatticVersion)).
      addDependency(new Project("org.chromattic", "chromattic.spi", "jar", chromatticVersion)).
      addDependency(new Project("org.chromattic", "chromattic.core", "jar", chromatticVersion)).
      addDependency(new Project("org.chromattic", "chromattic.apt", "jar", chromatticVersion)).
      addDependency(new Project("org.reflext", "reflext.api", "jar", reflectVersion)).
      addDependency(new Project("org.reflext", "reflext.core", "jar", reflectVersion)).
      addDependency(new Project("org.reflext", "reflext.spi", "jar", reflectVersion)).
      addDependency(new Project("org.reflext", "reflext.jlr", "jar", reflectVersion)).
      addDependency(new Project("org.reflext", "reflext.api", "jar", reflectVersion)).
      addDependency(module.component.web);

   module.component.identity =
   new Project("org.exoplatform.portal", "exo.portal.component.identity", "jar", module.version).
      addDependency(new Project("org.jboss.identity.idm", "idm-core", "jar", idmVersion)).
      addDependency(new Project("org.jboss.identity.idm", "idm-common", "jar", idmVersion)).
      addDependency(new Project("org.jboss.identity.idm", "idm-api", "jar", idmVersion)).
      addDependency(new Project("org.jboss.identity.idm", "idm-spi", "jar", idmVersion)).
      addDependency(new Project("org.jboss.identity.idm", "idm-hibernate", "jar", idmVersion)).
      addDependency(new Project("org.jboss.identity.idm", "idm-ldap", "jar", idmVersion));

   module.component.applicationRegistry =
   new Project("org.exoplatform.portal", "exo.portal.component.application-registry", "jar", module.version).
      addDependency(module.component.portal).
      addDependency(new Project("com.sun.xml.stream", "sjsxp", "jar", "1.0"));

   module.webui = {};
   module.webui.core =
   new Project("org.exoplatform.portal", "exo.portal.webui.core", "jar", module.version).
      addDependency(module.component.web);

   module.webui.eXo =
   new Project("org.exoplatform.portal", "exo.portal.webui.eXo", "jar", module.version).
      addDependency(module.component.applicationRegistry).
      addDependency(module.webui.core);

  //From WS
  ws.frameworks.cometd =
	new Project("org.exoplatform.ws", "exo.ws.frameworks.cometd.webapp", "war", cometVersion).
    addDependency(new Project("org.mortbay.jetty", "cometd-bayeux", "jar", "6.1.11")).
	addDependency(new Project("org.mortbay.jetty", "jetty-util", "jar", "6.1.11")).
	addDependency(new Project("org.mortbay.jetty", "cometd-api", "jar", "0.9.20080221")).
	addDependency(new Project("org.exoplatform.ws", "exo.ws.frameworks.cometd.service", "jar", cometVersion));  
      
      
   module.webui.portal =
   new Project("org.exoplatform.portal", "exo.portal.webui.portal", "jar", module.version).
      addDependency(module.component.common).
      addDependency(module.component.gifbackport).

      addDependency(module.component.resources).
      addDependency(module.component.identity).
      addDependency(module.component.pc).
      addDependency(module.component.wsrp).
      addDependency(module.webui.eXo).

      addDependency(kernel.container).
      addDependency(kernel.component.common).
      addDependency(kernel.component.remote).
      addDependency(kernel.component.cache).
      addDependency(kernel.component.command).

      addDependency(core.component.database).
      addDependency(core.component.organization).
      addDependency(core.component.organization.ldap).
      addDependency(core.component.ldap).
      addDependency(core.component.security.core).
      addDependency(core.component.xmlProcessing).
      addDependency(core.component.documents).

      addDependency(ws.frameworks.cometd).

      addDependency(jcr.services.jcr);

   module.portlet = {};

   module.portlet.exoadmin =
   new Project("org.exoplatform.portal", "exo.portal.portlet.exoadmin", "exo-portlet", module.version);

   module.portlet.web =
   new Project("org.exoplatform.portal", "exo.portal.portlet.web", "exo-portlet", module.version);

   module.portlet.browser =
   new Project("org.exoplatform.platform", "exo.portal.portlet.browser", "exo-portlet", module.version);

   module.portlet.dashboard =
   new Project("org.exoplatform.portal", "exo.portal.portlet.dashboard", "exo-portlet", module.version).
      addDependency(new Project("org.exoplatform.portal", "exo.portal.component.dashboard", "jar", module.version));

   module.sample = {};
   module.sample.framework =
   new Project("org.exoplatform.portal", "exo.portal.sample.framework", "war", module.version);
   module.sample.framework.deployName = "eXoSampleFramework";

   module.eXoGadgetServer =
   new Project("org.exoplatform.portal", "exo.portal.gadgets-server", "war", module.version).
      addDependency(new Project("commons-io", "commons-io", "jar", "1.4")).
      addDependency(new Project("net.oauth", "core", "jar", "20080621")).
      addDependency(new Project("com.google.collections", "google-collections", "jar", "1.0-rc2")).
      addDependency(new Project("com.google.code.guice", "guice", "jar", "2.0")).
      addDependency(new Project("com.google.code.guice", "guice-jmx", "jar", "2.0")).
      addDependency(new Project("commons-lang", "commons-lang", "jar", "2.4")).
      addDependency(new Project("rome", "rome", "jar", "0.9")).
      //addDependency(new Project("org.hamcrest", "hamcrest-all", "jar", "1.1")).
      //addDependency(new Project("nu.validator.htmlparser", "htmlparser", "jar", "1.0.7")).
      //addDependency(new Project("jaxen", "jaxen", "jar", "1.1.1")).
      addDependency(new Project("joda-time", "joda-time", "jar", "1.6")).
      addDependency(new Project("org.json", "json", "jar", "20070829")).
      addDependency(new Project("org.apache.shindig", "shindig-common", "jar", shindigVersion)).
      addDependency(new Project("org.apache.shindig", "shindig-gadgets", "jar", shindigVersion)).
      addDependency(new Project("org.apache.shindig", "shindig-features", "jar", shindigVersion)).
      addDependency(new Project("org.apache.shindig", "shindig-social-api", "jar", shindigVersion)).
      addDependency(new Project("jdom", "jdom", "jar", "1.0")).
      //addDependency(new Project("commons-codec", "commons-codec", "jar", "1.2")).
      addDependency(new Project("commons-httpclient", "commons-httpclient", "jar", "3.1")).
      addDependency(new Project("commons-collections", "commons-collections", "jar", "3.2.1")).
      addDependency(new Project("net.sf.ehcache", "ehcache", "jar", "1.6.0")).
      //addDependency(new Project("net.sf.jsr107cache", "jsr107cache", "jar", "1.0")).
      addDependency(new Project("xml-apis", "xml-apis", "jar", "1.3.04")).
      //addDependency(new Project("org.codehaus.woodstox", "wstx-asl", "jar", "3.2.1")).
      addDependency(new Project("com.ibm.icu", "icu4j", "jar", "3.8")).
      addDependency(new Project("net.sourceforge.nekohtml", "nekohtml", "jar", "1.9.9")).
      //addDependency(new Project("backport-util-concurrent", "backport-util-concurrent", "jar", "3.1")).
      addDependency(new Project("xerces", "xercesImpl", "jar", "2.9.1")).
      addDependency(new Project("com.thoughtworks.xstream", "xstream", "jar", "1.3.1")).
      addDependency(new Project("caja", "caja", "jar", "r3375")).
      addDependency(new Project("caja", "json_simple", "jar", "r1")).
      addDependency(new Project("org.apache.sanselan", "sanselan", "jar", "0.97-incubator")).
      addDependency(new Project("de.odysseus.juel", "juel-api", "jar", "2.1.2")).
      addDependency(new Project("de.odysseus.juel", "juel-impl", "jar", "2.1.2")).
      addDependency(new Project("org.jsecurity", "jsecurity", "jar", "0.9.0")).
      addDependency(new Project("aopalliance", "aopalliance", "jar", "1.0")).
      addDependency(new Project("org.exoplatform.portal", "exo.portal.gadgets-core", "jar", module.version));
   module.eXoGadgetServer.deployName = "eXoGadgetServer";


   module.eXoGadgets = new Project("org.exoplatform.portal", "exo.portal.eXoGadgets", "war", module.version);
   module.eXoGadgets.deployName = "eXoGadgets";

   module.web = {}
   module.web.eXoResources =
   new Project("org.exoplatform.portal", "exo.portal.web.eXoResources", "war", module.version);
 module.web.eXoMacSkin =
 new Project("org.exoplatform.portal", "exo.portal.web.eXoSkinMac", "war", module.version);
 module.web.eXoVistaSkin =
 new Project("org.exoplatform.portal", "exo.portal.web.eXoSkinVista", "war", module.version);
   module.web.rest =
   new Project("org.exoplatform.portal", "exo.portal.web.rest", "war", module.version).
      addDependency(ws.frameworks.servlet);

   module.web.portal =
   new Project("org.exoplatform.portal", "exo.portal.web.portal", "exo-portal", module.version).
      addDependency(jcr.frameworks.web).
      addDependency(jcr.frameworks.command);

   module.server = {}

   module.server.tomcat = {}
   module.server.tomcat.patch =
   new Project("org.exoplatform.portal", "exo.portal.server.tomcat.patch", "jar", module.version);

   module.server.jboss = {}
   module.server.jboss.patch =
   new Project("org.exoplatform.portal", "exo.portal.server.jboss.patch", "jar", module.version);

   module.server.jbossear = {}
   module.server.jbossear.patch =
   new Project("org.exoplatform.portal", "exo.portal.server.jboss.patch-ear", "jar", module.version);

   module.server.jonas = {}
   module.server.jonas.patch =
   new Project("org.exoplatform.portal", "exo.portal.server.jonas.patch", "jar", module.version);

   module.server.websphere = {}
   module.server.websphere.patch =
   new Project("org.exoplatform.portal", "exo.portal.server.websphere.patch", "jar", module.version);

   return module;
}
