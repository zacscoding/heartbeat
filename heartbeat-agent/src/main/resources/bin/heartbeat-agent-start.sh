#!/usr/bin/env bash

SCRIPTPATH=$( cd "$(dirname "$0")" ; pwd -P )
VERSION="0.0.1"
JAR="heartbeat-agent-$VERSION.jar"

function check_heartbeat_agent() {
    local agent_pid_file="$SCRIPTPATH/agent.pid"
    if [ -f "$agent_pid_file" ]; then
        if [ -s "$agent_pid_file" ]; then
            local agent_pid=$(cat $agent_pid_file)
            PID=$(ps -p $agent_pid | tail -1 | grep -v grep | grep -v vi | grep -v PID | awk '{print $1}')
       fi
   else
       PID=$(ps -ef | grep $JAR | grep -v grep | grep -v vi | grep -v PID | awk '{print $2}')
   fi
}
check_heartbeat_agent

if [ "$(id -u)" = "0" ]; then
    echo "It can not be executed by root." 1>&2
    exit 1
fi

if [ -z $PID ]; then
    #java -Dheartbeat.config.location=$SCRIPTPATH/config.json -jar $SCRIPTPATH/$JAR > /dev/null 2>&1 &
    nohup java -Dheartbeat.config.location=$SCRIPTPATH/config.json -jar $SCRIPTPATH/$JAR 1> $SCRIPTPATH/agent.log 2>&1 &
    PID=$!
    echo $PID > $SCRIPTPATH/agent.pid
else
    echo "Already heartbeat agent running"
fi
