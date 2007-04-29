@echo off
Rem eXo / JOnAS Post installation Patch
Rem This patch moves the appropriate file to complete the installation

Rem Keep variables local to this script
setlocal ENABLEDELAYEDEXPANSION

Rem %~f0 is the script path
set this_fqn=%~f0
for %%i in ( !this_fqn! ) do set bin_nt_dir=%%~dpi
for %%i in ( !bin_nt_dir!\.. ) do set bin_dir=%%~dpi
for %%i in ( !bin_dir! ) do set JONAS_ROOT=%%~dpi

:joram
if exist %JONAS_ROOT%\rars\autoload\joram_for_jonas_ra.rar goto mvjoram
echo [PATCH] Nothing to do
goto xmlapis

:mvjoram
echo [PATCH] Moving joram_for_jonas_ra.rar from autoload...
move %JONAS_ROOT%\rars\autoload\joram_for_jonas_ra.rar %JONAS_ROOT%\rars\

:xmlapis
if exist %JONAS_ROOT%\lib\endorsed\xml-apis.jar goto mvxmlapis
echo [PATCH] Nothing to do
goto template

:mvxmlapis
echo [PATCH] Renaming xml-apis.jar to xml-apis.jar.backup...
move %JONAS_ROOT%\lib\endorsed\xml-apis.jar %JONAS_ROOT%\lib\endorsed\xml-apis.jar.backup

:template
set CONF_DIR=%JONAS_ROOT%\templates\conf
if exist %CONF_DIR% if not exist %CONF_DIR%.bak goto createtemplate
echo [PATCH] Nothing to do
goto end

:createtemplate
echo [PATCH] Creating an eXo JOnAS BASE template...
xcopy /I /Q /Y /E %CONF_DIR% %CONF_DIR%.bak
xcopy /I /Q /Y /E %JONAS_ROOT%\apps\autoload\exoplatform.ear %CONF_DIR%\apps\autoload\exoplatform.ear
xcopy /I /Q /Y /E %JONAS_ROOT%\conf %CONF_DIR%\conf
xcopy /I /Q /Y /E %JONAS_ROOT%\lib\apps %CONF_DIR%\lib\apps

:end
echo [PATCH] Post patch complete
