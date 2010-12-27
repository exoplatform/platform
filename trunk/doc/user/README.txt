To run ODT->PDF conversion you need to install OpenOffice 3

You can adapt the location where OO server binary will be found by settings this variables in your local maven settings (~/settings.xml)    :
- openoffice.directory : The directory where OpenOffice is installed
- openoffice.bin.path : The subdirectory path where OpenOffice binaries are stored
- openoffice.server.exec : The name of the OpenOffice server binary.

By default, The build proposes these settings :

- Under windows
-- openoffice.directory   : C:/Program Files/OpenOffice.org 3
-- openoffice.bin.path    : program
-- openoffice.server.exec : soffice.exe

- Under unix
-- openoffice.directory   : /usr/lib64/openoffice.org3
-- openoffice.bin.path    : program
-- openoffice.server.exec : soffice.bin

- Under macos
-- openoffice.directory   : /Applications/OpenOffice.org.app
-- openoffice.bin.path    : Contents/MacOS
-- openoffice.server.exec : soffice.bin

To launch the generation of PDFs :

mvn install -Pdistrib