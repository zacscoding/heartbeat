package agent.heartbeat;

import agent.heartbeat.predicate.AlivePredicate;
import agent.heartbeat.predicate.TrueAlivePredicate;
import java.lang.management.ManagementFactory;

/**
 * Agent heartbeat
 *
 * TODO :: WORKING MULTIPLE HEARTBEAT
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
public class AgentHeartbeat extends Heartbeat {

    private Integer pid;

    public AgentHeartbeat(String serviceName) {
        super(serviceName);
    }

    @Override
    protected AlivePredicate getAlivePredicate() {
        return TrueAlivePredicate.getInstance();
    }

    @Override
    protected int getPid() {
        if (pid == null) {
            try {
                String jvmName = ManagementFactory.getRuntimeMXBean().getName();
                this.pid = Integer.valueOf(jvmName.split("@")[0]);
            } catch (Exception e) {
                this.pid = Integer.valueOf(0);
            }
        }

        return pid;
    }
}
