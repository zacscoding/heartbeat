package agent;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Agent properties
 *
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
public class AgentProperties {

    public static final AgentProperties INSTANCE = new AgentProperties();

    // current pid or 0
    private int pid;
    // service name or "DefaultService-XXXX"
    private String serviceName;
    // heartbeat-server urls
    private List<String> serverUrls;
    // heartbeat init delay
    private long initDelay;
    // heartbeat period
    private long period;

    private AgentProperties() {
        initialize();
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public List<String> getServerUrls() {
        return serverUrls;
    }

    public boolean hasServerUrls() {
        return !getServerUrls().isEmpty();
    }

    public long getInitDelay() {
        return initDelay;
    }

    public long getPeriod() {
        return period;
    }

    private void initialize() {
        StringBuilder logMessage = new StringBuilder();
        // pid
        try {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            this.pid = Integer.valueOf(jvmName.split("@")[0]);
        } catch (Exception e) {
            this.pid = Integer.valueOf(0);
        }
        logMessage.append("pid : ").append(pid);

        // service name
        this.serviceName = System.getProperty("heartbeat.service_name");
        if (serviceName == null || serviceName.length() == 0) {
            this.serviceName = "DefaultService-" + UUID.randomUUID().toString().substring(0, 4);
        }
        logMessage.append("\tservice name : ").append(serviceName);

        // server urls
        String urls = System.getProperty("heartbeat.server_urls");
        if (urls == null || urls.isEmpty()) {
            this.serverUrls = Collections.emptyList();
        } else {
            StringTokenizer st = new StringTokenizer(urls);
            this.serverUrls = new ArrayList<String>(st.countTokens());
            while (st.hasMoreTokens()) {
                this.serverUrls.add(st.nextToken());
            }
        }
        logMessage.append("\tserver urls : ").append(urls);

        // initDelay period
        this.initDelay = parseLong(System.getProperty("heartbeat.init_delay"), 5000);
        this.period = parseLong(System.getProperty("heartbeat.period"), 5000);
        logMessage.append("\tinit delay : ").append(initDelay).append(", period : ").append(period);

        AgentLogger.info(logMessage.toString());
    }

    private long parseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
