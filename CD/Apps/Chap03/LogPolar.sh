#!/bin/bash

# Shell script to invoke the LogPolar application.
# To use, install the JAR file LogPolar.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap03/LogPolar.jar:$CLASSPATH
export CLASSPATH
java LogPolar $1 $2 $3
