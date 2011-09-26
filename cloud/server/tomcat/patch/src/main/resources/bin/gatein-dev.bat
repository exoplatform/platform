@REM
@REM Copyright (C) 2009 eXo Platform SAS.
@REM 
@REM This is free software; you can redistribute it and/or modify it
@REM under the terms of the GNU Lesser General Public License as
@REM published by the Free Software Foundation; either version 2.1 of
@REM the License, or (at your option) any later version.
@REM 
@REM This software is distributed in the hope that it will be useful,
@REM but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
@REM Lesser General Public License for more details.
@REM 
@REM You should have received a copy of the GNU Lesser General Public
@REM License along with this software; if not, write to the Free
@REM Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
@REM 02110-1301 USA, or see the FSF site: http://www.fsf.org.
@REM

@echo off

rem Computes the absolute path of eXo
setlocal ENABLEDELAYEDEXPANSION
for %%i in ( !%~f0! ) do set BIN_DIR=%%~dpi
cd %BIN_DIR%

rem Sets some variables
set LOG_OPTS=-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog
set SECURITY_OPTS=-Djava.security.auth.login.config=..\conf\jaas.conf
set JMX_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false
set EXO_OPTS=-Dexo.product.developing=false -Dexo.conf.dir.name=gatein/conf
set EXO_CLOUD_OPTS=-javaagent:..\lib\cloud-instrument-1.1-M2.jar=..\gatein\conf\cloud\agent-configuration.xml -Dtenant.masterhost=localhost -Dtenant.repository.name=repository -Dtenant.data.dir=../gatein/data/jcr
set EXO_CLOUD_SECURITY_OPTS=-Djava.security.manager=org.exoplatform.cloudmanagement.security.TenantSecurityManager -Djava.security.policy==..\conf\catalina.policy
set EXO_CLOUD_ADMIN_OPTS=-Dcloud.admin.log.dir=../logs/cloud-admin -Dcloud.admin.data.dir=../gatein/data -Dcloud.admin.configuration.dir=../gatein/conf/cloud/cloud-admin -Dcloud.admin.configuration.file=../gatein/conf/cloud/cloud-admin/admin.properties

@REM Remote debug configuration
set REMOTE_DEBUG=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n

if "%EXO_PROFILES%" == "" (
  set EXO_PROFILES=-Dexo.profiles=default,cloud
)

set JAVA_OPTS=-Xms512m -Xmx2g -XX:MaxPermSize=256m -XX:+UseCompressedOops %LOG_OPTS% %SECURITY_OPTS% %EXO_CLOUD_SECURITY_OPTS%  %EXO_CLOUD_ADMIN_OPTS% %EXO_OPTS% %EXO_PROFILES% %EXO_CLOUD_OPTS% %JMX_OPTS% %REMOTE_DEBUG%

rem Launches the server
call catalina.bat %*
