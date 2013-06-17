#!/bin/bash
# Original Developer: Rupendra Bandyopadhyay
# Company: Pivotal Inc.
# Publish Date: 06/16/2013

bindir=$(dirname $0)
for file in $(ls -1 ${bindir}/../target/*.jar | grep -iv sqltool)
do
  if [ "$CLASSPATH" = "" ]
  then
    CLASSPATH=$file
  else
    CLASSPATH=$CLASSPATH:$file
  fi
done
export CLASSPATH
java sqlserver.GetDDLInfo ${1+$@}
