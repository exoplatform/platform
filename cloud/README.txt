eXo Platform Cloud 3.5
======================

Platform Cloud based on eXo Cloud 1.1 and consists of:
* GateIn extension. All Cloud configuration are in this extension WAR.
* Server patches.
* eXo build packaging modules (copy of similar ones from Platform packaging).

To create an assembly of Platform Cloud use Maven cloud profile: 

mvn clean install -Pexo-private,cloud

After successful build you can start Tomcat server (from packaging/pkg/target/tomcat) with start_eXo command.
This command enables eXo Kernel profile "cloud" as by default. If you use another profiles don't forget add "cloud" to them.


eXo Platform team.