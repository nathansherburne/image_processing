@echo off

REM MS-DOS batch file to invoke the MeanROI application.
REM To use, install the JAR file MeanROI.jar somewhere and
REM modify the 'set CLASSPATH' statement below so that it
REM points to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap05\MeanROI.jar;%CLASSPATH%
java MeanROI %1
set CLASSPATH=%OLDPATH%
set OLDPATH=
