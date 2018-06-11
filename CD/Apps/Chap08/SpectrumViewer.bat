@echo off

REM MS-DOS batch file to invoke the SpectrumViewer application.
REM To use, install the JAR file SpectrumViewer.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points
REM to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap08\SpectrumViewer.jar;%CLASSPATH%
java SpectrumViewer %1 %2
set CLASSPATH=%OLDPATH%
set OLDPATH=
