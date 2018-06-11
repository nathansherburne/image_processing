@echo off

REM MS-DOS batch file to invoke the ConvolutionTool application.
REM To use, install the JAR file ConvolutionTool.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points
REM to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap07\ConvolutionTool.jar;%CLASSPATH%
java ConvolutionTool %1 %2 %3
set CLASSPATH=%OLDPATH%
set OLDPATH=
