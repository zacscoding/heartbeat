package agent;

import agent.AgentProperties.Service;
import agent.heartbeat.DockerContainerHeartbeat;
import agent.heartbeat.Heartbeat;
import agent.heartbeat.HeartbeatClient;
import agent.heartbeat.ProcessHeartbeat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Keep application
 * - start heatbeat client
 *
 * @GitHub : https://github.com/zacscoding
 */
public class Bootstrap {

    private final CountDownLatch keepAliveLatch = new CountDownLatch(1);
    private final Thread keepAliveThread;
    private HeartbeatClient heartbeatClient;

    Bootstrap() {
        keepAliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    keepAliveLatch.await();
                } catch (InterruptedException e) {

                } finally {
                    if (heartbeatClient != null) {
                        heartbeatClient.stop();
                    }
                }
            }
        });
        keepAliveThread.setDaemon(false);
        setUpHeartbeatClient();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                keepAliveLatch.countDown();
            }
        }));
    }

    public void start() {
        try {
            heartbeatClient.start();
            keepAliveThread.start();
            keepAliveThread.join();
        } catch (Exception e) {
            e.printStackTrace();
            AgentLogger.error("Exception occur while running keepAliveThread", e);
            keepAliveLatch.countDown();
        }
    }

    private void setUpHeartbeatClient() {
        List<Service> services = AgentProperties.INSTANCE.getServices();
        if (services == null || services.size() == 0) {
            throw new RuntimeException("Service must be not empty");
        }

        List<Heartbeat> heartbeats = new ArrayList<Heartbeat>(services.size());

        for (Service service : services) {
            Heartbeat heartbeat = null;

            switch (service.getType()) {
                case AGENT:
                case UNKNOWN:
                    throw new UnsupportedOperationException("Not supported type : " + service.getType());
                case PROCESS:
                    heartbeat = new ProcessHeartbeat(
                        service.getServiceName(), service.getProcessIdFile(), service.getProcessNames()
                    );
                    break;
                case DOCKER:
                    heartbeat = new DockerContainerHeartbeat(
                        service.getServiceName(), service.getDockerNames()
                    );
            }

            heartbeats.add(heartbeat);
        }

        for (Heartbeat heartbeat : heartbeats) {
            AgentLogger.info("Registered heartbeat service : " + heartbeat.getServiceName());
        }

        this.heartbeatClient = new HeartbeatClient(heartbeats);
    }
}
