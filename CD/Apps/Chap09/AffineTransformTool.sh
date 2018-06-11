#!/bin/bash

# Shell script to invoke the AffineTransformTool application.
# To use, install the JAR file AffineTransformTool.jar somewhere
# and modify the redefinition of CLASSPATH so that it specifies
# the full pathname of the installed JAR file.

CLASSPATH=/cdrom/Apps/Chap09/AffineTransformTool.jar:$CLASSPATH
export CLASSPATH
java AffineTransformTool $1
