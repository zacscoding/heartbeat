package server.events.listener;

import static server.state.HostState.HEALTHY;
import static server.state.HostState.HEARTBEAT_LOST;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import server.configuration.SlackConfiguration;
import server.events.HostStateChangedEvent;
import server.events.publisher.HostStatePublisher;
import server.message.slack.SlackMessageConverter;
import server.message.slack.SlackWebHooks;
import server.state.HostEntity;
import server.state.HostState;

/**
 * Listen host state chaned & Notify slack message
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Component
@ConditionalOnBean(SlackConfiguration.class)
public class SlackNotifierListener {

    private SlackWebHooks slackWebHooks;
    private SlackMessageConverter messageConverter;


    @Autowired
    public SlackNotifierListener(HostStatePublisher hostStatePublisher, SlackWebHooks slackWebHooks,
        SlackMessageConverter messageConverter) {

        this.slackWebHooks = slackWebHooks;
        this.messageConverter = messageConverter;

        hostStatePublisher.register(this);
    }

    @Subscribe
    public void onHostStateChanged(HostStateChangedEvent event) {
        HostState prevState = event.getPrevState();
        HostEntity hostEntity = event.getHostEntity();

        switch (prevState) {
            case UNKNOWN:
                // register
                if (hostEntity.getHostState() == HEALTHY) {
                    sendHostStateChangedMessage("Register", hostEntity);
                }
                break;
            case HEALTHY:
                // stopped
                if (hostEntity.getHostState() == HEARTBEAT_LOST) {
                    sendHostStateChangedMessage("Stopped", hostEntity);
                }

                break;
            case HEARTBEAT_LOST:
                // started
                if (hostEntity.getHostState() == HEALTHY) {
                    sendHostStateChangedMessage("Restarted", hostEntity);
                }
                break;
        }
    }

    private void sendHostStateChangedMessage(String changedState, HostEntity hostEntity) {
        StringBuilder builder = new StringBuilder();
        builder.append("[ ").append(changedState).append(" ]")
            .append("\tService `").append(hostEntity.getServiceName()).append("`");

        slackWebHooks.invokeSlackWebHooks(
            messageConverter.convertHostStateChangedMessage(builder.toString())
        );
    }
}
