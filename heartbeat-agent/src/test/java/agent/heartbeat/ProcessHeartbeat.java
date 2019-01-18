package agent.heartbeat;

import agent.heartbeat.predicate.AlivePredicate;
import agent.heartbeat.predicate.ProcessAlivePredicate;

/**
 * Process heartbeat
 *
 * TODO :: WORKING MULTIPLE HEARTBEAT
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
public class ProcessHeartbeat extends Heartbeat {

    private String processName;
    private ProcessAlivePredicate alivePredicate;

    public ProcessHeartbeat(String serviceName, String processName) {
        super(serviceName);
        this.processName = processName;
        this.alivePredicate = new ProcessAlivePredicate(processName);
    }

    @Override
    protected AlivePredicate getAlivePredicate() {
        return null;
    }

    @Override
    protected int getPid() {
        return 0;
    }
}
