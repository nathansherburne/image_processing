#!/bin/bash

# Shell script to invoke the ConvolutionTool application.
# To use, install the JAR file ConvolutionTool.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap07/ConvolutionTool.jar:$CLASSPATH
export CLASSPATH
java ConvolutionTool $1 $2 $3
