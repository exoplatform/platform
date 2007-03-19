@echo off
set CURRENT_DIR=%cd%
cd ..
set TOMCAT_HOME=%cd%
cd bin

set LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
set SECURITY_OPTS="-Djava.security.auth.login.config=%TOMCAT_HOME%\conf\jaas.conf"
set EXO_OPTS="-Dexo.webui.reloadable.template=true"
set JAVA_OPTS=-Xshare:auto -Xms128m -Xmx512m %LOG_OPTS% %SECURITY_OPTS% %EXO_OPTS%
echo  %JAVA_OPTS%
set EXECUTABLE=%CURRENT_DIR%\catalina.bat

call "%EXECUTABLE%" %1


