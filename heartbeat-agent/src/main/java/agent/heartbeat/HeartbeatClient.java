package agent.heartbeat;

import agent.AgentLogger;
import agent.AgentProperties;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Heartbeat client
 *
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
public class HeartbeatClient implements Runnable {

    private static final int READ_TIMEOUT = 3000;
    private static final int CONNECT_TIMEOUT = 3000;
    private static final String USER_AGENT = "Heartbeat";

    private ScheduledExecutorService scheduledExecutor;
    private String clientId;

    public HeartbeatClient() {
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new HeartbeatThreadFactory("HeartbeatThread", true)
        );
        this.clientId = UUID.randomUUID().toString();
    }

    /**
     * Start heartbeat scheduler
     */
    public void start() {
        scheduledExecutor.scheduleAtFixedRate(
            this, AgentProperties.INSTANCE.getInitDelay(),
            AgentProperties.INSTANCE.getPeriod(), TimeUnit.MILLISECONDS
        );
    }

    /**
     * Stop heartbeat scheduler
     */
    public void stop() {
        scheduledExecutor.shutdown();
        try {
            scheduledExecutor.awaitTermination(3000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        AgentLogger.info(">> Heartbeat stopped");
    }

    @Override
    public void run() {
        if (!AgentProperties.INSTANCE.hasServerUrls()) {
            return;
        }

        final String queryString = getQueryString();
        for (String serverUrl : AgentProperties.INSTANCE.getServerUrls()) {
            // build heartbeat server url with query string
            String url = new StringBuilder(serverUrl.length() + queryString.length() + 1)
                .append(serverUrl).append('?').append(queryString).toString();

            doBeat(url);
        }
    }

    /**
     * Try to beat to url
     */
    private void doBeat(String url) {
        try {
            HttpURLConnection connection = createConnection(url);
            int responseCode = connection.getResponseCode();
            connection.disconnect();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Received response code : " + responseCode);
            }
        } catch (Exception e) {
            AgentLogger.error(String.format("Failed to send heartbeat. %s. reason : %s", url, e.getMessage()));
        }
    }

    /**
     * Getting query string from AgentProperties like below
     *
     * "serviceName=DefaultService&pid=870&clientId=UUID_VALUE"
     */
    private String getQueryString() {
        return new StringBuilder()
            .append("serviceName=").append(AgentProperties.INSTANCE.getServiceName())
            .append("&pid=").append(AgentProperties.INSTANCE.getPid())
            .append("&clientId=").append(clientId)
            .toString();
    }

    private HttpURLConnection createConnection(String url) throws Exception {
        URL heartbeatUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) heartbeatUrl.openConnection();

        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.addRequestProperty("beat-interval", Long.toString(AgentProperties.INSTANCE.getPeriod()));

        return connection;
    }
}