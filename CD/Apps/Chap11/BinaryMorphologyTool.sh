#!/bin/bash

# Shell script to invoke the BinaryMorphologyTool application.
# To use, install the JAR file BinaryMorphologyTool.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap11/BinaryMorphologyTool.jar:$CLASSPATH
export CLASSPATH
java BinaryMorphologyTool $1 $2 $3
