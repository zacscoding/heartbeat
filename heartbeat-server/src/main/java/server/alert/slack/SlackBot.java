package server.alert.slack;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import lombok.extern.slf4j.Slf4j;
import me.ramswaroop.jbot.core.common.Controller;
import me.ramswaroop.jbot.core.common.EventType;
import me.ramswaroop.jbot.core.common.JBot;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.models.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import server.alert.BotCommandHandler;
import server.alert.BotCommandReply;
import server.configuration.SlackConfiguration;
import server.configuration.properties.SlackProperties;
import server.state.HostEntity;
import server.state.repository.HostEntityRepository;

/**
 * Slack bot
 *
 * handle about "!" command such as "!servers"
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@JBot
@Component
@ConditionalOnBean(SlackConfiguration.class)
public class SlackBot extends Bot implements BotCommandReply {

    // autowired
    private SlackProperties slackProperties;
    private BotCommandHandler handler;


    @Autowired
    public SlackBot(SlackProperties slackProperties, HostEntityRepository hostEntityRepository) {
        this.slackProperties = slackProperties;
        this.handler = new BotCommandHandler(hostEntityRepository, this);
    }

    @Override
    public String getSlackToken() {
        return slackProperties.getSlackBotToken();
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Controller(events = EventType.MESSAGE, pattern = "^![a-zA-Z0-9].*?")
    public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) {
        String commandValue = event.getText();
        log.info("Receive slack bot command : {}", commandValue);

        handler.handleCommand(
            ImmutableMap.<String, Object>builder()
                .put("session", session)
                .put("event", event)
                .build()
            , commandValue.substring(1)
        );
    }

    @Override
    public void replyServersStateMessage(Map<String, Object> context, List<HostEntity> hosts) {
        WebSocketSession session = (WebSocketSession) context.get("session");
        Event event = (Event) context.get("event");

        reply(session, event, SlackMessageConverter.INSTANCE.convertServersState(hosts));
    }

    @Override
    public void replyHelpMessage(Map<String, Object> context) {
        WebSocketSession session = (WebSocketSession) context.get("session");
        Event event = (Event) context.get("event");

        reply(session, event, helpMessage);
    }

    @Override
    public void replyErrorMessage(Map<String, Object> context, Throwable throwable) {
        WebSocketSession session = (WebSocketSession) context.get("session");
        Event event = (Event) context.get("event");
        String errorMessage = "Exception occur > " + throwable == null ? "throwable is null" : throwable.getMessage();

        reply(session, event, errorMessage);
    }
}
