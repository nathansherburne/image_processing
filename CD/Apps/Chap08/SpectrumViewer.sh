#!/bin/bash

# Shell script to invoke the SpectrumViewer application.
# To use, install the JAR file SpectrumViewer.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap08/SpectrumViewer.jar:$CLASSPATH
export CLASSPATH
java SpectrumViewer $1 $2
