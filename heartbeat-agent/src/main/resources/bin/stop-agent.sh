#!/usr/bin/env bash

SCRIPTPATH=$( cd "$(dirname "$0")" ; pwd -P )
VERSION="0.0.1"
JAR="heartbeat-agent-$VERSION.jar"
AGENT_PID_FILE="$SCRIPTPATH/agent.pid"

if [ -f "$AGENT_PID_FILE" ]; then
    if [ -s "$AGENT_PID_FILE" ]; then
    	AGENT_PID=$(cat $AGENT_PID_FILE)
	PID=$(ps -p $AGENT_PID | tail -1 | grep -v grep | grep -v vi | grep -v PID | awk '{print $1}')
    fi
else
	PID=$(ps -ef | grep $JAR | grep -v grep | grep -v vi | grep -v PID | awk '{print $2}')
fi

if [ ! -z $PID ]; then
	$(kill -9 $PID)
	if [ -f $AGENT_PID_FILE ]; then
		$(rm $AGENT_PID_FILE)
	fi
	echo "Success to stop heartbeat agent"
else
	echo "Heartbeat agent is not running"
fi

