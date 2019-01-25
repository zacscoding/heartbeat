#!/usr/bin/env bash

SCRIPTPATH=$( cd "$(dirname "$0")" ; pwd -P )
JAR="heartbeat-server.jar"
CONFIG_LOCATION=$SCRIPTPATH/application.yaml

function check_heartbeat_server_pid() {
	local pid_file="$SCRIPTPATH/server.pid"
	if [ -f "$pid_file" ]; then
		if [ -s "$pid_file" ]; then
			local pid=$(cat $pid_file)
			PID=$(ps -p $pid | tail -1 | grep -v grep | grep -v vi | grep -v PID | awk '{print $1}')
		fi
	else
		PID=$(ps -ef | grep $JAR | grep -v grep | grep -v vi | grep -v PID | awk '{print $2}')
	fi
}
check_heartbeat_server_pid

if [ "$(id -u)" = "0" ]; then
	echo "It can not be executed by root." 1>&2
	exit 1
fi

if [ -z $PID ]; then
	nohup java -Xms512m -Xmx512m -Dheartbeat.config.location=$SCRIPTPATH/config.json -jar $SCRIPTPATH/$JAR --spring.config.location=$CONFIG_LOCATION  1> $SCRIPTPATH/heartbeat-server.log 2>&1 &
	PID=$!
	echo $PID > $SCRIPTPATH/server.pid
	exit 0
else
	echo "Already heartbeat server running"
	exit 0
fi
			
