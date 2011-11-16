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

REM Uncomment the above line and set path if you want to execute this scipt from any place
REM SET SERVER_DIR
setlocal ENABLEDELAYEDEXPANSION
rem Computes the absolute path of eXo
if "%SERVER_DIR%" == "" (
	for %%i in ( !%~f0! ) do set SERVER_DIR=%%~dpi
)

cd %SERVER_DIR%bin

echo *****************
echo Starting eXo ...
echo *****************

if "%1" == "" (
	set EXO_PROFILES=-Dexo.profiles=default
) else (
	set EXO_PROFILES=-Dexo.profiles=%*
)

echo eXo is launched with the %EXO_PROFILES% option as profile

set CATALINA_OPTS=-Xms256m -Xmx1024m -XX:MaxPermSize=256m %CATALINA_OPTS% %EXO_PROFILES%

rem Launches the server
call catalina.bat run %*

if errorlevel 1 goto error

goto end

:error
pause

:end
