package server.agent;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class HeartbeatMonitor implements Runnable {

    private Thread monitor = null;
    private boolean running = true;
    private long threadWakeupInterval;

    public void start() {
        if (monitor != null && monitor.isAlive()) {
            log.warn("Already started heart beat monitor");
            return;
        }

        monitor = new Thread(this, "heartbeat-monitor");
        monitor.setDaemon(true);
        monitor.start();
    }

    public void shutdown() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                doWork();
                Thread.sleep(threadWakeupInterval);
            } catch (InterruptedException e) {
                running = false;
            } catch (Exception e) {
                log.warn("Exception occur while doWork", e);
            }
        }
    }

    private void doWork() {

    }
}
