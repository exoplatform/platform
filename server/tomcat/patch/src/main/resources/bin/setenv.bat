@REM
@REM Copyright (C) 2011 eXo Platform SAS.
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

@REM production script to set environment variables for eXo Platform

rem Sets some variables
set LOG_OPTS=-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog
set SECURITY_OPTS=-Djava.security.auth.login.config=%CATALINA_HOME%\conf\jaas.conf
set EXO_OPTS=-Dexo.conf.dir.name=gatein\conf

set res=false
if "%EXO_PROFILES%" == "" set res=true
if "%EXO_PROFILES%" == "-Dexo.profiles=default" set res=true
if "%res%"=="true" (
    set EXO_PROFILES=-Dexo.profiles=default
)

rem Define the preferred XML parser
rem If you run eXo Platform on IBM Java, you must choose the first one
rem # set EXO_XML=-Djavax.xml.stream.XMLOutputFactory=com.sun.xml.stream.ZephyrWriterFactory -Djavax.xml.stream.XMLInputFactory=com.sun.xml.stream.ZephyrParserFactory -Djavax.xml.stream.XMLEventFactory=com.sun.xml.stream.events.ZephyrEventFactory
set EXO_XML=-Djavax.xml.stream.XMLOutputFactory=com.sun.xml.internal.stream.XMLOutputFactoryImpl -Djavax.xml.stream.XMLInputFactory=com.sun.xml.internal.stream.XMLInputFactoryImpl -Djavax.xml.stream.XMLEventFactory=com.sun.xml.internal.stream.events.XMLEventsFactoryImpl

set CATALINA_OPTS=-Xms256m -Xmx1024m -XX:MaxPermSize=256m %CATALINA_OPTS% %LOG_OPTS% %SECURITY_OPTS% %EXO_OPTS% %EXO_PROFILES% %EXO_XML%
