package server.alert;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import server.state.HostEntity;
import server.state.HostState;

/**
 * @GitHub : https://github.com/zacscoding
 */
public abstract class MessageConverter {

    /**
     * Convert host entities to below text
     * - [No1]ServiceName0001 : Status[ Up ] / Pid [ 4234 ] / Ip [192.168.79.12 ] / Last beat [ 19-02-11 09:08:07 ]
     */
    public String convertServersState(List<HostEntity> entities) {
        if (entities == null) {
            entities = Collections.emptyList();
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        final String leftAlignFormat = "- %s*%s* : Status[ `%s` ] / Pid [ %s ] / Ip [ %s ] / Last beat [ %s ]\n";
        StringBuilder message = new StringBuilder(400 + 100 * entities.size());

        /*message.append(
            "+--------------------------------+---------+------------+-----------------+----------------------+\n"
        ).append(
            String.format(leftAlignFormat, "ServiceName", "State", "Pid", "Ip", "Last Heartbeat")
        ).append(
            "+--------------------------------+---------+------------+-----------------+----------------------+\n"
        );*/
        message.append("*Result of server status*\n");
        for (int i = 0; i < entities.size(); i++) {
            HostEntity entity = entities.get(i);
            String number = "[No" + (i + 1) + "]. ";
            String hostState = getHostState(entity.getHostState());
            message.append(
                String.format(leftAlignFormat, number, entity.getServiceName(), hostState, entity.getPid()
                    , entity.getIp(), dateFormat.format(new Date(entity.getLastAgentTimestamp())))
            );
        }

        return message.toString();
    }

    private String getHostState(HostState state) {
        if (state == null) {
            state = HostState.UNKNOWN;
        }

        switch (state) {
            case HEALTHY:
                return "Up";
            case HEARTBEAT_LOST:
                return "Down";
            case UNKNOWN:
            default:
                return "Unknown";
        }
    }
}