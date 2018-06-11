#!/bin/bash

# Shell script to invoke the RankFilterTool application.
# To use, install the JAR file RankFilterTool.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap07/RankFilterTool.jar:$CLASSPATH
export CLASSPATH
java RankFilterTool $1
