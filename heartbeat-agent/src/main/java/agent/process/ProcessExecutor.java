package agent.process;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * @GitHub : https://github.com/zacscoding
 */
public interface ProcessExecutor {

    long WATCHDOG_EXIST_VALUE = -999L;

    long execute(String command) throws IOException;

    long execute(String command, long timeout) throws IOException;

    long execute(String command, long timeout, ProcessExecutorHandler executorHandler) throws IOException;

    ExecuteResult executeAndGetResult(String command) throws IOException;

    ExecuteResult executeAndGetResult(String command, long timeout) throws IOException;

    ExecuteResult executeAndGetResult(String command, long timeout, ProcessExecutorHandler executorHandler)
        throws IOException;
}
