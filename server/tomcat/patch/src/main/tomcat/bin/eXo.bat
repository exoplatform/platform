@echo off

rem Computes the absolute path of eXo
setlocal ENABLEDELAYEDEXPANSION
for %%i in ( !%~f0! ) do set BIN_DIR=%%~dpi
cd %BIN_DIR%

rem Sets some variables
set LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
set SECURITY_OPTS="-Djava.security.auth.login.config=..\conf\jaas.conf"
set EXO_OPTS="-Dexo.product.developing=true"
set EXO_CONFIG_OPTS="-Dorg.exoplatform.container.configuration.debug"
set JAVA_OPTS=-Xshare:auto -Xms128m -Xmx512m %LOG_OPTS% %SECURITY_OPTS% %EXO_OPTS% %EXO_CONFIG_OPTS%
set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=8000

rem Launches the server
call catalina.bat %*
