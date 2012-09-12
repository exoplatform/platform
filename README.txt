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

Platform build uses 2  profiles to be activated for packaging.

When no profile is specified it will build only entreprise Tomcat bundle.

The various profiles used inside platform are :

- pkg-tomcat   : package JBoss bundle which embeds platform EAR
- distrib      : build all (sources, docs, ear, trial package, community package, entreprise package, entreprise+ package)

2) Database configuration

By default the build uses a HSQLDB database. However, it is possible to use MySQL5/ORACLE or any RDBMS supported by eXo


Build instructions
==================

1) Clone Platform
--------------------------

git clone git@github.com:exodev/platform.git
cd platform

2) Build and package platform
----------------------------------

You can build platform without packaging (only entreprise Tomcat bundle)it by using the following command:

mvn clean install -Dmaven.test.skip=true

But that's only usable for development since in order to be able to run platform you have to package it.

Platform can be packaged with different web / application servers. The specific server to use is selected by using an appropriate profile.

  Packaging with JBoss-EAP -5.1.0.GA
  --------------------------------

If you have an existing JBoss-EAP-5.1.0.GA distribution (else download it), unpack it into ${exo.projects.directory.dependencies}/${exo.projects.app.jbosseap.version} where ${exo.projects.directory.dependencies} and {exo.projects.app.jbosseap.version} are configured on your settings.xml

In this case you can issue the following command:

mvn install -Ppkg-jboss -Dmaven.test.skip=true

The packaged Platform is available in packaging/jboss-bundle/target/

To start it, go to jboss directory, and run 'bin/run.sh' ('bin\run.bat' on Windows).

Access the portal at: http://localhost:8080/portal


  Packaging with Tomcat 6.x.x
  ---------------------------

If you have an existing Tomcat 6.x.x distribution (else download it), unpack it into ${exo.projects.directory.dependencies} directory.

In this case you can issue the following command:

mvn install -Dmaven.test.skip=true


The packaged GateIn is available in packaging/tomcat/target/tomcat.

To start, go to tomcat6 directory, and run 'start_eXo.sh' ('start_eXo.bat run' on Windows).

Access the portal at: http://localhost:8080/portal

 Packaging All
 ---------------------------

I you want to generate all package (sources, docs, trial package, community package, EARs..) ,you can issue the following command:
mvn install -Pdistrib -Dmaven.test.skip=true


Release instructions
====================


You should execute this magic command line:

mvn release:prepare
mvn release:perform


Troubleshooting
===============


Maven dependencies issues
-------------------------

While Platform should build without any extra maven repository configuration it may happen that the build complains about missing artifacts.

If you encounter this situation, please let us know via our forums (http://forum.exoplatform.org).

As a quick workaround you may try setting up maven repositories as follows.

Create file settings.xml in $HOME/.m2  (%HOMEPATH%\.m2 on Windows) with the following content:


<settings>
	<localRepository>d:\exoplatform\exo-dependencies\repository</localRepository>
	<servers>
		<server>
			<id>repository.exoplatform.org</id>
			<username>{USER}</username>
			<password>{PASSWORD}</password>
		<server>
			<id>exo.private</id>
			<username>{USER}</username>
			<password>{PASSWORD}</password>
		</server>
		<server>
			<id>exo.staging</id>
			<username>{USER}</username>
			<password>{PASSWORD}</password>
		</server>
		<server>
			<username>{USER}</username>
			<password>{PASSWORD}</password>
		</server>
		<server>
			<id>jboss-releases-repository</id>
			<username>{USER}</username>
			<password>{PASSWORD}</password>
		</server>
	</servers>
	<mirrors>
		<mirror>
			<id>exo-central-server</id>
			<mirrorOf>external:*,!exo.private,!exo.staging,!jboss.staging,</mirrorOf>
			<url>http://repository.exoplatform.org/public</url>
		</mirror>
	</mirrors>
	<profiles>
		<profile>
			<id>local-properties</id>
			<properties>

				<exo.projects.directory.dependencies>d:\exoplatform\exo-dependencies\</exo.projects.directory.dependencies>
				<exo.working.dir>d:\exoplatform\exo-working\</exo.working.dir>
				<exo.projects.app.tomcat.version>tomcat-6.0.32</exo.projects.app.tomcat.version>
				<exo.projects.app.jboss.version>jboss-5.1.0.GA</exo.projects.app.jboss.version>

			</properties>
		</profile>
		<profile>
			<id>release</id>
			<properties>
				<gpg.passphrase><!-- YOUR PGP KEY PASSPHRASE --></gpg.passphrase>
			</properties>
		</profile>
		<profile>
			<id>exo-private</id>
			<repositories>
				<repository>
					<id>exo.private</id>
					<url>http://repository.exoplatform.org/private</url>
					<releases>
						<enabled>true</enabled>
					</releases>
					<snapshots>
						<enabled>true</enabled>
						<updatePolicy>never</updatePolicy>
					</snapshots>
				</repository>
			</repositories>
			<pluginRepositories>
				<pluginRepository>
					<id>exo.private</id>
					<url>http://repository.exoplatform.org/private</url>
					<releases>
						<enabled>true</enabled>
					</releases>
					<snapshots>
						<enabled>true</enabled>
						<updatePolicy>never</updatePolicy>
					</snapshots>
				</pluginRepository>
			</pluginRepositories>
		</profile>
		<profile>
			<id>exo-staging</id>
			<repositories>
				<repository>
					<id>exo.staging</id>
					<url>http://repository.exoplatform.org/staging</url>
				</repository>
			</repositories>
			<pluginRepositories>
				<pluginRepository>
					<id>exo.staging</id>
					<url>http://repository.exoplatform.org/staging</url>
				</pluginRepository>
			</pluginRepositories>
		</profile>
		<profile>
			<id>jboss-staging</id>
			<repositories>
				<repository>
					<id>jboss.staging</id>
					<url>https://repository.jboss.org/nexus/content/groups/staging</url>
				</repository>
			</repositories>
			<pluginRepositories>
				<pluginRepository>
					<id>jboss.staging</id>
					<url>https://repository.jboss.org/nexus/content/groups/staging</url>
				</pluginRepository>
			</pluginRepositories>
		</profile>

		<profile>
			<id>exo-central</id>

			<repositories>
				<repository>
					<id>central</id>
					<url>http://fake</url>
					<releases>
						<enabled>true</enabled>
					</releases>
					<snapshots>
						<enabled>true</enabled>
						<updatePolicy>never</updatePolicy>
					</snapshots>
				</repository>
			</repositories>
			<pluginRepositories>
				<pluginRepository>
					<id>central</id>
					<url>http://fake</url>
					<releases>
						<enabled>true</enabled>
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
		<activeProfile>exo-central</activeProfile>
		<activeProfile>local-properties</activeProfile>
	</activeProfiles>
	<pluginGroups>
		<pluginGroup>org.exoplatform.maven.plugins</pluginGroup>
		<pluginGroup>org.sonatype.plugins</pluginGroup>
	</pluginGroups>
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

