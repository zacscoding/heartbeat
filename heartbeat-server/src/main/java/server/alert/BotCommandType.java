package server.alert;

import java.util.EnumSet;
import java.util.Set;

/**
 * @GitHub : https://github.com/zacscoding
 */
public enum BotCommandType {

    HELP,
    SERVERS,
    SERVER,
    UNKNOWN;

    private static final Set<BotCommandType> MESSAGE_TYPES = EnumSet.allOf(BotCommandType.class);

    public static BotCommandType getType(String name) {
        for (BotCommandType type : MESSAGE_TYPES) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return UNKNOWN;
    }
}