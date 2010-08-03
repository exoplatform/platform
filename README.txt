=========================
    eXo Platform 3.0
     3rd August 2010
=========================


* Introduction
--------------

Welcome to eXo Platform 3.0 Beta 2.



* Package Content
-----------------

This release contains:
 - GateIn 3.1 GA
 - WCM 2.1-CR01
 - xCMIS-1.1-Beta1
 - Collaboration 2.1-CR01
 - Knowledge  2.1-CR01
 - Social 1.1-CR01
 - IDE-1.0.0-Beta03
-  Crash 1.0.??



* Build and Packaging Instructions
----------------------------------

Detailed instructions are given here : 
http://wiki-int.exoplatform.org/display/PLF/Building+Platform+3.0

 - Prerequisites : You need Java 6. Make sure your maven settings are up-to-date

 - Check-out the source from  :
   http://svn.exoplatform.org/projects/platform/trunk/
   
 - Go into the main folder, run
   mvn clean install -Ppkg-tomcat,exo-private
   
 - Go into the folder /packaging/pkg/target/tomcat
   Here is your eXo Platform Tomcat distribution



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
 - Fisheye        :  http://fisheye.exoplatform.org/browse/platform

