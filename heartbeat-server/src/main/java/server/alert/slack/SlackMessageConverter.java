package server.alert.slack;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import me.ramswaroop.jbot.core.slack.models.Attachment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.springframework.stereotype.Component;
import server.alert.MessageConverter;
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
public class SlackMessageConverter extends MessageConverter {

    public static final SlackMessageConverter INSTANCE = new SlackMessageConverter();

    private SlackMessageConverter() {
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
}
