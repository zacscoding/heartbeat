package agent.heartbeat;

import agent.AgentLogger;
import agent.process.DefaultProcessExecutor;
import agent.process.ExecuteResult;
import agent.process.ProcessExecutor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Docker heartbeat
 *
 * @GitHub : https://github.com/zacscoding
 */
public class DockerContainerHeartbeat extends Heartbeat {

    private ProcessExecutor processExecutor;
    private JsonParser parser;
    private String dockerNames;

    private Integer pid;
    private Boolean isAlive;

    public DockerContainerHeartbeat(String serviceName, String dockerNames) {
        super(serviceName);
        this.dockerNames = dockerNames;
        this.processExecutor = DefaultProcessExecutor.INSTANCE;
        this.parser = new JsonParser();
    }

    @Override
    public int getPid() {
        if (this.pid == null) {
            lookupDockerContainer();
        }

        return pid;
    }

    @Override
    public boolean isAlive() {
        if (isAlive == null) {
            lookupDockerContainer();
        }

        return isAlive == null ? false : isAlive;
    }

    @Override
    public void resetState() {
        this.pid = null;
        this.isAlive = null;
    }

    private void lookupDockerContainer() {
        String command = "docker inspect " + dockerNames;
        try {
            ExecuteResult inspectResult = processExecutor.executeAndGetResult(command, 3000L);
            if (inspectResult.getExitValue() != 0L) {
                throw new Exception();
            }

            String inspectJson = inspectResult.getStandardOutput();
            JsonElement rootElt = parser.parse(inspectJson);
            if (rootElt.isJsonArray()) {
                JsonArray array = (JsonArray) rootElt;
                for (JsonElement elt : array) {
                    try {
                        if (parseInspectJson(elt.getAsJsonObject().get("State").getAsJsonObject())) {
                            break;
                        }
                    } catch (Exception e) {
                    }
                }
            } else if (rootElt.isJsonObject()) {
                parseInspectJson(rootElt.getAsJsonObject().get("State").getAsJsonObject());
            } else {
                AgentLogger.info("Failed to parse inspect result. \n", inspectJson);
                throw new Exception();
            }
        } catch (Exception e) {
            this.isAlive = Boolean.FALSE;
        }
    }

    /**
     * parse pid="State.Pid" / isAlive="State.Running"
     *
     * @return true if sucess to parse, false otherwise
     */
    private boolean parseInspectJson(JsonObject stateNode) {
        try {
            this.pid = stateNode.get("Pid").getAsInt();
            this.isAlive = stateNode.get("Running").getAsBoolean();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}