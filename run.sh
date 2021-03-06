#!/bin/sh

# Builds by mvn -Dmaven.test.skip=true assembly:assembly

DEBUGOPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

cd "`dirname ${0}`" &&
rlwrap -H ~/.suite_history java ${DEBUGOPTS} -jar target/suite-1.0-jar-with-dependencies.jar $*
