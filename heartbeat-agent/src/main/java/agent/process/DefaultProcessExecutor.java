package agent.process;

import agent.util.OSUtil;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class DefaultProcessExecutor implements ProcessExecutor {

    public static ProcessExecutor INSTANCE = new DefaultProcessExecutor();

    private DefaultProcessExecutor() {
    }

    @Override
    public long execute(String command) throws IOException {
        return execute(command, -1L);
    }

    @Override
    public long execute(String command, long timeout) throws IOException {
        return execute(command, timeout, null);
    }

    @Override
    public long execute(String command, long timeout, ProcessExecutorHandler executorHandler) throws IOException {
        return executeInternal(command, timeout, executorHandler, false).getExitValue();
    }

    @Override
    public ExecuteResult executeAndGetResult(String command) throws IOException {
        return executeAndGetResult(command, -1L);
    }

    @Override
    public ExecuteResult executeAndGetResult(String command, long timeout) throws IOException {
        return executeAndGetResult(command, timeout, null);
    }

    @Override
    public ExecuteResult executeAndGetResult(String command, long timeout, ProcessExecutorHandler executorHandler)
        throws IOException {
        return executeInternal(command, timeout, executorHandler, true);
    }

    private ExecuteResult executeInternal(String command, long timeout, ProcessExecutorHandler executorHandler,
        boolean collectLogOutput) throws IOException {
        boolean useWatchDog = timeout > -1L;
        ExecuteWatchdog watchDog = null;
        CommandLine cmdLine = CommandLine.parse(command);

        DefaultExecutor executor = new DefaultExecutor();
        if (useWatchDog) {
            watchDog = new ExecuteWatchdog(timeout);
            executor.setWatchdog(watchDog);
        }

        long exitValue;
        ExecuteLogOutputStream stdout = new ExecuteLogOutputStream(executorHandler, true, collectLogOutput);
        ExecuteLogOutputStream stderr = new ExecuteLogOutputStream(executorHandler, false, collectLogOutput);
        executor.setStreamHandler(new PumpStreamHandler(stdout, stderr));

        try {
            exitValue = executor.execute(cmdLine);
        } catch (ExecuteException e) {
            exitValue = e.getExitValue();
        }

        if (useWatchDog && watchDog.killedProcess()) {
            exitValue = WATCHDOG_EXIST_VALUE;
        }

        return new ExecuteResult(exitValue, stdout.getLogOutput(), stderr.getLogOutput());
    }

    private static class ExecuteLogOutputStream extends LogOutputStream {

        private static String NEW_LINE = OSUtil.getLineSeparator();
        private ProcessExecutorHandler handler;     // std out, err event handler
        private boolean forewordToStandardOutput;   // true : std out | false : std err
        private boolean collectLogOutput;           // whether collect log output
        private StringBuilder stringBuilder;        // log output collect
        private boolean existHandler;               // whether exist log out handler or not

        private ExecuteLogOutputStream(ProcessExecutorHandler handler, boolean forewordToStandardOutput,
            boolean collectLogOutput) {
            this.handler = handler;
            this.forewordToStandardOutput = forewordToStandardOutput;
            this.collectLogOutput = collectLogOutput;
            if (collectLogOutput) {
                this.stringBuilder = new StringBuilder();
            }
            this.existHandler = handler != null;
        }

        @Override
        protected void processLine(String line, int level) {
            // collect output
            if (collectLogOutput) {
                stringBuilder.append(line).append(NEW_LINE);
            }

            if (existHandler) {
                // std out handle
                if (forewordToStandardOutput) {
                    handler.onStandardOutput(line);
                } else { // std err handle
                    handler.onStandardError(line);
                }
            }
        }

        /**
         * @return collected output
         */
        public String getLogOutput() {
            return (stringBuilder != null) ? stringBuilder.toString() : null;
        }
    }
}