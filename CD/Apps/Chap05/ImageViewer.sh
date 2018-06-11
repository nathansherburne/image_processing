#!/bin/bash

# Shell script to invoke the ImageViewer application.
# To use, install the JAR file ImageViewer.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

if [ $# -gt 0 ]; then
  CLASSPATH=/cdrom/Apps/Chap05/ImageViewer.jar:$CLASSPATH
  export CLASSPATH
  java ImageViewer $1
else
  echo "ImageViewer: no image file specified"
fi
