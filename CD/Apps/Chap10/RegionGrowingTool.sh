#!/bin/bash

# Shell script to invoke the RegionGrowingTool application.
# To use, install the JAR file RegionGrowingTool.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap10/RegionGrowingTool.jar:$CLASSPATH
export CLASSPATH
java RegionGrowingTool $1
