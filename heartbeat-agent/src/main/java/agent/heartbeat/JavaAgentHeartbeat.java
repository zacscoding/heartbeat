package agent.heartbeat;

import java.lang.management.ManagementFactory;

/**
 * java agent heartbeat
 *
 * @GitHub : https://github.com/zacscoding
 */
public class JavaAgentHeartbeat extends Heartbeat {

    private Integer pid;

    public JavaAgentHeartbeat(String serviceName) {
        super(serviceName);
    }

    @Override
    public int getPid() {
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

    @Override
    public boolean isAlive() {
        // JavaAgentHeartbeat is always alive
        return true;
    }

    @Override
    public void resetState() {
        // do nothing
    }
}
