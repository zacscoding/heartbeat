# Heartbeat agent with server  

check alive about [java process, normal process, docker container]  

> ## Heartbeat agent  

- check process or docker container by runnging java daemon
- check process by using by using javaagent send heratbeat request  
(will added process monitor)  

> ## Heartbeat server  

- monitoring heartbeat > alert state changed
- support command in slack bot  

<br /><br />

---  

> ## Example  

![slack_webhooks](./pics/slack_webhooks.png)  

![slack_bot](./pics/slack_bot.png)  

<br /><br />

---  

> ## Getting started  

> ### 1. Maven build

```
$ mvn clean install
```  

> ### 2. Apply agent  

- agent config json  

```
{
  "serverUrls": [
    "http://localhost:8080/heartbeat"
  ],
  "heartbeatInitDelay": 500,
  "heartbeatPeriod": 1000,
  "services": [
    {
      // "javaagent" | "process" | "docker"
      "type": "javaagent",
      "serviceName": "DemoAppService",
      "processIdFile": "",
      "processNames": "",
      "dockerNames": "ndb"
    }
  ]
}
```

> #### Start heartbeat agent in javaagent

- **reference from elastic/apm-agent-java**  
(https://www.elastic.co/guide/en/apm/agent/java/current/application-server-setup.html)

- **Normal use**  

```
$ java -javaagent:/path/to/heartbeat-agent-<version>.jar -Dheartbeat.config.location=/path/config.json -jar yourapp.jar
```  

- **Tomcat (bin/setenv.sh)**   

```
export CATALINA_OPTS="$CATALINA_OPTS -javaagent:/path/to/heartbeat-agent-<version>.jar"
export CATALINA_OPTS="$CATALINA_OPTS -Dheartbeat.config.location=/path/config.json
```  

- **Tomcat (bin/setenv.bat)**  

```
set CATALINA_OPTS=%CATALINA_OPTS% -javaagent:/path/to/heartbeat-agent-<version>.jar
set CATALINA_OPTS=%CATALINA_OPTS% -Dheartbeat.config.location=/path/config.json
```  

> #### Start heartbeat agent with daemon  

```
$ java -Dheartbeat.config.location=/path/config.json -jar heartbeat-agent-0.0.1.jar  

or

$ use start.sh in resources/bin  
```  

<br /><br />  

---  

> ### 3. Running heartbeat server  

- **Running with jar**  

```
$ java -jar target/heartbeat-server-<version>.jar  --spring.config.location=/path/application.yaml
```  

- **Server configs**  

```
## Servers
server:
  port: 8080

## Spring
spring:
  datasource:
    username: sa
    password:
    url: jdbc:h2:file:./db/data;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    hikari:
      jdbc-url: jdbc:h2:file:./db/data;DB_CLOSE_ON_EXIT=FALSE
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update

## Slack apis
slack:
  enabled: true
  web-hook-url: <paste ur webhook url>
  bot-token: <paste ur bot token>
slackApi: https://slack.com/api


## logging
logging:
  level:
    server: debug
```

---  

### Simple design  

![design of heartbeat](./pics/heartbeat_design.png)

### TODO  

- [x] agent impl
- [x] agent process heartbeat  
- [x] support process heartbeat (1 client <-> n process)
- [ ] agent config file manager
- [x] server impl
- [x] adds slack or others
