package server.message.slack;

import java.util.Arrays;
import java.util.List;
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
public class SlackBot extends Bot {

    private String helpMessage;

    // autowireds
    private SlackProperties slackProperties;
    private HostEntityRepository hostEntityRepository;
    private SlackMessageConverter messageConverter;


    @Autowired
    public SlackBot(SlackProperties slackProperties, HostEntityRepository hostEntityRepository,
        SlackMessageConverter messageConverter) {

        this.slackProperties = slackProperties;
        this.hostEntityRepository = hostEntityRepository;
        this.messageConverter = messageConverter;
        this.helpMessage = new StringBuilder("Command usages\n")
            .append("!server [server name] : display server info\n")
            .append("!servers              : display server infos\n")
            .append("!help                 : display help messages\n")
            .toString();
    }

    @Override
    public String getSlackToken() {
        return slackProperties.getSlackBotToken();
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    /**
     * Receive !command and handle that
     * TODO :: change pattern & handle by matcher such as matcher.group(x)
     */
    @Controller(events = EventType.MESSAGE, pattern = "^![a-zA-Z0-9].*?")
    public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) {
        log.info("## Receive command : {}", event.getText());
        handleCommand(session, event, event.getText().substring(1));
    }

    /**
     * Handle commands
     * - [ HELP / SERVER / SERVERS ]
     */
    private void handleCommand(WebSocketSession session, Event event, String argsString) {
        String[] args = argsString.split("\\s+");
        String command = args[0];

        SlackBotCommand commandType = SlackBotCommand.getType(command);

        switch (commandType) {
            case HELP:
                handleHelpCommand(session, event);
                break;
            case SERVER:
                handleServerCommand(session, event, args);
                break;
            case SERVERS:
                handleServersCommand(session, event);
                break;
            case UNKNOWN:
                reply(session, event, "Unknown command type : " + command);
                break;
        }
    }

    /**
     * Handle servers command
     */
    private void handleServersCommand(WebSocketSession session, Event event) {
        replyServerStatesMessage(session, event, hostEntityRepository.findAll());
    }

    /**
     * Handle specify server command
     */
    private void handleServerCommand(WebSocketSession session, Event event, String[] args) {
        if (args.length < 2) {
            reply(session, event, "needs server name e.g) !server Service01");
            return;
        }

        String serviceName = args[1];

        Optional<HostEntity> hostEntityOptional = hostEntityRepository.findByServiceName(serviceName);
        if (!hostEntityOptional.isPresent()) {
            reply(session, event, "not found `" + serviceName + "`");
            return;
        }

        replyServerStatesMessage(session, event, Arrays.asList(hostEntityOptional.get()));
    }

    /**
     * Handle help command
     */
    private void handleHelpCommand(WebSocketSession session, Event event) {
        reply(session, event, helpMessage);
    }

    /**
     * Reply server states message
     */
    private void replyServerStatesMessage(WebSocketSession session, Event event, List<HostEntity> hosts) {
        StringBuilder message = new StringBuilder();

        message.append(messageConverter.getLineDelimiter())
            .append(messageConverter.getHostStateTitle())
            .append(messageConverter.getLineDelimiter());

        for (HostEntity hostEntity : hosts) {
            message.append(messageConverter.getHostStateString(hostEntity)).append("\n");
        }

        reply(session, event, message.toString());
    }
}
