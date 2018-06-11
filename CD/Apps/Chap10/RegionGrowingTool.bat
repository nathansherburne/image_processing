@echo off

REM MS-DOS batch file to invoke the RegionGrowingTool application.
REM To use, install the JAR file RegionGrowingTool.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points
REM to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap10\RegionGrowingTool.jar;%CLASSPATH%
java RegionGrowingTool %1
set CLASSPATH=%OLDPATH%
set OLDPATH=
