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
set EXO_OPTS=-Dexo.product.developing=true -Dexo.conf.dir.name=gatein\conf
set EXO_CONFIG_OPTS=-Dorg.exoplatform.container.configuration.debug
set REMOTE_DEBUG=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n
set EXO_PROFILES=-Dexo.profiles=default
set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MaxPermSize=256m %LOG_OPTS% %SECURITY_OPTS% %EXO_OPTS% %EXO_CONFIG_OPTS% %REMOTE_DEBUG% %EXO_PROFILES%
set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=8000

rem Launches the server
call catalina.bat %*
