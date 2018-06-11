@echo off

REM MS-DOS batch file to invoke the BinaryMorphologyTool application.
REM To use, install the JAR file BinaryMorphologyTool.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points
REM to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap11\BinaryMorphologyTool.jar;%CLASSPATH%
java BinaryMorphologyTool %1 %2 %3
set CLASSPATH=%OLDPATH%
set OLDPATH=
