#!/bin/bash
if test -z "$1"
then
    echo "USAGE: ./repo-install-query.sh VERSION"
else
    mvn install:install-file -Dfile=${HOME}/.m2/repository/com/googlecode/objectify-query/objectify-query/${1}/objectify-query-${1}.jar -DgroupId=com.googlecode.objectify-query -DartifactId=objectify-query -Dversion=${1} -Dpackaging=jar -DlocalRepositoryPath=./repo
fi