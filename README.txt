=========================
    eXo Platform 3.0
     28st June 2010
=========================


* Introduction
--------------

Welcome to eXo Platform 3.0 Beta 1.

This is the first release of eXo Platform, that will
allow you to discover and test all the Gate In based
extensions together!



* Package Content
-----------------

This release contains:
 - GateIn 3.1 GA
 - WCM 2.1 ??
 - xCMIS ??
 - Collaboration 2.1 ??
 - Knowledge  2.1 ??
 - Social 1.1 ??
 - IDEall ??
-  Crash ??



* Build and Packaging Instructions
----------------------------------

eXo Platform 3.0 needs several steps
to be built and packaged fully. Please follow them
carefully:

 - Check-out the source from 
   http://svn.exoplatform.org/projects/platform/trunk/
   
 - Go into the main folder, run
   mvn clean install -Ppkg-tomcat
   
 - Go into the folder /packaging/pkg/target/tomcat
   Here is your eXo Platform Tomcat distribution

The following steps are needed until some improvements
are made in the extensions.
You can find the detailed process on
http://wiki-int.exoplatform.org/display/PLF/Packaging+Prototype

 - Open /tomcat/webapps/portal.war/WEB-INF/web.xml
   At the end of the filter-mapping section, add:
      <filter-mapping>
      	<filter-name>ThreadLocalSessionProviderInitializedFilter</filter-name>
      	<url-pattern>/*</url-pattern>
      </filter-mapping>
      <filter-mapping>
      	<filter-name>ThreadLocalSessionProviderInitializedFilter</filter-name>
      	<url-pattern>/rest/private/*</url-pattern>
      </filter-mapping>
   

 - Set:
      <repository name="repository" system-workspace="system" default-workspace="collaboration">
   in all repository-configuration.xml of:
   - portal.war/WEB-INF/conf/jcr/
   - ecm-wcm-extension.war/WEB-INF/conf/wcm-extension/jcr
   - ecm-wcm-extension.war/WEB-INF/conf/dms-extension/jcr
   - social-ext.war/WEB-INF/conf/social-ext/jcr
   - ks-extension.war/WEB-INF/conf/ks-extension/jcr
   

TODO : define <repository name="repository" system-workspace="system" default-workspace="collaboration"> in platform-extension


 - In cs-extension.war/WEB-INF/cs-extension/cs/cs-configuration.xml,
   comment out the external-component-plugins for ReminderPeriodJob and PopupReminderPeriodJob
   


* Known Issues
--------------

 - On Linux environments, the starter.war might not start at the right time,
   and prevent the application from running correctly. To circumvent that:
   - Start from a clean, freshly extracted, tomcat
   - Move /tomcat/webapps/starter.war into /tomcat/
   - Start the server, it should be very fast
     E.g. INFO: Server startup in 23771 ms
   - Move back the starter.war in the folder /tomcat/webapps/
   - You should see some activity in the console, the server is started after a few minutes
   
 - To be continued with TC results...
 
 
 
* Other Resources
-----------------

 - Internal Wiki  :  http://wiki-int.exoplatform.org/display/PLF/
 - Jira           :  http://jira.exoplatform.org/browse/PLF
 - SVN            :  http://svn.exoplatform.org/projects/platform/trunk/
 - Intranet       :  N/A

