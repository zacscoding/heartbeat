package agent;

import agent.AgentProperties.Service;
import agent.heartbeat.Heartbeat;
import agent.heartbeat.HeartbeatClient;
import agent.heartbeat.HeartbeatServiceType;
import agent.heartbeat.JavaAgentHeartbeat;
import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * HeartbeatAgent premain & main class
 *
 * @GitHub : https://github.com/zacscoding
 */
public class HeartbeatAgent {

    private static Instrumentation instrumentation;

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    /**
     * java agent start method
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        if (HeartbeatAgent.instrumentation != null) {
            AgentLogger.info("Skip premain because HeartbeatAgent.instrumentation is not null");
            return;
        }

        try {
            HeartbeatAgent.instrumentation = inst;

            List<Service> services = AgentProperties.INSTANCE.getServices();
            if (services != null && services.size() == 1) {
                Service service = services.get(0);
                if (service.getType() == HeartbeatServiceType.AGENT) {
                    Heartbeat heartbeat = new JavaAgentHeartbeat(service.getServiceName());
                    new HeartbeatClient(heartbeat).start();
                    AgentLogger.info("Started heartbeat client");
                }
            }
        } catch (Throwable t) {
            AgentLogger.error("Failed to premain in HeartbeatAgent", t);
        }
    }

    /**
     * Independent runner
     * e.g) java -jar heartbeat-agent.jar &
     */
    public static void main(String[] args) {
        if (AgentProperties.INSTANCE.hasError()) {
            AgentLogger.error("Terminate application because of parsing error");
            return;
        }

        new Bootstrap().start();
    }
}
