#!/bin/bash

if [ "$(which jar)" = "" ]
then
  echo "jar is not found, please install java sdk and include \$JAVA_HOME/bin is your PATH"
  exit 1
fi
cd $(dirname $0)
bindir=$(pwd)
CLASSPATH=""
for jarfile in $(find ${bindir}/lib -name "*.jar" -print)
do
  if [ "$CLASSPATH" = "" ]
  then
    CLASSPATH=$jarfile
  else
    CLASSPATH=$CLASSPATH:$jarfile
  fi
done
export CLASSPATH
cd src
javac sqlserver/*.java -d ${bindir}/bin
if [ $? -ne 0 ]
then
  echo "build failed....."
  exit 1
fi
cd ${bindir}/bin
jar cvf ${bindir}/target/sqlserver.jar sqlserver
cp ${bindir}/lib/*.jar ${bindir}/target
echo "........."
echo "........."
echo "Files in ${bindir}/target"
ls -l ${bindir}/target
