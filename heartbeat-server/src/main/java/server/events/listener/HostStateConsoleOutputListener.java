package server.events.listener;

import static server.state.HostState.*;

import com.google.common.eventbus.Subscribe;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.events.HostStateChangedEvent;
import server.events.publisher.HostStatePublisher;
import server.state.HostEntity;
import server.state.HostState;

/**
 * For dev
 *
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Component
public class HostStateConsoleOutputListener {

    @Autowired
    public HostStateConsoleOutputListener(HostStatePublisher publisher) {
        Objects.requireNonNull(publisher, "publisher must be not null");
        publisher.register(this);
    }

    @Subscribe
    public void onHostStateChanged(HostStateChangedEvent event) {
        log.info("## Receive host state changed... " + event);
        HostState prevState = event.getPrevState();
        HostEntity hostEntity = event.getHostEntity();

        switch (prevState) {
            case UNKNOWN:
                if (hostEntity.getHostState() == HEALTHY) {
                    log.info("## new service ... {}\t{}", hostEntity.getServiceName(), hostEntity);
                }
                break;
            case HEALTHY:
                if (hostEntity.getHostState() == HEARTBEAT_LOST) {
                    log.info("## stopped  ... {}\t{}", hostEntity.getServiceName(), hostEntity);
                }

                break;
            case HEARTBEAT_LOST:
                if (hostEntity.getHostState() == HEALTHY) {
                    log.info("## restarted ... {}\t{}", hostEntity.getServiceName(), hostEntity);
                }
                break;
        }
    }
}
