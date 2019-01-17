package server.agent;

import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import server.events.HostStateChangedEvent;
import server.events.publisher.HostStatePublisher;
import server.state.HostEntity;
import server.state.HostState;
import server.state.repository.HostEntityRepository;

/**
 * Heartbeat monitor
 *
 * publish stopped / started event
 *
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class HeartbeatMonitor implements Runnable {

    private HostEntityRepository hostEntityRepository;
    private HostStatePublisher hostStatePublisher;

    private Thread monitor = null;
    private boolean running = true;
    private long threadWakeupInterval;

    public HeartbeatMonitor(HostEntityRepository hostEntityRepository, HostStatePublisher hostStatePublisher
        , long threadWakeupInterval) {

        Objects.requireNonNull(hostEntityRepository, "hostEntityRepository must be not null");
        Objects.requireNonNull(hostStatePublisher, "hostEntityRepository must be not null");
        Assert.isTrue(threadWakeupInterval >= 5000, "ThreadWakeupInterval must be lager than 5000");

        this.hostEntityRepository = hostEntityRepository;
        this.hostStatePublisher = hostStatePublisher;
        this.threadWakeupInterval = threadWakeupInterval;
    }

    public void start() {
        if (monitor != null && monitor.isAlive()) {
            log.warn("Already started heart beat monitor");
            return;
        }

        monitor = new Thread(this, "heartbeat-monitor");
        monitor.setDaemon(true);
        monitor.start();
    }

    public void shutdown() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                doWork();
                Thread.sleep(threadWakeupInterval);
            } catch (InterruptedException e) {
                running = false;
            } catch (Exception e) {
                log.warn("Exception occur while doWork", e);
            }
        }
    }

    private void doWork() {
        long now = System.currentTimeMillis();

        List<HostEntity> hosts = hostEntityRepository.findAll();
        for (HostEntity host : hosts) {
            // skip already lost service
            if (host.getHostState() == HostState.HEARTBEAT_LOST) {
                continue;
            }

            // lost heartbeat
            if (isLostHeartbeat(host.getLastAgentTimestamp(), now)) {
                // update heartbeat log to database
                log.info("Heartbeat lost : {}", host.getServiceName());
                HostState prevState = host.getHostState();
                host.setHostState(HostState.HEARTBEAT_LOST);
                host.setLastUpdatedTimestamp(now);
                host.setPid(0);
                HostEntity updated = hostEntityRepository.save(host);

                // publish lost event
                HostStateChangedEvent event = HostStateChangedEvent
                    .builder()
                    .prevState(prevState)
                    .hostEntity(updated)
                    .build();

                hostStatePublisher.publish(event);
            }
        }
    }

    private boolean isLostHeartbeat(long lastHeartbeatTime, long now) {
        return (lastHeartbeatTime + 2 * threadWakeupInterval) < now;
    }
}
