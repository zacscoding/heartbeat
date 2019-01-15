package server.agent;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Component
public class HeartbeatHandler {

    private HeartbeatMonitor heartbeatMonitor;

    @PostConstruct
    private void setUp() {
        heartbeatMonitor.start();
    }

    /**
     * Handle heart beat
     */
    public void handleHeartBeat(Heartbeat heartbeat) {
        // depends on state change, publish event & will consume
        // 1) Get host from this heartbeat
        // 2-1) Not exist > save
        // 2-2) Exist
        // 2-2-1) HEARTBEAT_LOST or not
    }
}
