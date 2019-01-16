package server.events.publisher;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import server.events.HostStateChangedEvent;
import server.util.ThreadUtil;

/**
 * HostState changed publisher
 *
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Component
public class HostStatePublisher {

    private EventBus asyncEventBus;

    public HostStatePublisher() {
        // string executor
        this.asyncEventBus = new AsyncEventBus("host-state-publisher", Executors.newCachedThreadPool());
    }

    public void publish(HostStateChangedEvent hostStateChangedEvent) {
        if (hostStateChangedEvent == null || hostStateChangedEvent.getHostEntity() == null) {
            log.warn("Published empty host changed event. stack trace : \n{}"
                , ThreadUtil.getStackTraceString(2));
            return;
        }

        asyncEventBus.post(hostStateChangedEvent);
    }

    /**
     * Register listener
     */
    public void register(Object listener) {
        Objects.requireNonNull(listener, "listener must be not null");
        log.info("Register hostStateChanged consumer : {}", listener.getClass().getName());

        asyncEventBus.register(listener);
    }
}
