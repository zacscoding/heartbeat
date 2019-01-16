# WORKING...........  

---  

# Heartbeat agent with server  

> ## Heartbeat agent  

by using javaagent send heratbeat request  

> ## Heartbeat server  

monitoring heartbeat & alert to slack  

> ## Getting started  

```aidl
-javaagent:/path/to/heartbeat-agent-<version>.jar 
-Dheartbeat.service_name=Service1
-Dheartbeat.server_urls=http://127.0.0.1:8080/heartbeat
#optional default 5000, 5000
-Dheartbeat.init_delay=5000 
-Dheartbeat.period=3000 

```  

---  

### Simple design  

![design of heartbeat](./pics/heartbeat_design.png)

### TODO  

- [x] agent impl
- [ ] agent process heartbeat
- [x] server impl
- [ ] adds slack or others
