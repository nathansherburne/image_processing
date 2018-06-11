@echo off

REM MS-DOS batch file to invoke the ImageViewer application.
REM To use, install the JAR file ImageViewer.jar somewhere and
REM modify the 'set CLASSPATH' statement below so that it
REM points to the JAR file.

if '%1'=='' goto NoImage

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap05\ImageViewer.jar;%CLASSPATH%
java ImageViewer %1
set CLASSPATH=%OLDPATH%
set OLDPATH=
goto End

:NoImage
echo ImageViewer: no image file specified

:End
