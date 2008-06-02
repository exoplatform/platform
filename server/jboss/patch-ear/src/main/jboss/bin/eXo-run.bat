@echo off
rem -------------------------------------------------------------------------
rem eXo Bootstrap Script for Win32
rem -------------------------------------------------------------------------

set JAVA_OPTS=%JAVA_OPTS% -Dorg.apache.catalina.STRICT_SERVLET_COMPLIANCE=false

call run.bat

