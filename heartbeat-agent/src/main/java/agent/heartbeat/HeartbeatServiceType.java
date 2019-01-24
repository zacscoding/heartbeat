package agent.heartbeat;

import java.util.EnumSet;
import java.util.Set;

/**
 * @GitHub : https://github.com/zacscoding
 */
public enum HeartbeatServiceType {

    AGENT, PROCESS, DOCKER, UNKNOWN;

    private static final Set<HeartbeatServiceType> MESSAGE_TYPES = EnumSet.allOf(HeartbeatServiceType.class);

    public static HeartbeatServiceType getType(String name) {
        if (name != null && name.length() > 0) {
            for (HeartbeatServiceType type : MESSAGE_TYPES) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }

        return UNKNOWN;
    }
}
