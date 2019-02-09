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
     *
     * | ServiceName                    | State   | Pid        | Ip              | Last heartbeat       |
     * [No1]. Berith-Api[DEV] / ON / 4234 / 127.0.0.1       | 19-02-09 05:19:05    |
     * | Berith-Wallet[DEV]             | OFF     | 24816      | 192.168.5.78    | 19-02-09 05:19:05    |
     */
    public String convertServersState(List<HostEntity> entities) {
        if (entities == null) {
            entities = Collections.emptyList();
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        final String leftAlignFormat = "- %s / %s / Working(%s) / Pid(%s) / Ip(%s) / Last beat(%s)\n";
        StringBuilder message = new StringBuilder(400 + 100 * entities.size());

        /*message.append(
            "+--------------------------------+---------+------------+-----------------+----------------------+\n"
        ).append(
            String.format(leftAlignFormat, "ServiceName", "State", "Pid", "Ip", "Last Heartbeat")
        ).append(
            "+--------------------------------+---------+------------+-----------------+----------------------+\n"
        );*/
        //message.append(String.format(leftAlignFormat, "No", "ServiceName", "State", "Pid", "Ip", "Last Heartbeat"));
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
                return "ON";
            case HEARTBEAT_LOST:
                return "OFF";
            case UNKNOWN:
            default:
                return "Unknown";
        }
    }
}