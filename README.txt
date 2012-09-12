Welcome to Platform
==================

This document explains how to build and package Platform bundles with Tomcat or JBoss

Prerequisites
=============

- Java Development Kit 1.6
- Recent Git client
- Recent Maven 3
- The eXo server will run on port 8080, make sure this port is not currently in use

Build configuration
===================

1) Profile configuration

Platform build uses several profiles to configure which packaging  to be generated

When no profile is specified it will build only entreprise Tomcat bundle.

The various profiles used inside platform are :

- pkg-tomcat     : generate the tomcat entreprise bundle.
- pkg-jboss      : generate the jboss eap entreprise bundle
- pkg-community  : generate the tomcat community bundle
- pkg-trial      : generate the tomcat trial bundle
- distrib        : generate documentations + EAR platform

2) Database configuration

By default the build uses a HSQLDB database. However, it is possible to use MySQL5, thanks to mysql profile.


Build instructions
==================

1) Clone Platform
--------------------------

git clone git@github.com:exodev/platform.git
cd platform

2) Prepare containers to use for packaging
------------------------------------------

Create a directory on your disk that will contain specific versions of JBoss AS, Tomcat, used as a packaging server.

Let’s refer to this directory as SERVERS_DIR.

2) Build and package platform
----------------------------------

You can build platform without packaging (only entreprise Tomcat bundle)it by using the following command:

mvn clean install -Dmaven.test.skip=true

But that's only usable for development since in order to be able to run platform you have to package it.

Platform can be packaged with different web / application servers. The specific server to use is selected by using an appropriate profile.

  Packaging with JBoss-EAP -5.1.0.GA
  --------------------------------

If you don’t have an existing JBoss AS distribution, the build can automatically download it for you.

Issue the following command:

mvn install -Ppkg-jboss -Dmaven.test.skip=true -Ddownload

If you have an existing JBoss-EAP distribution unpack it into SERVERS_DIR directory so that you get SERVERS_DIR/jboss-EAP-XYZ directory.

In this case you can issue the following command:

mvn install -Ppkg-jboss -Dmaven.test.skip=true -Dservers.dir=$SERVERS_DIR

The packaged Platform is available in packaging/jboss-bundle/target/

To start it, go to jboss directory, and run 'bin/run.sh' ('bin\run.bat' on Windows).

Access the portal at: http://localhost:8080/portal


  Packaging Community bundle with Tomcat 6.x.x
  ---------------------------

If you don’t have an existing Tomcat 6.x.x distribution, the build can automatically download it for you.

Issue the following command:

mvn install -Ppkg-community -Pdownload

If you have an existing Tomcat 6.x.x distribution, unpack it into SERVERS_DIR directory so that you get SERVERS_DIR/apache-tomcat-6.x.x directory.

In this case you can issue the following command:

mvn install -Ppkg-community-DskipTests -Dservers.dir=$SERVERS_DIR -Dgatein.dev=tomcat6 -Dserver.name=apache-tomcat-6.x.x


The packaged Platform is available in packaging/tomcat/target/tomcat.

To start, go to tomcat6 directory, and run 'start_eXo.sh' ('start_eXo.bat run' on Windows).

Access the portal at: http://localhost:8080/portal

Troubleshooting
===============

Maven dependencies issues
-------------------------

While Platform should build without any extra maven repository configuration it may happen that the build complains about missing artifacts.

If you encounter this situation, please let us know via our forums (http://forum.exoplatform.org).

As a quick workaround you may try setting up maven repositories as follows.

Create file settings.xml in $HOME/.m2  (%HOMEPATH%\.m2 on Windows) with the following content:

<settings>
  <profiles>
    <profile>
      <id>jboss-public-repository</id>
      <repositories>
        <repository>
          <id>jboss-public-repository-group</id>
          <name>JBoss Public Maven Repository Group</name>
          <url>https://repository.jboss.org/nexus/content/groups/public-jboss/</url>
          <layout>default</layout>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>jboss-public-repository-group</id>
          <name>JBoss Public Maven Repository Group</name>
          <url>https://repository.jboss.org/nexus/content/groups/public-jboss/</url>
          <layout>default</layout>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>

    <profile>
      <id>exo-public-repository</id>
      <repositories>
        <repository>
          <id>exo-public-repository-group</id>
          <name>eXo Public Maven Repository Group</name>
          <url>http://repository.exoplatform.org/content/groups/public</url>
          <layout>default</layout>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>exo-public-repository-group</id>
          <name>eXo Public Maven Repository Group</name>
          <url>http://repository.exoplatform.org/content/groups/public</url>
          <layout>default</layout>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>jboss-public-repository</activeProfile>
    <activeProfile>exo-public-repository</activeProfile>
  </activeProfiles>
</settings>


Going Further
=============
Your next stop will depend on who you are:

    * Developers: learn how to build your own portal, gadgets, REST services or eXo-based applications in the Developer Guide [http://docs.exoplatform.org/PLF35/topic/org.exoplatform.doc.35/bk03.html] and the Reference Documentation [http://docs.exoplatform.org/PLF35/topic/org.exoplatform.doc.35/bk05.html]
   * Administrators: learn how to install eXo Platform on a server in the Administrator Guide: http://docs.exoplatform.org/PLF35/topic/org.exoplatform.doc.35/bk02.html
    * End Users: learn more about using the features in the User Manuals: http://docs.exoplatform.org/PLF35/topic/org.exoplatform.doc.35/bk01.html


External Resources
==================

Support			http://support.exoplatform.com
Training			http://www.exoplatform.com/company/public/website/services/development/development-training
Consulting		http://www.exoplatform.com/company/public/website/services/development/development-consulting
Corporate Website	http://www.exoplatform.com
Blog			http://blog.exoplatform.org
Community Website	http://community.exoplatform.org
Forum			http://forum.exoplatform.org

