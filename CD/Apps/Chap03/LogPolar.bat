@echo off

REM MS-DOS batch file to invoke the LogPolar application.
REM To use, install the JAR file LogPolar.jar somewhere and
REM modify the 'set CLASSPATH' statement below so that it
REM points to the JAR file.

set OLDPATH=%CLASSPATH%
set CLASSPATH=G:\Apps\Chap03\LogPolar.jar;%CLASSPATH%
java LogPolar %1 %2 %3
set CLASSPATH=%OLDPATH%
set OLDPATH=
