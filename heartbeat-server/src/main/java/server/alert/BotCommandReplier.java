package server.alert;

import java.util.List;
import java.util.Map;
import server.state.HostEntity;

/**
 * @GitHub : https://github.com/zacscoding
 */
public interface BotCommandReplier {

    String helpMessage = "Command usages\n"
        + "!server [server name] : display server info\n"
        + "!servers              : display server infos\n"
        + "!help                 : display help messages\n";

    /**
     * Reply for ["!servers","!server"] command result
     */
    void replyServersStateMessage(Map<String, Object> context, List<HostEntity> hosts);

    /**
     * Reply for "!help" command result
     */
    void replyHelpMessage(Map<String, Object> context);

    /**
     * Reply for error result
     */
    void replyErrorMessage(Map<String, Object> context, Throwable throwable);
}