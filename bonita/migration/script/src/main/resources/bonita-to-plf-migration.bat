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

title Bonita to PLF migration

set CURRENT_DIR=%cd%
set CMD_LINE_JMX_JAR=%CURRENT_DIR%\cmdline-jmxclient-0.10.3.jar
set JMX_HOST=localhost
set JMX_PORT=9012
set CATALINA_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=%JMX_PORT% -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

if [%1] == [] (
	echo BOS standalone server location value is missing
	if [%2] == [] (
		echo The backup location value is missing
		)
		echo Bonita Migration Process: exit.
	exit /b
)

echo Starting the Bonita Migration Process..
set bosLocation=%1

@REM See if the user already added a JMX configuration
findstr "com.sun.management.jmxremote" %bosLocation%\bin\setenv.bat > nul
if not %errorlevel% equ 1 (
@REM If JMX is configured, just print this information [THE USER NEEDS TO EDIT IN THIS FILE IN ORDER TO HAVE THE SAME JMX_PORT NUMBER (here we use JMX_PORT=9012 as a default value)]
	echo The BOS standalone server alredy uses a JMX configuration..
	findstr "com.sun.management.jmxremote" %bosLocation%\bin\setenv.bat
) else (
	@REM If not add the JMX configuration
	echo Patching the server's execution environment..
	echo Adding configurations for JMX..
	@REM Stores the original file "setenv.bat" as a backup
	echo Saving a backup file for %bosLocation%\bin\setenv.bat ..
	copy %bosLocation%\bin\setenv.bat %bosLocation%\bin\setenv.bat.backup
	@REM Writes the JMX OPTS in the "setenv.bat" file
	echo. >> %bosLocation%\bin\setenv.bat
	echo set CATALINA_OPTS = %%CATALINA_OPTS%% %CATALINA_OPTS% >> %bosLocation%\bin\setenv.bat
	)
	
set backupLocation=%2
@REM Calling the remote JMX MBEan BOSBackupService
if exist %backupLocation%\backup (
	echo "backup" directory already exist.. moving its content to backup-"%date:/=-%-%time::=-%"
	mv %backupLocation%\backup %backupLocation%\backup-"%date:/=-%-%time::=-%"
	)
mkdir %backupLocation%\backup
if %errorlevel% equ 1 (
	echo Cannot create the backup directory at %backupLocation%\backup
	exit /b
)
echo Starting backup for repository db1 at %backupLocation%\backup
echo ...
set JMX_MBEAN_OPTS=%CMD_LINE_JMX_JAR% - %JMX_HOST%:%JMX_PORT% exo:service=bonita-ext,name=BOSBackupService,type=platform doBackup=%backupLocation%\backup
call java -jar %JMX_MBEAN_OPTS%
if not %errorlevel% equ 1 (
	echo done.
) else (
	echo Bonita Migration Process: exit.
	exit /b
)

@REM End.
echo Bonita document library backup has finished successfully.
