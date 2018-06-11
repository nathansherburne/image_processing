#!/bin/bash

# Shell script to invoke the SpectralProbe application.
# To use, install the JAR file SpectralProbe.jar somewhere
# and modify the redefinition of CLASSPATH so that it
# specifies the full pathname of the installed
# JAR file.

CLASSPATH=/cdrom/Apps/Chap08/SpectralProbe.jar:$CLASSPATH
export CLASSPATH
java SpectralProbe $1
