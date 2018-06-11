#!/bin/bash

# Shell script to invoke the GreyMapTool application.
# To use, install the JAR file GreyMapTool.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap06/GreyMapTool.jar:$CLASSPATH
export CLASSPATH
java GreyMapTool $1
