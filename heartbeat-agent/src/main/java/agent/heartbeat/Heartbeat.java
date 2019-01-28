package agent.heartbeat;

import agent.AgentProperties;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Abstract heart beat class
 *
 * @GitHub : https://github.com/zacscoding
 */
public abstract class Heartbeat implements Recyclable {

    private Object lock;
    private StringWriter sw;
    private JsonWriter jw;
    private long failedCount;

    // common heartbeat data
    private String serviceName;
    private String clientId;
    private String userAgent;
    private long beatInterval;

    public Heartbeat(String serviceName) {
        this.serviceName = serviceName;
        this.clientId = AgentProperties.INSTANCE.getClientId();
        this.beatInterval = AgentProperties.INSTANCE.getHeartbeatPeriod();
        this.userAgent = "heart-beat";

        this.lock = new Object();
        this.sw = new StringWriter();
        this.jw = new JsonWriter(sw);
        this.jw.setLenient(true);
    }

    public abstract int getPid();

    public abstract boolean isAlive();

    public String getServiceName() {
        return serviceName;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getJsonDate() throws IOException {
        synchronized (lock) {
            try {
                jw.beginObject();
                jw.name("serviceName").value(serviceName)
                    .name("clientId").value(clientId)
                    .name("pid").value(getPid())
                    .name("beatInterval").value(beatInterval);
                jw.endObject();
                jw.flush();
                return sw.toString();
            } finally {
                sw.getBuffer().setLength(0);
            }
        }
    }

    public long getFailedCount() {
        return failedCount;
    }

    public void incrementFailedCount() {
        this.failedCount += 1L;
    }

    public void resetFailedCount() {
        this.failedCount = 0L;
    }
}
