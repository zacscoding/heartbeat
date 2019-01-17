package agent;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Getter;

/**
 * TODO :: WORKING MULTIPLE HEARTBEAT
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
public class AgentConfigurer extends Thread {

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Object LOCK = new Object();
    private static AgentConfigurer INSTANCE;
    private static Thread daemon;

    private boolean running = true;

    private List<String> serverUrls;
    private long heartbeatInitDelay;
    private long heartbeatPeriod;
    private String serviceName;
    private List<Service> services;

    public static AgentConfigurer getInstance() {
        lock.readLock();
        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new AgentConfigurer();
                    daemon = INSTANCE;
                    daemon.setDaemon(true);
                    daemon.setName("Agent-Config-Manager");
                    daemon.start();
                }
            }
        }

        return INSTANCE;
    }

    private AgentConfigurer() {
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

            }
        } catch (Exception e) {
            AgentLogger.error("Failed to read config file");
        }

    }

    // getters
    public List<String> getServerUrls() {
        return serverUrls;
    }

    public long getHeartbeatInitDelay() {
        return heartbeatInitDelay;
    }

    public long getHeartbeatPeriod() {
        return heartbeatPeriod;
    }

    public String getServiceName() {
        return serviceName;
    }

    public List<Service> getServices() {
        return services;
    }

    @Getter
    public static class Service {

        private String serviceName;
        private String dockerNames;
        private String processNames;
    }
}
