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

   module.portlet = {};

   module.portlet.browser =
   new Project("org.exoplatform.platform", "exo.portal.portlet.browser", "exo-portlet", module.version);

   module.sample = {};
   module.sample.framework =
   new Project("org.exoplatform.portal", "exo.portal.sample.framework", "war", module.version);
   module.sample.framework.deployName = "eXoSampleFramework";

   module.web = {}
   module.web.eXoMacSkin =
   new Project("org.exoplatform.portal", "exo.portal.web.eXoSkinMac", "war", module.version);
   module.web.eXoVistaSkin =
   new Project("org.exoplatform.portal", "exo.portal.web.eXoSkinVista", "war", module.version);

   return module;
}