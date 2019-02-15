package server.agent;

import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import server.events.HostStateChangedEvent;
import server.events.publisher.HostStatePublisher;
import server.state.HostEntity;
import server.state.HostState;
import server.state.repository.HostEntityRepository;

/**
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Component
public class HeartbeatHandler {

    private HostEntityRepository hostEntityRepository;
    private HeartbeatMonitor heartbeatMonitor;
    private HostStatePublisher hostStatePublisher;

    @Autowired
    public HeartbeatHandler(HostEntityRepository hostEntityRepository, HostStatePublisher hostStatePublisher
        ) {

        this.hostEntityRepository = hostEntityRepository;
        this.hostStatePublisher = hostStatePublisher;
        //this.heartbeatMonitor = new HeartbeatMonitor(hostEntityRepository, hostStatePublisher, threadWakeUpInterval);
        this.heartbeatMonitor = new HeartbeatMonitor(hostEntityRepository, hostStatePublisher, 10000L);
    }

    @PostConstruct
    private void setUp() {
        heartbeatMonitor.start();
    }

    /**
     * Handle heartbeat
     */
    public void handleHeartBeat(Heartbeat heartbeat) {
        try {
            long now = System.currentTimeMillis();

            Optional<HostEntity> hostOptional = hostEntityRepository.findByServiceName(heartbeat.getServiceName());
            // register host & publish host started event
            if (!hostOptional.isPresent()) {
                log.info("Register service : {}", heartbeat.getServiceName());
                // save host
                HostEntity hostEntity = convertHeartbeatToHost(heartbeat, now);
                HostEntity saved = hostEntityRepository.save(hostEntity);

                // publish new service
                HostStateChangedEvent event = HostStateChangedEvent.builder()
                    .prevState(HostState.UNKNOWN)
                    .hostEntity(saved)
                    .build();
                hostStatePublisher.publish(event);
                return;
            }

            // update host
            HostEntity host = hostOptional.get();
            HostState prevState = host.getHostState();
            host.setLastAgentTimestamp(now);
            host.setLastUpdatedTimestamp(now);
            host.setHostState(HostState.HEALTHY);
            HostEntity updated = hostEntityRepository.save(host);

            // publish HEARTBEAT_LOST -> HEALTHY event
            if (prevState == HostState.HEARTBEAT_LOST) {
                log.info("Restarted {}", heartbeat.getServiceName());
                HostStateChangedEvent event = HostStateChangedEvent.builder()
                    .prevState(prevState)
                    .hostEntity(updated)
                    .build();

                hostStatePublisher.publish(event);
            }
        } catch (Exception e) {
            log.error("Exception occur while handling heartbeat", e);
        }
    }

    private HostEntity convertHeartbeatToHost(Heartbeat heartbeat, long now) {
        return HostEntity.builder()
            .serviceName(heartbeat.getServiceName())
            .ip(heartbeat.getIp())
            .pid(heartbeat.getPid())
            .registerTimestamp(now)
            .lastAgentTimestamp(now)
            .lastUpdatedTimestamp(now)
            .hostState(HostState.HEALTHY)
            .build();
    }
}
