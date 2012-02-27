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
set EXO_OPTS=-Dexo.product.developing=false -Dexo.conf.dir.name=gatein\conf
set IDE_OPTS=-Djavasrc=$JAVA_HOME/src.zip -Djre.lib=$JAVA_HOME/jre/lib
if "%EXO_PROFILES%" == "" (
	set EXO_PROFILES=-Dexo.profiles=default
)

set BPM_HOSTNAME=localhost
set BPM_HTTP_PORT=8080
set BPM_URI=http://%BPM_HOSTNAME%:%BPM_HTTP_PORT%
rem set BPM_URI=http://%BPM_HOSTNAME%

set BPM_OPTS=-Dorg.exoplatform.runtime.conf.gatein.host=%BPM_HOSTNAME% %BPM_OPTS%
set BPM_OPTS=-Dorg.exoplatform.runtime.conf.gatein.port=%BPM_HTTP_PORT% %BPM_OPTS%
set BPM_OPTS=-Dorg.exoplatform.runtime.conf.gatein.portal=portal %BPM_OPTS%

set BPM_OPTS=-Dorg.exoplatform.runtime.conf.cas.server.name=%BPM_URI% %BPM_OPTS%

set BONITA_HOME=-DBONITA_HOME=..\bonita
set REST=-Dorg.ow2.bonita.rest-server-address=%BPM_URI%/bonita-server-rest -Dorg.ow2.bonita.api-type=REST

set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MaxPermSize=256m %LOG_OPTS% %SECURITY_OPTS% %EXO_OPTS% %EXO_PROFILES% %BONITA_HOME% %REST% %BPM_OPTS%

rem Launches the server
call catalina.bat %*
