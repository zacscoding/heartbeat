package server.message.slack;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import me.ramswaroop.jbot.core.slack.models.Attachment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.springframework.stereotype.Component;
import server.state.HostEntity;

/**
 * Change to message
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Component
public class SlackMessageConverter {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    // ServiceName / Working / Pid / Ip / Last beat
    private String displayFormat = "%-20s    / %-10s    / %-10s    / %-10s    / %-20s\n";
    private String lineSeparator = "----------------------------------------------------------------------------------------------\n";

    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * Convert server state changed RichMessage
     *
     * @return Server state changed
     * | text
     */
    public RichMessage convertHostStateChangedMessage(String text) {
        RichMessage richMessage = new RichMessage("Server state changed");

        Attachment[] attachments = new Attachment[]{
            new Attachment()
        };
        attachments[0].setText(text);

        richMessage.setAttachments(attachments);
        return richMessage;
    }

    /**
     * Get host state message
     *
     * @return [ServiceName / Working / Pid / Ip / Last beat]
     */
    public String getHostStateString(HostEntity hostEntity) {
        String hostState = null;
        switch (hostEntity.getHostState()) {
            case HEALTHY:
                hostState = "ON";
                break;
            case HEARTBEAT_LOST:
                hostState = "OFF";
                break;
            case UNKNOWN:
                hostState = "Unknown";
        }

        return String.format(displayFormat, hostEntity.getServiceName(), hostState, hostEntity.getPid(),
            hostEntity.getIp(), dateFormat.format(new Date(hostEntity.getLastAgentTimestamp())));
    }

    /**
     * Get host state title
     *
     * @return ServiceName / Working / Pid / Ip / Last beat
     */
    public String getHostStateTitle() {
        return String.format(displayFormat, "ServiceName", "Working", "Pid", "Ip", "Last beat");
    }
}
