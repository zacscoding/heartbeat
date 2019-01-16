package server.state;

/**
 * HostState
 *
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
public enum HostState {

    /**
     * Receive heartbeat regularly
     */
    HEALTHY,
    /**
     * Lost heartbeat
     */
    HEARTBEAT_LOST,

    UNKNOWN
}
