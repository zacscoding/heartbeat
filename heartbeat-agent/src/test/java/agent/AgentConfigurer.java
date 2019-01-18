package agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO :: WORKING MULTIPLE HEARTBEAT
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
public class AgentConfigurer extends Thread {

    private static final String CONFIG_LOCATION = "heartbeat_config_location";
    private static final Object LOCK = new Object();
    private static boolean RUNNING = true;
    private static Thread DAEMON;
    private static AgentConfigurer INSTANCE;

    private ObjectMapper objectMapper;
    private long lastModified;
    private File configFile;

    private List<String> serverUrls;
    private long heartbeatInitDelay;
    private long heartbeatPeriod;
    private List<Service> services;

    public static boolean hasError() {
        return RUNNING == false;
    }

    public static AgentConfigurer getInstance() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                synchronized (LOCK) {
                    if (INSTANCE == null) {
                        INSTANCE = new AgentConfigurer();
                        DAEMON = INSTANCE;
                        DAEMON.setDaemon(true);
                        DAEMON.setName("Agent-Config-Manager");
                        DAEMON.start();
                    }
                }
            }
        }

        return INSTANCE;
    }

    private AgentConfigurer() {
        this.objectMapper = new ObjectMapper();
        this.lastModified = 0L;

        try {
            this.configFile = getConfigFile();
            loadConfiguration();
        } catch (Exception e) {
            RUNNING = false;
        }
    }

    /**
     * Checks config file modified every 30 sec.
     */
    @Override
    public void run() {
        try {
            while (RUNNING) {
                if (this.lastModified != configFile.lastModified()) {
                    loadConfiguration();
                }

                Thread.sleep(30000L);
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

    public List<Service> getServices() {
        return services;
    }

    public static class Service {

        private String serviceName;
        private String dockerNames;
        private String processNames;

        public String getServiceName() {
            return serviceName;
        }

        public String getDockerNames() {
            return dockerNames;
        }

        public String getProcessNames() {
            return processNames;
        }
    }

    // configurer
    private boolean loadConfiguration() throws Exception {
        synchronized (LOCK) {
            try {
                JsonNode rootNode = objectMapper.readTree(configFile);

                // server urls
                JsonNode serverUrlsNode = getOrThrowException(rootNode, "serverUrls");
                if (serverUrls == null) {
                    serverUrls = new ArrayList<String>();
                } else {
                    serverUrls.clear();
                }
                for (JsonNode serverUrlNode : serverUrlsNode) {
                    serverUrls.add(serverUrlNode.asText());
                }

                // times
                if (rootNode.has("heartbeatInitDelay")) {
                    heartbeatInitDelay = rootNode.get("heartbeatInitDelay").asLong();
                } else {
                    heartbeatInitDelay = 5000L;
                }
                if (rootNode.has("heartbeatPeriod")) {
                    heartbeatPeriod = rootNode.get("heartbeatPeriod").asLong();
                } else {
                    heartbeatPeriod = 5000L;
                }

                // services
                if (services == null) {
                    services = new ArrayList<Service>();
                } else {
                    services.clear();
                }

                JsonNode servicesNode = getOrThrowException(rootNode, "services");

                for (JsonNode serviceNode : servicesNode) {
                    Service service = new Service();
                    service.serviceName = getOrThrowException(serviceNode, "serviceName").asText();
                    service.processNames = getOrThrowException(serviceNode, "processNames").asText();
                    service.dockerNames = getOrThrowException(serviceNode, "dockerNames").asText();
                    services.add(service);
                }

                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * Get JsonNode or throw Exception
     */
    private JsonNode getOrThrowException(JsonNode jsonNode, String key) throws Exception {
        if (!jsonNode.has(key)) {
            throw new Exception("has no path : " + key);
        }

        return jsonNode.get(key);
    }


    /**
     * Getting config file from config location
     */
    private File getConfigFile() throws FileNotFoundException {
        String configLocation = System.getProperty(CONFIG_LOCATION);
        if (configLocation == null || configLocation.isEmpty()) {
            throw new FileNotFoundException("Not found config location");
        }

        File configFile = new File(configLocation);
        if (!configFile.exists()) {
            throw new FileNotFoundException(configLocation);
        }

        return configFile;
    }


}
