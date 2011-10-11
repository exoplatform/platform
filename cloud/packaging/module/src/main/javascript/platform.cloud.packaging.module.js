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
   module.name = "platform.cloud";
   
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
   var cloudVersion = "${org.exoplatform.cloud-management.version}";
   var ecmsVersion = "${org.exoplatform.ecms.version}";
   var crashVersion = "${org.crsh.version}";
   var webosVersion = "${org.exoplatform.webos.version}";
   var tomcatVersion = "${org.apache.tomcat.version}";

   module.patch = {};
   module.patch.tomcat =
      new Project("org.exoplatform.platform", "exo.platform.server.tomcat.patch", "jar", module.version);
   module.patch.tomcatCloud =
      new Project("com.exoplatform.platform.cloud", "exo.platform.cloud.server.tomcat.patch", "jar", module.version);
   
   // eXo Cloud
   module.cloud = {};

   var jmxRemote = new Project("org.apache.tomcat", "tomcat-catalina-jmx-remote", "jar", tomcatVersion);

   module.cloud.cloudAgent =
       new Project("org.exoplatform.cloud-management", "cloud-agent-war", "war", cloudVersion).
        addDependency(jmxRemote);
   module.cloud.cloudAgent.deployName = "cloud-agent"; 

   module.cloud.cloudAdmin =                                
       new Project("org.exoplatform.cloud-management", "cloud-admin-war", "war", cloudVersion).
        addDependency(new Project("ch.qos.logback", "logback-core", "jar", "0.9.20")).
        addDependency(new Project("ch.qos.logback", "logback-classic", "jar", "0.9.20")).
        addDependency(new Project("mx4j", "mx4j-tools", "jar", "3.0.1")).
        addDependency(new Project("commons-io", "commons-io", "jar", "2.0")).
        addDependency(new Project("asm", "asm", "jar", "3.2")).
        addDependency(new Project("asm", "asm-commons", "jar", "3.2")).
        addDependency(new Project("asm", "asm-util", "jar", "3.2")).
        addDependency(new Project("asm", "asm-analysis", "jar", "3.2")).
        addDependency(new Project("org.slf4j", "jcl-over-slf4j", "jar", "1.5.8")).        
        addDependency(new Project("org.slf4j", "jul-to-slf4j", "jar", "1.5.8")).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-agent", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-admin-valve", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-instrument", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-logback-logging", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-multitenancy", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-services-common", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-rest", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-rest-groovy", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-security", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-statistic", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-tomcat-valve", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-multitenant-rest-services", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform.cloud-management", "cloud-instrument-runtime", "jar", cloudVersion)).
        addDependency(new Project("org.exoplatform", "exo-jcr-services", "jar", jcr.version)).
        addDependency(jmxRemote);
   module.cloud.cloudAdmin.deployName = "cloud-admin"; 

   module.cloud.cloudExtension =
       new Project("com.exoplatform.platform.cloud", "exo.platform.cloud.extension.webapp", "war", module.version).
        addDependency(new Project("com.exoplatform.platform.cloud", "exo.platform.cloud.extension.config", "jar", module.version)).
        addDependency(new Project("com.exoplatform.platform.cloud", "exo.platform.cloud.services", "jar", module.version));
   module.cloud.cloudExtension.deployName = "cloud-extension"; 

   return module;
}
