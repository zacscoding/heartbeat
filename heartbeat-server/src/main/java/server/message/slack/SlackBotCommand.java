package server.message.slack;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
public enum SlackBotCommand {

    HELP,
    SERVERS,
    SERVER,
    UNKNOWN;

    private static final Set<SlackBotCommand> MESSAGE_TYPES = EnumSet.allOf(SlackBotCommand.class);

    public static SlackBotCommand getType(String name) {
        for (SlackBotCommand type : MESSAGE_TYPES) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return UNKNOWN;
    }
}
