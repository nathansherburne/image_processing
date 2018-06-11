@echo off

REM MS-DOS batch file to invoke the RankFilterTool application.
REM To use, install the JAR file RankFilterTool.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points
REM to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap07\RankFilterTool.jar;%CLASSPATH%
java RankFilterTool %1
set CLASSPATH=%OLDPATH%
set OLDPATH=
