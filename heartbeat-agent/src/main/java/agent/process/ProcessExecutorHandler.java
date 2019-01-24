package agent.process;

/**
 * @GitHub : https://github.com/zacscoding
 */
public interface ProcessExecutorHandler {

    void onStandardOutput(String line);

    void onStandardError(String line);
}