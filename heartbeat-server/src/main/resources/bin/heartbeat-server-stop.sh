#!/usr/bin/env bash

SCRIPTPATH=$( cd "$(dirname "$0")" ; pwd -P )
JAR="heartbeat-server.jar"
SERVER_PID_FILE="$SCRIPTPATH/server.pid"

if [ -f "$SERVER_PID_FILE" ]; then
	if [ -s "$SERVER_PID_FILE" ]; then
		SERVER_PID=$(cat $SERVER_PID_FILE)
		PID=$(ps -p $SERVER_PID | tail -1 | grep -v grep | grep -v vi | grep -v PID | awk '{print $1}')
	fi
else
	PID=$(ps -ef | grep $JAR | grep -v grep | grep -v vi | grep -v PID | awk '{print $2}')
fi

if [ ! -z $PID ]; then
	$(kill -9 $PID)
	if [ -f $SERVER_PID_FILE ]; then
		$(rm $SERVER_PID_FILE)
	fi
	echo "Success to stop heartbeat server"
else
	echo "Heartbeat server is not running"
fi

