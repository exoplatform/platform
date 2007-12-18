@echo off
rem Computes the absolute path of eXo
setlocal ENABLEDELAYEDEXPANSION
for %%i in ( !%~f0! )           do set NT_DIR=%%~dpi
for %%i in ( !%NT_DIR%\..\..! ) do set JONAS_ROOT=%%~dpni

rem Sets some variables
set LOG_OPTS=-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog
set EXO_OPTS="-Dexo.product.developing=true"
set EXO_CONFIG_OPTS="-Dorg.exoplatform.container.configuration.debug"
set JAVA_OPTS=-Xshare:auto -Xms128m -Xmx512m %LOG_OPTS% %EXO_OPTS% %EXO_CONFIG_OPTS%

rem Launches the server
cd %NT_DIR%
call jonas.bat %*
