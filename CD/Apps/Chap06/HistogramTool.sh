#!/bin/bash

# Shell script to invoke the HistogramTool application.
# To use, install the JAR file HistogramTool.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap06/HistogramTool.jar:$CLASSPATH
export CLASSPATH
java HistogramTool $1
