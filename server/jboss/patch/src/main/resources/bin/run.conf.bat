rem ### -*- batch file -*- ######################################################
rem #                                                                          ##
rem #  JBoss Bootstrap Script Configuration                                    ##
rem #                                                                          ##
rem #############################################################################

rem # $Id: run.conf.bat 102596 2010-03-18 22:14:51Z rrajesh $

rem #
rem # This batch file is executed by run.bat to initialize the environment 
rem # variables that run.bat uses. It is recommended to use this file to
rem # configure these variables, rather than modifying run.bat itself. 
rem #

if not "x%JAVA_OPTS%" == "x" goto JAVA_OPTS_SET

rem #
rem # Specify the JBoss Profiler configuration file to load.
rem #
rem # Default is to not load a JBoss Profiler configuration file.
rem #
rem set "PROFILER=%JBOSS_HOME%\bin\jboss-profiler.properties"

rem #
rem # Specify the location of the Java home directory (it is recommended that
rem # this always be set). If set, then "%JAVA_HOME%\bin\java" will be used as
rem # the Java VM executable; otherwise, "%JAVA%" will be used (see below).
rem #
rem set "JAVA_HOME=C:\opt\jdk1.6.0_13"

rem #
rem # Specify the exact Java VM executable to use - only used if JAVA_HOME is
rem # not set. Default is "java".
rem #
rem set "JAVA=C:\opt\jdk1.6.0_13\bin\java"

rem #
rem # Specify options to pass to the Java VM. Note, there are some additional
rem # options that are always passed by run.bat.
rem #

rem # JVM memory allocation pool parameters - modify as appropriate.
set "JAVA_OPTS=-Xms1303m -Xmx1303m -XX:MaxPermSize=256m -Dorg.jboss.resolver.warning=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dsun.lang.ClassLoader.allowArraySyntax=true"

rem # Reduce the RMI GCs to once per hour for Sun JVMs.
set "JAVA_OPTS=%JAVA_OPTS% -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000"

rem # Warn when resolving remote XML DTDs or schemas.
set "JAVA_OPTS=%JAVA_OPTS% -Dorg.jboss.resolver.warning=true"

rem # Sample JPDA settings for remote socket debugging
rem set "JAVA_OPTS=%JAVA_OPTS% -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

rem # Sample JPDA settings for shared memory debugging 
rem set "JAVA_OPTS=%JAVA_OPTS% -Xrunjdwp:transport=dt_shmem,address=jboss,server=y,suspend=n"

:JAVA_OPTS_SET

rem # Platform environment variables
set "EXO_PROFILES=-Dexo.profiles=default"
set "EXO_OPTS=-Dexo.product.developing=false -Dexo.conf.dir.name=gatein -Dgatein.data.dir=../gatein"
set "IDE_OPTS=-Djavasrc=$JAVA_HOME/src.zip -Djre.lib=$JAVA_HOME/jre/lib"
set "REMOTE_DEBUG=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Dcom.sun.management.jmxremote -Dorg.exoplatform.container.configuration.debug"
rem # Here you can define your preferred xml parser - choose one or the other, or none
rem set "EXO_XML=-Djavax.xml.stream.XMLOutputFactory=com.sun.xml.stream.ZephyrWriterFactory -Djavax.xml.stream.XMLInputFactory=com.sun.xml.stream.ZephyrParserFactory -Djavax.xml.stream.XMLEventFactory=com.sun.xml.stream.events.ZephyrEventFactory"
set "EXO_XML=-Djavax.xml.stream.XMLOutputFactory=com.sun.xml.internal.stream.XMLOutputFactoryImpl -Djavax.xml.stream.XMLInputFactory=com.sun.xml.internal.stream.XMLInputFactoryImpl -Djavax.xml.stream.XMLEventFactory=com.sun.xml.internal.stream.events.XMLEventsFactoryImpl"
set "JAVA_OPTS=%JAVA_OPTS% %EXO_OPTS% %IDE_OPTS% %EXO_PROFILES% %EXO_XML%"

