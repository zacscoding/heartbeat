package agent.heartbeat;

import agent.AgentLogger;
import agent.AgentProperties;
import agent.heartbeat.factory.HeartbeatThreadFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Heartbeat client
 *
 * @GitHub : https://github.com/zacscoding
 */
public class HeartbeatClient implements Runnable {

    private static final long READ_TIMEOUT = 3000L;
    private static final long CONNECT_TIMEOUT = 3000L;

    private ScheduledExecutorService scheduledExecutor;
    private List<Heartbeat> heartbeats;
    private OkHttpClient httpClient;


    public HeartbeatClient(Heartbeat heartbeat) {
        this(Arrays.asList(heartbeat));
    }

    public HeartbeatClient(List<Heartbeat> heartbeats) {
        this.heartbeats = heartbeats;
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new HeartbeatThreadFactory("HeartbeatThread", true)
        );
        this.httpClient = new OkHttpClient().newBuilder()
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .build();
    }

    public void start() {
        scheduledExecutor.scheduleAtFixedRate(
            this, AgentProperties.INSTANCE.getHeartbeatInitDelay(),
            AgentProperties.INSTANCE.getHeartbeatPeriod(), TimeUnit.MILLISECONDS
        );
    }

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
        try {
            if (!AgentProperties.INSTANCE.hasServerUrls()) {
                return;
            }

            List<Heartbeat> aliveHearts = new ArrayList<Heartbeat>(heartbeats.size());

            for (Heartbeat heartbeat : heartbeats) {
                if (heartbeat.isAlive()) {
                    aliveHearts.add(heartbeat);
                } else {
                    heartbeat.resetState();
                }
            }

            if (!aliveHearts.isEmpty()) {
                List<String> serverUrls = AgentProperties.INSTANCE.getServerUrls();
                int size = serverUrls.size();

                for (int i = 0; i < size; i++) {
                    for (Heartbeat heartbeat : aliveHearts) {
                        doBeat(serverUrls.get(i), heartbeat);
                        if (i == size - 1) {
                            heartbeat.resetState();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void doBeat(String serverUrl, Heartbeat heartbeat) {
        try {
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), heartbeat.getJsonDate()
            );

            Request request = new Request.Builder()
                .header("user-agent", heartbeat.getUserAgent())
                .url(serverUrl)
                .post(body)
                .build();
            Call call = httpClient.newCall(request);
            Response response = call.execute();

            if (response.code() != 200) {
                throw new Exception("Invalid status code : " + response.code());
            }

            heartbeat.resetFailedCount();
        } catch (Exception e) {
            heartbeat.incrementFailedCount();
            if (heartbeat.getFailedCount() % 100 == 0) {
                AgentLogger.error(
                    String.format("Failed to do beat (%d). url : %s / reason : %s",
                        heartbeat.getFailedCount(), serverUrl, e.getMessage())
                );
            }
        }
    }
}