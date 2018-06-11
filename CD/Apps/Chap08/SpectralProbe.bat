@echo off

REM MS-DOS batch file to invoke the SpectralProbe application.
REM To use, install the JAR file SpectralProbe.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points
REM to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap08\SpectralProbe.jar;%CLASSPATH%
java SpectralProbe %1
set CLASSPATH=%OLDPATH%
set OLDPATH=
