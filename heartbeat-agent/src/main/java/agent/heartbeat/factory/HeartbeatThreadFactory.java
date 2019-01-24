package agent.heartbeat.factory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Heartbeat thread factory
 *
 * @GitHub : https://github.com/zacscoding
 */
public class HeartbeatThreadFactory implements ThreadFactory {

    private final static AtomicInteger THREAD_FACTORY_NUMBER = new AtomicInteger(0);
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private boolean daemon;
    private String threadName;

    public HeartbeatThreadFactory() {
        this("Heartbeat", false);
    }

    public HeartbeatThreadFactory(String threadName) {
        this(threadName, false);
    }

    public HeartbeatThreadFactory(String threadName, boolean daemon) {
        this.threadName = new StringBuilder(threadName)
            .append("-")
            .append(THREAD_FACTORY_NUMBER.incrementAndGet())
            .toString();
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = generateThreadName();

        Thread t = Executors.defaultThreadFactory().newThread(r);

        t.setName(threadName);
        t.setDaemon(daemon);

        return t;
    }

    private String generateThreadName() {
        StringBuilder sb = new StringBuilder(threadName.length() + 8);
        return sb.append(threadName).append("-").append(threadNumber.incrementAndGet()).toString();
    }

}
