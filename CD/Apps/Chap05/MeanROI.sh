#!/bin/bash

# Shell script to invoke the MeanROI application.
# To use, install the JAR file MeanROI.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap05/MeanROI.jar:$CLASSPATH
export CLASSPATH
java MeanROI $1
