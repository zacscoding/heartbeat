package agent.heartbeat;

import agent.heartbeat.predicate.AlivePredicate;

/**
 * Heartbeat abstract class
 *
 * TODO :: WORKING MULTIPLE HEARTBEAT
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
public abstract class Heartbeat {

    private String clientId;
    private String userAgent = "Heartbeat";
    private String serviceName;
    private String beatInterval;

    public Heartbeat(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Getting [ App | Process | Docker ] is whether alive or not
     */
    public boolean testAlive() {
        return getAlivePredicate().test();
    }

    /**
     * Getting heartbeat json data
     */
    public String getHeartbeatData() {
        return null;
    }


    protected abstract AlivePredicate getAlivePredicate();

    protected abstract int getPid();
}
