#!/bin/bash
if test -z "$1"
then
    echo "USAGE: ./repo-install.sh VERSION"
else
    mvn install:install-file -Dfile=${HOME}/.m2/repository/com/googlecode/objectify-guice/objectify-guice/${1}/objectify-guice-${1}.jar -DgroupId=com.googlecode.objectify-guice -DartifactId=objectify-guice -Dversion=${1} -Dpackaging=jar -DlocalRepositoryPath=./repo
fi