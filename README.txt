
Thank you for downloading eXo Platform 4	

Follow the installation procedure and start eXo Platform 4 now!

------------------------------
System requirements
------------------------------

    * CPU:	3 GHz Multi-core recommended
    * Memory:	4GB of RAM (8GB recommended)
    * Disk:	2GB (depending of the amount of data)
    * OS:	Windows or Linux
    * JDK:	Java 6 or 7 (Set the JAVA_HOME environment variable)
    * Browser: 	Google Chrome 25+, Firefox 19+ or Internet Explorer 8+
    * The eXo server will run on port 8080, make sure this port is not currently in use

-------------------------------------
How to start the Platform
-------------------------------------

    * PLF_HOME is the location of the unzipped eXo Platform server.
    * On Windows: Open a DOS prompt command, go to PLF_HOME directory and type the command: "start_eXo.bat"
    * On Linux: Open a terminal, go to PLF_HOME directory and type the command: "./start_eXo.sh"


----------------------------------------------------------
How to access the Platform homepage
----------------------------------------------------------

    * Wait for the server to start. You should see something like this on the console

      INFO  | Server startup in XXXX ms

    * Enter the following URL into your browser's address bar: http://localhost:8080/portal

-------------------------------------
How to install extensions
-------------------------------------

Several extensions are not installed by default in the Express and Enterprise version of eXo Platform 4: 
    * crash	: Common Reusable SHell to interact with the JVM
    * acme (*)	: A demo website built with eXo Platform 4
    * cmis (*)	: Content Management Interoperability Services 
    * ide (*)	: Integrated development environment to develop applications online 
    * wai (*)	: A demo website following Accessibility standards 

On Windows, Open a DOS prompt command, go to PLF_HOME directory and type the command:
    * To install an extension use: extension.bat --install <extension>
    * To install all available extensions use: extension.bat --install all
    * List all available extensions use: extension.bat --list

On Linux: Open a terminal, go to PLF_HOME directory and type the command :
    * To install an extension use: extension.sh --install <extension>
    * To install all available extensions use: extension.sh --install all
    * List all available extensions use: extension.sh --list

(*) only on Express and Enterprise editions

-----------------------
eXo Resources
-----------------------

Community		http://community.exoplatform.com 
Forum			http://forum.exoplatform.com 
Documentation	http://docs.exoplatform.com 
Blog			http://blog.exoplatform.com 
Support		http://support.exoplatform.com 
eXo			http://www.exoplatform.com
Training		http://www.exoplatform.com/company/public/website/services/development/development-training
Consulting		http://www.exoplatform.com/company/public/website/services/development/development-consulting
