@echo off

REM MS-DOS batch file to invoke the JPEGTool application.
REM To use, install the JAR file JPEGTool.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points
REM to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap12\JPEGTool.jar;%CLASSPATH%
java JPEGTool %1
set CLASSPATH=%OLDPATH%
set OLDPATH=
