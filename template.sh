#!/bin/bash

echo "looking for assembly"
SPARK_APPLICATION_JAR_LOCATION=`find /opt/docker -iname '*-assembly-*.jar' | head -n1`
export SPARK_APPLICATION_JAR_LOCATION

if [ -z "$SPARK_APPLICATION_JAR_LOCATION" ]; then
	echo "Can't find a file *-assembly-*.jar in /opt/docker"
	exit 1
fi

echo "submitting ..."
/submit.sh