package agent;

import agent.heartbeat.HeartbeatServiceType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Agent properties
 *
 * @GitHub : https://github.com/zacscoding
 */
public class AgentProperties {

    public static final AgentProperties INSTANCE = new AgentProperties();

    private boolean error;
    // heartbeat client id
    private String clientId;
    // heartbeat server urls
    private List<String> serverUrls;
    // heartbeat start init delay
    private long heartbeatInitDelay;
    // heartbeat period
    private long heartbeatPeriod;
    // heartbeat target services
    private List<Service> services;

    private AgentProperties() {
        this.clientId = UUID.randomUUID().toString();
        this.heartbeatInitDelay = 5000L;
        this.heartbeatPeriod = 5000L;

        loadProperties();
    }

    public boolean hasError() {
        return this.error;
    }

    public List<String> getServerUrls() {
        return serverUrls;
    }

    public boolean hasServerUrls() {
        return (serverUrls != null && serverUrls.size() > 0);
    }

    public String getClientId() {
        return clientId;
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

    private boolean loadProperties() {
        try {
            String configLocation = System.getProperty("heartbeat.config.location");
            if (configLocation == null) {
                throw new Exception(
                    "Empty heartbeat agent config location. Adds -Dheartbeat.config.location=/path/"
                );
            }

            File configFile = new File(configLocation);
            if (!configFile.exists()) {
                throw new Exception(
                    "Not exist heartbeat agent config file. location : " + configLocation
                );
            }

            JsonParser parser = new JsonParser();
            JsonObject rootNode = (JsonObject) parser.parse(new FileReader(configFile));

            if (!rootNode.has("serverUrls")) {
                throw new Exception("has no key : serverUrls");
            }

            JsonArray serverUrlsObj = rootNode.getAsJsonArray("serverUrls");
            this.serverUrls = new ArrayList<String>(serverUrlsObj.size());

            for (JsonElement serverUrlElt : serverUrlsObj) {
                this.serverUrls.add(serverUrlElt.getAsString());
            }

            this.heartbeatInitDelay = rootNode.has("heartbeatInitDelay") ?
                rootNode.get("heartbeatInitDelay").getAsLong() : 5000L;

            this.heartbeatPeriod = rootNode.has("heartbeatPeriod") ?
                rootNode.get("heartbeatPeriod").getAsLong() : 5000L;

            this.services = parseServices(rootNode.getAsJsonArray("services"));

            this.error = false;
            return true;
        } catch (Exception e) {
            AgentLogger.error(e.getMessage());
            this.error = true;

            return false;
        }
    }

    private List<Service> parseServices(JsonArray servicesNode) throws Exception {
        if (servicesNode == null) {
            throw new Exception("Not exist services");
        }

        int size = servicesNode.size();
        if (size == 0) {
            throw new Exception("Empty services");
        }

        List<Service> services = new ArrayList<Service>(size);

        for (JsonElement serviceElt : servicesNode) {
            JsonObject serviceObj = serviceElt.getAsJsonObject();

            Service service = new Service();
            String typeValue = serviceObj.get("type").getAsString();
            HeartbeatServiceType type = HeartbeatServiceType.getType(typeValue);
            if (type == HeartbeatServiceType.UNKNOWN) {
                throw new Exception("Unknown service type : " + typeValue);
            }

            service.setType(type);
            service.setServiceName(serviceObj.get("serviceName").getAsString());

            if (service.getType().equals("agent")) {
                if (size != 1) {
                    throw new Exception("Agent must be 1 services");
                }

                services.add(service);
                return services;
            }

            switch (service.getType()) {
                case PROCESS:
                    if (serviceObj.has("processIdFile")) {
                        service.setProcessIdFile(serviceObj.get("processIdFile").getAsString());
                    }

                    if (serviceObj.has("processNames")) {
                        service.setProcessNames(serviceObj.get("processNames").getAsString());
                    }
                    break;
                case DOCKER:
                    if (!serviceObj.has("dockerNames")) {
                        throw new Exception("Docker names must be exist");
                    }

                    service.setDockerNames(serviceObj.get("dockerNames").getAsString());
                    break;
                case AGENT:
                case UNKNOWN:
                    throw new Exception("Unknown service type : " + service.getType());
            }

            services.add(service);
        }

        return services;
    }

    public static class Service {

        private HeartbeatServiceType type;
        private String serviceName;
        private String processIdFile;
        private String processNames;
        private String dockerNames;

        public boolean isAgentHeartbeatService() {
            return "agent".equals(type);
        }

        // getters , setters
        public HeartbeatServiceType getType() {
            return type;
        }

        public void setType(HeartbeatServiceType type) {
            this.type = type;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getProcessIdFile() {
            return processIdFile;
        }

        public void setProcessIdFile(String processIdFile) {
            this.processIdFile = processIdFile;
        }

        public String getProcessNames() {
            return processNames;
        }

        public void setProcessNames(String processNames) {
            this.processNames = processNames;
        }

        public String getDockerNames() {
            return dockerNames;
        }

        public void setDockerNames(String dockerNames) {
            this.dockerNames = dockerNames;
        }
    }
}
