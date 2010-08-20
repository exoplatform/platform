Thank you for downloading eXo Platform 3.0.

eXo Platform 3.0 is comprised of eXo Core and Extended Services. eXo Core Services, which integrates the GateIn portal framework with eXo Content, eXo IDE and other tools, provides all the functionality expected of a portal, but enhanced with enterprise content management, gadget-based development and deployment with an IDE as a service.  eXo Extended Services further enhance eXo Platform with user experience functionality, including eXo Collaboration, eXo Knowledge and eXo Social.

Follow the installation procedure and start eXo Platform 3.0 now!


------------------
Installation Guide
------------------

System requirements

    * CPU: 2GHz
    * Memory: 2GB of RAM
    * Disk: 200MB
    * OS: Windows, Linux or MAC OS
    * Java 6 (Set the JAVA_HOME environment variable)
    * Browser: Firefox 3+ or Internet Explorer 7+
    * The eXo server will run on port 8080, make sure this port is not currently in use

How to start the Platform

    * PLF_HOME is the location of the unzipped eXo Platform 3.0 server. It can be one of :
          o Tomcat server: bin/tomcat6-bundle
          o JBoss server: bin/jboss5-bundle
    * On Windows: Open a DOS prompt command, go to PLF_HOME/bin directory and type the command:
          o Tomcat server: "gatein.bat run"
          o JBoss server: "run.bat"
    * On Unix/Linux/MacOSX: Open a terminal, go to PLF_HOME/bin directory and type the command :
          o Tomcat server: "./gatein.sh run"
          o JBoss server: "run.sh"

You may need to change the permission of all *.sh files in the /bin directory by using: "chmod +x *.sh".
How to access the Platform homepage

    * Wait for the server to start. You should see something like this on the console

      INFO: Server startup in 353590 ms

    * Enter the following URL into your browser's address bar: http://localhost:8080/portal

How to configure and start the chat server
To use the instant messenging services, you'll need to start a separate chat server.

    * eXo Chat server is an XMPP engine powered by Openfire.
    * Learn more on .
    * Start the chat server: 
          o On Windows: Open a DOS prompt command, go to bin/chat-server/bin and type the command: "java -jar ../lib/startup.jar".
          o On Unix/Linux/MacOSX: Open a terminal, go to bin/chat-server/bin and type the command: "java -jar ../lib/startup.jar".

---------------
Getting Started
---------------

There are 3 sample portals included with eXo Platform:

    * The default Welcome portal is a single-page application with resources to help you explore and begin to use eXo Platform. It also serves as a "blank portal" that can be customized for your own use.
    * From the Welcome portal, you can also access two sample implementations from a fictitious company, Acme Industries:
          o The Acme Website (dev/samples/acme-website/)shows how the powerful content management features of eXo Platform help build a better user experience for customer-facing web apps.
          o The Acme Social Intranet (dev/samples/acme-intranet/) demonstrates eXo Platform's capabilities within an enterprise collaboration environment. It can be easily serve as the starting point for your own Social Intranet application.

Going Further

    * Developers: learn how to build your own portal, gadgets, REST services or eXo-based applications in the Developer Documentation (docs/dev/)
    * Administrators: learn how to install eXo Platform on a server in the Administrator Guide (docs/admin/AdministratorsGuide/) 
    * End Users: learn more about using the features in the User Manuals (docs/user/)

------------------
External Resources
------------------

Support 	http://support.exoplatform.com
Training 	http://www.exoplatform.com/company/public/website/services/development/development-training
Consulting 	http://www.exoplatform.com/company/public/website/services/development/development-consulting
Forum 	http://forum.exoplatform.org
Corporate Website 	http://www.exoplatform.com
Blog 	http://blog.exoplatform.org
Community JIRA 	http://jira.exoplatform.org
