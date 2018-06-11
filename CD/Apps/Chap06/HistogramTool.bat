@echo off

REM MS-DOS batch file to invoke the HistogramTool application.
REM To use, install the JAR file HistogramTool.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points
REM to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap06\HistogramTool.jar;%CLASSPATH%
java HistogramTool %1
set CLASSPATH=%OLDPATH%
set OLDPATH=