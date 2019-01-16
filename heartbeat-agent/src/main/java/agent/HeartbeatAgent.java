package agent;

import agent.heartbeat.HeartbeatClient;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

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
            new HeartbeatClient().start();
            AgentLogger.info("Started heartbeat client");
        } catch (Throwable t) {
            AgentLogger.error("Failed to premain in HeartbeatAgent", t);
        }
    }

    /**
     * Independent runner
     * e.g) java -jar heartbeat-agent.jar
     */
    public static void main(String[] args) throws InterruptedException {
        AgentLogger.info("Started from HeartbeatAgentMain. args : " + Arrays.toString(args));

        // TODO :: depends on args, will make AlivePredicate
        // Change pid about process
        AgentProperties.INSTANCE.setPid(0);
        new HeartbeatClient().start();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }
}
