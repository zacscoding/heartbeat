package agent.heartbeat;

import agent.process.DefaultProcessExecutor;
import agent.process.ExecuteResult;
import agent.process.ProcessExecutor;
import agent.util.OSUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.StringTokenizer;

/**
 * Process heart beat class
 *
 * checking process
 * 1) lookup process by using pid file
 * 2) lookup process by using process name
 *
 * @GitHub : https://github.com/zacscoding
 */
public class ProcessHeartbeat extends Heartbeat {

    private ProcessExecutor processExecutor = DefaultProcessExecutor.INSTANCE;
    private String pidFile;
    private String processName;

    private Integer pid;
    private Boolean isAlive;

    public ProcessHeartbeat(String serviceName, String pidFile, String processName) {
        super(serviceName);
        this.pidFile = pidFile;
        this.processName = processName;
    }

    @Override
    public int getPid() {
        if (this.pid == null) {
            lookupProcess();
        }

        return pid;
    }

    @Override
    public boolean isAlive() {
        if (isAlive == null) {
            lookupProcess();
        }

        return isAlive;
    }

    @Override
    public void resetState() {
        this.pid = null;
        this.isAlive = null;
    }

    /**
     * Look up process id & whether alive or not
     */
    private void lookupProcess() {
        if (OSUtil.isWindows()) {
            lookupWindowsProcess();
        } else if (OSUtil.isLinux()) {
            lookupLinuxProcess();
        } else {
            throw new UnsupportedOperationException("Not supported os type : " + OSUtil.getOSType());
        }
    }

    private void lookupWindowsProcess() {
        this.isAlive = false;
    }

    private void lookupLinuxProcess() {
        boolean complete = false;

        Integer readPid = readPidFromFile(pidFile);
        if (readPid == null) {
            readPid = readPidFromProcessName("ps awf", processName);
        }
        
        if (readPid != null) {
            try {
                String cmd = "ps -p " + readPid + " -o comm=";
                ExecuteResult processCmd = processExecutor.executeAndGetResult(cmd, 3000L);
                if (processCmd.getExitValue() == 0L
                    && processCmd.getStandardOutput() != null
                    && !processCmd.getStandardOutput().isEmpty()) {

                    pid = readPid;
                    isAlive = Boolean.TRUE;
                    complete = true;
                } else {
                    isAlive = Boolean.FALSE;
                    complete = true;
                }
            } catch (Exception e) {
            }
        }

        if (!complete) {
            isAlive = false;
        }
    }

    /**
     * Read pid from file or return null
     */
    private Integer readPidFromFile(String fileName) {
        try {
            File file = new File(fileName);

            if (!file.exists()) {
                return null;
            }

            byte[] buffer = new byte[16];
            InputStream is = new FileInputStream(file);
            while (is.read(buffer) != -1) {
            }
            return Integer.parseInt(new String(buffer).trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Read pid from process name or return null
     */
    private Integer readPidFromProcessName(String command, String processName) {
        try {
            ExecuteResult result = processExecutor.executeAndGetResult(command, 3000L);

            if (result.getExitValue() == 0L) {
                StringTokenizer lines = new StringTokenizer(result.getStandardOutput(), "\n");
                while (lines.hasMoreTokens()) {
                    String line = lines.nextToken();
                    if (line.contains(processName)) {
                        System.out.println("Find pid :: " + line);
                        String pidVal = line.trim().split("\\s+")[0];
                        return Integer.parseInt(pidVal);
                    }
                }
            }
        } catch (Exception e) {
        }

        return null;
    }
}