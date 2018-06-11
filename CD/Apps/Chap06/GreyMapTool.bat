@echo off

REM MS-DOS batch file to invoke the GreyMapTool application.
REM To use, install the JAR file GreyMapTool.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points
REM to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap06\GreyMapTool.jar;%CLASSPATH%
java GreyMapTool %1
set CLASSPATH=%OLDPATH%
set OLDPATH=
