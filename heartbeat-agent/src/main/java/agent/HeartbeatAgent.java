package agent;

import agent.heartbeat.HeartbeatClient;
import java.lang.instrument.Instrumentation;

/**
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
public class HeartbeatAgent {

    private static Instrumentation instrumentation;

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        if (HeartbeatAgent.instrumentation != null) {
            AgentLogger.info("Skip premain because HeartbeatAgent.instrumentation is not null");
            return;
        }

        try {
            HeartbeatAgent.instrumentation = inst;
            startHeartbeatClient();
            AgentLogger.info("Started heartbeat client");
        } catch (Throwable t) {
            AgentLogger.error("Failed to premain in HeartbeatAgent", t);
        }
    }

    private static void startHeartbeatClient() {
        new HeartbeatClient().start();
    }
}