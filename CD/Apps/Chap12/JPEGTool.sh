#!/bin/bash

# Shell script to invoke the JPEGTool application.
# To use, install the JAR file JPEGTool.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap12/JPEGTool.jar:$CLASSPATH
export CLASSPATH
java JPEGTool $1
