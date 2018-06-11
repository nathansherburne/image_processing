@echo off

REM MS-DOS batch file to invoke the AffineTransformTool application.
REM To use, install the JAR file AffineTransformTool.jar somewhere and
REM modify the 'set CLASSPATH' statement so that it points to the
REM JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap09\AffineTransformTool.jar;%CLASSPATH%
java AffineTransformTool %1
set CLASSPATH=%OLDPATH%
set OLDPATH=
