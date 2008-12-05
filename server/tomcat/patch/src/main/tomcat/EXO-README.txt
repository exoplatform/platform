FOR ALL THE TECHNICAL DOCUMENTATION :
http://docs.exoplatform.org


WARNING: eXo Enterprise WebOS is still under development. The HSQLDB database is currently
used to persist information. You actually have to remove the \tmp\hsql (in Windows) or
/tmp/hsql (in Unix) directory when upgrading from one pioneer build to another. Indeed,
database schemas are not stabilized yet. You will no longer need to do this operation as
soon as we switch to Derby (very soon, hopefully :-) ).



1) How to run Tomcat
 * On the Windows platform

   Open a DOS prompt command, go to exo-tomcat/bin and type the command eXo.bat run

 * On Unix/linux/cygwin

   Open a terminal, go to exo-tomcat/bin and type the command ./eXo.sh run
   You may need to change the permission of all *.sh files in the exo-tomcat/bin dir by using:
     chmod +x *.sh

 For both OS environments, you need to set the JAVA_HOME variable.

2) How to access the eXo Portal

 * Enter one of the following addresses into your browser address bar:
   
    http://localhost:8080/portal
    http://localhost:8080/portal/public/classic


 You can log into the portal with the following accounts: root, john, marry, demo. 
 All those accounts have the default password "exo".

For more documentation and latest updated news, please visit our website www.exoplatform.com.
If you have questions, please send a mail to the list exoplatform@objectweb.org.


Thank your for using eXo Platform products !
The eXo Platform team.
