package server.events.listener;

import static server.state.HostState.*;

import com.google.common.eventbus.AllowConcurrentEvents;
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
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Component
public class HostStateListener {

    @Autowired
    public HostStateListener(HostStatePublisher publisher) {
        Objects.requireNonNull(publisher, "publisher must be not null");
        publisher.register(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onHostStateChanged(HostStateChangedEvent event) {
        log.info("## Receive host state changed... " + event);
        HostState prevState = event.getPrevState();
        HostEntity hostEntity = event.getHostEntity();

        switch (prevState) {
            case UNKNOWN:
                if (hostEntity.getHostState() == HEALTHY) {
                    generateRegisterServiceMessage(hostEntity);
                }
                break;
            case HEALTHY:
                if (hostEntity.getHostState() == HEARTBEAT_LOST) {
                    generateStoppedServiceMessage(hostEntity);
                }

                break;
            case HEARTBEAT_LOST:
                if (hostEntity.getHostState() == HEALTHY) {
                    generateStartedServiceMessage(hostEntity);
                }
                break;
        }
    }

    private void generateRegisterServiceMessage(HostEntity hostEntity) {
        log.info("## new service ... {}\t{}", hostEntity.getServiceName(), hostEntity);
    }

    private void generateStartedServiceMessage(HostEntity hostEntity) {
        log.info("## restarted ... {}\t{}", hostEntity.getServiceName(), hostEntity);
    }

    private void generateStoppedServiceMessage(HostEntity hostEntity) {
        log.info("## stopped  ... {}\t{}", hostEntity.getServiceName(), hostEntity);
    }
}
