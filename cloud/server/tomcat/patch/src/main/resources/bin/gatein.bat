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

if "%EXO_PROFILES%" == "" (
  set EXO_PROFILES=-Dexo.profiles=default,cloud
)

@REM set JVM64_OPTS=-XX:+UseCompressedOops
set JAVA_OPTS=%JAVA_OPTS% -Xms512m -Xmx1400m -XX:MaxPermSize=256m %JVM64_OPTS% %LOG_OPTS% %SECURITY_OPTS% %EXO_CLOUD_SECURITY_OPTS% %EXO_CLOUD_ADMIN_OPTS% %EXO_OPTS% %IDE_OPTS% %EXO_PROFILES% %EXO_CLOUD_OPTS% %JMX_OPTS%

rem Launches the server
call catalina.bat %*
